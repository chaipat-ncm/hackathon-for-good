package org.c4i.nlp.ner;

import org.apache.commons.io.IOUtils;
import org.c4i.nlp.StopWords;
import org.c4i.nlp.WordTest;
import org.c4i.nlp.match.*;
import org.c4i.nlp.normalize.StringNormalizer;
import org.c4i.nlp.tokenize.Token;
import org.c4i.nlp.tokenize.TokenUtil;
import org.c4i.nlp.tokenize.Tokenizer;
import org.c4i.util.Csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Check for token sequence occurrences, and retrieve additional data when matched.
 * @author Arvid Halma
 * @version 01-08-2017
 */
public class DataSheet implements NamedEntityRecognition, EntityPlugin {
    private Map<String, List<EntityRecord>> entries;
    private List<String> columns;
    private String entityType;
    private String entityTypeUpper;

    public DataSheet(String entityType) {
        this.entries = new HashMap<>();
        this.entityType = entityType.toLowerCase();
        this.entityTypeUpper = entityType.toUpperCase();
    }

    public DataSheet(String entityType, Map<String, String> props, StringNormalizer normalizer, Tokenizer tokenizer) throws IOException {
        this(entityType);
        List<String> keys = new ArrayList<>(props.keySet());
        keys.sort((o1, o2) -> Integer.compare(o2.length(), o1.length())); // longer, more specific ones first
        for (String k : keys) {
            EntityRecord record = new EntityRecord();
            record.setOrg(k);
            List<Token> tokens = tokenizer.tokenize(record.getOrg());
            normalizer.normalizeTokens(tokens);
            record.setTokens(tokens);
            addEntry(record.setOrg(k));
        }
    }

    public DataSheet(String entityType, File file, StringNormalizer normalizer, Tokenizer tokenizer) throws IOException {
        this(entityType, file, normalizer, tokenizer, StopWords.EMPTY_STOPWORDS);
    }

    public DataSheet(String entityType, File file, StringNormalizer normalizer, Tokenizer tokenizer, WordTest wordTest) throws IOException {
        this(entityType, new FileInputStream(file), normalizer, tokenizer, wordTest);
    }

    public DataSheet(String entityType, String data, StringNormalizer normalizer, Tokenizer tokenizer, WordTest wordTest) throws IOException {
        this(entityType, IOUtils.toInputStream(data, "UTF-8"), normalizer, tokenizer, wordTest);
    }

    public DataSheet(String entityType, InputStream inputStream, StringNormalizer normalizer, Tokenizer tokenizer, WordTest wordTest) throws IOException {
        this(entityType);
        List<EntityRecord> entities = new ArrayList<>();
        new Csv()
                .setInputStream(inputStream)
                .formatTsv()
                .setUseHeader(true)
                .process(row -> {
                            String orgKey = row.getString(0);

                            if(orgKey == null || orgKey.isEmpty() || !wordTest.test(orgKey))
                                return;

                            EntityRecord record = new EntityRecord();
                            record.setOrg(orgKey);

                            columns = new ArrayList<>(row.getColumns());
                            for (String propKey : columns) {
                                String propVal = row.getString(propKey);
                                if(propVal != null) {
                                    if ("exclude".equals(propKey)) {
                                        List<Token[]> excludeTokens = new ArrayList<>();
                                        String[] parts = propVal.split("\\s*,\\s*");
                                        for (String value : parts) {
                                            Token[] excludeTok = tokenizer.tokenize(value).toArray(new Token[0]);
                                            normalizer.normalizeTokens(excludeTok);
                                            excludeTokens.add(excludeTok);
                                        }
                                        record.setExclude(excludeTokens);
                                    } else {
                                        record.props.put(propKey, propVal.intern());
                                    }
                                }
                            }

                            List<Token> tokens = tokenizer.tokenize(record.getOrg());
                            normalizer.normalizeTokens(tokens);
                            record.setTokens(tokens);

                            entities.add(record);
                        }
                );

        inputStream.close();

        entities.sort((o1, o2) -> Integer.compare(o2.org.length(), o1.org.length())); // longer, more specific ones first
        for (EntityRecord record : entities) {
            addEntry(record);
        }

    }

