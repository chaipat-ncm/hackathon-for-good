package org.c4i.nlp.normalize;

import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.*;

/**
 * Snowball stemmer.
 * @author Arvid Halma
 * @version 9-4-2015 - 20:51
 */
public class SnowballStemmer implements StringNormalizer {
    private String lang;
    private SnowballProgram stemmer;

    public SnowballStemmer(String lang) {
        this.lang = lang;
        switch (lang) {
            case "da": stemmer = new DanishStemmer(); break;
            case "nl": stemmer = new KpStemmer(); break; // The Kraaij-Pohlmann stemmer, instead of Dutch
            case "en": stemmer = new EnglishStemmer(); break;
            case "fi": stemmer = new FinnishStemmer(); break;
            case "fr": stemmer = new FrenchStemmer(); break;
            case "ge": stemmer = new German2Stemmer(); break;
            case "hu": stemmer = new HungarianStemmer(); break;
            case "it": stemmer = new ItalianStemmer(); break;
            case "no": stemmer = new NorwegianStemmer(); break;
            case "pt": stemmer = new PortugueseStemmer(); break;
            case "ro": stemmer = new RomanianStemmer(); break;
            case "ru": stemmer = new RussianStemmer(); break;
            case "es": stemmer = new SpanishStemmer(); break;
            case "sv": stemmer = new SwedishStemmer(); break;
            case "tu": stemmer = new TurkishStemmer(); break;
            default: throw new IllegalArgumentException("Unsupported stemmer language :" + lang);
        }
    }

    @Override
    public synchronized String normalize(String string) {
        stemmer.setCurrent(string);
        stemmer.stem();
        return stemmer.getCurrent();
    }

    @Override
    public String toString() {
        return "SnowballStemmer{" +
                "lang='" + lang + '\'' +
                '}';
    }


}
