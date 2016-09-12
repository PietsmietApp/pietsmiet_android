package de.pscom.pietsmiet.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.pscom.pietsmiet.util.PsLog;

public class CardItem {
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_STREAM = 2;
    public static final int TYPE_PIETCAST = 3;
    public static final int TYPE_SOCIAL_MEDIA_TWITTER = 4;
    public static final int TYPE_SOCIAL_MEDIA_FACEBOOK = 5;
    public static final int TYPE_UPLOAD_PLAN = 6;

    private String description = "";
    private String datetime = "";
    private String title = "Unknown";
    private Drawable icon = null;
    private Drawable preview = null;
    private int type = 0;

    public CardItem(){

    }

    public CardItem(String title, String description, String datetime, Drawable icon, Drawable preview, int type){
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.icon = icon;
        this.preview = preview;
        this.type = type;
    }

    public CardItem(String title, String description, String datetime, Drawable icon, String preview, int type){
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.icon = icon;
        this.preview = getDrawableFromUrl(preview);
        this.type = type;
    }

    public CardItem(String title, String description, String datetime, Drawable icon, int typeUploadPlan) {
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.icon = icon;
        this.type = typeUploadPlan;
    }

    public Drawable getPreview() {
        return preview;
    }

    public void setPreview(Drawable preview) {
        this.preview = preview;
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

    private Drawable getDrawableFromUrl(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
