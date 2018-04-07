package com.nycfoodblog;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.nycfoodblog.data.BlogPostManager;
import com.nycfoodblog.resources.BlogPostResource;

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
        final BlogPostManager manager = new BlogPostManager(dataPath);
        environment.lifecycle().manage(manager);
        environment.jersey().register(BlogPostManager.class);

        final BlogPostResource resource = new BlogPostResource(manager);
        environment.jersey().register(resource);

    }

}
