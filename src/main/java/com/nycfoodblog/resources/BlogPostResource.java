package com.nycfoodblog.resources;

import com.nycfoodblog.api.BlogPost;
import com.codahale.metrics.annotation.Timed;
import com.nycfoodblog.data.BlogPostManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.concurrent.atomic.AtomicLong;
import java.util.Optional;

@Path("/blogpost")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Timed
public class BlogPostResource {
    private final BlogPostManager manager;

    public BlogPostResource(BlogPostManager manager) {
        this.manager = manager;
    }

    @GET
    @Path("/all")
    public Response getAllBlogPostIds() {
        return Response.ok(manager.getAllBlogPostIds()).build();
    }

    @GET
    @Path("/{id}")
    public Response getBlogPost(@PathParam("id") long id) {
        BlogPost post = manager.getBlogPost(id);
        if (post == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(post).build();
    }

    @POST
    public Response putBlogPost(BlogPost post) {
        long id = manager.putBlogPost(post);
        return Response.ok(id).build();
    }

}
