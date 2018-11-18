package org.c4i.chitchat.api.sec;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Security config
 * @version 17-9-16
 * @author Arvid Halma
 */
public class AuthConfiguration {
    @NotNull
    @JsonProperty
    public String type;

    @JsonProperty
    public List<Credentials> credentials;

    public String getType() {
        return type;
    }

    public AuthConfiguration setType(String type) {
        this.type = type;
        return this;
    }

    public List<Credentials> getCredentials() {
        return credentials;
    }

    public AuthConfiguration setCredentials(List<Credentials> credentials) {
        this.credentials = credentials;
        return this;
    }


}
