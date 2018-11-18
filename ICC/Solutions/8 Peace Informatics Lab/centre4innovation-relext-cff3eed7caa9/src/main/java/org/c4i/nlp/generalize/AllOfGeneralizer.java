package org.c4i.nlp.generalize;

import org.c4i.nlp.tokenize.Token;

import java.util.Collection;
import java.util.HashSet;

/**
 * Generalize a list of words given as a composition of other generalizations.
 * Works as a non-short-circuiting AND.
 * @author Arvid
 * @version 14-5-2015 - 21:18
 */
public class AllOfGeneralizer implements Generalizer {
    private Generalizer[] generalizers;

    public AllOfGeneralizer(Generalizer... generalizers) {
        this.generalizers = generalizers;
    }

    @Override
    public Collection<Token[]> extend(Token ... token) {
        Collection<Token[]> result = new HashSet<>();
        for (Generalizer generalization : generalizers) {
            result.addAll(generalization.extend(token));
        }
        return result;
    }


}
