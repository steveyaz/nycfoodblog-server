package com.nycfoodblog;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class FoodBlogConfiguration extends Configuration {

    @NotEmpty
    private String appName;

    @NotEmpty
    private String dataPath;

    @JsonProperty
    public String getAppName() { return appName; }

    @JsonProperty
    public String getDataPath() { return dataPath; }
}
