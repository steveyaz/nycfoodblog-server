package com.nycfoodblog;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class FoodBlogConfiguration extends Configuration {

    @NotEmpty
    private String appName;

    @NotEmpty
    private String dataPath;

    @NotEmpty
    private List<String> users;

    @JsonProperty
    public String getAppName() { return appName; }

    @JsonProperty
    public String getDataPath() { return dataPath; }

    @JsonProperty
    public List<String> getUsers() { return users; }

}
