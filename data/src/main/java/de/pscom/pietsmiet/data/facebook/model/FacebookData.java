package de.pscom.pietsmiet.data.facebook.model;

import com.google.gson.annotations.SerializedName;

public class FacebookData {
    @SerializedName("from")
    private FacebookUser from;
    @SerializedName("created_time")
    private String createdTime;
    @SerializedName("message")
    private String message;
    @SerializedName("full_picture")
    private String fullPicture;
    @SerializedName("picture")
    private String picture;
    @SerializedName("id")
    private String id;

    public FacebookUser getFrom() {
        return from;
    }

    public void setFrom(FacebookUser from) {
        this.from = from;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFullPicture() {
        return fullPicture;
    }

    public void setFullPicture(String fullPicture) {
        this.fullPicture = fullPicture;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
