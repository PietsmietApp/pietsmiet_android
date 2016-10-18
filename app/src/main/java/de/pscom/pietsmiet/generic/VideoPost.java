package de.pscom.pietsmiet.generic;

import android.graphics.drawable.Drawable;

import java.util.Date;

import de.pscom.pietsmiet.util.DrawableFetcher;

/**
 * Represents a post with a video
 */
public class VideoPost extends ThumbnailPost {
    private final int durationInSeconds;

    public VideoPost(String title, String description, String type, Date date, String thumbnail, int durationInSeconds) {
        this(title, description, type, date, DrawableFetcher.getDrawableFromUrl(thumbnail), durationInSeconds);
    }

    public VideoPost(String title, String description, String type, Date date, Drawable thumbnail, int durationInSeconds) {
        super(title, description, type, date, thumbnail);
        this.durationInSeconds = durationInSeconds;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }
}
