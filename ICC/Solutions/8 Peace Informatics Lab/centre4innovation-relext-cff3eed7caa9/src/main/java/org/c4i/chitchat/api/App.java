package org.c4i.chitchat.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jdbi3.bundles.JdbiExceptionsBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.c4i.chitchat.api.chat.ConversationManager;
import org.c4i.chitchat.api.cmd.CreateLoginCommand;
import org.c4i.chitchat.api.cmd.ImportDataCommand;
import org.c4i.chitchat.api.db.*;
import org.c4i.chitchat.api.model.JsonDoc;
import org.c4i.chitchat.api.model.TextDoc;
import org.c4i.chitchat.api.resource.*;
import org.c4i.chitchat.api.sec.BasicAuthenticator;
import org.c4i.chitchat.api.sec.SimpleAuthorizer;
import org.c4i.chitchat.api.sec.TokenAuthenticator;
import org.c4i.chitchat.api.sec.User;
import org.c4i.graph.GraphModule;
import org.c4i.nlp.chat.Conversation;
import org.c4i.nlp.chat.Message;
import org.c4i.nlp.match.Range;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jdbi.v3.core.Jdbi;
import org.parboiled.common.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

/**
 * The Application class pulls together the various bundles and commands which provide basic functionality.
 * @author Arvid Halma
 * @version 3-4-2016
 */
public class App extends Application<Config> {
    private final Logger logger = LoggerFactory.getLogger(App.class);

    private Config config;

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public String getName() {
        return "relext";
    }


    public Config getConfig() {
        return config;
    }

