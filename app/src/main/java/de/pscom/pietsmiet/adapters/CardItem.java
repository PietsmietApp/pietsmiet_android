package de.pscom.pietsmiet.adapters;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import de.pscom.pietsmiet.util.PsLog;

public class CardItem implements Comparable<CardItem> {

    String description;
    Date datetime;
    String title;
    Drawable preview;
    CardItemType cardItemType;

    CardItem(String title, String description, Date datetime, Drawable preview, CardItemType cardItemType) {
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.preview = preview;
        this.cardItemType = cardItemType;
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
        return Color.parseColor("#bdbdbd");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    static Drawable getDrawableFromUrl(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            return Drawable.createFromStream(is, "src name");
        } catch (Exception e) {
            PsLog.w("Couldn't fetch thumbnail: " + e.toString());
            return null;
        }
    }

    public enum CardItemType {
        TYPE_VIDEO,
        TYPE_STREAM,
        TYPE_PIETCAST,
        TYPE_TWITTER,
        TYPE_FACEBOOK,
        TYPE_UPLOAD_PLAN
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
}
