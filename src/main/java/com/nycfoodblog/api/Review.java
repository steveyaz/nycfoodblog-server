package com.nycfoodblog.api;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Review {

    @JsonProperty private long id;
    @JsonProperty private Date dateCreated;

    @JsonProperty private String user;
    @JsonProperty private int overall;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Date getDateCreated() { return dateCreated; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }

    public String getUser() { return user; }

}
