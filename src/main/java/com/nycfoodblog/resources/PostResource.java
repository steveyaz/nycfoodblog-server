package com.nycfoodblog.resources;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;
import com.nycfoodblog.api.Post;
import com.nycfoodblog.manager.DataManager;

@Path("/post")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Timed
public class PostResource {

    private final DataManager manager;

    public PostResource(DataManager manager) {
        this.manager = manager;
    }

    @GET
    @Path("/all")
    public Response getAllPosts() {
        return Response.ok(manager.getAllPosts()).build();
    }

    @POST
    @PermitAll
    public Response putPost(Post post) {
        long id = 0;
        try {
            id = manager.putPost(post);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(id).build();
    }

}
