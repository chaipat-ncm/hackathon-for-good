package org.c4i.chitchat.api.view;

import io.dropwizard.views.View;

/**
 * Simple page without models
 * @author Arvid Halma
 */
public class SimpleView extends View {

    private final String contentFtl;

    public SimpleView(String contentFtl) {
        super("simple.ftl");
        this.contentFtl = contentFtl;
    }

    public String getContentFtl() {
        return contentFtl;
    }
}
