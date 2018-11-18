package org.c4i.nlp.generalize;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.c4i.nlp.tokenize.Token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Extend a collection of words with a complementary set of words.
 * @author Arvid
 * @version 14-5-2015 - 21:17
 */
public interface Generalizer {

    Generalizer DEFAULT = word -> Collections.emptySet();

    /*default Collection<Token[]> extend(Token token){
        return extend(new Token[]{token});
    }*/

    Collection<Token[]> extend(Token ... tokens);


    default Token[] extendInline(Token[] tokens){
        List<Token> result = new ArrayList<>();
        for (Token token : tokens) {
            result.add(token);
            Collection<Token[]> extension = extend(token);
            extension.forEach(words -> Collections.addAll(result, words));
        }
        return result.toArray(new Token[result.size()]);
    }

    default List<Token> extendInline(List<Token> tokens){
        List<Token> result = new ArrayList<>();
        for (Token token : tokens) {
            result.add(token);
            Collection<Token[]> extension = extend(token);
            extension.forEach(words -> Collections.addAll(result, words));
        }
        return result;
    }


}
