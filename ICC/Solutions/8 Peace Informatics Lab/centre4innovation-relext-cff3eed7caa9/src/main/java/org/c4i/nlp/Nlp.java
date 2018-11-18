package org.c4i.nlp;

import org.c4i.chitchat.api.model.LanguageProcessingConfig;
import org.c4i.nlp.detect.ConstantLanguageDetector;
import org.c4i.nlp.detect.LanguageDetector;
import org.c4i.nlp.detect.OpenLanguageDetector;
import org.c4i.nlp.generalize.FirstOfGeneralizer;
import org.c4i.nlp.generalize.Generalizer;
import org.c4i.nlp.generalize.RelatedWordsGeneralizer;
import org.c4i.nlp.match.*;
import org.c4i.nlp.ner.*;
import org.c4i.nlp.ner.DataSheet;
import org.c4i.nlp.normalize.*;
import org.c4i.nlp.normalize.kstem.KStem;
import org.c4i.nlp.normalize.kstem.KStemmer;
import org.c4i.nlp.tokenize.*;
import org.parboiled.common.FileUtils;
import org.parboiled.common.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.io.File;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Natural Language Processing resource manager.
 * @author Arvid Halma
 * @version 12-11-2017 - 15:02
 */
public class Nlp {
    private final Logger logger = LoggerFactory.getLogger(ScriptConfig.class);

    private File baseDir;

    private final Set<String> languages = new HashSet<>();
    private final Map<String, LanguageProcessingConfig> languageProcessing;
    private LanguageDetector languageDetector;

    private Substitution replyVariables;

    // Language to model
    private final HashMap<String, StopWords> stopWords = new HashMap<>();
    private final HashMap<String, StringNormalizer> stringNormalizers = new HashMap<>();
    private final HashMap<String, Tokenizer> tokenizers = new HashMap<>();
    private final HashMap<String, SentenceSplitter> sentenceSplitters= new HashMap<>();
    private final HashMap<String, List<EntityPlugin>> entityPlugins = new HashMap<>();
    private final HashMap<String, Generalizer> generalizers = new HashMap<>();
    private final HashMap<String, Substitution2Way> substitutions = new HashMap<>();


    public Nlp(File baseDir, Map<String, LanguageProcessingConfig> languageProcessing) {
        this.baseDir = baseDir;
        this.languageProcessing = languageProcessing;
        this.replyVariables = new Substitution1Way();
    }


    private void updateLanguageSupport(){
        languages.clear();
        File[] langDirs = baseDir.listFiles((current, name) -> new File(current, name).isDirectory());
        if(langDirs == null)
            return;
        for (File langDir : langDirs) {
            String lang = langDir.getName();
            if(languageProcessing == null || languageProcessing.containsKey(lang))
                languages.add(lang);
        }
    }

