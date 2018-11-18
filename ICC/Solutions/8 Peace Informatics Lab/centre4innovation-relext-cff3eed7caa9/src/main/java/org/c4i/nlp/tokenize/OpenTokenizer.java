package org.c4i.nlp.tokenize;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tokenizer using OpenNLP lib.
 * @author Arvid Halma
 * @version 12-11-2017 - 13:03
 */
public class OpenTokenizer implements Tokenizer {
    TokenizerME tokenizer;

    public OpenTokenizer(File modelFile) throws IOException {
        try (InputStream modelIn = new FileInputStream(modelFile)) {
            TokenizerModel model = new TokenizerModel(modelIn);
            tokenizer = new TokenizerME(model);
        }
    }

    public OpenTokenizer(TokenizerModel model) {
        tokenizer = new TokenizerME(model);
    }

    @Override
    public List<Token> tokenize(String text) {
        Span[] spans = tokenizer.tokenizePos(text);
        List<Token> result = new ArrayList<>(spans.length);
        for (int i = 0; i < spans.length; i++) {
            Span span = spans[i];
            String word = span.getCoveredText(text).toString();
            result.add(new Token(word,  i).setCharStart(span.getStart()).setCharEnd(span.getEnd()));
        }
        return result;
    }

    public String description(){
        return "OpenNLP model";
    }

    @Override
    public String toString() {
        return "OpenTokenizer{" + "sentenceDetector=" + tokenizer + '}';
    }
}
