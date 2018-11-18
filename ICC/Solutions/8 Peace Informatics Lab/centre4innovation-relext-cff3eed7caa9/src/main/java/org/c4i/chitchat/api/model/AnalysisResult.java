package org.c4i.chitchat.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.c4i.util.TimeValue;

import java.util.List;
import java.util.Map;

/**
 * A container for batch analysis results.
 * @author Arvid Halma, Wouter Eekhout
 */
public class AnalysisResult {
    @JsonProperty
    Map<String, Double> histogram;
    @JsonProperty
    List<AnalysisMessage> messages;
    @JsonProperty
    Map<String, List<TimeValue>> timeLines;

    public AnalysisResult() {
    }

    public AnalysisResult(Map<String, Double> histogram, List<AnalysisMessage> messages, Map<String, List<TimeValue>> timeLines) {
        this.messages = messages;
        this.histogram = histogram;
        this.timeLines = timeLines;
    }

    public Map<String, Double> getHistogram() {
        return histogram;
    }

    public AnalysisResult setHistogram(Map<String, Double> histogram) {
        this.histogram = histogram;
        return this;
    }

    public List<AnalysisMessage> getMessages() {
        return messages;
    }

    public AnalysisResult setMessages(List<AnalysisMessage> messages) {
        this.messages = messages;
        return this;
    }

    public Map<String, List<TimeValue>> getTimeLines() {
        return timeLines;
    }

    public AnalysisResult setTimeLines(Map<String, List<TimeValue>> timeLines) {
        this.timeLines = timeLines;
        return this;
    }
}
