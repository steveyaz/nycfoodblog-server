package com.nycfoodblog.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;
import com.nycfoodblog.api.Post;
import com.nycfoodblog.data.PostManager;

@Path("/post")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Timed
public class PostResource {

    private final PostManager manager;

    public PostResource(PostManager manager) {
        this.manager = manager;
    }

    @GET
    @Path("/all")
    public Response getAllPostIds() {
        return Response.ok(manager.getAllPostIds()).build();
    }

    @GET
    @Path("/{id}")
    public Response getPost(@PathParam("id") long id) {
        Post post = manager.getPost(id);
        if (post == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(post).build();
    }

    @POST
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
