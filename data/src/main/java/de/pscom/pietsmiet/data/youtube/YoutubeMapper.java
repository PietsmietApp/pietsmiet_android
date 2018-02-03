package de.pscom.pietsmiet.data.youtube;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import de.pscom.pietsmiet.data.BaseMapper;
import de.pscom.pietsmiet.data.youtube.model.YoutubeItem;
import de.pscom.pietsmiet.data.youtube.model.YoutubeSnippet;
import de.pscom.pietsmiet.domain.Post;

public class YoutubeMapper extends BaseMapper<Post, YoutubeItem> {
    @Override
    public Post transform(YoutubeItem item) {
        //todo Error Handling
        Post post = new Post();
        if (item.getId() != null) {
            String videoID = item.getId().getVideoId();
            if (videoID != null && !videoID.isEmpty()) {
                post.setUrl("http://www.youtube.com/watch?v=" + videoID);
            }
        }
        YoutubeSnippet snippet = item.getSnippet();

        if (snippet != null) {
            post.setThumbnailUrl(snippet.getThumbnails().getMedium().getUrl());
            post.setThumbnailHDUrl(snippet.getThumbnails().getMedium().getUrl());
            post.setTitle(snippet.getTitle());
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                post.setDate(dateFormat.parse(snippet.getPublishedAt()));
            } catch (ParseException e) {

            }
        }
        return post;
    }
}
