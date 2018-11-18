package org.c4i.chitchat;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.time.StopWatch;
import org.c4i.chitchat.api.model.LanguageProcessingConfig;
import org.c4i.nlp.Nlp;
import org.c4i.nlp.match.Compiler;
import org.c4i.nlp.match.Eval;
import org.c4i.nlp.match.Range;
import org.c4i.nlp.match.Script;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertTrue;

/**
 * @author Arvid Halma
 * @version 14-4-2017 - 20:29
 */
public class ScriptTest {

//    private Tokenizer tokenizer = new SplittingWordTokenizer();
//    private StringNormalizer normalizer = StringNormalizers.DEFAULT_STEMMED;
    private final static int N = 1_00_000;
    static Nlp nlp;

    @BeforeClass
    public static void before() throws IOException {
        nlp = new Nlp(new File("data/nlp"), ImmutableMap.of("en", new LanguageProcessingConfig()));
        nlp.loadModels();
    }

    @Test
    public void matchSimple1(){
        List<Range> eval = new Eval(nlp).find("@fruit <- apple | pear", "I like apple juice").getRanges();

        for (Range range : eval) {
            System.out.println(range);
        }

        assertTrue(new Eval(nlp).contains(eval, "fruit") && eval.size() == 1);
    }

    @Test
    public void matchSimple2(){
        List<Range> eval = new Eval(nlp).find("@fruit <- apple | pear", "I like cocktails").getRanges();

        for (Range range : eval) {
            System.out.println(range);
        }

        assertTrue(eval.isEmpty());
    }

    @Test
    public void matchSimple3(){
        List<Range> eval = new Eval(nlp).find("@fruit <- apple | pear\n" +
                "@drink <- milk | beer | cocktail", "Me like cocktail").getRanges();

        for (Range range : eval) {
            System.out.println(range);
        }

        assertTrue(new Eval(nlp).contains(eval, "drink") && eval.size() == 1);
    }

    @Test
    public void matchWithComments1(){
        Script ruleSet = Compiler.compile("@a <- a #cc\n" +
                "@a -> aa\n" +
                "# c\n" +
                "-@a -> Aaa!\n" +
                "# c\n" +
                "-@a -> Aaa!\n" );
        System.out.println("@ruleSet <- ...\n" + ruleSet);
        List<Range> eval = new Eval(nlp).find(ruleSet, "The monkey eats a pear").getRanges();

        System.out.println(ruleSet);
        for (Range range : eval) {
            System.out.println(range);
        }

        assertTrue(new Eval(nlp).contains(eval, "a"));
    }

    @Test
    public void matchWithComments2(){
        Script ruleSet = Compiler.compile("@vehicle <- car | bike | train  #won't match in this example\n" +
                "@fruit <- pear | (apple & -of_my_eye) | orange\n" +
                "\n" +
                "# Multiple answers, randomly picked\n" +
                "-@vehicle & -@fruit -> Hi! & What kind of car do you drive?\n" +
                "@vehicle -> I like fast cars | I use a bike | I don't like to travel\n" +
                "# Use the matched word for this rule\n" +
                "@fruit -> I like $fruit too! | I hate @fruit, though!\n" +
                "# Fallback case if no rule was matched\n" +
                "() -> I don't understand what you mean...");
        System.out.println("@ruleSet <- ...\n" + ruleSet);
        List<Range> eval = new Eval(nlp).find(ruleSet, "The monkey eats a pear").getRanges();

        System.out.println(ruleSet);
        for (Range range : eval) {
            System.out.println(range);
        }

        assertTrue(new Eval(nlp).contains(eval, "fruit"));
    }



    @Test
    public void matchSimple4(){
        List<Range> eval = new Eval(nlp).find("@fruit <- apple | pear\n" +
                "@drink <- milk | beer | cocktail\n" +
                "@food <- bread | @fruit", "The monkey eats a pear").getRanges();

        for (Range range : eval) {
            System.out.println(range);
        }

        assertTrue(new Eval(nlp).contains(eval, "food") && new Eval(nlp).contains(eval, "fruit"));
    }

    @Test
    public void matchSimple4n(){
        Script ruleSet = Compiler.compile(
                "@fruit <- apple | pear\n" +
                        "@animal <- bear & -beer OR monkey\n" +
                        "@drink <- milk | beer | cocktail\n" +
                        "@food <- bread | @fruit");
        System.out.println("@ruleSet <- ...\n" + ruleSet);
        List<Range> ranges = null;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String text = "The monkey eats a pear";
        final Eval eval = new Eval(nlp);
        for (int i = 0; i < N; i++) {
            ranges = eval.find(ruleSet, text).getRanges();
        }
        stopWatch.stop();

        for (Range range : ranges) {
            System.out.println(range);
        }
        System.out.println("@stopWatch <- " + stopWatch);
        System.out.println(eval.highlightWithTags(text, ranges));
        assertTrue(eval.contains(ranges, "food"));
    }

    @Test
    public void matchSimple4nMarkup(){
        Script ruleSet = Compiler.compile(
                "---\n" +
                        "languages : [ar,en]\n" +
                        "---\n" +
                        "# comment\n" +
                        "\n" +
                        "@fruit <- apple | pear\n" +
                        "@drink <- milk | beer | cocktail\n" +
                        "@food <- bread | @fruit\n" +
                        "\n" +
                        "@food2 <- bread | @fruit\n" +
                        "@food3 <- bread | @fruit\n" +
                        "@food4 <- bread | @fruit\n" +
                        "@food5 <- bread | @fruit\n" +
                        "@food6 <- bread | @fruit\n" +
                        "@food7 <- bread | @fruit\n");
        System.out.println("@ruleSet <- ...\n" + ruleSet);
        List<Range> ranges = null;
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        final Eval eval = new Eval(nlp);
        for (int i = 0; i < N; i++) {
            ranges = eval.find(ruleSet, "The monkey eats a pear").getRanges();
        }
        stopWatch.stop();

        for (Range range : ranges) {
            System.out.println(range);
        }
        System.out.println("@stopWatch <- " + stopWatch);
        assertTrue(eval.contains(ranges, "food"));
    }

