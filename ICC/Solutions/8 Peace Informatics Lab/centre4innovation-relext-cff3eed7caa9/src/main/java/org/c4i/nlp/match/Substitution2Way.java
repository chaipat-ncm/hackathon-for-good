package org.c4i.nlp.match;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * A collection of string substitutions.
 * All replacements will be applied and special care is taken, that
 * replacements a to b, and b to a don't cancel each other out.
 * @author Arvid Halma
 */
public class Substitution2Way implements Substitution {

    private static final String marker = "xyzyx";

    private Map<Pattern, String> patternSubst;

    public Substitution2Way() {
        patternSubst= ImmutableMap.of();
    }

    public Substitution2Way(Map<String, String> rewrites, boolean caseInsensitive) {
        // don't undo previous replacement you -> me -> you. Use unique, but alphabetic, marker
        patternSubst = new HashMap<>();
        for (Map.Entry<String, String> entry : rewrites.entrySet()) {
           patternSubst.put(Pattern.compile("\\b"+entry.getKey()+"\\b", caseInsensitive ? Pattern.CASE_INSENSITIVE : 0), marker+entry.getValue());
        }
    }

    public String apply(String text){
        for (Map.Entry<Pattern, String> entry: patternSubst.entrySet()) {
            text = entry.getKey().matcher(text).replaceAll(entry.getValue());
        }
        return text.replaceAll(marker, "");
    }

    @Override
    public Map<String, String> asMap() {
        return null;
    }
}
