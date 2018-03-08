package de.pscom.pietsmiet.data.facebook;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.pscom.pietsmiet.data.BaseMapper;
import de.pscom.pietsmiet.data.facebook.model.FacebookData;
import de.pscom.pietsmiet.domain.Post;

@Singleton
class FacebookMapper extends BaseMapper<Post, FacebookData> {
    private static final String FACEBOOK_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    @Inject
    FacebookMapper() {
    }

    @Override
    public Post transform(FacebookData facebookData) {
        Post post = new Post();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(FACEBOOK_DATE_FORMAT, Locale.ENGLISH);
            post.setDate(dateFormat.parse(facebookData.getCreatedTime()));
            post.setThumbnailHDUrl(facebookData.getFullPicture());
            post.setThumbnailUrl(facebookData.getPicture());
            post.setTitle(facebookData.getFrom().getName());
            post.setDescription(facebookData.getMessage());
            post.setUrl("https://www.facebook.com/" + facebookData.getId());
        } catch (ParseException e) {
            // todo add error handling
        }
        return post;
    }
}
