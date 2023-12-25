package com.example.tunisiepromo_miniproject;

import java.util.Date;

public class Rating {
    private String UID;
    private String Content;
    private float rating;
    private Date createdAt;

    public Rating() {
    }

    public Rating(String UID, String content, float rating, Date createdAt) {
        this.UID = UID;
        Content = content;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
