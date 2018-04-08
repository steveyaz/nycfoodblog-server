package com.nycfoodblog;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

import com.nycfoodblog.data.PostManager;
import com.nycfoodblog.resources.PostResource;
import com.nycfoodblog.resources.ReviewResource;

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
        List<String> users = configuration.getUsers();
        final PostManager manager = new PostManager(dataPath, users);
        environment.lifecycle().manage(manager);
        environment.jersey().register(PostManager.class);

        final PostResource postResource = new PostResource(manager);
        environment.jersey().register(postResource);

        final ReviewResource reviewResource = new ReviewResource(manager);
        environment.jersey().register(reviewResource);

    }

}
