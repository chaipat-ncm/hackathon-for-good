package org.c4i.nlp;

import org.apache.commons.lang3.time.StopWatch;
import org.c4i.nlp.normalize.*;

public class MetaphoneDemo {
    static int N = 1000;
    public static void main(String[] args) {
//        String txt = "My name is arvid and I would like to welcome you to this metaphone normalizer. The issue is that it maps different words to the same representation sometimes.";
        String txt = "The issue is issue";

        test(txt, new Metaphone());
        test(txt, new Metaphone2());
        test(txt, new Metaphone3());

        MetaphoneNL metaphoneNL = new MetaphoneNL();
        System.out.println("metaphoneNL.normalize(\"vrede\") = " + metaphoneNL.normalize("vrede"));
        System.out.println("metaphoneNL.normalize(\"wrede\") = " + metaphoneNL.normalize("wrede"));
        System.out.println("metaphoneNL.normalize(\"china\") = " + metaphoneNL.normalize("china"));
        System.out.println("metaphoneNL.normalize(\"Ik heet Arvid en ik zou je graag welkom heten bij deze metaphone normalizer!\") \n  = " + metaphoneNL.normalize("Ik heet Arvid en ik zou je graag welkom heten bij deze metaphone normalizer!".toLowerCase()));
    }

    public static void test(String txt, StringNormalizer n){
        System.out.println("txt = " + txt);
        System.out.println("n   = " + n.normalize(txt));
        System.out.print("n[] = " );
        for (String w : txt.split(" ")) {
            System.out.print(n.normalize(w));
            System.out.print(' ');
        }
        System.out.print('\n');
        System.out.println();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < N; i++) {
            for (String w : txt.split(" ")) {
                n.normalize(w);
            }
        }
        stopWatch.stop();
        System.out.println(stopWatch.toString());

    }
}
