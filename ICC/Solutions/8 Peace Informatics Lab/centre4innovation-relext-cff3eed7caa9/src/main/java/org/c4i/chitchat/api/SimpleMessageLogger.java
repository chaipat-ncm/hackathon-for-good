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
public class SimpleMessageLogger implements ConversationListener {
    private final Logger logger = LoggerFactory.getLogger("simplemessage");

    public SimpleMessageLogger() {
    }


    @Override
    public void onReceive(Conversation conversation, Message message) {
        logger.info("\t{}\t{}\t{}\t{}", message.getConversationId(), message.getSenderId(), message.getRecipientId(), message.getText());
    }

    @Override
    public void onSend(Conversation conversation, Result result) {
        for (Message message : result.getReplies()) {
            logger.info("\t{}\t{}\t{}\t{}", message.getConversationId(), message.getSenderId(), message.getRecipientId(), message.getText());
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
