package org.c4i.chitchat.api.resource;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import org.c4i.chitchat.api.Config;
import org.c4i.chitchat.api.chat.ConversationListener;
import org.c4i.chitchat.api.chat.ConversationManager;
import org.c4i.nlp.chat.Conversation;
import org.c4i.nlp.chat.Message;
import org.c4i.nlp.match.Compiler;
import org.c4i.nlp.match.Eval;
import org.c4i.nlp.match.Result;
import org.c4i.nlp.match.SurveyConverter;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import java.io.IOException;
import java.util.List;

/**
 * Chitchat Script utilities
 * @author Arvid Halma
 */
@Path("/script")
@Api("/script")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class ScriptResource {

    private Config config;

    private final Logger logger = LoggerFactory.getLogger(ScriptResource.class);

    public ScriptResource(Config config) {
        this.config = config;
    }

    @POST
    @Timed
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/survey/yaml")
    public String surveyFromYaml(@FormDataParam("yaml") String yaml) throws IOException {
        return SurveyConverter.convertYaml(yaml);
    }

    @POST
    @Timed
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/survey/csv")
    public String surveyFromCsv(@FormDataParam("csv") String csv, @FormDataParam("style") String style) throws IOException {
        return SurveyConverter.convertCsv(csv, style);
    }

    @POST
    @Timed
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/match")
    public Result match(
            @FormDataParam("script") String script,
            @FormDataParam("text") String text)
    {
        return new Eval(config.getNlp()).find(script, text);
    }


    @POST
    @Path("/normalize")
    @Timed
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public String normalize(@FormDataParam("script") String script)
    {
        try {
            return Compiler.compile(script, config.getNlp()).toString();
        } catch (Exception e) {
            logger.warn("Could not normalize script.", e);
            return script;
        }
    }
}
