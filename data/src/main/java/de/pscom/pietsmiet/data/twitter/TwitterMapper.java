package de.pscom.pietsmiet.data.twitter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.pscom.pietsmiet.data.BaseMapper;
import de.pscom.pietsmiet.data.twitter.model.TwitterStatus;
import de.pscom.pietsmiet.data.twitter.model.TwitterUser;
import de.pscom.pietsmiet.domain.Post;

@Singleton
class TwitterMapper extends BaseMapper<Post, TwitterStatus> {
    @Inject
    TwitterMapper() {
    }

    //todo Error Handling
    @Override
    public Post transform(TwitterStatus status) {
        Post post = new Post();
        post.setDescription(status.text);
        try {
            post.setDate(getTwitterDate(status.createdAt));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        post.setUrl("https://twitter.com/" + status.user.screenName + "/status/" + status.idStr);
        post.setUsername(status.user.screenName);
        post.setTitle(getDisplayName(status.user));
        if (status.entities.media != null && status.entities.media.size() > 0) {
            post.setThumbnailHDUrl(status.entities.media.get(0).mediaUrlHttps);
            post.setThumbnailUrl(status.entities.media.get(0).mediaUrlHttps + ":thumb");
        }
        return post;
    }

    private static Date getTwitterDate(String date) throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("EE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
        return sf.parse(date);
    }

    /**
     * @return A more human readable and static user name
     */
    private String getDisplayName(TwitterUser user) {
        int userId = (int) Math.max(Math.min(Integer.MAX_VALUE, user.id), Integer.MIN_VALUE);
        switch (userId) {
            case 109850283:
                return "Piet";
            case 832560607:
                return "Sep";
            case 120150508:
                return "Jay";
            case 400567148:
                return "Chris";
            case 394250799:
                return "Brammen";
            default:
                return user.screenName;
        }
    }
}
