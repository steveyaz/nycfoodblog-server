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
import com.nycfoodblog.api.Review;
import com.nycfoodblog.data.PostManager;

@Path("/review")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Timed
public class ReviewResource {

    private final PostManager manager;

    public ReviewResource(PostManager manager) {
        this.manager = manager;
    }

    @GET
    @Path("/{username}/{postId}")
    public Response getReview(@PathParam("username") String username, @PathParam("postId") long postId) {
        Review review = manager.getReview(username, postId);
        if (review == null) {
            return Response.ok().build();
        }
        return Response.ok(review).build();
    }

    @POST
    public Response putReview(Review review) {
        try {
            manager.putReview(review);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().build();
    }

}
