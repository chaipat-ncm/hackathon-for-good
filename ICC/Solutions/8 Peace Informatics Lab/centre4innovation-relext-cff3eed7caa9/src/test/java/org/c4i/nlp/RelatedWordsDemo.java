package org.c4i.nlp;

import org.c4i.nlp.generalize.RelatedWordsGeneralizer;
import org.c4i.nlp.normalize.StringNormalizer;
import org.c4i.nlp.tokenize.MatchingWordTokenizer;
import org.c4i.nlp.tokenize.Token;
import org.c4i.nlp.tokenize.TokenUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Arvid Halma
 * @version 4-12-17
 */
public class RelatedWordsDemo {
    public static void main(String[] args) throws IOException {
        MatchingWordTokenizer tokenizer = new MatchingWordTokenizer();

        String text = "Hi, I want to hear if you like beer.";
        System.out.println("text = " + text);

        List<Token> tokens = tokenizer.tokenize(text);

        System.out.println("tokens = " + tokens);

        RelatedWordsGeneralizer generalizer = new RelatedWordsGeneralizer(
                new RelatedWords(new File("data/nlp/en/en-synonyms.csv"), StringNormalizer.IDENTITY)
        );

        List<Token> tokens2 = generalizer.extendInline(tokens);

        System.out.println("tokens2 = " + tokens2);

        Collection<Token[]> extend = generalizer.extend(new Token("want"));
        System.out.println("want ~= ");
        for (Token[] t : extend) {
            System.out.println(" - " + TokenUtil.toSentence(t));
        }

    }
}