    @Override
    public void initialize(Bootstrap<Config> bootstrap) {
        // Serve static content
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));

        // Swagger API docs
        bootstrap.addBundle(new SwaggerBundle<Config>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(Config configuration) {
                return configuration.getSwaggerBundleConfiguration();
            }
        });

        // Add multipart message support
        bootstrap.addBundle(new MultiPartBundle());

        // Views
        bootstrap.addBundle(new ViewBundle<>());

        // Add CLI commands
        bootstrap.addCommand(new CreateLoginCommand());
        bootstrap.addCommand(new ImportDataCommand());

        // By adding the DBIExceptionsBundle to your application, Dropwizard will automatically
        // unwrap any thrown SQLException or DBIException instances.
        // This is critical for debugging, since otherwise only the common wrapper exception's
        // stack trace is logged.
        bootstrap.addBundle(new JdbiExceptionsBundle());

        // Add db migration support
        bootstrap.addBundle(new MigrationsBundle<Config>() {

            @Override
            public String getMigrationsFileName() {
                return "migrations.yml";
            }

            @Override
            public DataSourceFactory getDataSourceFactory(Config configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    /**
     * Cross-Origin Resource Sharing (CORS) is a mechanism that uses additional HTTP headers to tell a browser to
     * let a web application running at one origin (domain) have permission to access selected resources from a server
     * at a different origin.
     * A web application makes a cross-origin HTTP request when it requests a resource that has a different origin
     * (domain, protocol, and port) than its own origin.
     *
     * @see "https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS"
     * @param environment
     */
    private void configureCors(Environment environment) {
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "Cache-Control,If-Modified-Since,Pragma,Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");

        // DO NOT pass a preflight request to down-stream auth filters
        // unauthenticated preflight requests should be permitted by spec
        cors.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, "false");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

    }

    @Override
    public void run(Config config, Environment environment) throws IOException {
        this.config = config;

        // Set JSON formatting
        ObjectMapper objectMapper = environment.getObjectMapper();
        if(config.isPrettyPrintJsonResponse()) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        objectMapper.registerModule(new GraphModule());

        // Proper DateTime serialization
        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        config.setObjectMapper(objectMapper);

        // Auth
        final String realm = "RELEXT REALM";
        if("basiclist".equals(config.auth.type)){
            environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                    .setAuthenticator(new BasicAuthenticator(config.auth.credentials))
                    .setAuthorizer(new SimpleAuthorizer())
                    .setRealm(realm)
                    .buildAuthFilter()));
            environment.jersey().register(RolesAllowedDynamicFeature.class);
            //If you want to use @Auth to inject a custom Principal type into your resource
            environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        } else if("oauth".equals(config.auth.type)){
            environment.jersey().register(new AuthDynamicFeature(
                    new OAuthCredentialAuthFilter.Builder<User>()
                            .setAuthenticator(new TokenAuthenticator())
                            .setAuthorizer(new SimpleAuthorizer())
                            .setPrefix("Bearer")
                            .setRealm(realm)
                            .buildAuthFilter()));
            environment.jersey().register(RolesAllowedDynamicFeature.class);
            //If you want to use @Auth to inject a custom Principal type into your resource
            environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        }

        // Enable Cross-Origin Resource Sharing
        configureCors(environment);

        // Exceptions
        environment.jersey().register(ParseExceptionMapper.class);

        // Load data
        config.loadNlp();

        // DB
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, config.getDataSourceFactory(), "database");
        jdbi.registerRowMapper(Message.class, new MessageMapper());
        jdbi.registerRowMapper(Conversation.class, new ConversationMapper());
        jdbi.registerRowMapper(Range.class, new RangeMapper());
        jdbi.registerRowMapper(TextDoc.class, new TextDocMapper());
        jdbi.registerRowMapper(JsonDoc.class, new JsonDocMapper());

        // create DAOs
        config.dao = new Dao(
                jdbi.onDemand(ConversationDao.class),
                jdbi.onDemand(TextDocDao.class),
                jdbi.onDemand(JsonDocDao.class)
            );


        // Resources
        final SystemResource systemResource = new SystemResource(config);
        environment.jersey().register(systemResource);

        final NlpResource nlpResource = new NlpResource(config);
        environment.jersey().register(nlpResource);

        final DatabaseResource dbResource = new DatabaseResource(config);
        environment.jersey().register(dbResource);

        final ScriptResource scriptResource = new ScriptResource(config);
        environment.jersey().register(scriptResource);

        // Data sheets
        dbResource.loadAllDataSheets();

        // Reply variables
        dbResource.loadReplyVariables();

        /*ConversationManager fbChairman = new ConversationManager(10000, config.getMaxConversationTimeIntervalSeconds(), TimeUnit.SECONDS, ImmutableList.of(dbResource));
        final FacebookResource facebookResource = new FacebookResource(config, fbChairman, ImmutableList.of(dbResource, new MessageLogger(config)));
        try {
            facebookResource.loadLiveScript();
        } catch (Exception e) {
            logger.error("Could not load Facebook live script.");
        }
        config.facebookResource = facebookResource;
        environment.jersey().register(facebookResource);*/

        // Legacy stuff
        // environment.jersey().register(new LegacyFacebookResource(facebookResource));


        ConversationManager devChairman = new ConversationManager(10000, config.getMaxConversationTimeIntervalSeconds(), TimeUnit.SECONDS, ImmutableList.of(dbResource));
        final DevBotResource devBotResource = new DevBotResource(config, devChairman,
                ImmutableList.of(dbResource, new MessageLogger(config), new SimpleMessageLogger()));
        config.devBotResource = devBotResource;
        environment.jersey().register(devBotResource);

        ConversationManager ccChairman = new ConversationManager(10000, config.getMaxConversationTimeIntervalSeconds(), TimeUnit.SECONDS, ImmutableList.of(dbResource));
        final ChitChatResource chitChatResource = new ChitChatResource(config, ccChairman,
                ImmutableList.of(dbResource, new MessageLogger(config), new SimpleMessageLogger()));
        try {
            chitChatResource.loadLiveScript();
        } catch (Exception e) {
            logger.error("Could not load ChitChat live script.");
        }
        config.chitchatResource = chitChatResource;
        environment.jersey().register(chitChatResource);

        final FrontEndResource frontEndResource = new FrontEndResource();
        environment.jersey().register(frontEndResource);


        // Health checks
        final Health healthCheck = new Health(config);
        environment.healthChecks().register("dataDir", healthCheck);

        logger.warn("ChitChat application ready...");

    }


}