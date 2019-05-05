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
        userMap = new HashMap();
        for (User user : users) {
            userMap.put(user.getUsername(), user);
        }
    }

    @Override
    public Optional<User> authenticate(BasicCredentials credentials) {
        String cleanUsernameCredential = credentials.getUsername().toLowerCase();
        if (userMap.containsKey(cleanUsernameCredential) &&
                userMap.get(cleanUsernameCredential).getPassword().equals(credentials.getPassword())) {
            return Optional.of(userMap.get(cleanUsernameCredential));
        }
        return Optional.empty();
    }
}
