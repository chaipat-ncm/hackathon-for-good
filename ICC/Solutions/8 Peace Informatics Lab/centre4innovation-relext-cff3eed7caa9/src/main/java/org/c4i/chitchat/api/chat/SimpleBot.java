package org.c4i.chitchat.api.chat;

import org.c4i.nlp.chat.Conversation;
import org.c4i.nlp.chat.Message;
import org.c4i.nlp.chat.ChatBot;
import org.c4i.nlp.match.Result;
import org.parboiled.common.ImmutableList;

import java.util.List;

/**
 * A demonstration of how to create a simple, hardcoded bot.
 * @author Arvid Halma
 * @version 4-7-17
 */
public class SimpleBot implements ChatBot {

    @Override
    public List<Message> welcome(Conversation conversation) {
        return ImmutableList.of(Message.createReply(conversation, null));
    }

    @Override
    public List<Message> replyMessages(Conversation conversation) {

        Message msg = conversation.lastMessage();
        Message reply = Message.createReply(conversation, null);

        String incomingText = msg.getText().toLowerCase();
        if(incomingText.matches(".*?(hi|hello|yo).*")){
            reply.setText("Hi there! How are you?");
        } else if(incomingText.matches(".*?(good|fine|cool|awesome).*") & !incomingText.contains("no")){
            reply.setText("That's good to hear. I'm cool too!");
        } else if(incomingText.matches(".*?(bad|sucks|aweful|so so).*")){
            reply.setText("Sorry to hear that...");
        } else {
            reply.setText("I don't know what you mean. Tell me about how you feel");
        }

        return ImmutableList.of(reply);
    }


    @Override
    public Result reply(Conversation conversation) {
        return null; // todo: implement
    }
}
