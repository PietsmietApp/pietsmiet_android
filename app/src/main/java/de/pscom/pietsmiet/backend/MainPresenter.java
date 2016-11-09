package de.pscom.pietsmiet.backend;

import java.util.ArrayList;
import java.util.List;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.PsLog;

import static de.pscom.pietsmiet.util.PostType.TypeAllPosts;

class MainPresenter {
    @TypeAllPosts
    private final int postType;
    MainActivity view;
    Post post;
    @SuppressWarnings("CanBeFinal")
    List<Post> posts = new ArrayList<>();

    MainPresenter(@TypeAllPosts int postType) {
        this.postType = postType;
    }

    /**
     * Publishes the current posts to the specified activity
     */
    void finished() {
        if (view != null) {
            if (posts != null) {
                PsLog.v("Type" + postType + " posts:" + posts.size());
                view.addNewPosts(posts);
            } else {
                view.showError("Typ" + Integer.toString(postType) + " konnte nicht geladen werden :(");
            }
        }
    }

    /**
     * Add a "callback" activity to this class
     *
     * @param view Activity to call back
     */
    public void onTakeView(MainActivity view) {
        this.view = view;
    }
}
