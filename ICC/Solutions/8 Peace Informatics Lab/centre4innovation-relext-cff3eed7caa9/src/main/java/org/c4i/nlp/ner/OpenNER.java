package org.c4i.nlp.ner;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import org.c4i.nlp.match.Literal;
import org.c4i.nlp.match.Range;
import org.c4i.nlp.match.EntityPlugin;
import org.c4i.nlp.tokenize.Token;
import org.c4i.nlp.tokenize.TokenUtil;
import org.parboiled.common.ImmutableList;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Named Entity Recognition
 * The Name Finder can detect named entities and numbers in text.
 * @author Arvid Halma
 */
public class OpenNER implements NamedEntityRecognition, EntityPlugin {
    private TokenNameFinderModel model;
    private  String name;
    private String nameUpper;

    public OpenNER(File modelFile) throws IOException {
        model = new TokenNameFinderModel(new FileInputStream(modelFile));
        name = modelFile.getName();
        if(name.contains(".")) {
            name = name.substring(0, name.lastIndexOf('.'));
        }
        nameUpper = name.toUpperCase();
    }

    public OpenNER(String name, File modelFile) throws IOException {
        this.name = name;
        nameUpper = name.toUpperCase();
        this.model = new TokenNameFinderModel(new FileInputStream(modelFile));
    }

    @Override
    public boolean accept(Literal lit) {
        return lit.getTokens()[0].getWord().equals(nameUpper);
    }

    @Override
    public List<Range> find(Token[] text, Literal lit, String label, int location) {

        // run only once, at sentence level.
        if(location > 0){
            return ImmutableList.of();
        }

        NameFinderME nameFinder = new NameFinderME(model);

        String[] input = new String[text.length - location];
        for (int i = location; i < text.length; i++) {
            input[i] = text[i].getWord();

        }
        return Arrays.stream(nameFinder.find(input))
                .map(span -> {
                    int start = span.getStart() + location;
                    int end = span.getEnd()+ location;

                    Range range = new Range(span.getType(), start, end, text[start].getCharStart(), text[end - 1].getCharEnd());
                    range.props.put("type", name);
                    return range;
                })
                .collect(Collectors.toList());
    }

    @Override
    public String description() {
        return nameUpper;
    }

    public List<Range> find(List<Token> tokens){
        NameFinderME nameFinder = new NameFinderME(model);

        return Arrays.stream(nameFinder.find(TokenUtil.toWordArray(tokens, false)))
                .map(span -> {
                    Range range = new Range(span.getType(), span.getStart(), span.getEnd(), tokens.get(span.getStart()).getCharStart(), tokens.get(span.getEnd() - 1).getCharEnd());
                    range.props.put("type", name);
                    return range;
                })
        .collect(Collectors.toList());
    }
}
