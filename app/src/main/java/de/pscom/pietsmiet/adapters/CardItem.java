package de.pscom.pietsmiet.adapters;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import java.io.InputStream;
import java.net.URL;

import de.pscom.pietsmiet.util.PsLog;

public class CardItem {

    String description;
    String datetime;
    String title;
    Drawable preview;
    CardItemType cardItemType;

    CardItem(String title, String description, String datetime, Drawable preview, CardItemType cardItemType) {
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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
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
}
