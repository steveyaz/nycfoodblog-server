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
import com.nycfoodblog.api.Review;
import com.nycfoodblog.manager.DataManager;

@Path("/review")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Timed
public class ReviewResource {

    private final DataManager manager;

    public ReviewResource(DataManager manager) {
        this.manager = manager;
    }

    @GET
    @Path("/all")
    public Response getAllReviews() {
        return Response.ok(manager.getAllReviews()).build();
    }

    @POST
    @PermitAll
    public Response putReview(Review review) {
        try {
            manager.putReview(review);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().build();
    }

}
