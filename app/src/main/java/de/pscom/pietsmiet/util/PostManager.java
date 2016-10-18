package de.pscom.pietsmiet.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static de.pscom.pietsmiet.util.PostType.FACEBOOK;
import static de.pscom.pietsmiet.util.PostType.TWITTER;


public class PostManager {
    public static final int DISPLAY_ALL = 10;
    public static final int DISPLAY_SOCIAL = DISPLAY_ALL + 1;

    private List<Post> currentPorts = new ArrayList<>();
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
/*
        Observable.just(getAllPosts())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(Observable::from)
                .filter(card -> !card.getTitle().equals(cardItem.getTitle()) || !card.getDescription().equals(cardItem.getDescription()))
                .doOnNext(card -> {
                    if (isAllowedType(cardItem)) {
                        currentPorts.add(cardItem);
                        Collections.sort(currentPorts);
                        if (mView != null) {
                            mView.updateAdapter();
                        }
                    }
                })
                .toSortedList()
                .subscribe(list -> {
                    allPosts.clear();
                    allPosts.addAll(list);
                });
        */

        //todo: do this iteration asynchrounous! (rxjava?) like above, but better & working
        boolean add = true;
        try {
            for (Post post1 : getAllPosts()) {
                if (post1.getTitle().equals(post.getTitle()) && post1.getDescription().equals(post.getDescription())) {
                    add = false;
                }
            }
        } catch (Exception e){
            PsLog.i(e.getMessage());
        }


        if (add) {
            allPosts.add(post);
            Collections.sort(allPosts);

            if (isAllowedType(post)) {
                currentPorts.add(post);
                Collections.sort(currentPorts);
                if (mView != null) mView.updateAdapter();
            }
        }


    }

    /**
     * @return All fetched posts, whether they're currently shown or not
     */
    public List<Post> getAllPosts() {
        return currentPorts;
    }

    /**
     * Switches back to the "all" category. Shows all posts, independent of their category
     */
    public void displayAllPosts() {
        currentlyDisplayedType = DISPLAY_ALL;
        currentPorts.clear();
        currentPorts.addAll(allPosts);
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
                    currentPorts.clear();
                    currentPorts.addAll(posts);
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