    private boolean hasFeature(String lang, String feature){
        if(languageProcessing == null)
            return true;
        LanguageProcessingConfig lpc = languageProcessing.get(lang);
        if(lpc == null)
            return true;

        return lpc.getFeatures().contains(feature);
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public void loadModels(){
        if(baseDir == null){
            return;
        }

        try {
            languageDetector = new OpenLanguageDetector(new File(baseDir, "langdetect-183.bin"), "en", 200);
        } catch (IOException e) {
            logger.error("Can't load language detector model: " + e.getMessage());
            languageDetector = new ConstantLanguageDetector("en");
        }

        updateLanguageSupport();

        for (String lang : languages) {
            File langDir = new File(baseDir, lang);

            // STRING NORMALIZERS
            stringNormalizers.put(lang, createNormalizer(lang));

            // STOPWORDS
            if(hasFeature(lang, "stopwords")) {
                try {
                    logger.warn("Loading [{}] stopwords ", lang);
                    StringNormalizer stringNormalizer = stringNormalizers.get(lang);
                    stopWords.put(lang, new StopWords(stringNormalizer,
                            new File(langDir, "stopwords_" + lang + ".txt")
                    ));
                } catch (IOException e) {
                    logger.error("Can't load stopwords for lang = " + lang);
                    stopWords.put(lang, StopWords.EMPTY_STOPWORDS);
                }
            } else {
                stopWords.put(lang, StopWords.EMPTY_STOPWORDS);
            }

            // TOKENIZERS
            if(hasFeature(lang, "tokenizer")) {
                try {
                    logger.warn("Loading [{}] tokenizer", lang);
                    tokenizers.put(lang, new OpenTokenizer(new File(langDir, lang + "-token.bin")));
                } catch (Exception e) {
                    tokenizers.put(lang, new MatchingWordTokenizer());
                }
            } else {
                final LanguageProcessingConfig lpc = languageProcessing.get(lang);
                boolean useSplitingWT = false;
                if(lpc != null){
                    if("splitting".equalsIgnoreCase(lpc.getWordTokenizer())){
                        useSplitingWT = true;
                    }
                }
                tokenizers.put(lang, useSplitingWT ? new SplittingWordTokenizer() : new MatchingWordTokenizer());
            }

            entityPlugins.put(lang, new ArrayList<>());

            // OPEN NLP NER
            File modelFile;
            if(hasFeature(lang, "ner") || hasFeature(lang, "ner-person")) {
                modelFile = new File(langDir, lang + "-ner-person.bin");
                try {
                    logger.info("Loading [{}] PERSON NER", lang);
                    entityPlugins.get(lang).add(new OpenNER("person", modelFile));
                } catch (IOException e) {
                    logger.warn("Can't load OpenNER: {}", modelFile);
                }
            }

            if(hasFeature(lang, "ner") || hasFeature(lang, "ner-date")) {
                modelFile = new File(langDir, lang + "-ner-date.bin");
                try {
                    logger.info("Loading [{}] DATE NER", lang);
                    entityPlugins.get(lang).add(new OpenNER("date", modelFile));
                } catch (IOException e) {
                    logger.warn("Can't load OpenNER: {}", modelFile);
                }
            }

            if(hasFeature(lang, "ner") || hasFeature(lang, "ner-time")) {
                modelFile = new File(langDir, lang + "-ner-time.bin");
                try {
                    logger.info("Loading [{}] TIME NER", lang);
                    entityPlugins.get(lang).add(new OpenNER("time", modelFile));
                } catch (IOException e) {
                    logger.warn("Can't load OpenNER: {}", modelFile);
                }
            }

            if(hasFeature(lang, "ner") || hasFeature(lang, "ner-location")) {
                modelFile = new File(langDir, lang + "-ner-location.bin");
                try {
                    logger.info("Loading [{}] LOCATION NER", lang);
                    entityPlugins.get(lang).add(new OpenNER("location", modelFile));
                } catch (IOException e) {
                    logger.warn("Can't load OpenNER: {}", modelFile);
                }
            }

            // DEFAULT PLUGINS
            entityPlugins.get(lang).add(new TrueFalse());
            entityPlugins.get(lang).add(new RegexFinder(RegexUtil.NUMBER, "NUMBER"));
            entityPlugins.get(lang).add(new RegexFinder(RegexUtil.EMAIL, "EMAIL"));
            entityPlugins.get(lang).add(new RegexFinder(RegexUtil.URL, "URL"));
            entityPlugins.get(lang).add(new RegexFinder(RegexUtil.PHONE, "PHONE"));
//            entityPlugins.get(lang).add(new RegexFinder(Pattern.compile("^[A-Z][a-z]{3,} [A-Z][a-zA-Z]{4,}$"), "REGEXNAME"));
            entityPlugins.get(lang).add(new EmoFinder());
            entityPlugins.get(lang).add(new CnnHeadlineFinder());
            entityPlugins.get(lang).add(new FirstLastNameFinder());


            // GENERALIZERS
            if(hasFeature(lang, "synonyms")) {
                logger.info("Loading [{}] synonyms", lang);
                modelFile = new File(langDir, lang + "-synonyms.csv");
                try {
                    generalizers.put(lang, new RelatedWordsGeneralizer(new RelatedWords(modelFile, getNormalizer(lang))));
                } catch (IOException e) {
                    logger.warn("Can't load synonyms: {}", modelFile);
                }
            }

            try {
                entityPlugins.get(lang).add(new DateTimeFinder(new File(langDir, "DATETIME.csv" ), getNormalizer(lang), getWordTokenizer(lang)));
            } catch (IOException ignored) {}

            // SUBSTITUTIONS
            if("en".equals(lang)){
                Map<String, String> substMap = new HashMap<>();
                substMap.put("am", "are");
                substMap.put("was", "were");
                substMap.put("i", "you");
                substMap.put("i'd", "you would");
                substMap.put("i've", "you have");
                substMap.put("i'll", "you will");
                substMap.put("my", "your");
                substMap.put("are", "am");
                substMap.put("you've", "I have");
                substMap.put("you'll", "I will");
                substMap.put("your", "my");
                substMap.put("yours", "mine");
                substMap.put("you", "me");
                substMap.put("me", "you");
                substitutions.put(lang, new Substitution2Way(substMap, true));
            } else {
                substitutions.put(lang, new Substitution2Way());
            }

        }

    }

    /**x
     * List of datasheet names: e.g. "en/CITY" (with lang folder, without extension)
     * @param lang language
     * @return list of names in /data/nlp/{lang}/datasheet
     */
    public List<String> getFileDataSheetNames(String lang){
        List<String> names = new ArrayList<>();
        File langDir = new File(baseDir, lang);

        if(hasFeature(lang, "datasheet")) {
            File[] datasheets = new File(langDir, "datasheet").listFiles((dir, name) -> name.endsWith(".csv"));
            if (datasheets != null) {
                for (File nerFile : datasheets) {
                    String name = nerFile.getName();
                    if (name.contains("."))
                        name = name.substring(0, name.lastIndexOf('.'));

                    names.add(lang + "/" + name);
                }
            }
        }
        return names;
    }

    public String loadFileDataSheet(String name){
        File file = new File(baseDir, name.replaceFirst("/", "/datasheet/") + ".csv");
        String data = FileUtils.readAllText(file, Charset.forName("UTF-8"));
        loadDataSheet(name, data);
        return data;
    }

    public void loadDataSheet(String name, String data){
        String[] parts = name.split("/");
        if(parts.length != 2) {
            logger.error("The data sheet with name '{}' is skipped, since it does not follow the  'LANG/name' format.", name);
            return;
        }
        String lang = parts[0];
        String sheet = parts[1];

        entityPlugins.putIfAbsent(lang, new ArrayList<>());

        StopWords stopWords = StopWords.EMPTY_STOPWORDS;
        try {
            logger.warn("Loading datasheet '{}'", name);
            List<EntityPlugin> langPlugins = entityPlugins.get(lang);
            // remove old version
            langPlugins.removeIf(plugin -> sheet.equals(plugin.description()));
            // add new version
            langPlugins.add(new DataSheet(sheet, data, stringNormalizers.get(lang), new SplittingWordTokenizer(), w -> !stopWords.isStopWord(w)));
        } catch (IOException e) {
            logger.error("Can't load datasheet: {}", name);
        }
    }


   /* public void loadDataSheets(String lang) {
        File langDir = new File(baseDir, lang);
        // CUSTOM DATA SETS
        entityPlugins.put(lang, new ArrayList<>());
        if(hasFeature(lang, "datasheet")) {
            File[] datasheets = new File(langDir, "datasheet").listFiles();
            if (datasheets != null) {
            if (datasheets != null) {
                for (File nerFile : datasheets) {
                    String name = nerFile.getName();
                    if (name.contains("."))
                        name = name.substring(0, name.lastIndexOf('.'));

                    //StopWords stopWords = this.stopWords.get(lang);
                    StopWords stopWords = StopWords.EMPTY_STOPWORDS;
                    try {
                        logger.warn("Loading [{}] datasheet '{}'", lang, name);
                        entityPlugins.get(lang).add(new DataSheet(name, nerFile, stringNormalizers.get(lang), new SplittingWordTokenizer(), w -> !stopWords.isStopWord(w)));
                    } catch (IOException e) {
                        logger.error("Can't load datasheet: {}", nerFile);
                    }
                }
            }
        }
    }*/

    public Substitution getReplyVariables() {
        return replyVariables;
    }

    public Nlp setReplyVariables(Substitution replyVariables) {
        this.replyVariables = replyVariables;
        return this;
    }

    public HashMap<String, StopWords> getStopWords() {
        return stopWords;
    }

    public HashMap<String, StringNormalizer> getNormalizers() {
        return stringNormalizers;
    }

    public HashMap<String, Tokenizer> getTokenizers() {
        return tokenizers;
    }

    public HashMap<String, SentenceSplitter> getSentenceDetectors() {
        return sentenceSplitters;
    }

    public HashMap<String, List<EntityPlugin>> getEntityPlugins() {
        return entityPlugins;
    }

    public List<EntityPlugin> getEntityPlugins(ScriptConfig config){
        return getEntityPlugins(config.getLanguages());
    }

    public List<EntityPlugin> getEntityPlugins(List<String> langs){
        List<EntityPlugin> result = new ArrayList<>();
        for (String lang : langs) {
            result.addAll(entityPlugins.getOrDefault(lang, ImmutableList.of()));
        }
        return result;
    }

    public Generalizer getGeneralizer(String lang){
        return generalizers.getOrDefault(lang, Generalizer.DEFAULT);
    }

    public Generalizer getGeneralizer(ScriptConfig config){
        return getGeneralizer(config.getLanguages());
    }

    public Generalizer getGeneralizer(List<String> langs){
        FirstOfGeneralizer generalizer = new FirstOfGeneralizer();
        for (String lang : langs) {
            generalizer.add(getGeneralizer(lang));
        }
        return generalizer;
    }


    public StopWords getStopWords(String lang){
        return stopWords.getOrDefault(lang, StopWords.EMPTY_STOPWORDS);
    }

    public Map<String, StopWords> getStopWords(ScriptConfig config){
        return getStopWords(config.getLanguages());
    }

    public Map<String, StopWords> getStopWords(List<String> langs){
        Map<String, StopWords> result = new HashMap<>();
        for (String lang : langs) {
            result.put(lang, getStopWords(lang));
        }
        return result;
    }

    public Substitution2Way getSubstitution(String lang){
        return substitutions.getOrDefault(lang, new Substitution2Way());
    }

    public Substitution2Way getSubstitution(ScriptConfig config){
        for (String lang : config.getLanguages()) {
            if(substitutions.containsKey(lang)){
                return substitutions.get(lang);
            }
        }
        return new Substitution2Way();
    }

    public StringNormalizer getNormalizer(ScriptConfig config){
        return getNormalizer(config.getLanguages());
    }

    public StringNormalizer getNormalizer(String lang){
        return stringNormalizers.getOrDefault(lang, StringNormalizers.DEFAULT);
    }

    private StringNormalizer getNormalizer(List<String> langs){
        // Compose normalizers by language
        StringNormalizer result = StringNormalizer.IDENTITY;
        for (String lang : langs) {
            result = result.andThen(getNormalizer(lang));
        }
        return result;
    }

    private StringNormalizer createNormalizer(String lang){
        // Compose normalizers by type
        LanguageProcessingConfig lpc = languageProcessing.get(lang);
        if(lpc == null) {
            return StringNormalizers.DEFAULT;
        }
        StringNormalizer result = StringNormalizer.IDENTITY;
        for (String name : lpc.getNormalizers()) {
            if("default".equals(name)){
                result = result.andThen(StringNormalizers.DEFAULT);
            } else if("lowercase".equals(name)){
                result = result.andThen(StringNormalizers.LOWER_CASE);
            } else if("removeaccents".equals(name)){
                result = result.andThen(StringNormalizers.NO_ACCENTS);
            } else if("alphanumeric".equals(name)){
                result = result.andThen(StringNormalizers.ALPHA_NUM_ONLY);
            } else if("stem".equals(name)){
                StringNormalizer stemmerNomalizer = StringNormalizer.IDENTITY;

                try {
                    stemmerNomalizer = new LuceneStemmer(lang, false).compose(stemmerNomalizer);
                } catch (IllegalArgumentException e){
                    try {
                        stemmerNomalizer = new SnowballStemmer(lang).compose(stemmerNomalizer);
                    } catch (IllegalArgumentException ignored){}
                }

                result = result.andThen(stemmerNomalizer);
            } else if("stemlight".equals(name)){
                StringNormalizer stemmerNomalizer = StringNormalizer.IDENTITY;

                try {
                    stemmerNomalizer = new LuceneStemmer(lang, true).compose(stemmerNomalizer);
                } catch (IllegalArgumentException e){
                    try {
                        stemmerNomalizer = new SnowballStemmer(lang).compose(stemmerNomalizer);
                    } catch (IllegalArgumentException ignored){}
                }

                result = result.andThen(stemmerNomalizer);
            } else if ("kstem".equals(name)) {
                result = result.andThen(new KStem());
            } else if ("porter".equals(name)) {
                result = result.andThen(new PorterStemmer());
            } else if ("metaphone".equals(name)) {
                result = result.andThen(new Metaphone());
            } else if ("metaphone2".equals(name)) {
                result = result.andThen(new Metaphone2());
            } else if ("metaphone3".equals(name)) {
                result = result.andThen(new Metaphone3());
            } else if ("metaphone3long".equals(name)) {
                result = result.andThen(new Metaphone3Long(4));
            } else if ("sortinner".equals(name)) {
                result = result.andThen(StringNormalizers.SORTED_WITHIN);
            }
        }
        return result;
    }

    public SentenceSplitter getSentenceSplitter(ScriptConfig config){
        return sentenceSplitters.getOrDefault(config.getLanguages().get(0), new RegexSentenceSplitter());
    }

    public SentenceSplitter getSentenceSplitter(String lang){
        return sentenceSplitters.getOrDefault(lang, new RegexSentenceSplitter());
    }

    public Tokenizer getWordTokenizer(ScriptConfig config){
        return getWordTokenizer(config.getLanguages().get(0));
    }

    public Tokenizer getWordTokenizer(String lang){
        LanguageProcessingConfig lpc = languageProcessing.get(lang);
        if(lpc == null){
            return new MatchingWordTokenizer();
        }
        switch (lpc.getWordTokenizer()){
            case "splitting": return new SplittingWordTokenizer();
            case "default": return tokenizers.getOrDefault(lang, new MatchingWordTokenizer());
            default: return new MatchingWordTokenizer(); // "matching"
        }
    }

    public Map<String, Map<String, Object>> info(){
        Map<String, Map<String, Object>> result = new HashMap<>();
        for (String lang : languages) {
            String displayLanguage = Locale.forLanguageTag(lang).getDisplayLanguage();
            final List<String> features = languageProcessing.get(lang).getFeatures();
            LinkedHashMap<String, Object> modelInfo = new LinkedHashMap<>();
            modelInfo.put("language code", lang);
            modelInfo.put("enabled features", features.isEmpty() ? "ALL" : features);
            modelInfo.put("word normalization", languageProcessing.get(lang).getNormalizers());
            modelInfo.put("word tokenizer", tokenizers.get(lang).description());
            modelInfo.put("sentence splitter", getSentenceSplitter(lang).description());
            modelInfo.put("stop words", stopWords.get(lang).description());
            modelInfo.put("entity recognition", entityPlugins.get(lang).stream().map(EntityPlugin::description).collect(Collectors.toList()));
            result.put(displayLanguage, modelInfo);
        }
        return result;
    }
}
