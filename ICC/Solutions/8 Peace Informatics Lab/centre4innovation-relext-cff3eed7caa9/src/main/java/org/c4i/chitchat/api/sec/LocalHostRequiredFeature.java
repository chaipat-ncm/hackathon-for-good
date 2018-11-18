package org.c4i.chitchat.api.sec;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

/**
 * Protect Resource methods by only allowing requests from local host.
 * @author Arvid Halma
 */
@Provider
public class LocalHostRequiredFeature implements DynamicFeature {
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        if (resourceInfo.getResourceMethod().getAnnotation(LocalHostRequired.class) != null) {
            context.register(LocalHostRequiredFilter.class);
        }
    }
}