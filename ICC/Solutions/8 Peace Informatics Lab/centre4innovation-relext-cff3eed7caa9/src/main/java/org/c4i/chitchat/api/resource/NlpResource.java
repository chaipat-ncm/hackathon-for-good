package org.c4i.chitchat.api.resource;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.c4i.chitchat.api.Config;
import org.c4i.nlp.Nlp;
import org.c4i.nlp.match.ScriptConfig;
import org.c4i.nlp.tokenize.Token;
import org.c4i.nlp.tokenize.TokenUtil;
import org.c4i.util.Hash;
import org.c4i.util.Tail;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Natural language processing tools
 * @author Arvid Halma
 * @version 23-11-2015
 */

@Path("/nlp")
@Api("/nlp")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class NlpResource {
    Config config;
    Nlp nlp;

    private final Logger logger = LoggerFactory.getLogger(NlpResource.class);

    public NlpResource(Config configuration) {
        this.config = configuration;
        this.nlp = config.getNlp();
    }

    @GET
    @Path("/info")
    @ApiOperation(
            value = "Info about text ",
            response = Map.class)
    public Map<String, String> info(@QueryParam("text") String text){
        Map<String, String> info = new LinkedHashMap<>();
        if(text == null || text.isEmpty()){
            return info;
        }
        ScriptConfig config = new ScriptConfig();
        config.setLanguages(ImmutableList.of("en"));

        List<Token> tokens = nlp.getWordTokenizer(config).tokenize(text);
        info.put("normalized", nlp.getNormalizer(config).normalize(text));
        info.put("generalize", nlp.getGeneralizer(config).extendInline(tokens).stream().map(TokenUtil::toSentence).collect(Collectors.joining(", ")));

        return info;
    }



}