    private void addEntry(EntityRecord entry) {
        if (!entry.tokens.isEmpty()){
            String nword = entry.tokens.get(0).getNormalizedWord();
            if(!nword.isEmpty()){
                if(!entries.containsKey(nword)){
                    entries.put(nword, new ArrayList<>());
                }
                entries.get(nword).add(entry);
            }
        }
    }

    public String getEntityType() {
        return entityType;
    }

    @Override
    public boolean accept(Literal lit) {
        return lit.getTokens()[0].getWord().equals(entityTypeUpper);
    }

    @Override
    public List<Range> find(Token[] text, Literal ignoredLit, String ignoredLabel, int location) {
        List<Range> result = new ArrayList<>();
        Token token = text[location];
        String nword = token.getNormalizedWord();
        if(entries.containsKey(nword)){
            // first part occurs... do proper check
            List<EntityRecord> possiblePatterns = entries.get(nword);
            nextPat:
            for (EntityRecord record : possiblePatterns) {
                int patLen = record.tokens.size();
                if(location + patLen > text.length){
                    // pattern too long
                    continue nextPat;
                }
                for (int k = 1; k < patLen; k++) {
                    // first part already matched, check remaining ones
                    Token patToken = record.tokens.get(k);
                    if (!patToken.getNormalizedWord().equals(text[location + k].getNormalizedWord())) {
                        continue nextPat;
                    }
                }

                if(record.exclude == null || !findExclude(text, record.exclude)) {
                    // a match!
                    Range range = new Range(entityType, location, location + patLen, text[location].getCharStart(), text[location + patLen - 1].getCharEnd());
                    range.props = new LinkedHashMap<>(record.props);
                    range.props.put("type", entityType);
                    result.add(range);
                }
            }
        }
        return result;
    }

    @Override
    public List<Range> find(List<Token> tokens) {
        List<Range> result = new ArrayList<>();
        Token[] text = tokens.toArray(new Token[0]);

        nextWord:
        for (int i = 0, tokensSize = tokens.size(); i < tokensSize; i++) {
            Token token = tokens.get(i);
            String nword = token.getNormalizedWord();
            if(entries.containsKey(nword)){
                // first part occurs... do proper check
                List<EntityRecord> possiblePatterns = entries.get(nword);
                nextPat:
                for (EntityRecord record : possiblePatterns) {
                    int patLen = record.tokens.size();
                    for (int k = 1; k < patLen; k++) {
                        // first part already matched, check remaining ones
                        Token patToken = record.tokens.get(k);
                        if (!patToken.getNormalizedWord().equals(tokens.get(i + k).getNormalizedWord())) {
                            continue nextPat;
                        }
                    }

                    // a match!
                    if(record.exclude == null || !findExclude(text, record.exclude)) {
                        Range range = new Range(entityType, i, i + patLen, tokens.get(i).getCharStart(), tokens.get(i + patLen - 1).getCharEnd());
                        range.props = new LinkedHashMap<>(record.props);
                        range.props.put("type", entityType);
                        result.add(range);
                        continue nextWord;
                    }
                }
            }
        }
        return result;
    }

    private boolean findExclude(Token[] text, List<Token[]> exclude) {
        final Eval eval = new Eval(null);
        for (Token[] tokens : exclude) {
            if (!eval.findFirst(text, tokens).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String description() {
        return entityTypeUpper;
    }

    @Override
    public String toString() {
        return "EntityFinder{" +
                "entityType='" + entityType + '\'' +
                '}';
    }

    private static class EntityRecord {
        String org;
        List<Token> tokens;
        List<Token[]> exclude;
        Map<String, String> props;

        public EntityRecord() {
            props = new LinkedHashMap<>();
        }

        public String getOrg() {
            return org;
        }

        public EntityRecord setOrg(String org) {
            this.org = org;
            return this;
        }

        public List<Token> getTokens() {
            return tokens;
        }

        public EntityRecord setTokens(List<Token> tokens) {
            this.tokens = tokens;
            return this;
        }

        public List<Token[]> getExclude() {
            return exclude;
        }

        public EntityRecord setExclude(List<Token[]> exclude) {
            this.exclude = exclude;
            return this;
        }

        public Map<String, String> getProps() {
            return props;
        }

        public EntityRecord setProps(Map<String, String> props) {
            this.props = props;
            return this;
        }
    }
}
