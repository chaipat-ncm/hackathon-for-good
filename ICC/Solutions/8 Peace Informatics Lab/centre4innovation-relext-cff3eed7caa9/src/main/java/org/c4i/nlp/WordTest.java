package org.c4i.nlp;


import org.c4i.nlp.tokenize.Token;

import java.util.function.Predicate;

/**
 * Test if a string is a valid word.
 * Created by arvid on 16-7-15.
 */
public interface WordTest extends Predicate<String>{
    WordTest DEFAULT = s -> true;

    boolean test(String string);

    default boolean test(Token token){
        return test(token.getWord());
    }

    static WordTest fromPredicate(Predicate<String> predicate){
        return predicate::test;
    }
}
