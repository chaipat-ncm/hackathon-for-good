package org.c4i.nlp.detect;

/**
 * Will always assign the given language label
 * @author Arvid Halma
 */
public class ConstantLanguageDetector implements LanguageDetector{
    private final String lang;

    public ConstantLanguageDetector(String lang) {
        this.lang = lang;
    }

    @Override
    public String getLanguageCode(String text) {
        return lang;
    }
}
