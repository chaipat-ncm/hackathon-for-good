package org.c4i.nlp.normalize;

import org.apache.lucene.analysis.ar.ArabicNormalizer;
import org.apache.lucene.analysis.bn.BengaliNormalizer;
import org.apache.lucene.analysis.fa.PersianNormalizer;
import org.apache.lucene.analysis.hi.HindiNormalizer;
import org.apache.lucene.analysis.in.IndicNormalizer;

/**
 * Lucene/Solr word/char normalizers for several languages with a uniform interface.
 * See: https://github.com/apache/lucene-solr
 * @author Arvid Halma
 * @version 12-11-2017 - 21:54
 */
public class LuceneNormalizer implements StringNormalizer {
    String lang;
    StringNormalizer normalizer;

    public LuceneNormalizer(String lang) {
        this.lang = lang;
        this.lang = lang;
        switch (lang) {
            case "ar": {
                ArabicNormalizer nz = new ArabicNormalizer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = nz.normalize(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "bn": {
                BengaliNormalizer nz = new BengaliNormalizer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = nz.normalize(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "fa": {
                PersianNormalizer nz = new PersianNormalizer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = nz.normalize(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "hi": {
                HindiNormalizer nz = new HindiNormalizer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = nz.normalize(cs, cs.length); return new String(cs, 0, n);};
            } break;
            case "ni": {
                IndicNormalizer nz = new IndicNormalizer();
                normalizer = string -> { char[] cs = string.toCharArray(); int n = nz.normalize(cs, cs.length); return new String(cs, 0, n);};
            } break;
            
            
            default: normalizer = StringNormalizer.IDENTITY;
        }
    }

    @Override
    public String normalize(String string) {
        return normalizer.normalize(string);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LuceneNormalizer{");
        sb.append("lang='").append(lang).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
