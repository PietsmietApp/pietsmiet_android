package de.pscom.pietsmiet.repository;

import java.util.Date;

import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.view.MainActivity;
import rx.Observable;

abstract public class MainRepository {
    protected final MainActivity view;

    MainRepository(MainActivity view) {
        this.view = view;
    }

    /**
     * Fetches all posts after a specific date. Since the given date.
     *
     * @param dAfter Date
     */
    public abstract Observable<Post.PostBuilder> fetchPostsSinceObservable(Date dAfter, int numPosts);

    /**
     * Fetches all posts before a specific date. Until the given date.
     *
     * @param dBefore Date
     */
    public abstract Observable<Post.PostBuilder> fetchPostsUntilObservable(Date dBefore, int numPosts);

}
