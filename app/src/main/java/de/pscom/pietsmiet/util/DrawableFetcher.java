package de.pscom.pietsmiet.util;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.InputStream;
import java.net.URL;

import facebook4j.Post;
import twitter4j.MediaEntity;
import twitter4j.Status;

public class DrawableFetcher {

    @Nullable
    public static Drawable getDrawableFromPost(@Nullable Post post) {
        if (post != null) {
            URL imageUrl = post.getPicture();
            if (imageUrl != null) {
                return getDrawableFromUrl(imageUrl.toString());
            }
        }
        return null;
    }

    @Nullable
    public static Drawable getDrawableFromTweet(@Nullable Status status) {
        if (status != null) {
            MediaEntity[] mediaEntities = status.getMediaEntities();
            if (mediaEntities != null && mediaEntities.length > 0 && mediaEntities[0].getMediaURL() != null) {
                return getDrawableFromUrl(mediaEntities[0].getMediaURL());
            }
        }
        return null;
    }

    @Nullable
    public static Drawable getDrawableFromUrl(@NonNull String url) {
        try {
            PsLog.v("Fetching drawable from URL: " + url);
            InputStream is = (InputStream) new URL(url).getContent();
            return Drawable.createFromStream(is, "src name");
        } catch (Exception e) {
            PsLog.w("Couldn't fetch thumbnail: " + e.toString());
            return null;
        }
    }
}
