package org.c4i.chitchat;

import org.c4i.nlp.normalize.StringNormalizer;
import org.c4i.nlp.normalize.StringNormalizers;
import org.junit.Test;

/**
 * @author Arvid Halma
 * @version 1-10-2017 - 12:27
 */
public class DataSheetDemo {

    @Test
    public void demoDefault(){


        System.out.println("\nDemo default normalizer");
        String[] words = {"hyper-space", "f*cking", "arVId", "Caf\u00e9"};
        StringNormalizer normalizer = StringNormalizers.DEFAULT;
        for (String word : words) {
            System.out.printf("%s \t--> %s\n", word, normalizer.normalize(word));
        }
    }
}
