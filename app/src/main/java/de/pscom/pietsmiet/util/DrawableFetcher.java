package de.pscom.pietsmiet.util;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class DrawableFetcher {

    /**
     * @param post A facebook posting
     * @return The full drawable from the post, if available
     */
    public static String getThumbnailUrlFromFacebook(@Nullable JSONObject post, boolean isHD) throws JSONException {
        if (post != null) {
            if (!isHD && post.has("picture") && post.get("picture") != null) {
                return post.get("picture").toString();
            }
            if (isHD && post.has("full_picture") && post.get("full_picture") != null) {
                return post.get("full_picture").toString();
            }
        }
        return null;
    }

}
