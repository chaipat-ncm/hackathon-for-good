package org.c4i.nlp.pos;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.c4i.nlp.tokenize.Token;
import org.c4i.nlp.tokenize.TokenUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Part of Speech tagger using OpenNLP lib.
 * @author Arvid
 * @version 27-9-2015 - 18:11
 */
public class OpenPOSTagger implements POSTagger{
    private POSModel model;
    private Map<String, String> tagMapping;

    public OpenPOSTagger(File modelFile) throws IOException {
        this(modelFile, null);
    }

    public OpenPOSTagger(File modelFile, Map<String, String> tagMapping) throws IOException {
        this.tagMapping = tagMapping;
        this.model = new POSModel(new FileInputStream(modelFile));
    }

    @Override
    public List<Token> tag(List<Token> tokens){
        POSTaggerME tagger = new POSTaggerME(model);
        String[] tags = tagger.tag(TokenUtil.toWordArray(tokens, false));
        double[] probs = tagger.probs();

        for (int i = 0; i < probs.length; i++) {
            Token token = tokens.get(i);
            String tag = tags[i];
            token.setTag(tagMapping != null ? tagMapping.get(tag) : tag);
            token.setWeight(probs[i]);
        }

        return tokens;
    }
}
