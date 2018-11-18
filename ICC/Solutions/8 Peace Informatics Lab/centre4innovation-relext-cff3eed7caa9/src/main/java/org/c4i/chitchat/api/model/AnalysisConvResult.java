package org.c4i.chitchat.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.c4i.nlp.match.Result;
import org.c4i.util.TimeValue;

import java.util.List;
import java.util.Map;

/**
 * A container for batch analysis results.
 * @author Arvid Halma, Wouter Eekhout
 */
public class AnalysisConvResult {
    @JsonProperty
    Map<String, Double> labelHistogram;
    @JsonProperty
    List<Result> matchResults;
    @JsonProperty
    Map<String, List<TimeValue>> timeLines;

    @JsonProperty
    Map<String, Map<String, Double>> wordHistograms;

    public AnalysisConvResult() {
    }

    public AnalysisConvResult(Map<String, Double> labelHistogram, Map<String, Map<String, Double>> wordHistograms, List<Result> matchResults, Map<String, List<TimeValue>> timeLines) {
        this.matchResults = matchResults;
        this.labelHistogram = labelHistogram;
        this.wordHistograms = wordHistograms;
        this.timeLines = timeLines;
    }

    public Map<String, Double> getLabelHistogram() {
        return labelHistogram;
    }

    public AnalysisConvResult setLabelHistogram(Map<String, Double> labelHistogram) {
        this.labelHistogram = labelHistogram;
        return this;
    }

    public List<Result> getMatchResults() {
        return matchResults;
    }

    public AnalysisConvResult setMatchResults(List<Result> matchResults) {
        this.matchResults = matchResults;
        return this;
    }

    public Map<String, List<TimeValue>> getTimeLines() {
        return timeLines;
    }

    public AnalysisConvResult setTimeLines(Map<String, List<TimeValue>> timeLines) {
        this.timeLines = timeLines;
        return this;
    }

    public AnalysisConvResult(Map<String, Map<String, Double>> words) {
        this.wordHistograms = words;
    }
}
