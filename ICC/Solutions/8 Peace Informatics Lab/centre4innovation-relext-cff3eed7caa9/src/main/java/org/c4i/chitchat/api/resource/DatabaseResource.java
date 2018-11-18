package org.c4i.chitchat.api.resource;


import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableMap;
import dk.aaue.sna.alg.centrality.BrandesBetweennessCentrality;
import dk.aaue.sna.alg.centrality.CentralityResult;
import dk.aaue.sna.generate.ErdosRenyiGraphGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.c4i.chitchat.api.Config;
import org.c4i.chitchat.api.chat.ConversationListener;
import org.c4i.chitchat.api.db.JsonDocMapper;
import org.c4i.chitchat.api.model.*;
import org.c4i.graph.PropVertex;
import org.c4i.graph.PropWeightedEdge;
import org.c4i.graph.PropVertex;
import org.c4i.graph.WeightedEdge;
import org.c4i.nlp.Nlp;
import org.c4i.nlp.chat.Conversation;
import org.c4i.nlp.chat.Message;
import org.c4i.nlp.match.*;
import org.c4i.nlp.match.Compiler;
import org.c4i.nlp.normalize.StringNormalizer;
import org.c4i.nlp.normalize.StringNormalizers;
import org.c4i.nlp.tokenize.MatchingWordTokenizer;
import org.c4i.nlp.tokenize.Token;
import org.c4i.nlp.tokenize.TokenUtil;
import org.c4i.nlp.tokenize.Tokenizer;
import org.c4i.util.*;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.Length;
import org.jgrapht.VertexFactory;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.ISODateTimeFormat;
import org.parboiled.common.ImmutableList;
import org.parboiled.common.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

//import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

/**
 * Database storage
 * @author Arvid Halma
 * @version 23-11-2015
 */

