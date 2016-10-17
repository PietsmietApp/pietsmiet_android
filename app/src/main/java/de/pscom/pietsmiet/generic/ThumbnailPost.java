package de.pscom.pietsmiet.generic;

import java.util.Date;

import de.pscom.pietsmiet.adapters.CardItem;
import de.pscom.pietsmiet.util.Converter;
import de.pscom.pietsmiet.util.DrawableFetcher;


/**
 * Represents a post with a thumbnail
 */
public class ThumbnailPost extends Post {
    private final String thumbnail;

    public ThumbnailPost(String title, String description, String type, Date date, String thumbnail) {
        super(title, description, type, date);
        this.thumbnail = thumbnail;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public CardItem getCardItem() {
        return new CardItem(title, description, date, DrawableFetcher.getDrawableFromUrl(thumbnail), Converter.convertType(type));
    }
}
