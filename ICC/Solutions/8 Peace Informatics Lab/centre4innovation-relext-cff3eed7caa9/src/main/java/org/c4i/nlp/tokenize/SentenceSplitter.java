package org.c4i.nlp.tokenize;

/**
 * @author Arvid Halma
 * @version 9-6-2017 - 20:40
 */
public interface SentenceSplitter {

    String[] split(String text);

    default String description(){
        return "undefined";
    }
}