@Path("/db")
@Api("/db")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class DatabaseResource implements ConversationListener {

    private Config config;
    private final Logger logger = LoggerFactory.getLogger(DatabaseResource.class);

    public DatabaseResource(Config configuration) {
        this.config = configuration;
    }

    @Override
    public void onReceive(Conversation conversation, Message message) {
        config.dao.conversationDao.upsert(conversation);
        config.dao.conversationDao.upsert(message);
    }

    @Override
    public void onSend(Conversation conversation, Result result) {
        config.dao.conversationDao.upsert(conversation);
        config.dao.conversationDao.upsertMessages(result.getReplies());
        config.dao.conversationDao.insertRangesIfNew(result.getRanges());
        DateTime timestamp = conversation.lastMessage().getTimestamp();
        config.dao.textDocDao.upsert(
                new TextDoc()
                        .setId(conversation.getId())
                        .setType("highlight")
                        .setBody(result.getHighlight())
                        .setCreated(timestamp)
                        .setUpdated(timestamp)
        );
    }

    @Override
    public void timout(Conversation conversation) {
        backup(conversation);
    }

    @Override
    public void reset(Conversation conversation) {
        backup(conversation);
    }

    public void backup(Conversation conversation) {
        final String orgConvId = conversation.getId();
        String newConvId = orgConvId + "." + DateTime.now();
        logger.info("Ending conversation after timeout: {}", newConvId);

        // store conversation under new id.
        config.dao.conversationDao.replaceConversation(orgConvId, newConvId);
        config.dao.textDocDao.updateId(orgConvId, newConvId);

    }

    public void loadAllDataSheets() {
        // first, prefer datasheets stored in the DB over sheets from files.
        final List<String> dbDataSheetNames = config.dao.textDocDao.getAllNames("datasheet");
        for (String name : dbDataSheetNames) {
            loadDataSheet(name);
        }

        // add missing datasheets from file
        for (String lang : config.getNlp().getLanguages()) {
            final List<String> fileDataSheetNames = config.getNlp().getFileDataSheetNames(lang);
            fileDataSheetNames.removeAll(dbDataSheetNames);
            for (String name : fileDataSheetNames) {
                loadDataSheet(name);
            }
        }
    }


    @PUT
    @Path("/sheet/load/{name}")
    @ApiOperation(
            value = "Load the latest data sheet",
            notes = "Either loads from the database or, when it does not exist, from file '/data/{lang}/datasheet'")
    public void loadDataSheet(@PathParam("name") String name) {
        final TextDoc sheetDoc = config.dao.textDocDao.getLastUpdatedByName("datasheet", name);
        if (sheetDoc == null) {
            final String body = config.getNlp().loadFileDataSheet(name);
            saveTextDoc("datasheet", name, body); // save in db for next time
        } else {
            config.getNlp().loadDataSheet(name, sheetDoc.getBody());
        }
    }

    @POST
    @Path("/sheet")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(
            value = "Save datasheet",
            notes = "Updates or inserts the datasheet in the database")
    public void saveDataSheet(@FormDataParam("name") String name, @FormDataParam("tsv") String tsv) {
        saveTextDoc("datasheet", name, tsv);
        loadDataSheet(name);
    }

    @PUT
    @Path("/vars/load")
    @ApiOperation(
            value = "Load the latest reply variables from the database",
            notes = "")
    public void loadReplyVariables() {
        final TextDoc varsDoc = config.dao.textDocDao.getLastUpdatedByName("vars", "Reply variables");
        if (varsDoc != null) {
            config.getNlp().setReplyVariables(new Substitution1Way(varsDoc.getBody(), false));
        } else {
            config.getNlp().setReplyVariables(new Substitution1Way());
        }
    }

    @POST
    @Path("/vars")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(
            value = "Save the latest reply variables",
            notes = "They will be readily available in scripts.")
    public void setReplyVariables(@FormDataParam("tsv") String tsv) {
        config.dao.textDocDao.upsert(new TextDoc().setType("vars").setName("Reply variables").setBody(tsv));
        loadReplyVariables();
    }

    @GET
    @Path("/vars")
    @ApiOperation(
            value = "Get all reply variables",
            response = Map.class)
    public Map<String, String> getReplyVariables() {
        final Substitution substitution = config.getNlp().getReplyVariables();
        return substitution == null ? ImmutableMap.of() : substitution.asMap();
    }

    /////////////// TextDoc /////////////// 

    @GET
    @Path("/textdoc/id/{id}")
    @ApiOperation(
            value = "Get text document by id",
            response = TextDoc.class)
    public TextDoc getTextDoc(@PathParam("id") String id) {
        return config.dao.textDocDao.getById(id);
    }

    @GET
    @Path("/textdoc/id/{id}/body")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(
            value = "Get text document body/content by id",
            response = String.class)
    public String getTextDocBody(@PathParam("id") String id) {
        return config.dao.textDocDao.getById(id).getBody();
    }

    @GET
    @Path("/textdoc/{type}/meta")
    @ApiOperation(
            value = "Get text document meta information by id",
            notes = "Everything, but the body",
            response = TextDoc.class,
            responseContainer = "List"
    )
    public List<TextDoc> getTextDocMetas(@PathParam("type") String type) {
        return config.dao.textDocDao.getAllMetaLastUpdated(type);
    }

    @GET
    @Path("/textdoc/{type}/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(
            value = "Get last updated text document content/body by type and name",
            response = TextDoc.class)
    public String getLastTextDoc(@PathParam("type") String type, @PathParam("name") String name) {
        final TextDoc doc = config.dao.textDocDao.getLastUpdatedByName(type, name);
        if (doc == null) {
            throw new WebApplicationException(404);
        }
        return doc.getBody();
    }

    @GET
    @Path("/textdoc/{type}")
    @ApiOperation(
            value = "Get all text documents by type",
            response = TextDoc.class,
            responseContainer = "List")
    public List<TextDoc> getTextDocs(@PathParam("type") String type) {
        return config.dao.textDocDao.getAllLastUpdated(type);
    }

    @GET
    @Path("/textdoc/{type}/{name}/meta")
    @ApiOperation(
            value = "Get text document meta information for a given type and name",
            notes = "I.e. retrieve all versions. Everything, but the body",
            response = TextDoc.class,
            responseContainer = "List"
    )
    public List<TextDoc> getTextDocVersionMetas(@PathParam("type") String type, @PathParam("name") String name) {
        return config.dao.textDocDao.getAllMetas(type, name);
    }

    @DELETE
    @Path("/textdoc/{type}/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(
            value = "Delete all text document versions by type and name")
    public void deleteTextDoc(@PathParam("type") String type, @PathParam("name") @NotNull @Length(min = 1, max = 64) String name) {
        config.dao.textDocDao.deleteByName(type, name);
    }

    @GET
    @Path("/textdoc/{type}/name")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(
            value = "Get text document names for a given type",
            response = String.class,
            responseContainer = "List"
    )
    public List<String> getTextDocNames(@PathParam("type") String type) {
        return config.dao.textDocDao.getAllNames(type);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/textdoc/{type}/{name}")
    @ApiOperation(
            value = "Save a text document given a type and name",
            notes = "It won't overwrite possible earlier versions, but create another version if the same name is used."
    )
    public void saveTextDoc(@PathParam("type") String type, @PathParam("name") @NotNull @Length(min = 1, max = 64) String name, @FormDataParam("body") String body) {
        config.dao.textDocDao.upsert(new TextDoc().setName(name).setType(type).setBody(body));
    }

    /////////////// JsonDoc /////////////// 

    @GET
    @Path("/jsondoc/id/{id}")
    @ApiOperation(
            value = "Get JSON document by id",
            response = JsonDoc.class)
    public JsonDoc getJsonDoc(@PathParam("id") String id) {
        return config.dao.jsonDocDao.getById(id);
    }

    @GET
    @Path("/jsondoc/id/{id}/body")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(
            value = "Get JSON document body/content by id",
            response = String.class)
    public Map<String, Object> getJsonDocBody(@PathParam("id") String id) {
        return config.dao.jsonDocDao.getById(id).getBody();
    }

    @GET
    @Path("/jsondoc/{type}/meta")
    @ApiOperation(
            value = "Get JSON document meta information by id",
            notes = "Everything, but the body",
            response = JsonDoc.class,
            responseContainer = "List"
    )
    public List<JsonDoc> getJsonDocMetas(@PathParam("type") String type) {
        return config.dao.jsonDocDao.getAllMetaLastUpdated(type);
    }

    @GET
    @Path("/jsondoc/{type}/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(
            value = "Get last updated JSON document content/body by type and name",
            response = JsonDoc.class)
    public Map<String, Object> getLastJsonDoc(@PathParam("type") String type, @PathParam("name") String name) {
        final JsonDoc doc = config.dao.jsonDocDao.getLastUpdatedByName(type, name);
        if (doc == null) {
            throw new WebApplicationException(404);
        }
        return doc.getBody();
    }

    @GET
    @Path("/jsondoc/{type}")
    @ApiOperation(
            value = "Get all JSON documents by type",
            response = JsonDoc.class,
            responseContainer = "List")
    public List<JsonDoc> getJsonDocs(@PathParam("type") String type) {
        return config.dao.jsonDocDao.getAllLastUpdated(type);
    }

    @GET
    @Path("/jsondoc/{type}/{name}/meta")
    @ApiOperation(
            value = "Get JSON document meta information for a given type and name",
            notes = "I.e. retrieve all versions. Everything, but the body",
            response = JsonDoc.class,
            responseContainer = "List"
    )
    public List<JsonDoc> getJsonDocVersionMetas(@PathParam("type") String type, @PathParam("name") String name) {
        return config.dao.jsonDocDao.getAllMetas(type, name);
    }

    @DELETE
    @Path("/jsondoc/{type}/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(
            value = "Delete all JSON document versions by type and name")
    public void deleteJsonDoc(@PathParam("type") String type, @PathParam("name") @NotNull @Length(min = 1, max = 64) String name) {
        config.dao.jsonDocDao.deleteByName(type, name);
    }

    @GET
    @Path("/jsondoc/{type}/name")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(
            value = "Get JSON document names for a given type",
            response = String.class,
            responseContainer = "List"
    )
    public List<String> getJsonDocNames(@PathParam("type") String type) {
        return config.dao.jsonDocDao.getAllNames(type);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/jsondoc/{type}/{name}")
    @ApiOperation(
            value = "Save a JSON document given a type and name",
            notes = "It won't overwrite possible earlier versions, but create another version if the same name is used."
    )
    public void saveJsonDoc(@PathParam("type") String type, @PathParam("name") @NotNull @Length(min = 1, max = 64) String name, @FormDataParam("body") String body) {
        final LinkedHashMap<String, Object> bodyMap = JsonDocMapper.readJson(body);
        config.dao.jsonDocDao.upsert(new JsonDoc().setName(name).setType(type).setBody(bodyMap));
    }

    /////////////// Conversations /////////////// 

    @GET
    @Path("/conversation/export")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @ApiOperation(
            value = "Download conversation data as CSVs bundled in a zip file.",
            response = Response.class
    )
    public Response exportConversation() throws IOException {
        java.nio.file.Path tempDirPath = Files.createTempDirectory("chitchat");
        File tempDirFile = tempDirPath.toFile();
        String cnvFile = new File(tempDirFile, "conversation.txt").getAbsolutePath();
        String msgFile = new File(tempDirFile, "message.txt").getAbsolutePath();
        String lblFile = new File(tempDirFile, "label.txt").getAbsolutePath();

        config.dao.conversationDao.exportConversation(cnvFile);
        config.dao.conversationDao.exportMessage(msgFile);
        config.dao.conversationDao.exportRange(lblFile);

        String zipName = "chitchat-export-" + System.currentTimeMillis() + ".zip";
        String destinationFile = new File(tempDirFile, zipName).getAbsolutePath();
        ZipUtil.zip(destinationFile,
                ImmutableList.of(cnvFile, msgFile, lblFile));

        InputStream is = new FileInputStream(destinationFile);
        return Response.ok(is)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + zipName + "\"")
                .build();
    }

    @GET
    @Path("/conversation/export/anonymous")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @ApiOperation(
            value = "Download anonymized conversation data as CSVs bundled in a zip file.",
            response = Response.class
    )
    public Response exportAnonymousConversation() throws IOException {
        java.nio.file.Path tempDirPath = Files.createTempDirectory("chitchat");
        File tempDirFile = tempDirPath.toFile();
        String cnvFile = new File(tempDirFile, "conversation.txt").getAbsolutePath();
        String msgFile = new File(tempDirFile, "message.txt").getAbsolutePath();
        String lblFile = new File(tempDirFile, "label.txt").getAbsolutePath();

        config.dao.conversationDao.exportAnonymousConverstation(cnvFile);
        config.dao.conversationDao.exportAnonymousMessage(msgFile);
        config.dao.conversationDao.exportAnonymousRange(lblFile);

        String zipName = "chitchat-anonymous-export-" + System.currentTimeMillis() + ".zip";
        String destinationFile = new File(tempDirFile, zipName).getAbsolutePath();
        ZipUtil.zip(destinationFile,
                ImmutableList.of(cnvFile, msgFile, lblFile));

        InputStream is = new FileInputStream(destinationFile);
        return Response.ok(is)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + zipName + "\"")
                .build();
    }

    @GET
    @Path("/conversation/between")
    @ApiOperation(
            value = "Get conversations within a period",
            notes = "A conversation is a map with properties as: id, channel, from, to, labels...",
            response = Map.class,
            responseContainer = "List"
    )
    public List<Map<String, Object>> getConversation(
            @QueryParam("channel") String channel,
            @QueryParam("from") String from,
            @QueryParam("to") String to
    ) {
        DateTime fromDate = ISODateTimeFormat.dateTimeParser().parseDateTime(from);
        DateTime toDate = ISODateTimeFormat.dateTimeParser().parseDateTime(to);
        return config.dao.conversationDao.conversationOverview(channel, fromDate, toDate);
    }

    @GET
    @Path("/conversation/stats")
    @ApiOperation(
            value = "Get conversation counts per available channel within a period",
            response = Map.class
    )
    public Map<String, Long> getConversationStats() {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("conv.sample.count", config.dao.conversationDao.conversationCount("sample"));
        result.put("conv.sample.user.count", config.dao.conversationDao.conversationUserCount("sample"));
        result.put("conv.sample.message.count", config.dao.conversationDao.conversationMessageCount("sample"));

        result.put("conv.chitchat.count", config.dao.conversationDao.conversationCount("chitchat"));
        result.put("conv.chitchat.user.count", config.dao.conversationDao.conversationUserCount("chitchat"));
        result.put("conv.chitchat.message.count", config.dao.conversationDao.conversationMessageCount("chitchat"));

//        result.put("conv.devbot.count", config.dao.conversationDao.conversationCount("devbot"));
//        result.put("conv.devbot.user.count", config.dao.conversationDao.conversationUserCount("devbot"));
//        result.put("conv.devbot.message.count", config.dao.conversationDao.conversationMessageCount("devbot"));

        result.put("conv.fb.count", config.dao.conversationDao.conversationCount("fb"));
        result.put("conv.fb.user.count", config.dao.conversationDao.conversationUserCount("fb"));
        result.put("conv.fb.message.count", config.dao.conversationDao.conversationMessageCount("fb"));
        return result;

    }

    @GET
    @Path("/conversation/freqs")
    @ApiOperation(
            value = "Conversation counts per day, per available channel",
            response = Map.class
    )
    public Map<String, List<TimeValue>> getConversationFreqs() {
        Map<String, List<TimeValue>> result = new LinkedHashMap<>();
        result.put("sample", config.dao.conversationDao.dailyConversationCounts("sample"));
        result.put("fb", config.dao.conversationDao.dailyConversationCounts("fb"));
        result.put("chitchat", config.dao.conversationDao.dailyConversationCounts("chitchat"));
        return result;

    }

    @GET
    @Path("/conversation/{id}/message")
    @ApiOperation(
            value = "Get message for the specified conversation",
            response = Message.class,
            responseContainer = "List"
    )
    public List<Message> getConversationMessages(@PathParam("id") String id) {
        return config.dao.conversationDao.conversationMessages(id);
    }

    @GET
    @Path("/conversation/{id}/range")
    @ApiOperation(
            value = "Get matched ranges for the specified conversation",
            response = Range.class,
            responseContainer = "List"
    )
    public List<Range> getConversationRanges(@PathParam("id") String id) {
        return config.dao.conversationDao.conversationRanges(id);
    }

    @GET
    @Path("/labels/{channel}")
    @ApiOperation(
            value = "Get all unique labels that matched for a given channel",
            response = Range.class,
            responseContainer = "List"
    )
    public List<String> getLabels(@PathParam("channel") String channel) {
        return config.dao.conversationDao.labels(channel);
    }

    @DELETE
    @Path("/conversation")
    @ApiOperation(
            value = "Remove all conversations for a channel, along with the associated data from the database"
    )
    public void deleteConversationsForChannel(@QueryParam("channel") String channel) {
        config.dao.conversationDao.deleteConversationsForChannel(channel);
    }

    @DELETE
    @Path("/conversation/{id}")
    @ApiOperation(
            value = "Remove a conversation, along with the associated data from the database"
    )
    public void deleteConversationById(@PathParam("id") String id) {
        config.dao.conversationDao.deleteConversationById(id);
    }


    @POST
    @Timed
    @Path("/sample/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed("ADMIN")
    @ApiOperation(
            value = "Upload conversations for the sample channel",
            response = Response.class
    )
    public Response uploadFile(
            @FormDataParam("file") final InputStream fileInputStream,
            @FormDataParam("file") final FormDataContentDisposition contentDispositionHeader) throws IOException {

        String fileName = contentDispositionHeader.getFileName();

        CSVParser parser = CSVParser.parse(fileInputStream, StandardCharsets.UTF_8, CSVFormat.EXCEL.withIgnoreSurroundingSpaces());
        List<Message> messages = new ArrayList<>();
        List<Conversation> conversations = new ArrayList<>();

        int i = 1;
        for (CSVRecord record : parser) {
            try {
                Message msg = new Message();
                msg.setIncoming(true);
                //msg.setRecipientId("Unknown");
                msg.setSenderId(StringUtil.truncate(record.get(1), 64));
                msg.setChannel("sample");
                msg.setId(StringUtil.truncate("msg-" + record.get(0) + record.get(1), 64));
                msg.setText(record.get(2));
                msg.setTimestamp(DateTimeUtil.parseLiberalDateTime(record.get(0)));

                String conversationId = StringUtil.truncate(msg.getTimestamp().toString().substring(0, 10) + "_" + record.get(1), 64);

                msg.setConversationId(conversationId);


                messages.add(msg);

                conversations.add(new Conversation(conversationId).setUserId(msg.getSenderId()).setBotId("Unknown").setChannel("sample"));
            } catch (IndexOutOfBoundsException e) {
                System.out.println("record = " + record);
                System.out.println("e = " + e);
            }
        }

        // Save
        config.dao.conversationDao.upsertConversations(conversations);
        config.dao.conversationDao.upsertMessages(messages);

        return Response.status(200).entity(fileName).build();
    }

    @POST
    @Timed
    @Path("/match")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(
            value = "Analyse all selected conversations by running a matching script",
            response = AnalysisResult.class
    )
    public AnalysisResult match(
            @FormDataParam("channel") String channel,
            @FormDataParam("from") String from,
            @FormDataParam("to") String to,
            @FormDataParam("labels") String labels,
            @FormDataParam("script") String source) {
        DateTime fromDate = ISODateTimeFormat.dateTimeParser().parseDateTime(from);
        DateTime toDate = ISODateTimeFormat.dateTimeParser().parseDateTime(to);

        List<AnalysisMessage> messages = Collections.synchronizedList(new ArrayList<>());
        Histogram<String> histogram = new Histogram<>();
        Map<String, List<TimeValue>> timeLineMap = new HashMap<>();

        final ImmutableList<String> labelList = ImmutableList.of(labels.split(","));
        Script script = Compiler.compile(source, config.getNlp());
        final List<Message> selectedMessages = config.dao.conversationDao.channelMessages(channel, fromDate, toDate);

        // parallelStream() is ~2x faster with 8 cores...
        selectedMessages.parallelStream().forEach(message -> {
            Result matchResult = new Eval(config.getNlp()).find(script, message.getText());

            if (labels.isEmpty() || matchResult.containsAllLabel(labelList)) {
                Histogram<String> histogramResult = Eval.histogram(matchResult.getRanges());
                histogram.join(histogramResult);

                messages.add(new AnalysisMessage(message.getTimestamp(), message.getSenderId(), matchResult.getHighlight(), matchResult.getRanges()));
            }
        });

        TimeLine<AnalysisMessage> tl = new TimeLine<>(messages);
        for (String label : histogram.getEntries()) {
            TimeLine<AnalysisMessage> labelLine = tl.filter(sm -> sm.getMatches().stream().anyMatch(r -> r.label.equals(label)));
            TimeLine<TimeValue> counts = labelLine.countPerDay(tl.getStartDateTime(), tl.getEndDateTime().plus(Period.days(1)));
            timeLineMap.put(label, counts.asList());
        }

        return new AnalysisResult(histogram.asMap(), messages, timeLineMap);
    }

    @POST
    @Timed
    @Path("/conversation/match")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(
            value = "Analyse all selected conversations by running a matching script",
            response = AnalysisResult.class
    )
    public AnalysisConvResult conversationMatch(
            @FormDataParam("channel") String channel,
            @FormDataParam("from") String from,
            @FormDataParam("to") String to,
            @FormDataParam("labels") String labels,
            @FormDataParam("script") String source) {
        DateTime fromDate = ISODateTimeFormat.dateTimeParser().parseDateTime(from);
        DateTime toDate = ISODateTimeFormat.dateTimeParser().parseDateTime(to);

        Histogram<String> labelHistogram = new Histogram<>();
        Map<String, Histogram<String>> wordHistograms = Collections.synchronizedMap(new HashMap<>());

        final ImmutableList<String> labelList = ImmutableList.of(labels.split(","));
        final Nlp nlp = config.getNlp();

        Script script = Compiler.compile(source, nlp);

        List<Conversation> conversations = config.dao.conversationDao.conversations(channel, fromDate, toDate);
        List<Result> results = Collections.synchronizedList(new ArrayList<>());

        final StringNormalizer normalizer = nlp.getNormalizer(script.getConfig());
        final Tokenizer wordTokenizer = nlp.getWordTokenizer(script.getConfig());

        // parallelStream() is ~2x faster with 8 cores...
        conversations.parallelStream().forEach(conversation -> {
            Result matchResult = new Eval(nlp).reply(script, conversation);
            // add entire conversation to result (not done by default)
            matchResult.setConversation(conversation);
            // strip replies (not needed for analysis)
            matchResult.setReplies(null);

            if (labels.isEmpty() || matchResult.containsAllLabel(labelList)) {
                final List<Range> ranges = matchResult.getRanges();
                for (Range range : ranges) {
                    wordHistograms.computeIfAbsent(range.label, key -> new Histogram<>());
                    final List<Token> words = wordTokenizer.tokenize(range.value);
                    normalizer.normalizeTokens(words);
                    wordHistograms.get(range.label).add(TokenUtil.toSentenceMaybeNormalized(words));
                }


                Histogram<String> histogramResult = Eval.histogram(ranges);
                labelHistogram.join(histogramResult);

                results.add(matchResult);
            }
        });

        Map<String, Map<String, Double>> wordHistoMaps = new HashMap<>();
        for (String label : labelHistogram) {
            wordHistoMaps.put(label, wordHistograms.get(label).asSortedMap(10));
        }


        Map<String, List<TimeValue>> timeLineMap = new HashMap<>();
        TimeLine<Result> tl = new TimeLine<>(results);
        for (String label : labelHistogram.getEntries()) {
            TimeLine<Result> labelLine = tl.filter(sm -> sm.containsLabel(label));
            TimeLine<TimeValue> counts = labelLine.countPerDay(tl.getStartDateTime(), tl.getEndDateTime().plus(Period.days(1)));
            timeLineMap.put(label, counts.asList());
        }

        return new AnalysisConvResult(labelHistogram.asMap(), wordHistoMaps, results, timeLineMap);
    }

    @POST
    @Timed
    @Path("/conversation/match/db")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(
            value = "Analyse all selected conversations by running a matching script",
            response = AnalysisResult.class
    )
    public AnalysisConvResult conversationMatchDb(
            @FormDataParam("channel") String channel,
            @FormDataParam("from") String from,
            @FormDataParam("to") String to,
            @FormDataParam("labels") String labels) {
        DateTime fromDate = ISODateTimeFormat.dateTimeParser().parseDateTime(from);
        DateTime toDate = ISODateTimeFormat.dateTimeParser().parseDateTime(to);

        Histogram<String> labelHistogram = new Histogram<>();
        Map<String, Histogram<String>> wordHistograms = Collections.synchronizedMap(new HashMap<>());

        final ImmutableList<String> labelList = ImmutableList.of(labels.split(","));


        List<Conversation> conversations = config.dao.conversationDao.conversations(channel, fromDate, toDate);
        List<Range> ranges = config.dao.conversationDao.conversationRanges(channel, fromDate, toDate);
        Map<String, List<Range>> convToRanges = new HashMap<>();
        for (Range range : ranges) {
            final String cid = range.conversationId;
            convToRanges.computeIfAbsent(cid, key -> new ArrayList<>());
            convToRanges.get(cid).add(range);
        }

        List<Result> results = Collections.synchronizedList(new ArrayList<>());

        final StringNormalizer normalizer = StringNormalizers.DEFAULT;
        final Tokenizer wordTokenizer = new MatchingWordTokenizer();

        // parallelStream() is ~2x faster with 8 cores...
        conversations.parallelStream().forEach(conversation -> {
            final List<Range> convRanges = convToRanges.getOrDefault(conversation.getId(), ImmutableList.of());
            final Map<String, List<Range>> convRangeMap = new HashMap<>();
            for (Range convRange : convRanges) {
                convRangeMap.computeIfAbsent(convRange.label, key -> new ArrayList<>(2));
                convRangeMap.get(convRange.label).add(convRange);
            }

            StringBuilder inText = new StringBuilder();
            for (Message message : conversation.getMessages()) {
                if (message.getIncoming()) {
                    inText.append(message.getText()).append(" \n\n"); // todo: think about addText?
                }
            }

            Result matchResult = new Result();
            matchResult.setMatches(convRangeMap);
            matchResult.setConversation(conversation);
            matchResult.setHighlight(Eval.highlightWithTags(inText.toString(), convRanges));


            if (labels.isEmpty() || matchResult.containsAllLabel(labelList)) {
                for (Range range : convRanges) {
                    wordHistograms.computeIfAbsent(range.label, key -> new Histogram<>());
                    final List<Token> words = wordTokenizer.tokenize(range.value);
                    normalizer.normalizeTokens(words);
                    wordHistograms.get(range.label)
                            .add(
                                    TokenUtil.toSentenceMaybeNormalized(words)
                            );
                }


                Histogram<String> histogramResult = Eval.histogram(convRanges);
                labelHistogram.join(histogramResult);

                results.add(matchResult);
            }
        });

        Map<String, Map<String, Double>> wordHistoMaps = new HashMap<>();
        for (String label : labelHistogram) {
            wordHistoMaps.put(label, wordHistograms.get(label).asSortedMap(10));
        }


        Map<String, List<TimeValue>> timeLineMap = new HashMap<>();
        TimeLine<Result> tl = new TimeLine<>(results);
        for (String label : labelHistogram.getEntries()) {
            TimeLine<Result> labelLine = tl.filter(sm -> sm.containsLabel(label));
            TimeLine<TimeValue> counts = labelLine.countPerDay(tl.getStartDateTime(), tl.getEndDateTime().plus(Period.days(1)));
            timeLineMap.put(label, counts.asList());
        }

        return new AnalysisConvResult(labelHistogram.asMap(), wordHistoMaps, results, timeLineMap);
    }

    @GET
    @Timed
    @Path("/conversation/extract")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(
            value = "Extract CSV from data",
            response = AnalysisResult.class
    )
    public String conversationExtract(
            @QueryParam("script") String script,
            @QueryParam("channel") String channel,
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("labels") String labels) {
        DateTime fromDate = ISODateTimeFormat.dateTimeParser().parseDateTime(from);
        DateTime toDate = ISODateTimeFormat.dateTimeParser().parseDateTime(to);
        final ImmutableList<String> labelList = ImmutableList.of(labels.split(","));

        final List<Range> ranges = new ArrayList<>();
        final HashMap<String, Conversation> conversationMap = new HashMap<>();

        if(script == null || script.isEmpty()) {
            ranges.addAll(config.dao.conversationDao.conversationRanges(channel, fromDate, toDate));
            List<Conversation> conversations = config.dao.conversationDao.conversations(channel, fromDate, toDate);
            for (Conversation conversation : conversations) {
                conversationMap.put(conversation.getId(), conversation);
            }
        } else {
            final Nlp nlp = config.getNlp();
            Script scr = Compiler.compile(script, nlp);

            List<Conversation> conversations = config.dao.conversationDao.conversations(channel, fromDate, toDate);

            conversations.forEach(conversation -> {
                Result matchResult = new Eval(nlp).reply(scr, conversation);
                if(matchResult != null) {
                    conversationMap.put(conversation.getId(), conversation);
                    ranges.addAll(matchResult.getRanges());
                }
            });
        }


        LinkedHashMap<String, LinkedHashMap<String, String>> data = new LinkedHashMap<>();

        LinkedHashMap<String, String> emptyRow = new LinkedHashMap<>();
        for (String label : labelList) {
            emptyRow.put(label, "");
        }

        for (Range range : ranges) {
            final String conv = range.conversationId;
            if(labels.contains(range.label)) {
                data.putIfAbsent(conv, new LinkedHashMap<>(emptyRow));

                data.get(conv).computeIfPresent(range.label, (k, v) -> v.isEmpty() ? range.getValue() : v + ";" + range.getValue());
            }
        }

        StringBuilder sb = new StringBuilder();
        // header
        sb.append("conversation\t");
        sb.append(String.join("\t", labelList));
        sb.append("\ttext");
        sb.append('\n');
        for (Map.Entry<String, LinkedHashMap<String, String>> row : data.entrySet()) {
            final String convId = row.getKey();
            sb.append(convId);
            sb.append('\t');
            sb.append(String.join("\t", row.getValue().values()));
            sb.append('\t');
            sb.append(conversationMap.get(convId).toSingleLineString());
            sb.append('\n');
        }
        return sb.toString();
    }

    @POST
    @Timed
    @Path("/graph")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(
            value = "Analyse connection in texts",
            response = WeightedGraph.class
    )
    public WeightedGraph<PropVertex, PropWeightedEdge> graph(@FormDataParam("script") String script) {
//        final WeightedGraph<String, DefaultWeightedEdge> graph = SNATest.getGraph();
        final WeightedGraph<PropVertex, PropWeightedEdge> g2 = new ListenableUndirectedWeightedGraph<>(PropWeightedEdge.class);

        final Nlp nlp = config.getNlp();
        Script scr = Compiler.compile(script, nlp);
        StringNormalizer normalizer = nlp.getNormalizer(scr.getConfig());

        List<TextDoc> docs = config.dao.textDocDao.getAllLastUpdated("doc");
        for (TextDoc doc : docs) {
            String body = doc.getBody();
            String docName = doc.getName();
            logger.info("Analysing {}", docName);

            Tuple2<Result, WeightedGraph<PropVertex, PropWeightedEdge>> tuple = new Eval(config.getNlp()).findGraph(scr, docName, body);

            WeightedGraph<PropVertex, PropWeightedEdge> g = tuple.b;

            for (PropVertex v : g.vertexSet()) {
                if(!g2.containsVertex(v)){
                    g2.addVertex(v);
                }
            }

            for (PropWeightedEdge e : g.edgeSet()) {
                if(!g2.containsEdge(e)){
                    PropVertex source = (PropVertex) e.getSource();
                    PropVertex target = (PropVertex) e.getTarget();
                    PropWeightedEdge propWeightedEdge = g2.addEdge(source, target);
                    if(propWeightedEdge != null) { // todo: why null?
                        propWeightedEdge.setProps(e.getProps());
                    }
                }
            }

        }

        // calculate centrality

        BrandesBetweennessCentrality<PropVertex, PropWeightedEdge> centralityMeasure = new BrandesBetweennessCentrality<>(g2);

        CentralityResult<PropVertex> centralityResult = centralityMeasure.calculate();
        Map<PropVertex, Double> edgeWeights = centralityResult.getRaw();
        edgeWeights.forEach((vertex, value) -> vertex.put("betweenness", value));

        return g2;
    }
}