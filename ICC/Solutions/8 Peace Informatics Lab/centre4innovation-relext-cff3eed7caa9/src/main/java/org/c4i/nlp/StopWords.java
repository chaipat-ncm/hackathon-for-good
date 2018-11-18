package org.c4i.nlp;

import org.c4i.nlp.normalize.StringNormalizer;
import org.c4i.nlp.tokenize.MatchingWordTokenizer;
import org.c4i.nlp.tokenize.Token;
import org.c4i.util.LineParser;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Stop word removal.
 * @author Arvid
 * @version 6-4-2015 - 11:58
 */
public class StopWords implements StringNormalizer, WordTest{
    private Set<String> stopWords;
    private StringNormalizer normalizer;
    public final static StopWords EMPTY_STOPWORDS = new StopWords();

    private StopWords(){
        this.stopWords = Collections.emptySet();
        this.normalizer = StringNormalizer.IDENTITY;
    }

    public StopWords(StringNormalizer normalizer, File stopWordFile) throws IOException {
        this.normalizer = normalizer;
        this.stopWords = new HashSet<>();
        LineParser.lines(line -> {
            String nword = normalizer.normalize(line);
            if (!nword.isEmpty())
                stopWords.add(nword);
        }, stopWordFile);
    }

    public StopWords(StringNormalizer normalizer, Set<String> stopWords){
        this.stopWords = stopWords;
        this.normalizer = normalizer;
    }

    public Set<String> getStopWords() {
        return stopWords;
    }

    @Override
    public boolean test(String word) {
        return !isStopWord(word);
    }

    public boolean isStopWord(String word){
        String nword = normalizer.normalize(word);
        return stopWords.contains(nword);
    }

    public boolean isStopWord(Token token){
        String nword = token.isNormalized() ? token.getNormalizedWord() : normalizer.normalize(token.getWord());
        return stopWords.contains(nword);
    }

    public List<Boolean> isStopWord(Collection<String> words){
        return words.stream().map(this::isStopWord).collect(Collectors.toList());
    }

    public List<Boolean> isStopWordTokens(Collection<Token> tokens){
        return tokens.stream().map(this::isStopWord).collect(Collectors.toList());
    }

    public Predicate<String> stopWordPredicate(){
        return this::isStopWord;
    }

    public Collection<String> filter(Collection<String> words){
        words.removeIf(this::isStopWord);
        return words;
    }

    public List<String> filter(List<String> words){
        words.removeIf(this::isStopWord);
        return words;
    }

    public Collection<Token> filterTokens(Collection<Token> words){
        words.removeIf(this::isStopWord);
        return words;
    }

    public List<Token> filterTokens(List<Token> words){
        words.removeIf(this::isStopWord);
        return words;
    }

    @Override
    public String normalize(String string) {
        return new MatchingWordTokenizer().tokenize(string).stream()
                .filter(not(this::isStopWord)).map(Token::getWord).collect(Collectors.joining(" "));
    }

    public static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }

    public String description(){
        return stopWords.size() + " stop words";
    }

    @Override
    public String toString() {
        return "StopWords" + stopWords;
    }
}
