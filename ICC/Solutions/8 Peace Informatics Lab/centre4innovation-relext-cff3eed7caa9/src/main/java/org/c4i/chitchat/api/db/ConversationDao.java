package org.c4i.chitchat.api.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.c4i.nlp.chat.Conversation;
import org.c4i.nlp.chat.Message;
import org.c4i.nlp.match.Range;
import org.c4i.util.TimeValue;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.mapper.MapMapper;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manage persistence of conversation/messages/ranges
 * @author Arvid Halma
 * @version 13-4-18
 */
public interface ConversationDao  extends SqlObject {

    @RegisterRowMapper(MapMapper.class)
    @SqlQuery(// language=SQL
            "SELECT count(*) FROM (SELECT DISTINCT userId FROM conversation WHERE channel= :channel) AS tmp;")
    long conversationUserCount(@Bind("channel") String channel);

    @RegisterRowMapper(MapMapper.class)
    @SqlQuery(// language=SQL
            "SELECT count(*) FROM conversation WHERE channel= :channel;")
    long conversationCount(@Bind("channel") String channel);

    @RegisterRowMapper(MapMapper.class)
    @SqlQuery(// language=SQL
            "SELECT count(*) FROM message JOIN conversation c2 on message.conversationid = c2.id WHERE channel = :channel;")
    long conversationMessageCount(@Bind("channel") String channel);



    default int exportConversation(String file) throws IOException {
        return export(//language=SQL
                "SELECT * FROM conversation;", file);
    }

    default int exportMessage(String file) throws IOException {
        return export(//language=SQL
                "SELECT * FROM message;", file);
    }

    default int exportRange(String file) throws IOException {
        return export(//language=SQL
                "SELECT * FROM range;", file);
    }

    default int exportAnonymousConverstation(String file) throws IOException {
        return export(//language=SQL
                "SELECT encode(digest(id, 'md5'), 'hex') id, " +
                        "lang, botid, encode(digest(userid, 'md5'), 'hex') userid, " +
                        "channel \n"+
                        "FROM conversation;", file);
    }

    default int exportAnonymousMessage(String file) throws IOException {
        return export(//language=SQL
                "SELECT encode(digest(id, 'md5'), 'hex') id, " +
                        "incoming, encode(digest(conversationid, 'md5'), 'hex') conversationid, " +
                        "encode(digest(senderid, 'md5'), 'hex') senderid, " +
                        "encode(digest(recipientid, 'md5'), 'hex') recipientid, " +
                        "text, timestamp \n"+
                        "FROM message;", file);
    }

    default int exportAnonymousRange(String file) throws IOException {
        return export(//language=SQL
                "SELECT encode(digest(label || charStart || encode(digest(conversationid, 'md5'), 'hex'), 'md5'), 'hex') id, \n" +
                        "label, value, encode(digest(conversationid, 'md5'), 'hex'), tokenstart, tokenend, charstart, charend, props, section \n" +
                        "FROM range;", file);
    }

