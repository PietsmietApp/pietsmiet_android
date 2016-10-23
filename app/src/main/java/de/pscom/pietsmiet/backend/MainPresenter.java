package de.pscom.pietsmiet.backend;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.PsLog;

import static de.pscom.pietsmiet.util.PostType.TypeAllPosts;

public class MainPresenter {
    @TypeAllPosts
    private int postType;
    private MainActivity view;
    Post post;

    MainPresenter(@TypeAllPosts int postType) {
        this.postType = postType;
    }

    /**
     * Publishes the current post to the specified activity
     */
    void publish() {
        if (view != null) {
            if (post != null) {
                post.setPostType(postType);
                view.addNewPost(post);
            } else {
                view.showError("Typ" + Integer.toString(postType) + " konnte nicht geladen werden :(");
            }
        }
    }

    void finished() {
        PsLog.v("Type" + postType + " finished loading;");
        if (view != null) view.updateCurrentPosts();
    }

    /**
     * Add a "callback" activity to this class
     * @param view Activity to call back
     */
    public void onTakeView(MainActivity view) {
        this.view = view;
    }
}
