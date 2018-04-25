package com.nycfoodblog.resources;

import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;
import com.nycfoodblog.api.AuthCreds;
import com.nycfoodblog.auth.BasicAuthenticator;
import com.nycfoodblog.auth.User;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Timed
public class AuthenticationResource {

    private BasicAuthenticator authenticator;

    public AuthenticationResource(BasicAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @POST
    public Response checkAuthentication(AuthCreds creds) {
        try {
            Optional<User> user = authenticator.authenticate(new BasicCredentials(creds.getUsername(), creds.getPassword()));
            return Response.ok(user.isPresent()).build();
        } catch (AuthenticationException e) {
            return Response.serverError().build();
        }
    }

}
