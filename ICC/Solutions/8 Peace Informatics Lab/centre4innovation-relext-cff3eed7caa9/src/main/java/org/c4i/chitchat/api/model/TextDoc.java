package org.c4i.chitchat.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import java.util.Objects;

/**
 * Generic text-based document.
 * @author Arvid Halma
 */
public class TextDoc {
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
    private String body;

    public TextDoc() {
    }

    public String getId() {
        return id;
    }

    public TextDoc setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public TextDoc setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public TextDoc setType(String type) {
        this.type = type;
        return this;
    }

    public DateTime getCreated() {
        return created;
    }

    public TextDoc setCreated(DateTime created) {
        this.created = created;
        return this;
    }

    public DateTime getUpdated() {
        return updated;
    }

    public TextDoc setUpdated(DateTime updated) {
        this.updated = updated;
        return this;
    }

    public String getBody() {
        return body;
    }

    public TextDoc setBody(String body) {
        this.body = body;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextDoc textDoc = (TextDoc) o;
        return Objects.equals(id, textDoc.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
