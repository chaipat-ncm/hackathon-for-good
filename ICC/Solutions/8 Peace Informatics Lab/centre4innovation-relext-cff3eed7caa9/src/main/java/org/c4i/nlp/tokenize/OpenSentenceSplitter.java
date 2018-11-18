package org.c4i.nlp.tokenize;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Sentence detector using OpenNLP lib.
 * @author Arvid Halma
 * @version 12-11-2017 - 13:44
 */
public class OpenSentenceSplitter implements SentenceSplitter{

    SentenceDetector sentenceDetector;

    public OpenSentenceSplitter(File modelFile) throws IOException {
        try (InputStream modelIn = new FileInputStream(modelFile)) {
            SentenceModel model = new SentenceModel(modelIn);
            sentenceDetector = new SentenceDetectorME(model);
        }
    }

    public OpenSentenceSplitter(SentenceModel model) {
        sentenceDetector = new SentenceDetectorME(model);
    }

    @Override
    public String[] split(String text) {
        return sentenceDetector.sentDetect(text);
    }

    @Override
    public String description() {
        return "OpenNLP splitter";
    }

    @Override
    public String toString() {
        return "OpenSentenceSplitter{" + "sentenceDetector=" + sentenceDetector + '}';
    }
}
