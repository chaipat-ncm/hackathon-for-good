package org.c4i.nlp.match;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;

/**
 * Multiple choice question for {@link Survey}s.
 * @version Arvid Halma
 */
public class Question {
    @JsonProperty
    public String text;

    @JsonProperty
    public LinkedHashMap<String, Answer> answers;

    @Override
    public String toString() {
        return "Question{" +
                "text='" + text + '\'' +
                ", answers=" + answers +
                '}';
    }
}
