package org.c4i.chitchat.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Generic text-based document.
 * @author Arvid Halma
 */
public class JsonDoc {
    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String type;

    @JsonProperty
    private DateTime created;

    @JsonProperty
    private DateTime updated;

    @JsonProperty
    private LinkedHashMap<String, Object> body;

    public JsonDoc() {
    }

    public String getId() {
        return id;
    }

    public JsonDoc setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public JsonDoc setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public JsonDoc setType(String type) {
        this.type = type;
        return this;
    }

    public DateTime getCreated() {
        return created;
    }

    public JsonDoc setCreated(DateTime created) {
        this.created = created;
        return this;
    }

    public DateTime getUpdated() {
        return updated;
    }

    public JsonDoc setUpdated(DateTime updated) {
        this.updated = updated;
        return this;
    }

    public LinkedHashMap<String, Object> getBody() {
        return body;
    }

    public JsonDoc setBody(Map<String, Object> body) {
        this.body = body == null ? null : new LinkedHashMap<>(body);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonDoc textDoc = (JsonDoc) o;
        return Objects.equals(id, textDoc.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
