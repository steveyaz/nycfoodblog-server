package com.nycfoodblog.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Review {

    @JsonProperty private String username;
    @JsonProperty private long postId;

    @JsonProperty private int foodRating;
    @JsonProperty private int vibesRating;
    @JsonProperty private int ecRating;
    @JsonProperty private String text;

    public String getUsername() { return username.toLowerCase(); }
    public long getPostId() { return postId; }

}
