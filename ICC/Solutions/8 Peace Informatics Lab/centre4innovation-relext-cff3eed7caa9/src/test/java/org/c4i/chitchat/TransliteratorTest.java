package org.c4i.chitchat;

import org.c4i.nlp.transliterator.ArabicTransliterator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Wouter Eekhout on 14/07/2017.
 */
public class TransliteratorTest {

    @Test
    public void transliteratorTest() {
        String input = "د";

        String output = ArabicTransliterator.transform(input);

        String expected = "d";

        Assert.assertEquals("Unexpected ouput", expected, output);
    }
}
