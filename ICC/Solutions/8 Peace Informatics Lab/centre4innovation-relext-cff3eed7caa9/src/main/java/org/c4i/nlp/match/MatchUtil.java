package org.c4i.nlp.match;

import org.c4i.nlp.normalize.StringNormalizer;
import org.c4i.nlp.normalize.StringNormalizers;
import org.c4i.nlp.tokenize.*;
import org.c4i.util.ArrayUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * One-stop shop for matching phrase patterns to texts.
 * @version 8-11-16
 * @author Arvid Halma
 */
public class MatchUtil {

    public static final Pattern PARAGRAPH_SPLITTER = Pattern.compile("( *(.+?)( *(\n( *\n)+)|\\z))");

    /**
     * Tokenizes a text using {@link MatchingWordTokenizer}.
     * @param text original text
     * @param normalizer how words are preprocessed
     * @param tokenizer how words are extracted
     * @return an array of separate 'words'
     */
    public static Token[] textToTokens(String text, StringNormalizer normalizer, Tokenizer tokenizer){
        List<Token> textTokens = tokenizer.tokenize(text);
        Token[] tokens = textTokens.toArray(new Token[0]);
        normalizer.normalizeTokens(tokens);
        return tokens;
    }

    /**
     * Tokenizes a text using {@link MatchingWordTokenizer}.
     * @param text original text
     * @param normalizer how words are preprocessed
     * @return an array of separate 'words'
     */
    public static Token[] textToTokens(String text, StringNormalizer normalizer){
        return textToTokens(text, normalizer, new MatchingWordTokenizer());
    }

    /**
     * Tokenizes a text into sentences.
     * @param text original text
     * @param normalizer how words are preprocessed
     * @param tokenizer word splitter
     * @return an array of separate 'words', grouped by sentence
     */
    public static Token[][] textToSentenceTokens(
            final String text,
            final StringNormalizer normalizer,
            final Tokenizer tokenizer,
            final SentenceSplitter sentenceSplitter)
    {
        final String[] sents = sentenceSplitter.split(text);
        final List<Token[]> resultList = new ArrayList<>();

        int charOffset = 0;
        int tokenOffset = 0;

        for (int i = 0; i < sents.length; i++) {
            String sent = sents[i];
            List<Token> textTokens = tokenizer.tokenize(sent);
            Token[] tokens = textTokens.toArray(new Token[0]);
            normalizer.normalizeTokens(tokens);

            for (Token token : tokens) {
                token.addCharOffset(charOffset);
                token.addTokenOffset(tokenOffset);
                token.setSection(i);
            }
            charOffset += sent.length() + 1; // newline
            tokenOffset += tokens.length;

            if (tokens.length > 0) {
                resultList.add(tokens);
            }
        }
        return resultList.toArray(new Token[0][]);
    }

    /**
     * Tokenizes a text and groups by sentence and paragraph.
     * @param text original text
     * @param normalizer how words are preprocessed
     * @param tokenizer word splitter
     * @return an array paragraphs/sentences/words
     */
    public static Token[][][] textToSectionTokens(
            final String text,
            final StringNormalizer normalizer,
            final Tokenizer tokenizer,
            final SentenceSplitter sentenceSplitter)
    {
        Matcher matcher = PARAGRAPH_SPLITTER.matcher(text);

        List<Token[][]> pars = new ArrayList<>();
        int tokenOffset = 0;
        int section = 0;
        while (matcher.find()) {
            final int charOffset = matcher.start();
            final Token[][] tokens = textToSentenceTokens(matcher.group(2), normalizer, tokenizer, sentenceSplitter);
            int t = 0;
            for (Token[] sent : tokens) {
                for (Token token : sent) {
                    token.addCharOffset(charOffset);
                    token.addTokenOffset(tokenOffset);
                    token.setSection(section);
                    t++;
                }
            }
            tokenOffset += t;
            section++;
            pars.add(tokens);

        }
        return pars.toArray(new Token[0][][]);
    }

    /**
     * Tokenizes a text into sentences. Tokens have a relevant section field (paragraph  index).
     * @param text original text
     * @param normalizer how words are preprocessed
     * @param tokenizer word splitter
     * @return an array of separate 'words', grouped by sentence
     */
    public static Token[][] textToSentenceTokensWithSections(
            final String text,
            final StringNormalizer normalizer,
            final Tokenizer tokenizer,
            final SentenceSplitter sentenceSplitter)
    {
        final Token[][][] sections = textToSectionTokens(text, normalizer, tokenizer, sentenceSplitter);

        int totalLength = 0;
        for (Token[][] sents : sections) {
            totalLength += sents.length;
        }
        final Token[][] result = new Token[totalLength][];
        int offset = 0;
        for (Token[][] sents : sections) {
            System.arraycopy(sents, 0, result, offset, sents.length);
            offset += sents.length;
        }
        return result;
    }

    public static int lastSectionIx(Token[][] sents){
        int n = sents.length;
        if(n == 0){
            return 0;
        }
        return sents[n - 1][0].getSection();
    }

    public static Token[][] selectSection(Token[][] sents, int section){
        int n = sents.length;
        if(n == 0){
            return new Token[0][];
        }
        List<Token[]> result = new ArrayList<>();
        for (int i = n - 1; i >= 0; i--) {
            Token[] sent = sents[i];
            if(sent.length > 0 && sent[0].getSection() == section){
                result.add(sent);
            }
        }
        Collections.reverse(result);
        return result.toArray(new Token[0][]);
    }

