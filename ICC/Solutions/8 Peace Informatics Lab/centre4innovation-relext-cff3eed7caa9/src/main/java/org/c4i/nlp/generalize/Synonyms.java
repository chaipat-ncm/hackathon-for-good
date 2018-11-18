package org.c4i.nlp.generalize;

import org.c4i.nlp.WordTest;
import org.c4i.nlp.normalize.StringNormalizer;
import org.c4i.util.LineParser;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Build synonyms dictionary from a file.
 * @author Arvid
 * @version 9-4-2015 - 20:10
 */
public class Synonyms  implements WordTest {
    private HashMap<String, Set<String>> synMap;
    private StringNormalizer normalizer;
    private File file;

    public Synonyms(File file, StringNormalizer normalizer) throws IOException {
        this.file = file;
        this.normalizer = normalizer;
        load();
    }

    protected Synonyms load() throws IOException {
        synMap = new HashMap<>();
        LineParser.lines(line -> {
            HashSet<String> set = new HashSet<>();
            for(String word : line.split("\t")){
                if(!(word.contains(" ") || word.contains("_"))){
                    // only atomic stopWords
                    set.add(word);
                }
            }
            if(set.size() > 1) {
                for (String word : set) {
                    String nword = normalizer.normalize(word);
                    if (!synMap.containsKey(nword)) {
                        synMap.put(nword, set);
                    } else {
                        // synMap.get(nword).addAll(set);
                    }
                }
            }
        }, file);

        return this;
    }

    public Set<String> get(String word){
        String nword = normalizer.normalize(word);
        return synMap.getOrDefault(nword, Collections.emptySet());
    }

    @Override
    public boolean test(String string) {
        return synMap.containsKey(string);
    }
}
