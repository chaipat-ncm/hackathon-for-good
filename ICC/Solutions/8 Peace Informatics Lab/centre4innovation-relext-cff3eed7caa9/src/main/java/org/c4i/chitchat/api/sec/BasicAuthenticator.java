package org.c4i.chitchat.api.sec;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Checks credentials with a fixed list of known accounts.
 * @author Arvid Halma
 */
public class BasicAuthenticator implements Authenticator<BasicCredentials, User> {
    private Map<String, Credentials> credentials;

    public BasicAuthenticator(List<Credentials> credentials) {
        this.credentials = new HashMap<>();
        for (Credentials c : credentials) {
            this.credentials.put(c.getUser().getName(), c);
        }
    }

    @Override
    public Optional<User> authenticate(BasicCredentials basicCredentials) throws AuthenticationException {
        String name = basicCredentials.getUsername();
        if(credentials.containsKey(name)){
            Credentials c = credentials.get(name);
            if(c.match(basicCredentials.getPassword())){
                return Optional.of(new User(name, c.getUser().getRoles()));
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
}