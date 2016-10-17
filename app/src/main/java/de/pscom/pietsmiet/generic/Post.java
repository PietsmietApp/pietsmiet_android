package de.pscom.pietsmiet.generic;

import java.util.Date;

import de.pscom.pietsmiet.adapters.CardItem;
import de.pscom.pietsmiet.util.Converter;

/**
 * Represents a generic post of any kind
 */
public class Post {
    protected final String title;
    protected final String description;
    protected final String type;
    protected final Date date;

    public Post(String title, String description, String type, Date date) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public CardItem getCardItem() {
        return new CardItem(title, description, date, Converter.convertType(type));
    }
}
