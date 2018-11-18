package org.c4i.chitchat.api.resource;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.c4i.chitchat.api.Config;
import org.c4i.chitchat.api.chat.ConversationListener;
import org.c4i.chitchat.api.chat.ConversationManager;
import org.c4i.nlp.chat.Conversation;
import org.c4i.nlp.chat.Message;
import org.c4i.nlp.match.*;
import org.c4i.nlp.match.Compiler;
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
 * A Chatbot for local development purposes.
 * @author Arvid Halma, Wouter Eekhout
 */
@Path("/channel/devbot")
@Api("/channel/devbot")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class DevBotResource {
    private static final String BOT_ID = "devbot";

    private Config config;
    private ConversationManager chairman;
    private List<ConversationListener> listeners;


    private final Logger logger = LoggerFactory.getLogger(DevBotResource.class);

    public DevBotResource(Config config, ConversationManager chairman, List<ConversationListener> listeners) {
        this.config = config;
        this.chairman = chairman;
        this.listeners = listeners;
    }

    @POST
    @Timed
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/reply")
    @ApiOperation(
            value = "Generate a reply, given the last message from the user.",
            notes = "Internally, previous messages are kept, so it can be used by a bot for context.",
            response = Result.class)
    public Result reply(
            @Context Request request,
            @FormDataParam("script") String script,
            @FormDataParam("msg") String msg) throws IOException {

        Message message = this.config.getObjectMapper().readValue(msg, Message.class);
        message.setIncoming(true);
        message.setChannel(BOT_ID);
        Conversation conversation = chairman.update(message);

        listeners.forEach(cl -> cl.onReceive(conversation, message));

        Result result = new Eval(config.getNlp()).reply(Compiler.compile(script, config.getNlp()), conversation);

        if(result.getMatches().containsKey("RESET")){
            // a reset label was triggered
            chairman.reset(message.getSenderId(), BOT_ID);
            listeners.forEach(cl -> cl.reset(conversation));
        }

        // update conversation
        result.getReplies().forEach(reply -> chairman.update(reply));
        listeners.forEach(cl -> cl.onSend(conversation, result));

        return result;
    }


    @POST
    @Timed
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/reset")
    @ApiOperation(
            value = "Reset a conversation state for for a specific user.",
            notes = "All previous messages in this conversation will be ignored.",
            response = Boolean.class)
    public void reset(String senderId) {
        Conversation conversation = chairman.getConversation(senderId, BOT_ID);
        listeners.forEach(cl -> cl.reset(conversation));
        chairman.reset(senderId, BOT_ID);
    }

    @POST
    @Timed
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/match")
    @ApiOperation(
            value = "Find all matching patterns in the text.",
            notes = "The label rules in the script define the patterns.",
            response = Result.class)
    public Result match(
            @FormDataParam("script") String script,
            @FormDataParam("text") String text)
    {
        return new Eval(config.getNlp()).find(script, text);
    }


    @DELETE
    @Path("/conversation/reset")
    @ApiOperation(
            value = "Reset all conversation states for this channel",
            notes = "All previous messages in conversations will be ignored.",
            response = Boolean.class)
    public void resetConversations() throws IOException {
        chairman.reset();
    }

}
