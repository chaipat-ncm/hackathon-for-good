package org.c4i.nlp.match;

import org.c4i.nlp.tokenize.Token;

import java.util.List;

/**
 * Special case token matchers.
 * For example for named entity recognition or regex implementations.
 * @author Arvid Halma
 * @version 4-8-2017 - 10:09
 */
public interface EntityPlugin {

    boolean accept(Literal lit);

    /**
     * Try to find classes/labels in the text, given a query/literal
     * @param text the array of tokens in the text
     * @param lit the query
     * @param label the class/label to assign to a matched range
     * @param location offset at which the match should occur
     * @return a list of matches
     */
    List<Range> find(Token[] text, Literal lit, String label, int location);

    default String description(){
        return "undefined";
    }
}
