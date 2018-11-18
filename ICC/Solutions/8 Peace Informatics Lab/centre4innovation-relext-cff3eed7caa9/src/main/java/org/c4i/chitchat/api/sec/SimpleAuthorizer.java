package org.c4i.chitchat.api.sec;

import io.dropwizard.auth.Authorizer;

/**
 * Simple match between a user's defined roles and the required role.
 * See: https://github.com/dropwizard/dropwizard/blob/master/dropwizard-example/src/main/java/com/example/helloworld/auth/ExampleAuthorizer.java
 * @author Arvid Halma
 */
public class SimpleAuthorizer implements Authorizer<User> {

    @Override
    public boolean authorize(User user, String role) {
        return user.getRoles() != null && user.getRoles().contains(role);
    }
}