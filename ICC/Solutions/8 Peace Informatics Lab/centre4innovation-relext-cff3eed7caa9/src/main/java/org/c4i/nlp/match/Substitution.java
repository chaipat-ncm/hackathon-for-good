package org.c4i.nlp.match;

import java.util.Map;
import java.util.function.Function;

/**
 * Applies a series of replacements to a {@link String}.
 * @author Arvid Halma
 */
public interface Substitution extends Function<String, String> {
    String apply(String text);

    Map<String, String> asMap();
}
