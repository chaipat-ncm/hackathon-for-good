package org.c4i.chitchat;


import org.apache.commons.lang3.time.StopWatch;
import org.c4i.nlp.tokenize.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Arvid
 * @version 6-4-2015 - 12:06
 */
public class WordTokenizerDemo {
    final static int N = 100_000;


    public static void main(String[] args) throws IOException {
        String text;

        text = "Helloo there! I am @arvid, so you #know... Can't complain ☺, 'can you' ?!? Let's \"put\" a :smiley: here: EMO_HAPPY at 15 o'clock. http://tkjh.co/bla#foo \"Jean-Marie Le'Blanc\", \"Żółć\", \"Ὀδυσσεύς\", \"原田雅彦\"dgasd :-) a.sdfs@gmail.com (gasd A)SDg asgd \nAsgd!! aap. $foo.bar A € is $1.30 my friend. EMO-SMILE 2x bla. 412 41.4 -56?";
        System.out.println("text = " + text);
        System.out.println("SplittingWordTokenizer       = " + new SplittingWordTokenizer().tokenize(text));

        List<Token> tokens = new MatchingWordTokenizer().tokenize(text);
        System.out.println("MatchingWordTokenizer        = " + tokens);
        System.out.println("MatchingWordTokenizer (full) = " + new MatchingWordTokenizer().tokenizeFull(text));
        System.out.println("OpenTokenizer                = " + new OpenTokenizer(new File("data/nlp/en/en-token.bin")).tokenize(text));
        System.out.println(".. sensible ...");
        System.out.println("WordPredicates.filterSensible(words) = " + WordPredicates.filterTokens(tokens, WordPredicates.SENSIBLE));

        System.out.println();

        text = "أعلن الرئيس الروسي فلاديمير بوتين نيته عقد مؤتمر \"حوار وطني\" في روسيا لوضع حد للحرب الأهلية في سوريا المستمرة منذ 6 سنوات.\n" +
                "وتأتي تصريحات بوتين بعد محادثات أجراها مع الرئيسين الإيراني والتركي رجب طيب أروغان ودعا فيها الحكومة السورية والمعارضة للمشاركة \"الفعالة\" في المؤتمر المقرر عقده في سوتشي في جنوب غرب روسيا.\n";

        System.out.println("MatchingWordTokenizer (full) = " + new MatchingWordTokenizer().tokenizeFull(text));
        System.out.println("OpenTokenizer (en)           = " + new OpenTokenizer(new File("data/nlp/en/en-token.bin")).tokenize(text));

        System.out.println();
        speedTest();

    }

    public static void speedTest() throws IOException {
        System.out.println("Speed test");

        String text = "Helloo there! I am @arvid, so you #know... Can't complain ☺, 'can you' ?!? Let's \"put\" a :smiley: here: EMO_HAPPY at 15 o'clock. http://tkjh.co/bla#foo \"Jean-Marie Le'Blanc\", \"Żółć\", \"Ὀδυσσεύς\", \"原田雅彦\"dgasd :-) a.sdfs@gmail.com (gasd A)SDg asgd \nAsgd!! aap. $foo.bar A € is $1.30 my friend. EMO-SMILE 2x bla. 412 41.4 -56?";
        System.out.println("text = " + text);

        System.out.println("MatchingWordTokenizer (full) ...");
        MatchingWordTokenizer matchingWordTokenizer = new MatchingWordTokenizer();
        List<Token> tokens = null;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < N; i++) {
             tokens = matchingWordTokenizer.tokenizeFull(text);
        }
        stopWatch.stop();
        System.out.println("stopWatch = " + stopWatch);

        System.out.println("OpenTokenizer ...");
        OpenTokenizer openTokenizer = new OpenTokenizer(new File("data/nlp/en/en-token.bin"));
        tokens = null;
        stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < N; i++) {
            tokens = matchingWordTokenizer.tokenizeFull(text);
        }
        stopWatch.stop();
        System.out.println("stopWatch = " + stopWatch);



    }
}
