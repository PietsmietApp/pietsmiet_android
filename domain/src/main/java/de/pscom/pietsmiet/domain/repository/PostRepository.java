package de.pscom.pietsmiet.domain.repository;

import java.util.Date;

import de.pscom.pietsmiet.domain.Post;
import io.reactivex.Observable;

public interface PostRepository {
    Observable<Post> getOlderPosts(Date lastPostDate, int numPosts);

    Observable<Post> getNewerPosts(Date firstPostDate, int numPosts);
}
