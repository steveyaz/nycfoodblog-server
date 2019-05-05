package com.nycfoodblog.auth;

import java.security.Principal;

public class User implements Principal {
    private final String username;
    private final String password;

    public User(String username, String password) {
        this.username = username.toLowerCase();
        this.password = password;
    }

    @Override
    public String getName() {
        return getUsername();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}