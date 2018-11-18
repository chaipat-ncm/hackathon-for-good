package org.c4i.nlp.normalize;

import org.apache.lucene.analysis.ar.ArabicStemmer;
import org.apache.lucene.analysis.bg.BulgarianStemmer;
import org.apache.lucene.analysis.bn.BengaliStemmer;
import org.apache.lucene.analysis.cz.CzechStemmer;
import org.apache.lucene.analysis.de.GermanLightStemmer;
import org.apache.lucene.analysis.el.GreekStemmer;
import org.apache.lucene.analysis.en.EnglishMinimalStemmer;
import org.apache.lucene.analysis.es.SpanishLightStemmer;
import org.apache.lucene.analysis.fi.FinnishLightStemmer;
import org.apache.lucene.analysis.fr.FrenchLightStemmer;
import org.apache.lucene.analysis.gl.GalicianStemmer;
import org.apache.lucene.analysis.hi.HindiStemmer;
import org.apache.lucene.analysis.hu.HungarianLightStemmer;
import org.apache.lucene.analysis.id.IndonesianStemmer;
import org.apache.lucene.analysis.it.ItalianLightStemmer;
import org.apache.lucene.analysis.lv.LatvianStemmer;
import org.apache.lucene.analysis.no.NorwegianLightStemmer;
import org.apache.lucene.analysis.pt.PortugueseLightStemmer;
import org.apache.lucene.analysis.ru.RussianLightStemmer;
import org.apache.lucene.analysis.sv.SwedishLightStemmer;

import java.io.File;
import java.io.IOException;

/**
 * Lucene/Solr stemmers for several languages with a uniform interface.
 * See: https://github.com/apache/lucene-solr
 * @author Arvid Halma
 * @version 12-11-2017 - 21:54
 */
public class LuceneStemmer implements StringNormalizer {
    String lang;
    StringNormalizer normalizer;

    public LuceneStemmer(String lang, boolean light) {
        this.lang = lang;
        switch (lang) {
            case "ar": {
                ArabicStemmer stemmer = new ArabicStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "bg": {
                BulgarianStemmer stemmer = new BulgarianStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "bn": {
                BengaliStemmer stemmer = new BengaliStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "cz": {
                CzechStemmer stemmer = new CzechStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "de": {
                GermanLightStemmer stemmer = new GermanLightStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "el": {
                GreekStemmer stemmer = new GreekStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "en": {
//                KStemmer stemmer = new KStemmer();
                if(light) {
                    EnglishMinimalStemmer stemmer = new EnglishMinimalStemmer();
                    normalizer = string -> {
                        char[] cs = string.toCharArray();
                        int n = stemmer.stem(cs, cs.length);
                        return new String(cs, 0, n);
                    };
                } else {
                    normalizer = new PorterStemmer();
                }
            } break;
            case "es": {
                SpanishLightStemmer stemmer = new SpanishLightStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "fi": {
                FinnishLightStemmer stemmer = new FinnishLightStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "fr": {
                FrenchLightStemmer stemmer = new FrenchLightStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "gl": {
                GalicianStemmer stemmer = new GalicianStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "hi": {
                HindiStemmer stemmer = new HindiStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "hu": {
                HungarianLightStemmer stemmer = new HungarianLightStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "id": {
                IndonesianStemmer stemmer = new IndonesianStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length, true); return new String(cs, 0, n);};
            } break;
            case "it": {
                ItalianLightStemmer stemmer = new ItalianLightStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "lv": {
                LatvianStemmer stemmer = new LatvianStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "no": {
                NorwegianLightStemmer stemmer = new NorwegianLightStemmer(NorwegianLightStemmer.BOKMAAL);
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "pt": {
                PortugueseLightStemmer stemmer = new PortugueseLightStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "ru": {
                RussianLightStemmer stemmer = new RussianLightStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "sv": {
                SwedishLightStemmer stemmer = new SwedishLightStemmer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = stemmer.stem(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "sw": {
                try {
                    normalizer = new SwahiliStemmer(new File("data/nlp/sw/sw-stem.txt")); // todo: move elsewhere
                } catch (IOException e) {
                    normalizer = IDENTITY;
                }
            } break;

            
            default: normalizer = StringNormalizer.IDENTITY;
        }
    }

    @Override
    public String normalize(String string) {
        return normalizer.normalize(string);
    }


}
