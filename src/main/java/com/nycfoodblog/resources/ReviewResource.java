package com.nycfoodblog.resources;

import java.util.List;

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
import com.nycfoodblog.manager.PostManager;

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
    @Path("/{postId}")
    public Response getReviews(@PathParam("postId") long postId) {
        List<Review> reviews = manager.getReviews(postId);
        if (reviews == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(reviews).build();
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
