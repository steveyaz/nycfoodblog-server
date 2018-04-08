package com.nycfoodblog;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

import com.nycfoodblog.data.PostManager;
import com.nycfoodblog.resources.PostResource;

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
        final PostManager manager = new PostManager(dataPath);
        environment.lifecycle().manage(manager);
        environment.jersey().register(PostManager.class);

        final PostResource resource = new PostResource(manager);
        environment.jersey().register(resource);

    }

}
