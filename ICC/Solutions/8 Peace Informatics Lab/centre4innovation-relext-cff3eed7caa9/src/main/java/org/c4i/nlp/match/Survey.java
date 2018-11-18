package org.c4i.nlp.match;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A list of {@link Question}s
 * @version Arvid Halma
 */
public class Survey {
    public LinkedHashMap<String, Question> questions;

    @Override
    public String toString() {
        return questions.toString();
    }
}
