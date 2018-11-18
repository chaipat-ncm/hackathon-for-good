package org.c4i.nlp;

import edu.mit.jwi.Dictionary;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;

/**
 * @author Arvid
 * @version 14-5-2015 - 21:36
 */
public class WordnetUtilDemo {
    public static void main(String[] args) throws IOException {
        arabic();
        System.out.println("WordNetUtils.getHypernyms(fork) = " + WordNetUtils.getHypernyms("fork"));
        System.out.println("WordNetUtils.getHypernyms(knife) = " + WordNetUtils.getHypernyms("knife"));
        System.out.println("WordNetUtils.getHypernyms(soccer) = " + WordNetUtils.getHypernyms("soccer"));
        System.out.println("WordNetUtils.getHypernyms(baseball) = " + WordNetUtils.getHypernyms("baseball"));
        System.out.println("WordNetUtils.getHypernyms(run) = " + WordNetUtils.getHypernyms("run"));
        System.out.println("WordNetUtils.getHypernyms(walk) = " + WordNetUtils.getHypernyms("walk"));
        System.out.println("WordNetUtils.getHypernyms(corporation) = " + WordNetUtils.getHypernyms("corporation"));
        System.out.println("WordNetUtils.getHypernyms(corporate) = " + WordNetUtils.getHypernyms("corporate"));
        System.out.println("WordNetUtils.getHypernyms(corporat) = " + WordNetUtils.getHypernyms("corporat"));
        System.out.println("WordNetUtils.getHyperHypernyms(monkey) = " + WordNetUtils.getHyperHypernyms("monkey"));
        System.out.println("WordNetUtils.getHypernyms(monkey) = " + WordNetUtils.getHypernyms("monkey"));

        System.out.println("WordNetUtils.getHyperHypernyms(programming) = " + WordNetUtils.getHyperHypernyms("programming"));
        System.out.println("WordNetUtils.getHypernyms(programming) = " + WordNetUtils.getHypernyms("programming"));

        System.out.println("WordNetUtils.getHyperHypernyms(olive) = " + WordNetUtils.getHyperHypernyms("olive"));
        System.out.println("WordNetUtils.getHypernyms(olive) = " + WordNetUtils.getHypernyms("olive"));

    }

    public static void arabic() throws IOException {
        Dictionary iDictionary = null;
        URL url = new URL("file", null, "data/nlp/ar/wordnet");
        System.out.println(" Arabic");
        iDictionary = new Dictionary(url);
        iDictionary.open();


        HashSet<String> hypernyms = WordNetUtils.getHypernyms(iDictionary, "سيارة", "N", true);
        System.out.println("hypernyms (car) = " + hypernyms);

    }
}
