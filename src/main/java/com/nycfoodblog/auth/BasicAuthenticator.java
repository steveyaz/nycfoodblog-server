package com.nycfoodblog.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

public class BasicAuthenticator implements Authenticator<BasicCredentials, User> {
    private Map<String, User> userMap;

    public BasicAuthenticator(List<User> users) {
        userMap = new HashMap<String, User>();
        for (User user : users) {
            userMap.put(user.getName().toLowerCase(), user);
        }
    }

    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
        if (userMap.containsKey(credentials.getUsername().toLowerCase()) &&
                userMap.get(credentials.getUsername().toLowerCase()).getPassword().equals(credentials.getPassword())) {
            return Optional.of(userMap.get(credentials.getUsername().toLowerCase()));
        }
        return Optional.empty();
    }
}
