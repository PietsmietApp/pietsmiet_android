package de.pscom.pietsmiet.generic;

import android.graphics.drawable.Drawable;

import java.util.Date;

/**
 * Created by User on 17.10.2016.
 */

public class ThumbnailPost extends Post {
    private final Drawable thumbnail;

    public ThumbnailPost(String title, String description, Date date, Drawable thumbnail) {
        super(title, description, date);
        this.thumbnail = thumbnail;
    }
}
