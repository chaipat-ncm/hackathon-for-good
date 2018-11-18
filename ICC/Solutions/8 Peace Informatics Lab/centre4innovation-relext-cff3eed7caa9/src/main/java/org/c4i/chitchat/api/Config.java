package org.c4i.chitchat.api;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.c4i.chitchat.api.db.Dao;
import org.c4i.chitchat.api.model.FacebookSettings;
import org.c4i.chitchat.api.model.LanguageProcessingConfig;
import org.c4i.chitchat.api.resource.ChitChatResource;
import org.c4i.chitchat.api.resource.DevBotResource;
import org.c4i.chitchat.api.resource.FacebookResource;
import org.c4i.chitchat.api.sec.AuthConfiguration;
import org.c4i.nlp.Nlp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Configuration class which specifies environment-specific parameters.
 * These parameters are specified in a YAML configuration file which is deserialized
 * to an instance of your application's configuration class and validated.
 *
 * Docs:
 * https://dropwizard.github.io/dropwizard/manual/configuration.html
 *
 * @author Arvid Halma
 * @version 3-4-2016
 */
@SuppressWarnings("WeakerAccess")
public class Config extends Configuration {

    private final Logger logger = LoggerFactory.getLogger(Config.class);

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty
    @Min(1)
    private long maxConversationTimeIntervalSeconds;

    @JsonProperty("languageProcessing")
    private Map<String, LanguageProcessingConfig> languageSupport;

    @JsonProperty
    private boolean prettyPrintJsonResponse;

    @JsonProperty("swagger")
    private SwaggerBundleConfiguration swaggerBundleConfiguration;

    @Valid
    @NotNull
    @JsonProperty("auth")
    public AuthConfiguration auth;

    private File dataDir = new File("data");

    @Valid
    @NotNull
    @JsonProperty("facebookSettings")
    public FacebookSettings facebookSettings;

    @JsonIgnore
    public Dao dao;

    @JsonIgnore
    private ObjectMapper objectMapper;

    @JsonIgnore
    private Nlp nlp;

    @JsonIgnore
    public FacebookResource facebookResource;
    @JsonIgnore
    public DevBotResource devBotResource;
    @JsonIgnore
    public ChitChatResource chitchatResource;

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    public long getMaxConversationTimeIntervalSeconds() {
        return maxConversationTimeIntervalSeconds;
    }

    public void setMaxConversationTimeIntervalSeconds(long maxConversationTimeIntervalSeconds) {
        this.maxConversationTimeIntervalSeconds = maxConversationTimeIntervalSeconds;
    }

    public boolean isPrettyPrintJsonResponse() {
        return prettyPrintJsonResponse;
    }

    public SwaggerBundleConfiguration getSwaggerBundleConfiguration() {
        return swaggerBundleConfiguration;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void loadNlp(){
        this.nlp = new Nlp(new File(dataDir, "nlp"), languageSupport);
        this.nlp.loadModels();
    }

    public Nlp getNlp() {
        return nlp;
    }


}