    public static Token[][] selectLastSection(Token[][] sents){
        return selectSection(sents, lastSectionIx(sents));
    }

    /**
     * Tokenizes a text using {@link MatchingWordTokenizer}.
     * Words are normalized with {@link StringNormalizers#DEFAULT}.
     * @param text original text
     * @return an array of separate 'words'
     */
    public static Token[] textToTokens(String text){
        return textToTokens(text, StringNormalizers.DEFAULT);
    }

    /**
     * Compiles a phrase rule to a form that can efficiently be reused when matching.
     * @param pattern a phrase expression
     * @param normalizer how words are preprocessed
     * @return an expression in Conjunctive Normal Form
     */
    public static Literal[][] compilePattern(String pattern, StringNormalizer normalizer){
        return Compiler.compileBody(pattern, true, normalizer);
    }

    /**
     * Compiles a phrase rule to a form that can efficiently be reused when matching.
     * @param pattern a phrase expression
     * @return an expression in Conjunctive Normal Form
     */
    public static Literal[][] compilePattern(String pattern){
        return Compiler.compileBody(pattern, true, StringNormalizers.DEFAULT);
    }

    /**
     * Tries to finds the first possible occurrence of the pattern in the text.
     * @param text original text
     * @param rule a phrase expression
     * @return a array of length 2: {token_start_index_inclusive, token_end_index_exclusive}, or null when the pattern was not found
     */
    public static List<Range> findRange(final Token[] text, final LabelRule rule){
        return new Eval(null).findRule(text, rule, null, null);
    }

    /**
     * Tries to finds the first possible occurrence of the pattern in the text.
     * @param text original text
     * @param rule a phrase expression
     * @return a array of length 2: {token_start_index_inclusive, token_end_index_exclusive}, or null when the pattern was not found
     */
    public static List<Range> findRange(final String text, final LabelRule rule){
        return new Eval(null).findRule(textToTokens(text), rule, null, null);
    }

    /**
     * Tries to finds the first possible occurrence of the pattern in the text.
     * @param text original text
     * @param pattern a phrase expression
     * @return a array of length 2: {token_start_index_inclusive, token_end_index_exclusive}, or null when the pattern was not found
     */
    public static List<Range> findRange(final String text, final String pattern){
        return new Eval(null).findRule(textToTokens(text), new LabelRule(null, compilePattern(pattern)), null, null);
    }

    /**
     * Tries to finds the first possible occurrence of the pattern in the text.
     * @param text original text
     * @param pattern a phrase expression
     * @param normalizer how words are preprocessed and compared
     * @return a array of length 2: {token_start_index_inclusive, token_end_index_exclusive}, or null when the pattern was not found
     */
    public static List<Range> findRange(final String text, final String pattern, final StringNormalizer normalizer){
        return new Eval(null).findRule(textToTokens(text, normalizer), new LabelRule(null, compilePattern(pattern, normalizer)), null, null);
    }

    /**
     * Checks whether a given pattern occurs in the text.
     * @param text the content to be queried
     * @param pattern the expression to look for
     * @return true if the pattern is found, false otherwise
     */
    public static boolean contains(String text, String pattern){
        Token[] tokens = textToTokens(text);
        return contains(tokens, pattern, StringNormalizers.DEFAULT);
    }

    /**
     * Checks whether a given pattern occurs in the text.
     * @param text the content to be queried
     * @param pattern the expression to look for
     * @param normalizer how words are preprocessed and compared
     * @return true if the pattern is found, false otherwise
     */
    public static boolean contains(String text, String pattern, StringNormalizer normalizer){
        Token[] tokens = textToTokens(text, normalizer);
        return contains(tokens, pattern, normalizer);
    }

    /**
     * Checks whether a given pattern occurs in the text.
     * @param text the content to be queried
     * @param pattern the expression to look for
     * @param normalizer how words are preprocessed and compared
     * @return true if the pattern is found, false otherwise
     */
    public static boolean contains(Token[] text, String pattern, StringNormalizer normalizer){
        Literal[][] cnf = compilePattern(pattern, normalizer);
        return new Eval(null).contains(text, new LabelRule(null, cnf));
    }

    /**
     * Checks whether a given pattern occurs in the text.
     * @param text the content to be queried
     * @param pattern the expression to look for
     * @param normalizer how words are preprocessed and compared
     * @return true if the pattern is found, false otherwise
     */
    public static boolean contains(String text, Literal[][] pattern, StringNormalizer normalizer){
        Token[] tokens = textToTokens(text, normalizer);
        return contains(tokens, pattern);
    }

    /**
     * Checks whether a given pattern occurs in the text.
     * @param text the content to be queried
     * @param pattern the expression to look for
     * @return true if the pattern is found, false otherwise
     */
    public static boolean contains(Token[] text, Literal[][] pattern){
        return new Eval(null).contains(text, new LabelRule(null, pattern));
    }

    /**
     * Checks whether at least one of the given patterns occurs in the text.
     * @param text the content to be queried
     * @param patterns the expression to look for
     * @return true if the pattern is found, false otherwise
     */
    public static boolean containsAny(Token[] text, List<Literal[][]> patterns){
        return patterns.stream().anyMatch(p -> contains(text, p));
    }

    /**
     * Checks whether all of the given patterns occurs in the text.
     * @param text the content to be queried
     * @param patterns the expression to look for
     * @return true if the pattern is found, false otherwise
     */
    public static boolean containsAll(Token[] text, List<Literal[][]> patterns){
        return patterns.stream().allMatch(p -> contains(text, p));
    }

}
