package de.pscom.pietsmiet.backend;

import java.util.ArrayList;
import java.util.List;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.PsLog;

import static de.pscom.pietsmiet.util.PostType.TypeAllPosts;

class MainPresenter {
    final MainActivity view;
    @TypeAllPosts
    private final int postType;
    Post post;
    @SuppressWarnings("CanBeFinal")
    List<Post> posts = new ArrayList<>();

    MainPresenter(MainActivity view, @TypeAllPosts int postType) {
        this.view = view;
        this.postType = postType;
    }

    /**
     * Publishes the current posts to the specified activity
     */
    void finished() {
        if (view != null) {
            if (posts != null) {
                PsLog.v("Type " + PostType.getName(postType) + " posts: " + posts.size());
                view.addNewPosts(posts);
            } else {
                view.showError("Typ " + PostType.getName(postType) + " konnte nicht geladen werden :(");
            }
        }
    }
}
