package de.pscom.pietsmiet.generic;

import java.util.Date;

public class Post {
    private final String title;
    private final String description;
    private final Date date;

    public Post(String title, String description, Date date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }
}
