package org.c4i.chitchat.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.c4i.chitchat.api.chat.ConversationListener;
import org.c4i.nlp.chat.Conversation;
import org.c4i.nlp.chat.Message;
import org.c4i.nlp.match.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log all messages
 * @author Arvid Halma
 */
public class MessageLogger implements ConversationListener {
    private final Logger logger = LoggerFactory.getLogger("message");
    private Config config;
    private ObjectMapper objectMapper;

    public MessageLogger(Config configuration) {
        this.config = configuration;
        this.objectMapper = config.getObjectMapper().copy().disable(SerializationFeature.INDENT_OUTPUT);
    }


    @Override
    public void onReceive(Conversation conversation, Message message) {
        try {
            logger.info(objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSend(Conversation conversation, Result result) {
        try {
            for (Message message : result.getReplies()) {
                logger.info(objectMapper.writeValueAsString(message));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void timout(Conversation conversation) {
        logger.warn("TIMEOUT " + conversation.getId());
    }

    @Override
    public void reset(Conversation conversation) {
        logger.warn("RESET " + conversation.getId());
    }
}
