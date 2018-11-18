package org.c4i.nlp.detect;

import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Detect language for a given text sample, using an OpenNLP model.
 * @author Arvid Halma
 * @version 9-5-2015 - 13:45
 */
public class OpenLanguageDetector implements LanguageDetector{
    private final LanguageDetectorModel detectorModel;
    private String defaultLanguage;
    private int maxInputChars;

    public OpenLanguageDetector (File model, String defaultLanguage, int maxInputChars) throws IOException {
        this.defaultLanguage = defaultLanguage;
        this.detectorModel = new LanguageDetectorModel(new FileInputStream(model));
        this.maxInputChars = maxInputChars;
    }

    public String getLanguageCode(String text){
        if(text == null || text.isEmpty()){
            return defaultLanguage;
        }
        int inputLength = maxInputChars < 0 ? text.length() : Math.min(text.length(), maxInputChars);
        text = text.substring(0, inputLength);
        opennlp.tools.langdetect.LanguageDetector myCategorizer = new LanguageDetectorME(detectorModel);

        // Get the most probable language
        Language bestLanguage = myCategorizer.predictLanguage(text);
        String code2 = LanguageISO6393.toCode2(bestLanguage.getLang());
        return code2 == null ? defaultLanguage : code2;
    }


}