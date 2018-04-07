package com.nycfoodblog.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

public class BlogPost {

    private long id;

    private String restaurantName;

    public BlogPost() { }

    public BlogPost(long id, String restaurantName) {
        this.id = id;
        this.restaurantName = restaurantName;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonProperty
    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

}
