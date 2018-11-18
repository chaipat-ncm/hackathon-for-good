package org.c4i.nlp.match;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Combined default properties for label and reply rules.
 * @author Arvid Halma
 * @version 14-2-18
 */
public class RuleProperties {
    @JsonProperty
    Map<String,Object> reply = new HashMap<>();
    @JsonProperty
    Map<String,Object> label = new HashMap<>();

    public RuleProperties() {
    }

    public Map<String, Object> getReply() {
        return reply;
    }

    public RuleProperties setReply(Map<String, Object> reply) {
        this.reply = reply;
        return this;
    }

    public Map<String, Object> getLabel() {
        return label;
    }

    public RuleProperties setLabel(Map<String, Object> label) {
        this.label = label;
        return this;
    }

    @Override
    public String toString() {
        return "RuleProperties{" +
                "reply=" + reply +
                ", label=" + label +
                '}';
    }
}
