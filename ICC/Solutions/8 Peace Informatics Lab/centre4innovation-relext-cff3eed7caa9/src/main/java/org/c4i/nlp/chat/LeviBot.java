package org.c4i.nlp.chat;

import org.c4i.nlp.match.Result;
import org.c4i.nlp.normalize.StringNormalizer;
import org.c4i.nlp.tokenize.MatchingWordTokenizer;
import org.c4i.nlp.tokenize.Token;
import org.c4i.nlp.tokenize.TokenUtil;
import org.c4i.nlp.tokenize.Tokenizer;
import org.c4i.util.StringUtil;
import org.parboiled.common.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple bot that can be trained with example replies.
 * One way to look at it, is that it looks up its nearest neighbor, given the Levenshtein distance to the input message.
 */
public class LeviBot implements ChatBot{
    private Map<String, List<String>> inputToReplies;
    private List<String> fallbackMessages;

    public LeviBot(Map<String, List<String>> inputToReplies) {
        this.inputToReplies = inputToReplies;
        this.fallbackMessages = ImmutableList.of("What do you mean?");
    }

    public LeviBot(Map<String, List<String>> inputToReplies, List<String> fallbackMessages) {
        this.inputToReplies = inputToReplies;
        this.fallbackMessages = fallbackMessages;
    }

    public LeviBot(Map<String, List<String>> inputToReplies, List<String> fallbackMessages, StringNormalizer normalizer) {
        this.inputToReplies = new HashMap<>();
        Tokenizer tokenizer = new MatchingWordTokenizer();

        for (Map.Entry<String, List<String>> entry : inputToReplies.entrySet()) {
            List<Token> tokens = tokenizer.tokenize(entry.getKey());
            normalizer.normalizeTokens(tokens);
            this.inputToReplies.put(TokenUtil.toSentence(tokens), entry.getValue());
        }
        this.fallbackMessages = fallbackMessages;
    }

    @Override
    public List<Message> welcome(Conversation conversation) {
        return null;
    }


    @Override
    public Result reply(Conversation conversation){
        Message msgIn = conversation.lastMessage();

        double maxSim = 0;
        List<String> maxReplies = null;
        for (Map.Entry<String, List<String>> entry : inputToReplies.entrySet()) {
            double d = StringUtil.levenshteinMatchScore(msgIn.getText(), entry.getKey());
            if(d < maxSim){
                maxReplies = entry.getValue();
                maxSim = d;
            }
        }

        if(maxSim == 0) {
            maxReplies = fallbackMessages;
        }

        Message reply = Message.createReply(conversation, maxReplies.get((int)(maxReplies.size() * Math.random())));
        return new Result().setReplies(ImmutableList.of(reply));
    }
}
