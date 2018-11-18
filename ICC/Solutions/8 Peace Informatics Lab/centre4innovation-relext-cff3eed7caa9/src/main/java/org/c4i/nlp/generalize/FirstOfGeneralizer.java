package org.c4i.nlp.generalize;

import org.c4i.nlp.tokenize.Token;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Generalize a list of words given other generalizations (tried in order).
 * Works as a fallback mechanism, a.k.a. short-circuiting OR.
 * @author Arvid
 * @version 14-5-2015 - 21:18
 */
public class FirstOfGeneralizer implements Generalizer {
    private List<Generalizer> generalizations;

    public FirstOfGeneralizer(Generalizer... generalizations) {
        this.generalizations = new ArrayList<>();
        this.generalizations.addAll(Arrays.asList(generalizations));
    }

    public FirstOfGeneralizer(List<Generalizer> generalizations) {
        this.generalizations = generalizations;
    }

    public FirstOfGeneralizer add(Generalizer generalizer){
        generalizations.add(generalizer);
        return this;
    }

    public Collection<Token[]> extend(Token ... token) {
        for (Generalizer generalization : generalizations) {
            Collection<Token[]> extension = generalization.extend(token);
            if(!extension.isEmpty()){
                return extension;
            }
        }
        return Collections.emptySet();
    }


}
