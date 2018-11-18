package org.c4i.chitchat;

import org.apache.commons.lang3.time.StopWatch;
import org.c4i.nlp.match.*;
import org.c4i.nlp.match.Compiler;
import org.c4i.nlp.ner.DateTimeFinder;
import org.c4i.nlp.ner.EmoFinder;
import org.c4i.nlp.ner.DataSheet;
import org.c4i.nlp.normalize.StringNormalizer;
import org.c4i.nlp.normalize.StringNormalizers;
import org.c4i.nlp.tokenize.MatchingWordTokenizer;
import org.c4i.nlp.tokenize.Token;
import org.c4i.nlp.tokenize.Tokenizer;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test match results.
 * @author Arvid Halma
 * @version 25-11-2015 - 11:52
 */
public class MatchTest {
    private Tokenizer tokenizer = new MatchingWordTokenizer();
//    private Tokenizer tokenizer = new SplittingWordTokenizer();
    private StringNormalizer normalizer = StringNormalizers.DEFAULT_STEMMED;
    private DataSheet dictionary = new DataSheet("CITYSD", new File("data/nlp/en/datasheet/CITYSD.csv"), normalizer, tokenizer);
    private DateTimeFinder dateTimeFinder = new DateTimeFinder(new File("data/nlp/en/DATETIME.csv"), normalizer, tokenizer);
    private EmoFinder emoFinder = new EmoFinder();
    private final static int N = 1_000;

    @org.junit.Rule
    public final ExpectedException exception = ExpectedException.none();

    public MatchTest() throws IOException {
    }


    @Test
    public void match1(){
        match(true, "Come to The Hague to join the Hub!", "to");
    }

    @Test
    public void match2(){
        match(false, "Come to The Hague to join the Hub!", "arvid");
    }

    @Test
    public void matchAnd1(){
        match(true, "Come to The Hague to join the Hub!", "join & hub");
    }

    @Test
    public void matchAnd2(){
        match(false, "Come to The Hague to join the Hub!", "join & foo");
    }

    @Test
    public void matchOr1(){
        match(true, "Come to The Hague to join the Hub!", "foo | hub");
    }

    @Test
    public void matchOr2(){
        match(false, "Come to The Hague to join the Hub!", "foo | bar");
    }

    @Test
    public void matchNot1(){
        match(true, "apple", "-orange");
    }

    @Test
    public void matchNot2(){
        match(false, "apple", "-apple");
    }

    @Test
    public void matchNot2b(){
        match(false, "apple", "-(apple)");
    }

    @Test
    public void matchNot2c(){
        match(true, "big apple", "-big_orange");
    }

    @Test
    public void matchNot2d(){
        match(false, "big apple", "-(big_apple)");
    }

    @Test
    public void matchNot2f(){
        match(false, "big apple", "-big_apple");
    }

    @Test
    public void matchNot3(){
        match(true, "Come to The Hague to join the Hub!", "-foo");
    }

    @Test
    public void matchNot4(){
        match(false, "Come to The Hague to join the Hub!", "-hague");
    }

    @Test
    public void matchNot5(){
        match(true, "Come to The Hague to join the Hub!", "-(foo|bar)");
    }

    @Test
    public void matchNot6(){
        match(false, "Come to The Hague to join the Hub!", "hague & -hub");
    }

    @Test
    public void matchConcat1(){
        match(true, "Come to The Hague to join the Hub!", "the_hague");
    }

    @Test
    public void matchConcat2(){
        match(false, "Come to The Hague to join the Hub!", "the_foo");
    }

    @Test
    public void matchConcatAlt1(){
        match(true, "Come to The Hague to join the Hub!", "'the hague'");
    }

    @Test
    public void matchConcatAlt2(){
        match(false, "Come to The Hague to join the Hub!", "'the foo'");
    }

    @Test
    public void matchExact1(){
        match(true, "Come to The Hague to join the Hub!", "\"Hague\"");
    }

    @Test
    public void matchExact2(){
        match(false, "Come to The Hague to join the Hub!", "\"hague\"");
    }

    @Test
    public void matchExactConcat1(){
        match(true, "Come to The Hague to join the Hub!", "\"The\"_\"Hague\"");
    }

