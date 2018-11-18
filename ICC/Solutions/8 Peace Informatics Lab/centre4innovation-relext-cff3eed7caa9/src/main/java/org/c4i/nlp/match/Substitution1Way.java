package org.c4i.nlp.match;

import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A collection of string substitutions.
 * All replacements will be applied, with the assumption that the replacement values are not also keys.
 * see: https://stackoverflow.com/questions/1326682/java-replacing-multiple-different-substring-in-a-string-at-once-or-in-the-most
 * @author Arvid Halma
 */
public class Substitution1Way implements Substitution {
    private Pattern pattern;


    private Map<String, String> rewrites;

    public Substitution1Way() {
        rewrites = ImmutableMap.of();
    }

    public Substitution1Way(String tsv, boolean caseInsensitive) {
        final Map<String, String> rewrites = new LinkedHashMap<>();
        Arrays.stream(tsv.split("\n"))
                .map(line -> line.split("\t"))
                .filter(row -> row.length > 1)
                .forEach(row -> rewrites.put(row[0].trim(), row[1].trim()));
        setRewrites(rewrites, caseInsensitive);

    }

    public Substitution1Way(Map<String, String> rewrites, boolean caseInsensitive) {
        setRewrites(rewrites, caseInsensitive);
    }

    private void setRewrites(Map<String, String> rewrites, boolean caseInsensitive){
        this.rewrites = rewrites;
        if(!rewrites.isEmpty()) {
            String patternString = rewrites.keySet().stream().collect(Collectors.joining("\\E|\\Q", "\\$(\\Q", "\\E)\\b"));
            pattern = Pattern.compile(patternString, caseInsensitive ? Pattern.CASE_INSENSITIVE : 0);
        }
    }

    @Override
    public String apply(String text){
        if(rewrites.isEmpty()) {
            return text;
        }
        Matcher matcher = pattern.matcher(text);

        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            matcher.appendReplacement(sb, Matcher.quoteReplacement(rewrites.get(matcher.group(1))));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    @Override
    public Map<String, String> asMap() {
        return rewrites;
    }
}
