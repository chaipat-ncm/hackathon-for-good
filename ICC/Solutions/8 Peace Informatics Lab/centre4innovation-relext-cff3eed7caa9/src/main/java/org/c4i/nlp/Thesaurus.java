package org.c4i.nlp;


import org.c4i.nlp.normalize.StringNormalizer;
import org.c4i.util.LineParser;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Dictionary/meaning lookup
 * Build synonyms dictionary from a file.
 * @author Arvid
 * @version 9-4-2015 - 20:10
 */
public class Thesaurus implements WordTest{
    private HashMap<String, Set<String>> data;
    private StringNormalizer normalizer;
    private File file;

    public Thesaurus(File file, StringNormalizer normalizer) throws IOException {
        this.file = file;
        this.normalizer = normalizer;
        load();
    }

    protected Thesaurus load() throws IOException {
        data = new HashMap<>();
        LineParser.lines(line -> {
            String[] parts = line.split(";");
            String key = normalizer.normalize(parts[0]);

            if(data.containsKey(key)){
                // first definitions only
                return;
            }

            HashSet<String> set = new HashSet<>();
            for (int i = 1; i < parts.length; i++) {
                String part = normalizer.normalize(parts[i]);
                if(part.equals(key)){
                    return;
                }
                set.add(part);
            }
            if(set.size() > 1) {
                data.put(key, set);
            }
        }, file);

        return this;
    }

    public Set<String> get(String word){
        String nword = normalizer.normalize(word);
        return data.containsKey(nword) ? data.get(nword) : Collections.<String>emptySet();
    }

    @Override
    public boolean test(String string) {
        return data.containsKey(string);
    }
}
