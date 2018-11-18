package org.c4i.chitchat.api.sec;

import com.google.common.collect.ImmutableSet;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.Optional;

/**
 * Allows all tokens when one is provided. Effectively canceling auth.
 */
public class PermitAllAuthenticator implements Authenticator<BasicCredentials, User> {

    @Override
    public Optional<User> authenticate(BasicCredentials token) throws AuthenticationException {
            return Optional.of(new User("anyone", ImmutableSet.of("ADMIN", "BASIC_GUY")));
    }
}