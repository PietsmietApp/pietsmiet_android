package de.pscom.pietsmiet.util;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.mcsoxford.rss.MediaThumbnail;
import org.mcsoxford.rss.RSSItem;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import facebook4j.Post;
import twitter4j.MediaEntity;
import twitter4j.Status;

public class DrawableFetcher {

    /**
     * @param post A facebook posting
     * @return The drawable from the post, if available
     */
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

    /**
     * @param rssItem A rss item
     * @return The drawable from the item, if available
     */
    @Nullable
    public static Drawable getDrawableFromRss(@Nullable RSSItem rssItem) {
        if (rssItem != null) {
            List<MediaThumbnail> thumbs = rssItem.getThumbnails();
            if (thumbs != null
                    && thumbs.size() > 0
                    && thumbs.get(0) != null
                    && thumbs.get(0).getUrl() != null) {
                return getDrawableFromUrl(thumbs.get(0).getUrl().toString());
            }
        }
        return null;
    }

    /**
     * @param status A tweet
     * @return The drawable from the tweet, if available
     */
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

    /**
     * @param url Url to the image
     * @return A drawable from the url
     */
    @Nullable
    public static Drawable getDrawableFromUrl(@NonNull String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable toReturn = Drawable.createFromStream(is, "src name");
            if (toReturn.getMinimumHeight() > 0 && toReturn.getMinimumWidth() > 0) return toReturn;
        } catch (Exception e) {
            PsLog.w("Couldn't fetch thumbnail: " + e.toString());
        }
        return null;
    }
}
