package org.c4i.nlp.pos;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//See: https://github.com/arabicNLP/ARABICPOSTAGGER
public class ArabicPoS {
    //You need OPENNLP-TOOLS:
    // http://opennlp.apache.org/download.html
    private POSTaggerME tagger;

    private final Logger logger = LoggerFactory.getLogger(ArabicPoS.class);

    public ArabicPoS(String dataFolder) {
        try {
            InputStream modelIn = new FileInputStream(new File(dataFolder, "ar-POS.bin"));
            POSModel model = new POSModel(modelIn);
            tagger = new POSTaggerME(model);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
    }

    public String[] postag(String sentence) {
        String[] tokens = splitSentence(sentence);

        return tagger.tag(tokens);
    }

    public String[] postag(String[] tokens) {
        return tagger.tag(tokens);
    }

    private String[] splitSentence(String sentence) {
        sentence = sentence.trim();

        return sentence.trim().split(" ");
    }
}