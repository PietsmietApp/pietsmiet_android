package de.pscom.pietsmiet.adapters;

import android.graphics.drawable.Drawable;

public class CardItem {
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_STREAM = 2;
    public static final int TYPE_SOCIAL_MEDIA = 3;
    public static final int TYPE_UPLOAD_PLAN = 4;
    public static final int TYPE_PIETCAST = 5;

    private String description = "";
    private String datetime = "";
    private String title = "Unknown";
    private Drawable icon = null;
    private int type = 0;

    public CardItem(){

    }

    public CardItem(String title, String description, String datetime, Drawable icon, int type){
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.icon = icon;
        this.type = type;

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
