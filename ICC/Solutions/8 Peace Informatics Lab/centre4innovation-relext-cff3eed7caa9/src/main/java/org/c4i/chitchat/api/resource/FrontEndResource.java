package org.c4i.chitchat.api.resource;

import io.swagger.annotations.Api;
import org.c4i.chitchat.api.view.PageView;
import org.c4i.chitchat.api.view.SimpleView;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * User interface server side rendering
 * @version 24-10-16
 * @author Arvid Halma
 */

@Path("/ui")
@Api("/ui")
@Consumes({MediaType.APPLICATION_JSON})
@Produces(MediaType.TEXT_HTML)
public class FrontEndResource {

    @GET
    public PageView base(){
        throw redirect("/api/v1/ui/dashboard");
    }

    @GET
    @Path("chatclient")
    public SimpleView chatclient(){
        return new SimpleView("chatclient.ftl");
    }

    @GET
    @Path("dashboard")
    public PageView dashboard(){
        return new PageView("dashboard.ftl");
    }


    @GET
    @Path("create/survey")
    public PageView scriptBuilder(){
        return new PageView("create-survey.ftl");
    }

    @GET
    @Path("create/match")
    public PageView scriptMatch(){
        return new PageView("create-match.ftl");
    }

    @GET
    @Path("create/script")
    public PageView scriptChat(){
        return new PageView("create-script.ftl");
    }

    @GET
    @Path("data/variables")
    @RolesAllowed("ADMIN")
    public PageView variables() {
        return new PageView("data-variables.ftl");
    }

    @GET
    @Path("data/sheets")
    @RolesAllowed("ADMIN")
    public PageView datasheets() {
        return new PageView("data-sheets.ftl");
    }

    @GET
    @Path("data/export")
    @RolesAllowed("ADMIN")
    public PageView export() {
        return new PageView("data-export.ftl");
    }

    @GET
    @Path("analyse/overview")
    @RolesAllowed("ADMIN")
    public PageView overview() {
        return new PageView("analyse-overview.ftl");
    }

   /* @GET
    @Path("analyse/conversations")
    public PageView conversations(){
        return new PageView("analyse-conversations.ftl");
    }

    @GET
    @Path("analyse/statistics")
    @RolesAllowed("ADMIN")
    public PageView statistics(){
        return new PageView("analyse-analyse.ftl");
    }*/

    @GET
    @Path("analyse/analyse")
    @RolesAllowed("ADMIN")
    public PageView ultimate(){
        return new PageView("analyse-analyse.ftl");
    }


    @GET
    @Path("analyse/graph")
    @RolesAllowed("ADMIN")
    public PageView graph(){
        return new PageView("analyse-graph.ftl");
    }

    @GET
    @Path("live/fb")
    @RolesAllowed("ADMIN")
    public PageView liveFacebook() {
        return new PageView("live-fb.ftl");
    }

    @GET
    @Path("live/chitchat")
    @RolesAllowed("ADMIN")
    public PageView liveChitchat() {
        return new PageView("live-chitchat.ftl");
    }

    @GET
    @Path("system/docs")
    public PageView docs(){
        return new PageView("system-docs.ftl");
    }

    @GET
    @Path("system/apidoc")
    @RolesAllowed("ADMIN")
    public PageView apidoc(){
        return new PageView("system-apidoc.ftl");
    }


    @GET
    @Path("system/info")
    @RolesAllowed("ADMIN")
    public PageView system() {
        return new PageView("system.ftl");
    }

    @GET
    @Path("about")
    public PageView about() {
        return new PageView("about.ftl");
    }

    @GET
    @Path("privacy")
    public PageView privacy() {
        return new PageView("privacy.ftl");
    }

    private static WebApplicationException redirect(String path) {
        URI uri = UriBuilder.fromUri(path).build();
        Response response = Response.seeOther(uri).build();
        return new WebApplicationException(response);
    }

}
