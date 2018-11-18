package org.c4i.nlp.detect;

import java.util.Locale;

/**
 * Detect language for a given text sample
 * @author Arvid Halma
 */
public interface LanguageDetector {

    String getLanguageCode(String text);

    default Locale getLocale(String text){
        return Locale.forLanguageTag(getLanguageCode(text));
    }

    default String getLanguageName(String text){
        return Locale.forLanguageTag(getLanguageCode(text)).getDisplayLanguage();
    }

}
