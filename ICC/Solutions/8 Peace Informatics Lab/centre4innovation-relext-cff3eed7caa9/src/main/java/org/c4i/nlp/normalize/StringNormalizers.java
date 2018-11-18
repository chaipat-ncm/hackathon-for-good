package org.c4i.nlp.normalize;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.regex.Pattern;

import static java.text.Normalizer.Form;
import static java.text.Normalizer.normalize;

/**
 * Collection of common string normalizations.
 * @author Arvid Halma
 * @version 9-4-2015 - 20:46
 */
public class StringNormalizers {

    private static final Pattern IN_COMBINING_DIACRITICAL_MARKS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final Pattern BETWEEN_BRACKETS_PATTERN = Pattern.compile("(\\(.*?\\))|(\\[.*?\\])|(\\{.*?\\})");
    private static final Pattern NON_ALPHA_NUM_PATTERN = Pattern.compile("[^\\p{L}\\p{M}0-9:]"); //todo: semicolon as surprising exception, because of :emoticon:
    private static final Pattern NON_ALPHA_NUM_SPACE_PATTERN = Pattern.compile("[^\\p{L}\\p{M}0-9 :]"); //todo: semicolon as surprising exception, because of :emoticon:


    /**
     * Case invariance.
     */
    public static StringNormalizer LOWER_CASE = String::toLowerCase;

    /**
     * Remove space chars at start and end of a string.
     */
    public static StringNormalizer TRIMMED = String::trim;

    /**
     * Remove enclosing brackets: (), [], {}
     */
    public static StringNormalizer NO_BRACKETS = s -> BETWEEN_BRACKETS_PATTERN.matcher(s).replaceAll("");

    /**
     * Unicode is difficult. Make sure accents etc. match.
     */
    public static StringNormalizer UNICODE = s -> Normalizer.normalize(s, Form.NFD);

    /**
     * Remove accents from words: e.g. café -&gt; cafe
     */
    public static StringNormalizer NO_ACCENTS = s -> IN_COMBINING_DIACRITICAL_MARKS_PATTERN.matcher(UNICODE.normalize(s)).replaceAll("");

//    public static StringNormalizer IDENTITY = s -> NO_ACCENTS.normalize(NON_ALPHA_NUM_PATTERN.matcher(s.trim()).replaceAll("").toLowerCase());

    /**
     * Remove all non alpha-numeric character (except semicolon).
     */
    public static StringNormalizer ALPHA_NUM_ONLY = s -> NON_ALPHA_NUM_SPACE_PATTERN.matcher(s).replaceAll("");

    /**
     * Sensible default: lowercase, no spaces at start/end, no accents, no strange chars
     */
    public static StringNormalizer DEFAULT = TRIMMED.andThen(ALPHA_NUM_ONLY).andThen(LOWER_CASE).andThen(UNICODE).andThen(NO_ACCENTS);


    /**
     * Porter stemmer
     */
    public static StringNormalizer STEMMED = new PorterStemmer();

    /**
     * Apply default normalizer, then Porter stemmer.
     */
    public static StringNormalizer DEFAULT_STEMMED = STEMMED.compose(DEFAULT);

    /**
     * Some robustness against typos: ignore char swaps
     */
    public static StringNormalizer SORTED = s -> {
        // no collation stuff, assumed to be normalized before
        char[] cs = s.toCharArray();
        Arrays.sort(cs);
        return String.valueOf(cs);
    };

    /**
     * Some robustness against typos: ignore char swaps, except the first and last character.
     * Inspiration: https://www.mrc-cbu.cam.ac.uk/people/matt.davis/cmabridge/
     */
    public static StringNormalizer SORTED_WITHIN = s -> {
        // no collation stuff, assumed to be normalized before
        int n = s.length();
        if(n <= 3)
            return s;
        char[] cs = s.toCharArray();
        Arrays.sort(cs, 1, n - 2);
        return String.valueOf(cs);
    };




}
