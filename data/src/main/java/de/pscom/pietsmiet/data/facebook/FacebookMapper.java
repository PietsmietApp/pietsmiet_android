package de.pscom.pietsmiet.data.facebook;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.pscom.pietsmiet.data.BaseMapper;
import de.pscom.pietsmiet.domain.Post;

@Singleton
class FacebookMapper extends BaseMapper<Post, JSONObject> {
    @Inject
    FacebookMapper() {
    }

    @Override
    public Post transform(JSONObject jsonObject) {
        Post post = new Post();
        try {
            if (!(jsonObject.has("from") && jsonObject.getJSONObject("from").has("name") && jsonObject.has("created_time"))) {
                // error todo
            }

            post.setThumbnailUrl(getThumbnailUrlFromFacebook(jsonObject, false));
            post.setThumbnailUrl(getThumbnailUrlFromFacebook(jsonObject, true));
            post.setTitle(jsonObject.getJSONObject("from").getString("name"));
            post.setDescription((jsonObject.has("message")) ? jsonObject.getString("message") : "");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
            post.setDate(dateFormat.parse(jsonObject.getString("created_time")));
            if (jsonObject.has("id") && jsonObject.getString("id") != null && !jsonObject.getString("id").isEmpty()) {
                post.setUrl("http://www.facebook.com/" + jsonObject.getString("id"));
            }
        } catch (JSONException | ParseException e) {
            // todo add error handling
        }
        return post;
    }

    /**
     * @param post A facebook posting
     * @return The full drawable from the post, if available
     */
    private static String getThumbnailUrlFromFacebook(@Nullable JSONObject post, boolean isHD) throws JSONException {
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
