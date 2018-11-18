package org.c4i.chitchat;

import org.c4i.nlp.match.Eval;
import org.c4i.nlp.match.Range;
import org.c4i.nlp.ner.DataSheet;
import org.c4i.nlp.normalize.StringNormalizer;
import org.c4i.nlp.normalize.StringNormalizers;
import org.c4i.nlp.tokenize.SplittingWordTokenizer;
import org.c4i.nlp.tokenize.Token;
import org.c4i.nlp.tokenize.Tokenizer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Arvid Halma
 * @version 1-8-17
 */
public class DataSheetTest {
    static StringNormalizer normalizer;
    static Tokenizer tokenizer;
    static DataSheet checker;

    @BeforeClass
    public static void before() throws IOException {
        normalizer = StringNormalizers.DEFAULT;
        tokenizer = new SplittingWordTokenizer();
        checker = new DataSheet("city", new File("data/nlp/en/datasheet/CITYSD.csv"), normalizer, tokenizer);
    }

    @Test
    public void testFalse1(){
        assertFalse(test("I'm living in The Hague"));
    }

    @Test
    public void testTrue1(){
        assertTrue(test("Going out in Karêmah is cool"));
    }

    @Test
    public void testTrue2(){
        assertTrue(test("I'm living in Al Hasaheisa"));
    }

    @Test
    public void testTrue3(){
        assertTrue(test("Going out in الضعین is cool"));
    }


    private boolean test(String text){
        System.out.println("text = " + text);
        Collection<Token> tokens = normalizer.normalizeTokens(tokenizer.tokenize(text));
        List<Range> ranges = checker.find(new ArrayList<>(tokens));
        System.out.println("match = " + Eval.highlight(text, ranges));
        System.out.println("match = " + ranges);
        return !ranges.isEmpty();
    }




}
