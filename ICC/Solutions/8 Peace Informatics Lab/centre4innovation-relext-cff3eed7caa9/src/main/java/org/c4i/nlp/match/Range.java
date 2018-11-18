package org.c4i.nlp.match;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.c4i.util.Hash;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A single match indicated by its token and character location, accompanied by a label that indicated the cause of the match.
 * @author Arvid Halma
 * @version 20-4-2017 - 13:35
 */
public class Range implements Comparable<Range>{
    @JsonProperty
    public String label;
    @JsonProperty
    public String value;
    @JsonProperty
    public int tokenStart, tokenEnd;
    @JsonProperty
    public int charStart, charEnd;
    @JsonProperty
    public Map<String, String> props;
    @JsonProperty
    public String conversationId;
    @JsonProperty
    public int section;

    @JsonIgnore
    private boolean fromNegation;

    public Range() {
    }

    public Range(String label, int tokenStart, int tokenEnd, int charStart, int charEnd) {
        this.label = label;
        this.tokenStart = tokenStart;
        this.tokenEnd = tokenEnd;
        this.charStart = charStart;
        this.charEnd = charEnd;
        this.props = new LinkedHashMap<>();
    }

    public Range(String label, int tokenStart, int tokenEnd, int charStart, int charEnd, Map<String, String> props) {
        this.label = label;
        this.tokenStart = tokenStart;
        this.tokenEnd = tokenEnd;
        this.charStart = charStart;
        this.charEnd = charEnd;
        this.props = props;
    }

    public Range(String label, int tokenStart, int tokenEnd, int charStart, int charEnd, Map<String, String> props, int section) {
        this.label = label;
        this.tokenStart = tokenStart;
        this.tokenEnd = tokenEnd;
        this.charStart = charStart;
        this.charEnd = charEnd;
        this.props = props;
        this.section = section;
    }

    public Range(String label, String value, int tokenStart, int tokenEnd, int charStart, int charEnd) {
        this.label = label;
        this.value = value;
        this.tokenStart = tokenStart;
        this.tokenEnd = tokenEnd;
        this.charStart = charStart;
        this.charEnd = charEnd;
        this.props = new LinkedHashMap<>();
    }

    public Range(Range that) {
        this.label = that.label;
        this.value = that.value;
        this.tokenStart = that.tokenStart;
        this.tokenEnd = that.tokenEnd;
        this.charStart = that.charStart;
        this.charEnd = that.charEnd;
        this.props = new LinkedHashMap<>(that.props);
        this.section = that.section;
    }

    public String getId(){
        return Hash.sha256Hex(label + charStart + conversationId);

    }

    public String getLabel() {
        return label;
    }

    public Range setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Range setValue(String value) {
        this.value = value;
        return this;
    }

    public int getTokenStart() {
        return tokenStart;
    }

    public Range setTokenStart(int tokenStart) {
        this.tokenStart = tokenStart;
        return this;
    }

    public int getTokenEnd() {
        return tokenEnd;
    }

    public Range setTokenEnd(int tokenEnd) {
        this.tokenEnd = tokenEnd;
        return this;
    }

    public int getCharStart() {
        return charStart;
    }

    public Range setCharStart(int charStart) {
        this.charStart = charStart;
        return this;
    }

    public int getCharEnd() {
        return charEnd;
    }

    public Range setCharEnd(int charEnd) {
        this.charEnd = charEnd;
        return this;
    }

    public Map<String, String> getProps() {
        return props;
    }

    public Range setProps(Map<String, String> props) {
        this.props = props;
        return this;
    }

    public String getConversationId() {
        return conversationId;
    }

    public Range setConversationId(String conversationId) {
        this.conversationId = conversationId;
        return this;
    }

    public int getSection() {
        return section;
    }

    public Range setSection(int section) {
        this.section = section;
        return this;
    }

    public boolean isFromNegation() {
        return fromNegation;
    }

    public Range setFromNegation(boolean fromNegation) {
        this.fromNegation = fromNegation;
        return this;
    }

    public boolean contains(Range b){
        return this.charStart <= b.charStart && this.charEnd >= b.charEnd;
    }

    public boolean within(Range a){
        return a.contains(this);
    }

    public Range intersect(Range b){
        if(charStart < b.charEnd && charEnd > b.charStart){
            LinkedHashMap<String, String> props = new LinkedHashMap<>(this.props);
            props.putAll(b.props);
            return new Range(label,
                    Math.max(tokenStart, b.tokenStart),
                    Math.min(tokenEnd, b.tokenEnd),
                    Math.max(charStart, b.charStart),
                    Math.min(charEnd, b.charEnd)
            , props).setSection(Math.max(section, b.section));
        }
        return null;
    }
    public void updateValue(String text){
        this.value = text.substring(charStart, charEnd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Range)) return false;
        Range range = (Range) o;
        return charStart == range.charStart &&
                Objects.equals(label, range.label) &&
                Objects.equals(conversationId, range.conversationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, charStart, conversationId);
    }

    @Override
    public String toString() {
        return "Range{" +
                "label='" + label + '\'' +
                ", value='" + value + '\'' +
                ", tokenStart=" + tokenStart +
                ", tokenEnd=" + tokenEnd +
                ", charStart=" + charStart +
                ", charEnd=" + charEnd +
                ", props=" + props +
                ", conversationId='" + conversationId + '\'' +
                ", part=" + section +
                ", fromNegation=" + fromNegation +
                '}';
    }

    @Override
    public int compareTo(Range m) {
        // prefer earlier, then longer ranges for highlighting purposes
        int start = Integer.compare(charStart, m.charStart);
        if (start == 0) { // same start loc, compare length
            return Integer.compare(m.charEnd - m.charStart, charEnd - charStart);
        } else {
            return start;
        }
    }
}

