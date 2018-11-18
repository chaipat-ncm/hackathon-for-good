package org.c4i.chitchat;

import org.c4i.nlp.normalize.PorterStemmer;
import org.c4i.nlp.normalize.StringNormalizer;
import org.c4i.nlp.normalize.StringNormalizers;
import org.c4i.nlp.normalize.kstem.KStem;
import org.c4i.nlp.normalize.kstem.KStemmer;
import org.junit.Test;

/**
 * Demonstrate normalizers by example
 * @author Arvid
 * @version 14-10-2015 - 21:02
 */
public class NormalizerTest {

    @Test
    public void demoAlphaNumOnly(){
        demo(StringNormalizers.ALPHA_NUM_ONLY);
    }

    @Test
    public void demoUnicode(){
        demo(StringNormalizers.UNICODE);
    }

    @Test
    public void demoDefault(){
        demo(StringNormalizers.DEFAULT);
    }

    public void demo(StringNormalizer normalizer){
        System.out.println("\nDemo default " + normalizer.getClass().getSimpleName());
        String[] words = {"hyper-space", "f*cking", "arVId", "Caf\u00e9", "4"};
        for (String word : words) {
            System.out.printf("%s \t--> %s\n", word, normalizer.normalize(word));
        }
    }

    @Test
    public void demoCompose(){
        System.out.println("\nDemo composition of normalizers");
        String[] words = {"hyper-space", "f*cking", "arVId", "Caf\u00e9", "4"};
        StringNormalizer lower = StringNormalizers.LOWER_CASE;
        StringNormalizer noAccents = StringNormalizers.NO_ACCENTS;
        for (String word : words) {
            System.out.printf("%s \t--> %s --> %s --> %s\n", word, lower.normalize(word), noAccents.normalize(word), lower.compose(noAccents).apply(word));
        }
    }

    @Test
    public void demoPorter(){
        System.out.println("\nDemo Porter stemmer");
        String[] words = {"caresses", "flies", "dies", "mules", "denied",
                "died", "agreed", "owned", "humbled", "sized",
                "meeting", "stating", "siezing", "itemization",
                "sensational", "traditional", "reference", "colonizer",
                "plotted", "4", "124142.4"};

        PorterStemmer stemmer = new PorterStemmer();

        for (String word : words) {
            System.out.printf("%s \t--> %s\n", word, stemmer.normalize(word));
        }
    }

    @Test
    public void demoKStem(){
        System.out.println("\nKstemmer");
        String[] words = {"caresses", "flies", "dies", "mules", "denied",
                "died", "agreed", "owned", "humbled", "sized",
                "meeting", "stating", "siezing", "itemization",
                "sensational", "traditional", "reference", "colonizer",
                "plotted", "4", "124142.4"};

        KStemmer stemmer = new KStemmer();

        for (String word : words) {
            System.out.printf("%s \t--> %s\n", word, stemmer.normalize(word));
        }
    }

    @Test
    public void demoKStem2(){
        System.out.println("\nKstem");
        String[] words = {"caresses", "flies", "dies", "mules", "denied",
                "died", "agreed", "owned", "humbled", "sized",
                "meeting", "stating", "siezing", "itemization",
                "sensational", "traditional", "reference", "colonizer",
                "plotted", "4", "124142.4"};

        StringNormalizer stemmer = new KStem();

        for (String word : words) {
            System.out.printf("%s \t--> %s\n", word, stemmer.normalize(word));
        }
    }
}
