package de.pscom.pietsmiet.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.FACEBOOK;
import static de.pscom.pietsmiet.util.PostType.TWITTER;


public class PostManager {
    public static final int DISPLAY_ALL = 10;
    public static final int DISPLAY_SOCIAL = DISPLAY_ALL + 1;

    private List<Post> currentPosts = new ArrayList<>();
    private List<Post> allPosts = new ArrayList<>();
    private MainActivity mView;
    @PostType.TypeDrawer
    private int currentlyDisplayedType = DISPLAY_ALL;

    public PostManager(MainActivity view) {
        mView = view;
    }

    /**
     * Adds a post to the post list. If the post belongs to the current category / type, it'll be shown instant
     *
     * @param post Post Item
     */
    public void addPost(Post post) {

        Observable.just(post)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .filter(item -> {
                    // Use an array to avoid concurrent modification exceptions
                    Post[] posts = getAllPosts().toArray(new Post[getAllPosts().size()]);
                    for (Post post1 : posts) {
                        if (post.getTitle() == null)
                            if (item.getTitle().equals(post1.getTitle()) && item.getDescription().equals(post1.getDescription()) && item.getDate().equals(post1.getDate())) {
                                return false;
                            }

                    }
                    return true;
                })
                .doOnNext(item -> {

                })
                .subscribe(item -> {
                    allPosts.add(item);

                    if (isAllowedType(item)) {
                        currentPosts.add(item);
                    }
                }, Throwable::printStackTrace);
    }

    /**
     * Sorts all posts. This should be called as few times as possible because it kills performance otherwise
     */
    public void sortPosts() {
        Collections.sort(allPosts);

        Observable.just(currentPosts)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(Observable::from)
                .toSortedList()
                .subscribe(list -> {
                    currentPosts.clear();
                    currentPosts.addAll(list);
                    if (mView != null) mView.updateAdapter();
                }, Throwable::printStackTrace);
    }

    /**
     * @return All fetched posts, whether they're currently shown or not
     */
    public List<Post> getAllPosts() {
        return currentPosts;
    }

    /**
     * Switches back to the "all" category. Shows all posts, independent of their category
     */
    public void displayAllPosts() {
        currentlyDisplayedType = DISPLAY_ALL;
        currentPosts.clear();
        currentPosts.addAll(allPosts);
        if (mView != null) mView.updateAdapter();
    }

    /**
     * Show only posts that belong to a certain category / type
     *
     * @param postType Type that the posts should belong to
     */
    public void displayOnlyPostsFromType(@PostType.TypeDrawer int postType) {
        currentlyDisplayedType = postType;
        Observable.just(allPosts)
                .flatMap(Observable::from)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(this::isAllowedType)
                .toList()
                .subscribe(posts -> {
                    currentPosts.clear();
                    currentPosts.addAll(posts);
                    if (mView != null) {
                        mView.updateAdapter();
                        mView.scrollToTop();
                    }
                }, Throwable::printStackTrace);
    }

    /**
     * @param post Post item
     * @return If the specified post belongs to the currently shown category / type or not
     */
    private boolean isAllowedType(Post post) {
        int postType = post.getPostType();
        if (currentlyDisplayedType == DISPLAY_ALL) return true;
        else if (currentlyDisplayedType == DISPLAY_SOCIAL) {
            if (postType == TWITTER
                    || postType == FACEBOOK) {
                return true;
            }
        } else {
            //noinspection WrongConstant
            if (postType == currentlyDisplayedType) return true;
        }
        return false;
    }


}
