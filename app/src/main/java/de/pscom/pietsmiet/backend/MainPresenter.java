package de.pscom.pietsmiet.backend;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;

abstract class MainPresenter {
    MainActivity view;
    Post.PostBuilder postBuilder;
    @SuppressWarnings("CanBeFinal")
    List<Post> posts = new ArrayList<>();

    MainPresenter(MainActivity view) {
        this.view = view;
    }

    /** Fetches all posts after a specific date. Since the given date.
     *  @param dAfter Date
     */
    public abstract void fetchPostsSince(Date dAfter);

    /** Fetches all posts before a specific date. Until the given date.
     *  @param dBefore Date
     */
    public abstract void fetchPostsUntil(Date dBefore, int numPosts);


}
