package org.c4i.chitchat.api.chat;

import org.c4i.nlp.chat.Conversation;
import org.c4i.nlp.chat.Message;
import org.c4i.nlp.chat.ChatBot;

/**
 * Feeds a given chatbot with a message every now and then.
 * @author Arvid Halma
 * @version 4-7-17
 */
public class RandomChatter {

    ChatBot chatBot;

    public RandomChatter(ChatBot chatBot) {
        this.chatBot = chatBot;

        Conversation conversation = new Conversation();
        conversation.setBotId("RandomChatter");
        conversation.setUserId("you");
        conversation.setLang("en");
        new Thread(() -> {
            try {
                long x = (long) (1000 * Math.random());
                Thread.sleep(x);
                Message msg = new Message();
                msg.setText("Hi "+ x);
                msg.setSenderId("RandomChatter");
                msg.setConversationId(conversation.getId());
                msg.setRecipientId("you");
                msg.setIncoming(false);
                conversation.getMessages().add(msg);
                chatBot.replyMessages(conversation);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


}
