package de.pscom.pietsmiet.backend;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import rx.Observable;

abstract class MainPresenter {
    final MainActivity view;
    Post.PostBuilder postBuilder;
    @SuppressWarnings("CanBeFinal")
    List<Post> posts = new ArrayList<>();

    MainPresenter(MainActivity view) {
        this.view = view;
    }

    /**
     * Fetches all posts after a specific date. Since the given date.
     *
     * @param dAfter Date
     */
    public abstract Observable<Post.PostBuilder> fetchPostsSinceObservable(Date dAfter);

    /**
     * Fetches all posts before a specific date. Until the given date.
     *
     * @param dBefore Date
     */
    public abstract Observable<Post.PostBuilder> fetchPostsUntilObservable(Date dBefore, int numPosts);

}
