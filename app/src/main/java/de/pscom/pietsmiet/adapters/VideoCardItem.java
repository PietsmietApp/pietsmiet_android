package de.pscom.pietsmiet.adapters;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import java.util.Date;

public class VideoCardItem extends CardItem {


    public VideoCardItem(String title, String description, Date datetime, Drawable preview, CardItemType cardItemType) {
        super(title, description, datetime, preview, cardItemType);

    }

    public VideoCardItem(String title, String description, Date datetime, String preview, CardItemType cardItemType) {
        super(title, description, datetime, getDrawableFromUrl(preview), cardItemType);
    }

    public Drawable getPreview() {
        return preview;
    }

    public void setPreview(Drawable preview) {
        super.preview = preview;
    }

    @Override
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
            default:
                return super.getBackgroundColor();
        }
        return Color.parseColor(hexColor);
    }
}