    @Test
    public void matchExactConcat2(){
        match(false, "Come to The Hague to join the Hub!", "\"The\"_\"hague\"");
    }

    @Test
    public void matchExactConcat3(){
        match(true, "Come to The Hague to join the Hub!", "\"The\"_\"hague\" | join_the");
    }

    @Test
    public void matchExactWilcards1(){
        match(true, "Come to The Hague to join the Hub!", "JOIN_?_Hub");
    }

    @Test
    public void matchExactWilcards2(){
        match(true, "Come to The Hague to join the Hub!", "to_?_the");
    }

    @Test
    public void matchExactWilcards3(){
        match(true, "Come to The Hague to join the Hub!", "to_*_the");
    }

    @Test
    public void matchExactWilcards4(){
        match(true, "Come to The Hague to join the Hub!", "to_+_the");
    }

    @Test
    public void matchExactWilcards5(){
        match(false, "Come to The Hague to join the Hub!", "come_?_to");
    }

    @Test
    public void matchExactWilcards6(){
        match(true, "Come to The Hague to join the Hub!", "join_*_the");
    }

    @Test
    public void matchExactWilcards7(){
        match(false, "Come to The Hague to join the Hub!", "join_+_the");
    }

    @Test
    public void matchExactWilcards8(){
        match(true, "hi there", "hi_*_there");
    }

    @Test
    public void matchExactUTF8(){
        match(true, "café", "café");
    }

    @Test
    public void matchCombi1(){
        match(true, "Come to The Hague to join the Hub!", "Hello & world OR -(join_the_hub) OR ?_Hague");
    }

    @Test
    public void matchNer1(){
        match(true, "Come to Khartoum to join the Hub!", "CITYSD");
    }

    @Test
    public void matchNer2(){
        match(true, "Come to Khartoum North to join the Hub!", "CITYSD");
    }

    @Test
    public void matchArab1(){
        match(true, "تعال إلى لاهاي للانضمام إلى المحور!", "Hello & world OR لاهاي");
    }

    @Test
    public void matchNerCombi1(){
        match(true, "in Khartum.", "in_CITYSD");
    }

    @Test
    public void matchNerCombi2(){
        match(true, "in Al Hilāliyya.", "in_CITYSD");
    }

    @Test
    public void matchNerCombi3(){
        match(true, "Khartum party!", "CITYSD_party");
    }

    @Test
    public void matchNerCombi4(){
        match(true, "Al Hilāliyya party!", "CITYSD_party");
    }

    @Test
    public void matchDatetime1(){
        match(true, "yesterday", "DATETIME");
    }

    @Test
    public void matchSrcCombi1(){
        matchRuleDef(true, "Come to The Hague to join the Hub!", "@foo <- Hello & world OR -(join_the_hub) OR ?_Hague");
    }

    @Test
    public void matchSrcEmo1(){
        matchRuleDef(true, "I am :)!", "@foo  <- smiley");
    }

    @Test
    public void matchNegSeq2(){
        try {
            match(false, "The phrase fruit apple of my eye refers to something or someone that one cherishes above all others.", "apple_(NOT of_my_eye)");
            fail("no Compiler.ParseError thrown: sequences should only contain (negated) literals");
        } catch (Compiler.ParseError e) {
            // ok
        }
    }

    @Test
    public void matchNegSeq3(){
        try{
            match(false, "The phrase fruit apple of my eye refers to something or someone that one cherishes above all others.", "apple_-(of_my_eye)");
            fail("no Compiler.ParseError thrown: sequences should only contain (negated) literals");
        } catch (Compiler.ParseError e) {
            // ok
        }
    }

    @Test
    public void matchNegSeq5(){
        match(false, "The phrase fruit apple of my eye refers to something or someone that one cherishes above all others.", "-fruit_apple");
    }

    @Test
    public void matchStart1(){
        match(true, "Hello people", "TEXTSTART_hello");
    }

    @Test
    public void matchStart2(){
        match(false, "Hello people", "TEXTSTART_people");
    }

    @Test
    public void matchStart3(){
        match(false, "Hello people", "-TEXTSTART_hello");
    }

