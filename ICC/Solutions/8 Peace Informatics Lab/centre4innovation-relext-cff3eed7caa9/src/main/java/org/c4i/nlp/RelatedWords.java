package org.c4i.nlp;


import org.c4i.nlp.normalize.StringNormalizer;
import org.c4i.nlp.tokenize.Token;
import org.c4i.nlp.tokenize.TokenUtil;
import org.c4i.util.Csv;
import org.parboiled.common.ImmutableList;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Build synonyms dictionary from a file.
 * @author Arvid
 * @version 9-4-2015 - 20:10
 */
public class RelatedWords implements WordTest{
    private HashMap<List<Token>, Set<Token[]>> wordMap;
    private StringNormalizer normalizer;
    private File file;
    private boolean atomicWordsOnly = false;

    public RelatedWords(File file, StringNormalizer normalizer) throws IOException {
        this.file = file;
        this.normalizer = normalizer;
        load();
    }

    protected RelatedWords load() throws IOException {
        wordMap = new HashMap<>();
        new Csv().formatTsv().setInputFile(file).process(row -> {
            String[] words = row.getString(0).split(" ");
            if(atomicWordsOnly && words.length > 1){
                return; // skip
            }
            List<Token> tokens = TokenUtil.toTokenList(words);
            normalizer.normalizeTokens(tokens);

            if(wordMap.containsKey(tokens)){
                return; // already inserted: only first entry
            }

            List<String> data = row.getData();

            Set<Token[]> related = new HashSet<>();
            for (int i = 1; i < data.size(); i++) {
                String[] synonym = data.get(i).split(" ");
                if(atomicWordsOnly && synonym.length > 0){
                    continue; // skip
                }
                Token[] synTokens = TokenUtil.toTokenArray(synonym);
                normalizer.normalizeTokens(synTokens);
                related.add(synTokens);
            }

            if(!related.isEmpty()){
                wordMap.put(tokens, related);
            }
        });


        return this;
    }

    public Set<String> get(String word){
        return get(ImmutableList.of(new Token(word))).stream().map(ts -> String.join(" ", ts)).collect(Collectors.toSet());
    }

    public Set<Token[]> get(Token ... tokens){
        return wordMap.getOrDefault(Arrays.asList(tokens), Collections.emptySet());
    }

    public Set<Token[]> get(List<Token> tokens){
        return wordMap.getOrDefault(tokens, Collections.emptySet());
    }

    @Override
    public boolean test(String string) {
        return get(string).isEmpty();
    }
}

