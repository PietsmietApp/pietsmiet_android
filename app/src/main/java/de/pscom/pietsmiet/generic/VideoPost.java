package de.pscom.pietsmiet.generic;

import java.util.Date;

/**
 * Represents a post with a video
 */
public class VideoPost extends ThumbnailPost {
    private final int durationInSeconds;

    public VideoPost(String title, String description, String type, Date date, String thumbnail, int durationInSeconds) {
        super(title, description, type, date, thumbnail);
        this.durationInSeconds = durationInSeconds;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }
}
