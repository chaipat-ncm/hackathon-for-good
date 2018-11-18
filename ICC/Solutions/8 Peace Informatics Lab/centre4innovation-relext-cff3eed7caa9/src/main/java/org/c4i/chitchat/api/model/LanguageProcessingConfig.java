package org.c4i.chitchat.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.parboiled.common.ImmutableList;

import java.util.List;
import java.util.Objects;

public class LanguageProcessingConfig {
    @JsonProperty
    private List<String> normalizers = ImmutableList.of("default", "stem");

    @JsonProperty
    private List<String> features = ImmutableList.of("datasheet");

    @JsonProperty
    private String wordTokenizer = "matching";

    @JsonProperty
    private String sentenceSplitter = "simple";


    public LanguageProcessingConfig() {
    }

    public List<String> getNormalizers() {
        return normalizers;
    }

    public LanguageProcessingConfig setNormalizers(List<String> normalizers) {
        this.normalizers = normalizers;
        return this;
    }

    public List<String> getFeatures() {
        return features;
    }

    public LanguageProcessingConfig setFeatures(List<String> features) {
        this.features = features;
        return this;
    }

    public String getWordTokenizer() {
        return wordTokenizer;
    }

    public LanguageProcessingConfig setWordTokenizer(String wordTokenizer) {
        this.wordTokenizer = wordTokenizer;
        return this;
    }

    public String getSentenceSplitter() {
        return sentenceSplitter;
    }

    public LanguageProcessingConfig setSentenceSplitter(String sentenceSplitter) {
        this.sentenceSplitter = sentenceSplitter;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LanguageProcessingConfig that = (LanguageProcessingConfig) o;
        return Objects.equals(normalizers, that.normalizers) &&
                Objects.equals(features, that.features) &&
                Objects.equals(wordTokenizer, that.wordTokenizer) &&
                Objects.equals(sentenceSplitter, that.sentenceSplitter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(normalizers, features, wordTokenizer, sentenceSplitter);
    }

    @Override
    public String toString() {
        return "LanguageProcessingConfig{" +
                "normalizers=" + normalizers +
                ", features=" + features +
                ", wordTokenizer='" + wordTokenizer + '\'' +
                ", sentenceSplitter='" + sentenceSplitter + '\'' +
                '}';
    }
}
