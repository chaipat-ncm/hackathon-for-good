package org.c4i.nlp.match;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single answer in the context of Multiple choice {@link Question}s/.
 * @version Arvid Halma
 */
public class Answer {
    @JsonProperty
    public String text;

    @JsonProperty("continue")
    public String kontinue;

    @Override
    public String toString() {
        return "Answer{" +
                "text='" + text + '\'' +
                ", continue='" + kontinue + '\'' +
                '}';
    }
}
