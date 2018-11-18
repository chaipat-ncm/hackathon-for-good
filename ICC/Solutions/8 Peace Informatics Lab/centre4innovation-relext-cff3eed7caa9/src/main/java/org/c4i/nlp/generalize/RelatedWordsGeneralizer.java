package org.c4i.nlp.generalize;

import org.c4i.nlp.RelatedWords;
import org.c4i.nlp.tokenize.Token;

import java.util.Collection;

/**
 * Lookup related words from a dictionary
 * @author Arvid
 * @version 14-5-2015 - 21:18
 */
public class RelatedWordsGeneralizer implements Generalizer {

    RelatedWords relatedWords;

    public RelatedWordsGeneralizer(RelatedWords relatedWords) {
        this.relatedWords = relatedWords;
    }

    @Override
    public Collection<Token[]> extend(Token ... tokens) {
        return relatedWords.get(tokens);
    }

}
