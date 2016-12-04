package de.pscom.pietsmiet.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import rx.Observable;
import rx.schedulers.Schedulers;


public class PostManager {
    private final MainActivity mView;
    @SuppressWarnings("CanBeFinal")
    private List<Post> currentPosts = new ArrayList<>();
    @SuppressWarnings("CanBeFinal")
    private List<Post> allPosts = new ArrayList<>();
    public Map<Integer, Boolean> allowedTypes = new HashMap<>();

    public PostManager(MainActivity view) {
        mView = view;
    }

    /**
     * Adds posts to the "global" post list, removes duplicates and sorts it. This is done asynchronous!
     *
     * @param posts Post Items
     */
    public void addPosts(List<Post> posts) {
        if (posts.size() == 0) {
            PsLog.w("addPosts called with zero posts");
            return;
        }

        posts.addAll(getAllPosts());

        Observable.just(posts)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .filter(post -> post != null)
                .distinct()
                .toSortedList()
                .subscribe(items -> {
                    allPosts.clear();
                    allPosts.addAll(items);
                }, Throwable::printStackTrace, this::updateCurrentPosts);
    }

    /**
     * 1) Iterates through all posts
     * 2) Add post to displayed post list if they belong to the current displayed category
     * 3) Sorts the displayed post list
     * 4) Notifies the adapter about the change
     * <p>
     * This should be called as few times as possible because it kills performance if it's called too often
     */
    public void updateCurrentPosts() {
        // Use an array to avoid concurrent modification exceptions todo this could be more beautiful
        Post[] posts = getAllPosts().toArray(new Post[getAllPosts().size() - 1]);

        Observable.just(posts)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .filter(this::isAllowedType)
                .toSortedList()
                .subscribe(list -> {
                    currentPosts.clear();
                    currentPosts.addAll(list);
                }, Throwable::printStackTrace, () -> {
                    if (mView != null) mView.updateAdapter();
                });
    }

    /**
     * @return All fetched posts, whether they're currently shown or not
     */
    public List<Post> getAllPosts() {
        return allPosts;
    }

    /**
     * @return All posts that should be displayed (the adapter is "linked" to this arrayList)
     */
    public List<Post> getPostsToDisplay() {
        return currentPosts;
    }

    /**
     * @param post Post item
     * @return returns true if the specified post is allowed (belongs to the currently shown category / type)
     */
    private boolean isAllowedType(Post post) {
        Boolean allowed = allowedTypes.get(post.getPostType());
        if (allowed == null) {
            allowed = true;
        }
        return allowed;
    }


}
