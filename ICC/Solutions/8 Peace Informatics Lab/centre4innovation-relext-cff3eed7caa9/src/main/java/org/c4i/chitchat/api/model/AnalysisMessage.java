package org.c4i.chitchat.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.c4i.nlp.match.Range;
import org.c4i.util.Timestamped;
import org.joda.time.DateTime;

import java.util.List;

/**
 * A container for messages resulting from batch analysis.
 * @author Arvid Halma, Wouter Eekhout
 */
public class AnalysisMessage implements Timestamped {
    @JsonProperty
    DateTime timestamp;
    @JsonProperty
    String sender;
    @JsonProperty
    String text;
    @JsonProperty
    List<Range> matches;

    public AnalysisMessage() {
    }

    public AnalysisMessage(DateTime timestamp, String sender, String text, List<Range> matches) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.text = text;
        this.matches = matches;
    }

    @Override
    public DateTime getTimestamp() {
        return timestamp;
    }

    public AnalysisMessage setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getSender() {
        return sender;
    }

    public AnalysisMessage setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public String getText() {
        return text;
    }

    public AnalysisMessage setText(String text) {
        this.text = text;
        return this;
    }

    public List<Range> getMatches() {
        return matches;
    }

    public AnalysisMessage setMatches(List<Range> matches) {
        this.matches = matches;
        return this;
    }
}
