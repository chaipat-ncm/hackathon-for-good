package org.c4i.chitchat.api.sec;


import org.c4i.util.LocalHost;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Resources that can only be accessed from the local machine.
 * @author Arvid
 * @version 5-6-2015 - 22:01
 */
@Provider
public class LocalHostRequiredFilter implements ContainerRequestFilter {

    @Context
    private HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if(!LocalHost.isLocalHost(request.getRemoteAddr())){
            throw new WebApplicationException("You can only access this resource from localhost.", Response.Status.FORBIDDEN);
        }
    }
}
