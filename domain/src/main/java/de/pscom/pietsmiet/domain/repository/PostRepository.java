package de.pscom.pietsmiet.domain.repository;

import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.domain.Post;
import io.reactivex.Observable;

public interface PostRepository {
    Observable<List<Post>> getOlderPosts(Date lastPostDate, int numPosts);

    Observable<List<Post>> getNewerPosts(Date firstPostDate, int numPosts);
}
