package org.c4i.chitchat.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class FacebookCredentials {
    @NotEmpty
    @JsonProperty
    private String accessToken; //Page access Token

    @NotEmpty
    @JsonProperty
    private String appId;

    @NotEmpty
    @JsonProperty
    private String appSecret;

    @NotEmpty
    @JsonProperty
    private String verificationToken;

    @NotEmpty
    @JsonProperty
    private String pageId;

    public FacebookCredentials() {

    }

    public FacebookCredentials(String accessToken, String appId, String appSecret, String verificationToken, String pageId) {
        this.accessToken = accessToken;
        this.appId = appId;
        this.appSecret = appSecret;
        this.verificationToken = verificationToken;
        this.pageId = pageId;
    }

    public String getPageId() {
        return pageId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getVerificationToken() {
        return verificationToken;
    }
}