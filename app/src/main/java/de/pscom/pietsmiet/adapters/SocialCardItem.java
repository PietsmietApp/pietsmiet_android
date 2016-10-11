package de.pscom.pietsmiet.adapters;

import android.graphics.Color;

import java.util.Date;

public class SocialCardItem extends CardItem {
    public SocialCardItem(String title, String description, Date datetime, CardItemType cardItemType) {
        super(title, description, datetime, null, cardItemType);
    }

    public SocialCardItem(String title, String description, CardItemType cardItemType) {
        super(title, description, null, null, cardItemType);
    }

    @Override
    public int getBackgroundColor() {
        String hexColor;
        switch (cardItemType) {
            case TYPE_FACEBOOK:
            case TYPE_TWITTER:
                hexColor = "#42a5f5";
                break;
            case TYPE_UPLOAD_PLAN:
                hexColor = "#26a69a";
                break;
            default:
                return super.getBackgroundColor();
        }
        return Color.parseColor(hexColor);
    }

}
