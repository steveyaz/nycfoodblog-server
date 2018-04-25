package com.nycfoodblog.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthCreds {

    @JsonProperty private String username;
    @JsonProperty private String password;

    public String getUsername() { return username; }
    public String getPassword() { return password; }

}
