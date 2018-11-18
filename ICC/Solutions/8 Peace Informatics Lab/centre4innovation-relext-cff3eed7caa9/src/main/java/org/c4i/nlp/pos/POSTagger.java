package org.c4i.nlp.pos;

import org.c4i.nlp.tokenize.Token;
import org.c4i.nlp.tokenize.TokenUtil;

import java.util.List;

/**
 * Part of Speech tagger inteface
 * @author Arvid Halma
 * @version 15-7-2017 - 15:07
 */
public interface POSTagger {

    POSTagger DEFAULT = tokens -> {
        tokens.forEach(t -> {t.setTag("NN"); t.setWeight(1);});
        return tokens;
    };

    List<Token> tag(List<Token> tokens);

    default List<String> tagStrings(List<String> tokens){
        return TokenUtil.toTagList(tag(TokenUtil.toTokenList(tokens)));
    }

    default String[] tagStrings(String[] tokens){
        return TokenUtil.toTagArray(tag(TokenUtil.toTokenList(tokens)));
    }


}
