package de.pscom.pietsmiet.repository;

import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.generic.ViewItem;
import rx.Observable;

public interface PostRepository {
    Observable<Post> fetchNextPosts(Date lastPostDate, int numPosts);

    Observable<Post> fetchNewPosts(Date firstPostDate, int numPosts);

    void cachePosts(List<Post> posts);

    void clear();
}