    @Test
    public void matchStart4(){
        match(true, "Hello people", "TEXTSTART_hello_people");
    }

    @Test
    public void matchEnd1(){
        match(true, "Hello people", "people_TEXTEND");
    }

    @Test
    public void matchEnd2(){
        match(false, "Hello people", "hello_TEXTEND");
    }

    @Test
    public void matchEnd3(){
        match(false, "Hello people", "-people_TEXTEND");
    }

    @Test
    public void matchStartEnd1(){
        match(false, "Hello people", "TEXTSTART_hello_TEXTEND");
    }

    @Test
    public void matchStartEnd2(){
        match(true, "Hello people", "TEXTSTART_hello_people_TEXTEND");
    }

    @Test
    public void matchStartEnd3(){
        match(true, "reset", "TEXTSTART_reset_TEXTEND");
    }

    @Test
    public void ruleSetToString(){
        String src = "---\nlanguages : [en]\n---\n@food <- (-boat | apple) & foo\n@food -> Mmmmm!!";
        Script ruleSet = Compiler.compile(src);
        String src2 = ruleSet.toString();
        System.out.println("ruleSet <- \n" + src2);
        Script ruleSet2 = Compiler.compile(src2);
        assertEquals(src2, ruleSet2.toString());
    }

    private void match(boolean expected, String text, String pattern){
        System.out.printf("The text \"%s\" is expected to%s match (%s)\n", text, (expected ? "" : " NOT"), pattern );

        List<Token> textTokens = tokenizer.tokenize(text);
        Token[] tokens = textTokens.toArray(new Token[textTokens.size()]);
        normalizer.normalizeTokens(tokens);
        System.out.println(" - text tokens <- " + Arrays.toString(tokens));

        Literal[][] cnf = Compiler.compileBody(pattern, true, normalizer);
        System.out.println(" - cnf <- " + CNFTransform.toString(cnf));
        System.out.println();
        List<Range> ranges = null;

        Script context = new Script();
        context.getPlugins().add(dictionary);
        context.getPlugins().add(dateTimeFinder);
        context.getPlugins().add(emoFinder);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final Eval eval = new Eval(null);
        for (int i = 0; i < N; i++) {
            ranges = eval.findRule(tokens, new LabelRule(null, cnf), context, null);
        }
        stopWatch.stop();
        System.out.println(" - find <- " + ranges);
        System.out.println(" - highlight <- " + (ranges == null ? text : Eval.highlight(text, ranges)));
        System.out.println(" - stopWatch: " + stopWatch);
        System.out.println(" - speed (evals/s): " + (int)((double)N/ (stopWatch.getTime()/1000.0)));

        assertEquals(expected, (ranges != null && !ranges.isEmpty()));
    }

    private void matchRuleDef(boolean expected, String text, String ruleSrc){
        System.out.printf("The text \"%s\" is expected to%s match (%s)\n", text, (expected ? "" : " NOT"), ruleSrc);

//        text = EmojiAlias.matchCodify(text);

        List<Token> textTokens = tokenizer.tokenize(text);
        Token[] tokens = textTokens.toArray(new Token[textTokens.size()]);
        normalizer.normalizeTokens(tokens);
        System.out.println(" - text tokens <- " + Arrays.toString(tokens));

        Script script = Compiler.compile(ruleSrc);
        script.getPlugins().add(dictionary);
        script.getPlugins().add(dateTimeFinder);
        script.getPlugins().add(emoFinder);

        System.out.println(" - rules <- " + script);
        System.out.println();
        Result ranges = null;

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final Eval eval = new Eval(null);
        for (int i = 0; i < N; i++) {
            ranges = eval.find(script, text);
        }
        stopWatch.stop();
        System.out.println(" - find <- " + ranges);
        System.out.println(" - highlight <- " + (ranges == null ? text : Eval.highlight(text, ranges.getRanges())));
        System.out.println(" - stopWatch: " + stopWatch);
        System.out.println(" - speed (evals/s): " + (int)((double)N/ (stopWatch.getTime()/1000.0)));

        assertEquals(expected, ranges != null);
    }
}
