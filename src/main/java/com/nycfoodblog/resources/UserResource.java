package com.nycfoodblog.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;
import com.nycfoodblog.manager.DataManager;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Timed
public class UserResource {

    private final DataManager manager;

    public UserResource(DataManager manager) {
        this.manager = manager;
    }

    @GET
    @Path("/all")
    public Response getAllUsernames() {
        return Response.ok(manager.getAllUsernames()).build();
    }

}
