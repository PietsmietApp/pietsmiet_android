package de.pscom.pietsmiet.repository;

import java.util.Date;

import de.pscom.pietsmiet.generic.Post;
import rx.Observable;

public interface PostRepository {
    Observable<Post> fetchNextPosts(Date lastPostDate, int numPosts);

    Observable<Post> fetchNewPosts(Date firstPostDate, int numPosts);
}
