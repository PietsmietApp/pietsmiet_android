package de.pscom.pietsmiet.presenter;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.generic.Post;
import rx.Observable;

abstract class MainPresenter {
    Post.PostBuilder postBuilder;
    @SuppressWarnings("CanBeFinal")
    List<Post> posts = new ArrayList<>();
    protected Context context;

    MainPresenter(Context context) {
        this.context = context;
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
