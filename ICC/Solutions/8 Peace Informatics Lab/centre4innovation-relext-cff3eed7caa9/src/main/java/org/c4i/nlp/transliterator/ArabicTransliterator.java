package org.c4i.nlp.transliterator;

import com.ibm.icu.text.*;

/**
 * Created by Wouter Eekhout on 14/07/2017.
 *
 */
public class ArabicTransliterator {
    private static Transliterator arabicToLatinTrans = Transliterator.getInstance("Arabic-Latin");

    public static String transform(String text) {
        return arabicToLatinTrans.transform(text);
    }
}
