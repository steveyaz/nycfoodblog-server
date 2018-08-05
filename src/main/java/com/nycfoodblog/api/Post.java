package com.nycfoodblog.api;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Post {

    @JsonProperty private long id;
    @JsonProperty private Date dateCreated;

    @JsonProperty private String restaurantName;
    @JsonProperty private Date dateVisited;
    @JsonProperty private String neighborhood;
    @JsonProperty private String addressStreet;
    @JsonProperty private String addressCity;
    @JsonProperty private String addressState;
    @JsonProperty private String addressZip;
    @JsonProperty private String instagramUrl;
    @JsonProperty private List<String> order;
    @JsonProperty private double cost;
    @JsonProperty private List<String> tags;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Date getDateCreated() { return dateCreated; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }

}
