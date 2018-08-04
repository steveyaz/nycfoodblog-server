package com.nycfoodblog;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.nycfoodblog.auth.BasicAuthenticator;
import com.nycfoodblog.auth.User;
import com.nycfoodblog.manager.PostManager;
import com.nycfoodblog.resources.AuthenticationResource;
import com.nycfoodblog.resources.PostResource;
import com.nycfoodblog.resources.ReviewResource;
import com.nycfoodblog.resources.UserResource;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Environment;

public class FoodBlogApplication extends Application<FoodBlogConfiguration> {

    private String appName;

    public static void main(String[] args) throws Exception {
        new FoodBlogApplication().run(args);
    }

    @Override
    public String getName() {
        return "nycfoodblog";
    }

    @Override
    public void run(FoodBlogConfiguration configuration, Environment environment) {

        Path dataPath = Paths.get(configuration.getDataPath());
        List<User> users = new ArrayList<User>();
        final PostManager manager = new PostManager(dataPath, users);
        environment.lifecycle().manage(manager);
        environment.jersey().register(PostManager.class);

        final PostResource postResource = new PostResource(manager);
        environment.jersey().register(postResource);

        final ReviewResource reviewResource = new ReviewResource(manager);
        environment.jersey().register(reviewResource);

        final UserResource userResource = new UserResource(manager);
        environment.jersey().register(userResource);

        for (String userString : configuration.getUsers()) {
            users.add(new User(userString.split(":")[0], userString.split(":")[1]));
        }
        BasicAuthenticator authenticator = new BasicAuthenticator(users);
        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<User>()
                        .setAuthenticator(authenticator)
                        .buildAuthFilter()));

        final AuthenticationResource authResource = new AuthenticationResource(authenticator);
        environment.jersey().register(authResource);

    }

}
