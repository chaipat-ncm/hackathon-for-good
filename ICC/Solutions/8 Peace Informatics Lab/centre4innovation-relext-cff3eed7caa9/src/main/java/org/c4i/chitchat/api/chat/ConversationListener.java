package org.c4i.chitchat.api.chat;

import org.c4i.nlp.chat.Conversation;
import org.c4i.nlp.chat.Message;
import org.c4i.nlp.match.Result;

import java.io.IOException;

/**
 * Listen to conversation/message updates
 * @author Arvid Halma
 * @version 28-3-18
 */
public interface ConversationListener {

    void onReceive(Conversation conversation, Message message);

    void onSend(Conversation conversation, Result result);

    void timout(Conversation conversation);

    void reset(Conversation conversation);



}
