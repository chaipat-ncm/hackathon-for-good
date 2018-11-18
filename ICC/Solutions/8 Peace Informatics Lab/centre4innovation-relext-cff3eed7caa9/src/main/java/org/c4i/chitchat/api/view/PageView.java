package org.c4i.chitchat.api.view;

import io.dropwizard.views.View;

/**
 * Base page
 * @author Arvid Halma
 * @version 18-9-2016 - 10:05
 */
public class PageView extends View {

    private final String contentFtl;

    public PageView(String contentFtl) {
        super("page.ftl");
        this.contentFtl = contentFtl;
    }

    public String getContentFtl() {
        return contentFtl;
    }
}
