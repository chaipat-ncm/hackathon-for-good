package org.c4i.chitchat.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Facebook credentials.
 */
public class FacebookSettings {
    @JsonProperty
    public List<FacebookCredentials> credentials;

    public List<FacebookCredentials> getFacebookCredentials() {
        return credentials;
    }

    public FacebookSettings setFacebookCredentials(List<FacebookCredentials> credentials) {
        this.credentials = credentials;
        return this;
    }

}