    default int export(String query, String file) throws IOException {
        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(file));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.TDF);
        ) {
            AtomicInteger count = new AtomicInteger();
            getHandle()
                    .createQuery(query)
                    .map(new MapMapper())
                    .stream()
                    .forEach(row -> {
                            //System.out.println("row = " + row);
                            // do header
                            try {
                                if (count.getAndIncrement() == 0) {
                                    csvPrinter.printRecord(row.keySet());
                                }
                                csvPrinter.printRecord(row.values());
                            } catch (IOException e) {

                            }
                    });
            csvPrinter.flush();

            return count.get();
        }
    }

    ////////////// Message //////////////


    @SqlUpdate(// language=SQL
            "INSERT INTO message (id, conversationId, senderId, recipientId, text, timestamp, incoming) " +
                    "VALUES (:id, :conversationId, :senderId, :recipientId, truncm(:text, 65536), :timestamp, :incoming) " +
                    "ON CONFLICT (id)\n" +
                    "DO UPDATE SET (conversationId, senderId, recipientId, text, timestamp, incoming) = (:conversationId, :senderId, :recipientId, truncm(:text, 65536), :timestamp, :incoming)\n" +
                    "WHERE message.id = :id;")
    void upsert(@BindBean Message message);

    @SqlBatch(// language=SQL
            "INSERT INTO message (id, conversationId, senderId, recipientId, text, timestamp, incoming) " +
                    "VALUES (:id, :conversationId, :senderId, :recipientId, truncm(:text, 65536), :timestamp, :incoming) " +
                    "ON CONFLICT (id)\n" +
                    "DO UPDATE SET (conversationId, senderId, recipientId, text, timestamp, incoming) = (:conversationId, :senderId, :recipientId, truncm(:text, 65536), :timestamp, :incoming)\n" +
                    "WHERE message.id = :id;")
    void upsertMessages(@BindBean Collection<Message> messages);


    @RegisterRowMapper(TimeValueMapper.class)
    @SqlQuery(// language=SQL
            "SELECT date_trunc('day', timestamp) AS day, count(*)\n" +
                    "FROM message\n" +
                    "WHERE message.timestamp BETWEEN :fromDate AND :toDate\n" +
                    "GROUP BY date_trunc('day', timestamp)\n" +
                    "ORDER BY date_trunc('day', timestamp) ASC;" )
    List<TimeValue> dailyMessageCounts(@Bind("fromDate") DateTime fromDate, @Bind("toDate") DateTime toDate);

    @RegisterRowMapper(TimeValueMapper.class)
    @SqlQuery(// language=SQL
            "SELECT date_trunc('day', timestamp) AS day, count(*)\n" +
                    "FROM message\n" +
                    "GROUP BY date_trunc('day', timestamp)\n" +
                    "ORDER BY date_trunc('day', timestamp) ASC;" )
    List<TimeValue> dailyMessageCounts();

    @RegisterRowMapper(TimeValueMapper.class)
    @SqlQuery(// language=SQL
            "SELECT date_trunc('day', timestamp) AS day, count(*)\n" +
                    "FROM message\n" +
                    "JOIN conversation ON message.conversationid = conversation.id \n" +
                    "WHERE channel = :channel\n" +
                    "GROUP BY date_trunc('day', timestamp)\n" +
                    "ORDER BY date_trunc('day', timestamp) ASC;" )
    List<TimeValue> dailyMessageCounts(@Bind("channel") String channel);

    ////////////// Range //////////////

    default long upsert(Range range){
        try {
            return upsertRange(range.label, range.value, range.conversationId, range.tokenStart, range.tokenEnd, range.charStart, range.charEnd, RangeMapper.PROPS_MAPPER.writeValueAsString(range.props), range.section);
        } catch (JsonProcessingException e) {
            return -1;
        }
    }

    default void upsertRanges(Collection<Range> ranges) {
        if(ranges == null || ranges.isEmpty())
            return;

        Handle handle = getHandle();
        PreparedBatch batch = handle.prepareBatch(// language=SQL
                "INSERT INTO range (id, label, value, conversationId, tokenStart, tokenEnd, charStart, charEnd, props) " +
                        " VALUES (encode(digest(:label || :charStart || :conversationId, 'sha256'), 'hex'), :label, :value, :conversationId, :tokenStart, :tokenEnd, :charStart, :charEnd, to_jsonb(:jsonProps))" +
                        "ON CONFLICT (id)\n" +
                        "DO UPDATE SET (label, value, conversationId, tokenStart, tokenEnd, charStart, charEnd, props) = (:label, :value, :conversationId, :tokenStart, :tokenEnd, :charStart, :charEnd, to_jsonb(:jsonProps))\n" +
                        "WHERE range.id = encode(digest(:label || :charStart || :conversationId, 'sha256'), 'hex');");

        try {
             for (Range range : ranges) {
                batch
                    .bind("label", range.label)
                    .bind("value", range.value)
                    .bind("tokenStart", range.tokenStart)
                    .bind("tokenEnd", range.tokenEnd)
                    .bind("charStart", range.charStart)
                    .bind("charEnd", range.charEnd)
                    .bind("conversationId", range.conversationId)
                    .bind("jsonProps", RangeMapper.PROPS_MAPPER.writeValueAsString(range.props))
                    .add();
            }
        } catch (JsonProcessingException e){

        }
        batch.execute();
    }


    @SqlUpdate(// language=SQL
            "DELETE FROM range WHERE conversationid = :conversationId;")
    void deleteRangesForConversation(@Bind("conversationId") String conversationId);

    @Transaction
    default void updateRanges(Collection<Range> ranges) {
        if(ranges == null || ranges.isEmpty())
            return;

        String conversationId = ranges.iterator().next().conversationId;
        deleteRangesForConversation(conversationId);
        upsertRanges(ranges);
    }

    @GetGeneratedKeys
    @SqlUpdate(// language=SQL
            "INSERT INTO range (id, label, value, conversationId, tokenStart, tokenEnd, charStart, charEnd, props, section) " +
                    " VALUES (encode(digest(:label || :charStart || :conversationId, 'sha256'), 'hex'), :label, :value, :conversationId, :tokenStart, :tokenEnd, :charStart, :charEnd, to_jsonb(:jsonProps), :section)" +
                    "ON CONFLICT (id)\n" +
                    "DO UPDATE SET (label, value, conversationId, tokenStart, tokenEnd, charStart, charEnd, props, section) = (:label, :value, :conversationId, :tokenStart, :tokenEnd, :charStart, :charEnd, to_jsonb(:jsonProps), :section)\n" +
                    "WHERE range.id = encode(digest(:label || :charStart || :conversationId, 'sha256'), 'hex');"
                    )
    long upsertRange(@Bind("label") String label
            , @Bind("value") String value
            , @Bind("conversationId") String conversationId
            , @Bind("tokenStart") int tokenStart
            , @Bind("tokenEnd") int tokenEnd
            , @Bind("charStart") int charStart
            , @Bind("charEnd") int charEnd
            , @Bind("jsonProps") String jsonProps
            , @Bind("section") int section);

    @GetGeneratedKeys
    @SqlUpdate(// language=SQL
            "INSERT INTO range (id, label, value, conversationId, tokenStart, tokenEnd, charStart, charEnd, props) " +
                    " VALUES (encode(digest(:label || :charStart || :conversationId, 'sha256'), 'hex'), :label, :value, :conversationId, :tokenStart, :tokenEnd, :charStart, :charEnd, to_jsonb(:jsonProps))" +
                    "ON CONFLICT (id)\n" +
                    "DO NOTHING;"
    )
    long insertRangeIfNew(@Bind("label") String label
            , @Bind("value") String value
            , @Bind("conversationId") String conversationId
            , @Bind("tokenStart") int tokenStart
            , @Bind("tokenEnd") int tokenEnd
            , @Bind("charStart") int charStart
            , @Bind("charEnd") int charEnd
            , @Bind("jsonProps") String jsonProps);



    default void insertRangesIfNew(Collection<Range> ranges) {
        if(ranges == null || ranges.isEmpty())
            return;

        Handle handle = getHandle();
        PreparedBatch batch = handle.prepareBatch(// language=SQL
                "INSERT INTO range (id, label, value, conversationId, tokenStart, tokenEnd, charStart, charEnd, props, section) " +
                        " VALUES (encode(digest(:label || :charStart || :conversationId, 'sha256'), 'hex'), :label, :value, :conversationId, :tokenStart, :tokenEnd, :charStart, :charEnd, to_jsonb(:jsonProps), :section)" +
                        "ON CONFLICT (id)\n" +
                        "DO NOTHING;");

        try {
            for (Range range : ranges) {
                batch
                        .bind("id", range.getId())
                        .bind("label", range.label)
                        .bind("value", range.value)
                        .bind("tokenStart", range.tokenStart)
                        .bind("tokenEnd", range.tokenEnd)
                        .bind("charStart", range.charStart)
                        .bind("charEnd", range.charEnd)
                        .bind("conversationId", range.conversationId)
                        .bind("jsonProps", RangeMapper.PROPS_MAPPER.writeValueAsString(range.props))
                        .bind("section", range.section)
                        .add();
            }
        } catch (JsonProcessingException e){

        }
        batch.execute();
    }



    ////////////// Conversation //////////////
    @SqlUpdate(// language=SQL
            "DELETE FROM conversation WHERE channel = :channel;")
    void deleteConversationsForChannel(@Bind("channel") String channel);

    @SqlUpdate(// language=SQL
            "DELETE FROM conversation WHERE id = :id;")
    void deleteConversationById(@Bind("id") String id);

    @SqlUpdate(// language=SQL
            "INSERT INTO conversation (id, botId, userId, lang, channel) " +
                    "VALUES (:id, :botId, :userId, :lang, :channel) " +
                    "ON CONFLICT (id)\n" +
                    "DO UPDATE SET (botId, userId, lang, channel) = (:botId, :userId, :lang, :channel)\n" +
                    "WHERE conversation.id = :id;")
    void upsert(@BindBean Conversation conversation);

    @SqlBatch(// language=SQL
            "INSERT INTO conversation (id, botId, userId, lang, channel) " +
                    "VALUES (:id, :botId, :userId, :lang, :channel) " +
                    "ON CONFLICT (id)\n" +
                    "DO UPDATE SET (botId, userId, lang, channel) = (:botId, :userId, :lang, :channel)\n" +
                    "WHERE conversation.id = :id;")
    void upsertConversations(@BindBean Collection<Conversation> conversation);

    @SqlQuery(// language=SQL
            "SELECT * FROM conversation WHERE id = :id;")
    Conversation getConversationWithoutMessages(@Bind("id") String id);


    @RegisterRowMapper(MapMapper.class)
    @SqlQuery(// language=SQL
            "SELECT conversationid, string_agg(label, ', ') AS labels FROM (SELECT DISTINCT conversationid, label FROM range ORDER BY label) AS tmp GROUP BY conversationid;" )
    List<Map<String,Object>> conversationLabels();

    @RegisterRowMapper(MapMapper.class)
    @SqlQuery(// language=SQL
            "SELECT conversation.id,\n" +
            "  conversation.channel,\n" +
            "  conversation.userId,\n" +
            "  conversationtimes.min AS \"from\",\n" +
            "  conversationtimes.max AS \"to\",\n" +
            "  conversationlabels.labels\n" +
            "FROM conversation\n" +
            "  LEFT JOIN conversationtimes ON conversation.id = conversationtimes.conversationid\n" +
            "  LEFT JOIN conversationlabels ON conversation.id = conversationlabels.conversationid\n" +
            "WHERE (channel = :channel OR :channel IS NULL OR :channel = '') AND \n" +
            " (conversationtimes.min BETWEEN CAST(:fromDate AS TIMESTAMPTZ) AND CAST(:toDate AS TIMESTAMPTZ));")
    List<Map<String, Object>> conversationOverview(@Bind("channel") String channel, @Bind("fromDate") DateTime fromDate, @Bind("toDate") DateTime toDate);

    @SqlQuery(// language=SQL
            "SELECT  * FROM message WHERE conversationid = :conversationId ORDER BY timestamp ASC;")
    List<Message> conversationMessages(@Bind("conversationId") String conversationId);

    @SqlQuery(// language=SQL
            "SELECT  * FROM message JOIN conversation ON message.conversationid = conversation.id WHERE channel = :channel ORDER BY timestamp ASC;")
    List<Message> channelMessages(@Bind("channel") String channel);

    @SqlQuery(// language=SQL
            "SELECT  * FROM message JOIN conversation ON message.conversationid = conversation.id WHERE \n" +
            "(channel = :channel OR :channel IS NULL OR :channel = '') AND \n" +
            "(message.timestamp BETWEEN CAST(:fromDate AS TIMESTAMPTZ) AND CAST(:toDate AS TIMESTAMPTZ)) ORDER BY timestamp ASC;")
    List<Message> channelMessages(@Bind("channel") String channel, @Bind("fromDate") DateTime fromDate, @Bind("toDate") DateTime toDate);


    default Conversation getConversation(String id){
        Handle handle = getHandle();

        // language=SQL
        String query = "SELECT \n" +
                "conversation.id c_id, conversation.botId c_botId, conversation.userId c_userId, conversation.lang c_lang, conversation.channel c_channel, \n"+
                "message.id m_id, message.conversationId m_conversationId, message.senderId m_senderId, message.recipientId m_recipientId, message.text m_text, message.timestamp m_timestamp, message.incoming m_incoming\n" +
                "FROM message JOIN conversation ON message.conversationid = conversation.id WHERE \n" +
                "(id = :id) ORDER BY timestamp ASC;";

        Collection<Conversation> conversations = handle.createQuery(query)
                .bind("id", id)
                .reduceResultSet(new LinkedHashMap<>(),
                        this::conversationAccumulator)
                .values();

        return conversations.size() == 1 ? conversations.toArray(new Conversation[0])[0] : null;
    }


    default List<Conversation> conversations(String channel, DateTime fromDate, DateTime toDate){
        Handle handle = getHandle();

        // language=SQL
        String query = "SELECT \n" +
                "conversation.id c_id, conversation.botId c_botId, conversation.userId c_userId, conversation.lang c_lang, conversation.channel c_channel, \n"+
                "message.id m_id, message.conversationId m_conversationId, message.senderId m_senderId, message.recipientId m_recipientId, message.text m_text, message.timestamp m_timestamp, message.incoming m_incoming\n" +
                "FROM message JOIN conversation ON message.conversationid = conversation.id WHERE \n" +
                "(channel = :channel OR :channel IS NULL OR :channel = '') AND \n" +
                "(message.timestamp BETWEEN CAST(:fromDate AS TIMESTAMPTZ) AND CAST(:toDate AS TIMESTAMPTZ)) ORDER BY timestamp ASC;";

        return new ArrayList<>(handle.createQuery(query)
                .bind("channel", channel)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .reduceResultSet(new LinkedHashMap<>(), this::conversationAccumulator)
                .values());
    }


    @SqlQuery(// language=SQL
            "SELECT  * FROM range WHERE conversationid = :conversationId;")
    List<Range> conversationRanges(@Bind("conversationId") String conversationId);


    @SqlQuery(// language=SQL
            "SELECT  * FROM range \n" +
            "  JOIN conversation ON range.conversationid = conversation.id \n" +
            "  JOIN conversationtimes ON range.conversationid = conversationtimes.conversationid \n" +
            "WHERE \n" +
            "(conversation.channel = :channel OR :channel IS NULL OR :channel = '') AND \n" +
            "(conversationtimes.min BETWEEN CAST(:fromDate AS TIMESTAMPTZ) AND CAST(:toDate AS TIMESTAMPTZ)) ORDER BY conversationtimes.min ASC;")
    List<Range> conversationRanges(@Bind("channel") String channel, @Bind("fromDate") DateTime fromDate, @Bind("toDate") DateTime toDate);

    @SqlQuery(// language=SQL
            "SELECT DISTINCT label from range JOIN conversation ON range.conversationid = conversation.id where conversation.channel =  :channel"
    )
    List<String> labels(@Bind("channel") String channel);


