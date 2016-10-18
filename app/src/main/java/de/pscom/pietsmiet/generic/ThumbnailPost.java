package de.pscom.pietsmiet.generic;

import android.graphics.drawable.Drawable;

import java.util.Date;

import de.pscom.pietsmiet.adapters.CardItem;
import de.pscom.pietsmiet.util.Converter;
import de.pscom.pietsmiet.util.DrawableFetcher;


/**
 * Represents a post with a thumbnail
 */
public class ThumbnailPost extends Post {
    private final Drawable thumbnail;

    public ThumbnailPost(String title, String description, String type, Date date, String thumbnail) {
        this(title, description, type, date, DrawableFetcher.getDrawableFromUrl(thumbnail));
    }

    public ThumbnailPost(String title, String description, String type, Date date, Drawable
            thumbnail) {
        super(title, description, type, date);
        this.thumbnail = thumbnail;
    }

    public Drawable getThumbnail() {
        return thumbnail;
    }

    @Override
    public CardItem getCardItem() {
        return new CardItem(title, description, date, thumbnail, Converter.convertType(type));
    }
}
