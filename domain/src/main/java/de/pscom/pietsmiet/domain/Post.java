package de.pscom.pietsmiet.domain;

import com.sun.istack.internal.Nullable;

import java.util.Date;

public class Post {
    private String username;
    private String title;
    private int duration;
    private String url;
    @Nullable
    private String description;
    @Nullable
    private String thumbnailUrl;
    @Nullable
    private String thumbnailHDUrl;
    private Date date;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getThumbnailHDUrl() {
        return thumbnailHDUrl;
    }

    public void setThumbnailHDUrl(String thumbnailHDUrl) {
        this.thumbnailHDUrl = thumbnailHDUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