/*    @Transaction
    @SqlUpdate(// language=SQL
            "INSERT INTO conversation (id, botId, userId, lang, channel)\n" +
                    "SELECT\n" +
                    "  :newId, botId, userId, lang, channel\n" +
                    "FROM conversation WHERE id = :orgId;\n" +
                    "INSERT INTO message (id, conversationId, senderId, recipientId, text, timestamp, incoming)\n" +
                    "SELECT\n" +
                    "  CAST(uuid_generate_v4() AS VARCHAR), :newId, senderId, recipientId, text, timestamp, incoming\n" +
                    "FROM message WHERE conversationid = :orgId;\n" +
                    "INSERT INTO range (id, label, value, conversationId, tokenStart, tokenEnd, charStart, charEnd, props) \n" +
                    "SELECT\n" +
                    "  NEXTVAL('range_id_seq'), label, value, :newId, tokenStart, tokenEnd, charStart, charEnd, props\n" +
                    "FROM range WHERE conversationId = :orgId;\n")
    void copyConversation(@Bind("orgId") String orgId, @Bind("newId") String newId);*/

    @Transaction
    @SqlUpdate(// language=SQL
            "INSERT INTO conversation (id, botId, userId, lang, channel)\n" +
            "SELECT\n" +
            "  :newId, botId, userId, lang, channel\n" +
            "FROM conversation WHERE id = :orgId;\n" +
            "UPDATE message SET conversationId = :newId  WHERE conversationId = :orgId;\n" +
            "UPDATE range SET (id, conversationId) = (encode(digest(label || charStart || :newId, 'sha256'), 'hex'), :newId) WHERE conversationId = :orgId;\n" +
            "DELETE FROM conversation WHERE id = :orgId;\n"
    )
    void replaceConversation(@Bind("orgId") String orgId, @Bind("newId") String newId);


    @RegisterRowMapper(TimeValueMapper.class)
    @SqlQuery(// language=SQL
            "SELECT date_trunc('day', conversationtimes.min) AS day, count(*)\n" +
                    "FROM conversationtimes\n" +
                    "JOIN conversation ON conversationtimes.conversationid = conversation.id \n" +
                    "WHERE conversation.channel = :channel\n" +
                    "GROUP BY date_trunc('day', conversationtimes.min)\n" +
                    "ORDER BY date_trunc('day', conversationtimes.min) ASC;" )
    List<TimeValue> dailyConversationCounts(@Bind("channel") String channel);


    default LinkedHashMap<String, Conversation> conversationAccumulator(LinkedHashMap<String, Conversation> acc, ResultSet resultSet, StatementContext ctx) throws SQLException {
        String conversationId = resultSet.getString("c_id");
        Conversation conversation;
        if (acc.containsKey(conversationId)) {
            conversation = acc.get(conversationId);
        } else {
            conversation = new Conversation(conversationId)
                    .setUserId(resultSet.getString("c_userId"))
                    .setBotId(resultSet.getString("c_botId"))
                    .setUserId(resultSet.getString("c_userId"))
                    .setChannel(resultSet.getString("c_channel"))
                    .setLang(resultSet.getString("c_lang"));
            acc.put(conversationId, conversation);
        }

        String messageId = resultSet.getString("m_id");
        if (!resultSet.wasNull()) {
            String pgTime = resultSet.getString("m_timestamp").replaceFirst(" ", "T") + ":00";
            Message message = new Message()
                    .setId(messageId)
                    .setText(resultSet.getString("m_text"))
                    .setConversationId(resultSet.getString("m_conversationId"))
                    .setRecipientId(resultSet.getString("m_recipientId"))
                    .setSenderId(resultSet.getString("m_senderId"))
                    .setIncoming(resultSet.getBoolean("m_incoming"))
                    .setTimestamp(ISODateTimeFormat.dateTimeParser().parseDateTime(pgTime));


            conversation.getMessages().add(message);
        }

        return acc;
    }
}
