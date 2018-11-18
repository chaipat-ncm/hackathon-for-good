package org.c4i.chitchat.api.resource;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.c4i.chitchat.api.Config;
import org.c4i.chitchat.api.chat.ConversationListener;
import org.c4i.chitchat.api.chat.ConversationManager;
import org.c4i.chitchat.api.model.TextDoc;
import org.c4i.nlp.chat.Conversation;
import org.c4i.nlp.chat.Message;
import org.c4i.nlp.chat.ScriptBot;
import org.c4i.nlp.match.Compiler;
import org.c4i.nlp.match.Eval;
import org.c4i.nlp.match.Result;
import org.c4i.nlp.match.Script;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import java.io.IOException;
import java.util.List;

/**
 * Chitchat bot connector.
 * @author Arvid Halma
 */
@Path("/channel/chitchat")
@Api("/channel/chitchat")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class ChitChatResource {
    private Config config;

    private final Logger logger = LoggerFactory.getLogger(ChitChatResource.class);

    private ScriptBot chatBot;
    private ConversationManager chairman;
    private List<ConversationListener> listeners;

    public ChitChatResource(Config config, ConversationManager chairman, List<ConversationListener> listeners) {
        this.config = config;
        this.chairman = chairman;
        this.listeners = listeners;
    }


    @POST
//    @RolesAllowed("ADMIN")
    @Path("/script/reload")
    @ApiOperation(
            value = "Load the latest 'ChitChat live script' from the database and use it as the current chatbot",
            response = Boolean.class)
    public boolean loadLiveScript(){
        TextDoc src = config.dao.textDocDao.getLastUpdatedByName("ccs", "ChitChat live script");
        if(src != null) {
            Script script = Compiler.compile(src.getBody(), config.getNlp());
            this.chatBot = new ScriptBot(script, config.getNlp());
            return true;
        }
        return false;
    }

    @DELETE
    @Path("/conversation/reset")
    @ApiOperation(
            value = "Reset all conversation states for this channel",
            notes = "All previous messages in conversations will be ignored.",
            response = Boolean.class)
    public void resetConversations() {
        chairman.reset();
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
            @FormDataParam("msg") String msg) throws IOException {

        Message message = this.config.getObjectMapper().readValue(msg, Message.class);
        message.setIncoming(true);
        message.setChannel("chitchat");
        message.setTimestamp(DateTime.now()); // override client timestamp
        Conversation conversation = chairman.update(message);

        listeners.forEach(cl -> cl.onReceive(conversation, message));

        Result result = chatBot.reply(conversation);

        if(result.getMatches().containsKey("RESET")){
            // a reset label was triggered
            chairman.reset(message.getSenderId(), "chitchat");
            listeners.forEach(cl -> cl.reset(conversation));
        }

        // update conversation
        result.getReplies().forEach(reply -> chairman.update(reply));
        listeners.forEach(cl -> cl.onSend(conversation, result));

        return result;
    }


}