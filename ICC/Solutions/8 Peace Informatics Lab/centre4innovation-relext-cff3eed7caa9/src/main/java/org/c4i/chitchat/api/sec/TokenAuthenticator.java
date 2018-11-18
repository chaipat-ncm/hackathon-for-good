package org.c4i.chitchat.api.sec;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Token based/OAuth authenticator.
 */
public class TokenAuthenticator implements Authenticator<String, User> {
    private final Logger logger = LoggerFactory.getLogger(TokenAuthenticator.class);

    private Cache<String, User> tokenToUserCache;

    public TokenAuthenticator(){
        this(1000, 1, TimeUnit.MINUTES);
    }

    public TokenAuthenticator(int maxEntries, long maxEntryTime, TimeUnit maxEntryTimeUnit) {
        createCache(maxEntries, maxEntryTime, maxEntryTimeUnit);
    }

    private void createCache(int maxEntries, long maxEntryTime, TimeUnit maxEntryTimeUnit) {
        this.tokenToUserCache = CacheBuilder.newBuilder()
                .maximumSize(maxEntries)
                .expireAfterWrite(maxEntryTime, maxEntryTimeUnit)
                .removalListener((RemovalListener<String, User>) removalNotification ->
                        logger.debug("User '{}' (token: {}) will be removed from the auhthentication cache.", removalNotification.getValue().getName(), removalNotification.getKey()))
                .build();
    }

    public void enroll(User user, String token ){
        tokenToUserCache.put(token, user);
    }

    @Override
    public Optional<User> authenticate(String token) throws AuthenticationException {
        User user = tokenToUserCache.getIfPresent(token);
        if(user != null){
            return Optional.of(user);
        } else {
            // todo: try login in
            return Optional.empty();
        }

    }
}