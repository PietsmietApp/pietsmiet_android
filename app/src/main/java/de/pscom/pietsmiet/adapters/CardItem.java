package de.pscom.pietsmiet.adapters;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public class CardItem implements Comparable<CardItem> {

    String description;
    Date datetime;
    String title;
    Drawable thumbnail;
    CardItemType cardItemType;


    /**
     * Creates a new card item
     *
     * @param title        Title of the card
     * @param description  Description or message of the card
     * @param datetime     Time of the card
     * @param cardItemType Type of the card
     */
    public CardItem(String title, String description, Date datetime, CardItemType cardItemType) {
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.cardItemType = cardItemType;
    }

    /**
     * Creates a new card item with a thumbnail
     *
     * @param title        Title of the card
     * @param description  Description or message of the card
     * @param datetime     Time of the card
     * @param cardItemType Type of the card
     * @param thumbnail    Thumbnail image
     */
    public CardItem(String title, String description, Date datetime, Drawable thumbnail, CardItemType cardItemType) {
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.thumbnail = thumbnail;
        this.cardItemType = cardItemType;
    }


    @Nullable
    public Drawable getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(Drawable thumbnail) {
        this.thumbnail = thumbnail;
    }

    public CardItemType getCardItemType() {
        return cardItemType;
    }

    public void setCardItemType(CardItemType type) {
        this.cardItemType = type;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBackgroundColor() {
        String hexColor;
        switch (cardItemType) {
            case TYPE_VIDEO:
            case TYPE_STREAM:
                hexColor = "#ef5350";
                break;
            case TYPE_PIETCAST:
                hexColor = "#5c6bc0";
                break;
            case TYPE_FACEBOOK:
                hexColor = "#ff7043";
                break;
            case TYPE_TWITTER:
                hexColor = "#42a5f5";
                break;
            case TYPE_UPLOAD_PLAN:
                hexColor = "#26a69a";
                break;
            default:
                hexColor = "#bdbdbd";
                break;
        }
        return Color.parseColor(hexColor);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    boolean isVideoView() {
        return cardItemType.equals(CardItemType.TYPE_VIDEO)
                || cardItemType.equals(CardItemType.TYPE_STREAM)
                || cardItemType.equals(CardItemType.TYPE_PIETCAST);
    }

    @Override
    public int compareTo(@NonNull CardItem item) {
        if (item.getDatetime() == null) return 1;
        else if (this.getDatetime() == null) return -1;
        return item.getDatetime().compareTo(this.getDatetime());
    }

    /**
     * Possible Card types
     */
    public enum CardItemType {
        /**
         * Youtube Video
         */
        TYPE_VIDEO,
        /**
         * Twitch Stream
         */
        TYPE_STREAM,
        /**
         * Pietcast
         */
        TYPE_PIETCAST,
        /**
         * Tweet
         */
        TYPE_TWITTER,
        /**
         * Facebook Post
         */
        TYPE_FACEBOOK,
        /**
         * New Uploadplan from pietsmiet.de
         */
        TYPE_UPLOAD_PLAN
    }

}
