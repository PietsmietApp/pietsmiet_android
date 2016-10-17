package de.pscom.pietsmiet.generic;

import android.graphics.drawable.Drawable;

import java.util.Date;

/**
 * Created by User on 17.10.2016.
 */

public class VideoPost extends ThumbnailPost {
    private final int durationInSeconds;

    public VideoPost(String title, String description, Date date, Drawable thumbnail, int durationInSeconds) {
        super(title, description, date, thumbnail);
        this.durationInSeconds = durationInSeconds;
    }
}
