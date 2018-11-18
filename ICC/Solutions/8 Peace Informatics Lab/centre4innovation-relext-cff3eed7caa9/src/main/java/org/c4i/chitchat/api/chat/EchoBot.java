package org.c4i.chitchat.api.chat;

import org.c4i.nlp.chat.ChatBot;
import org.c4i.nlp.chat.Conversation;
import org.c4i.nlp.chat.Message;
import org.c4i.nlp.match.Result;
import org.parboiled.common.ImmutableList;

import java.util.List;

/**
 * A demonstration of how to create a simple, bot that copies a user's message.
 * @author Arvid Halma
 * @version 4-7-17
 */
public class EchoBot implements ChatBot {


    @Override
    public List<Message> welcome(Conversation conversation) {
        return ImmutableList.of();
    }

    @Override
    public Result reply(Conversation conversation) {
        Message msg = conversation.lastMessage();
        return new Result().setReplies(ImmutableList.of(Message.createReply(conversation, msg.getText())));
    }
}
