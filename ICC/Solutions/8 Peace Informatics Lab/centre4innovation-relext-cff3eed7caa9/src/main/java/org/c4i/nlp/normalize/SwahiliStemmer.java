package org.c4i.nlp.normalize;

import org.c4i.util.LineParser;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Swahili stemmer
 * @author Arvid Halma
 * @version 29-3-18
 */
public class SwahiliStemmer implements StringNormalizer {
    private Set<String> keepPostFix;
    private Map<String, String> replace;
    private int minPostfixLen = 20, maxPostfixLen = 0;


    public SwahiliStemmer(File modelFile) throws IOException {
        keepPostFix = new HashSet<>();

        replace = new HashMap<>();
        LineParser.lines(line -> {
            if(line.startsWith("-")){
                // keep suffix
                String postfix = line.substring(1);
                int postfixLen = postfix.length();
                minPostfixLen = Math.min(minPostfixLen, postfixLen);
                maxPostfixLen = Math.max(maxPostfixLen, postfixLen);
                keepPostFix.add(postfix);
            } else {
                String[] parts = line.split("/");
                replace.put(parts[1].substring(0, parts[1].length()-1) + parts[0], parts[0]);
            }
        }, modelFile);
    }

    @Override
    public String normalize(String word) {
        if(replace.containsKey(word)){
            return replace.get(word);
        }

        int wordLen = word.length(); // look for longer postfixes first
        for (int i = Math.min(wordLen, maxPostfixLen); i >= minPostfixLen; i--) {
            String postfix = word.substring(wordLen - i, wordLen);
            if (keepPostFix.contains(postfix)) {
                return postfix;
            }
        }
        return word;
    }
}
