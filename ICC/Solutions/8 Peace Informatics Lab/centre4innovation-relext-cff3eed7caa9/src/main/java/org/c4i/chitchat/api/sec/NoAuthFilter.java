package org.c4i.chitchat.api.sec;

import io.dropwizard.auth.AuthFilter;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;
import java.io.IOException;

/**
 * Allow all
 * @author Arvid Halma
 * @version 6-2-18
 */
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class NoAuthFilter extends AuthFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // nothing
    }
}