    @Test
    public void matchSimple4nMarkupParallel(){
        Script ruleSet = Compiler.compile(
                "---\n" +
                        "languages : [ar,en]\n" +
                        "---\n" +
                        "# comment\n" +
                        "\n" +
                        "@fruit <- apple | pear\n" +
                        "@drink <- milk | beer | cocktail\n" +
                        "@food <- bread | @fruit\n" +
                        "\n" +
                        "@food2 <- bread | @fruit\n" +
                        "@food3 <- bread | @fruit\n" +
                        "@food4 <- bread | @fruit\n" +
                        "@food5 <- bread | @fruit\n" +
                        "@food6 <- bread | @fruit\n" +
                        "@food7 <- bread | @fruit\n");
        System.out.println("@ruleSet <- ...\n" + ruleSet);
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        ExecutorService executor = Executors.newFixedThreadPool(8);
        final Eval eval = new Eval(nlp);

        for (int i = 0; i < N; i++) {
            executor.execute(() -> {
                eval.find(ruleSet, "The monkey eats a pear").getRanges();
            });

        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        stopWatch.stop();
        System.out.println("Finished all threads");

        System.out.println("@stopWatch <- " + stopWatch);
    }

    public void matchMultinline(String src){
        Script ruleSet = Compiler.compile(src);
        System.out.println("ruleSet.dependencyTree() = " + ruleSet.dependencyTree());
        System.out.println("@ruleSet <- ...\n" + ruleSet);
        List<Range> eval = new Eval(nlp).find(ruleSet, "The monkey eats a pear").getRanges();

        System.out.println(ruleSet);
        for (Range range : eval) {
            System.out.println(range);
        }

        assertTrue(new Eval(nlp).contains(eval, "food") && new Eval(nlp).contains(eval, "fruit"));
    }

    @Test
    public void matchMultinlineBeforeOp(){
        matchMultinline(
                "@fruit <- apple \n" +
                    "  | pear\n" +
                    "@drink <- milk | beer | cocktail\n" +
                    "@food <- bread | @fruit");
    }

    @Test
    public void matchMultinlineAfterOp(){
        matchMultinline(
                "@fruit <- apple  | pear\n" +
                "@drink <- milk | beer | \n" +
                "    cocktail\n" +
                "@food <- bread | @fruit");
    }

    @Test
    public void matchMultinlineWithEmptyLines(){
        matchMultinline(
                "@fruit <- apple  | pear\n" +
                    "@drink <- milk | beer | \n" +
                    "    cocktail\n" +
                    "   \n" +
                    "@food <- bread | @fruit");
    }

    @Test
    public void matchMultinlineStartEmpty(){
        matchMultinline(
                "@fruit <-\n" +
                    "   apple  | pear\n" +
                    "@drink <- milk | beer | \n" +
                    "    cocktail\n" +
                    "   \n" +
                    "@food <- bread | @fruit");
    }

    @Test
    public void matchMultinlineStartEmpty2(){
        matchMultinline(
                "\n\n@fruit <-\n" +
                        "   apple  | pear\n" +
                        "@drink <- milk | beer | \n" +
                        "    cocktail\n" +
                        "   \n" +
                        "@food <- bread | @fruit");
    }

    @Test(expected = Compiler.ParseError.class)
    public void recursionError1(){
        try {
            Script script = Compiler.compile("@fruit <- apple | pear | @fruit\n" +
                    "");
            System.out.println("script.dependencyTree() = " + script.dependencyTree());
        } catch (Compiler.ParseError e){
            System.out.println("@e <- " + e);
            throw e;
        }
    }

    @Test(expected = Compiler.ParseError.class)
    public void recursionError2(){
        try {
            Compiler.compile("@fruit <- apple | pear | @foo\n" +
                    "@foo <- @bar\n" +
                    "@bar <- @fruit");
        } catch (Compiler.ParseError e){
            System.out.println("e <- " + e);
            throw e;
        }
    }

    @Test(expected = Compiler.ParseError.class)
    public void recursionError3(){
        try {
            Compiler.compile("@fruit <- apple | pear | @foo\n" +
                    "@foo <- @bar\n" +
                    "@bar <- @foo");
        } catch (Compiler.ParseError e){
            System.out.println("e <- " + e);
            throw e;
        }
    }

    @Test
    public void noRecursionError1(){
        try {
            Script script = Compiler.compile(
                    "@foo <- apple | @bar | @baz\n" +
                            "@bar <- @baz\n" +
                            "@baz <- qux");
            System.out.println("script.dependencyTree() = " + script.dependencyTree());
        } catch (Compiler.ParseError e){
            System.out.println("e <- " + e);
            Assert.fail();
        }
    }

    @Test
    public void noRecursionError2(){
        try {
            Script script = Compiler.compile(
                    "@foo <- apple | @bar | @baz\n" +
                            "@bar <- @baz\n" +
                            "@baz <- qux");
            System.out.println("script.dependencyTree() = " + script.dependencyTree());
        } catch (Compiler.ParseError e){
            System.out.println("e <- " + e);
            Assert.fail();
        }
    }



}
