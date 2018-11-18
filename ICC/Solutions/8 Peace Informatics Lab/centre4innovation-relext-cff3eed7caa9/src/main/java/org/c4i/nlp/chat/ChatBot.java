package org.c4i.nlp.chat;

import org.c4i.nlp.match.Result;
import org.parboiled.common.ImmutableList;

import java.util.List;

/**
 * A ChatBot interface that, given a previous conversation (list of messages), defines a reply (list of messages).
 * @author Arvid Halma
 * @version 4-7-17
 */
public interface ChatBot {

    default List<Message> welcome(Conversation conversation){
        return ImmutableList.of();
    }

    default List<Message> replyMessages(Conversation conversation){
        return reply(conversation).getReplies();
    }

    Result reply(Conversation conversation);
}
