package org.c4i.nlp.generalize;

import edu.mit.jwi.IDictionary;
import org.c4i.nlp.WordNetUtils;
import org.c4i.nlp.tokenize.Token;

import java.util.Collection;
import java.util.HashSet;

/**
 * Extend a list of words with its hypernyms.
 * @author Arvid
 * @version 14-5-2015 - 21:18
 */
public class HypernymsGeneralization implements Generalizer {

    IDictionary dict;

    public HypernymsGeneralization(IDictionary dict) {
        this.dict = dict;
    }

    @Override
    public Collection<Token[]> extend(Token ... token) {
        HashSet<Token[]> extension = new HashSet<>();

        for (String hypernym : WordNetUtils.getHypernyms(dict, token[0].getWord())) {
            hypernym = hypernym.replace("_", " ");
            extension.add(new Token[]{new Token(hypernym, token[0].getTag(), "WNHyper+", token[0].getWeight())});
        }

        return extension;
    }

}
