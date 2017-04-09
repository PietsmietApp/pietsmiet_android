package de.pscom.pietsmiet.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.backend.FacebookPresenter;
import de.pscom.pietsmiet.backend.PietcastPresenter;
import de.pscom.pietsmiet.backend.TwitterPresenter;
import de.pscom.pietsmiet.backend.UploadplanPresenter;
import de.pscom.pietsmiet.backend.YoutubePresenter;
import de.pscom.pietsmiet.generic.Post;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.AllTypes;
import static de.pscom.pietsmiet.util.PostType.TWITTER;
import static de.pscom.pietsmiet.util.PostType.getPossibleTypes;


public class PostManager {
    public static boolean CLEAR_CACHE_FLAG = false;
    public static boolean FETCH_DIRECTION_DOWN = false;

    private final MainActivity mView;
    @SuppressWarnings("CanBeFinal")
    private List<Post> currentPosts = new ArrayList<>();
    @SuppressWarnings("CanBeFinal")
    private List<Post> allPosts = new ArrayList<>();

    private List<Post> queuedPosts = new ArrayList<>();

    public Map<Integer, Boolean> allowedTypes = new HashMap<>();

    private Map<Integer, Boolean> fetchingEnded = new HashMap<>();

    private int numPostLoadCount = 5;

    public PostManager(MainActivity view) {
        mView = view;
        resetFetchingEnded();
    }

    public void resetFetchingEnded() {
        fetchingEnded.clear();
        //queuedPosts.clear();
        for (int k : getPossibleTypes()) {
            fetchingEnded.put(k, false);
        }
    }

    /**
     * Adds posts to the post list, where all posts are stored; removes duplicates and sorts it.
     * This happens on a background thread
     *
     * @param lPosts posts to add
     */

    public void addPosts(List<Post> lPosts) {



        List<Post> listPosts = new ArrayList<>();
        listPosts.addAll(lPosts);

        // ACHTUNG !!! DA HIER NUR DER POINTER ÜBERGEBEN WIRD BRAUCHT MAN EIN NEUES OBJEKT! todo MERKEN! SPART ZEIT ;)
        if (listPosts.size() == 0) {
            PsLog.w("addPosts called with zero posts");
            resetFetchingEnded();
            mView.setRefreshAnim(false);
            queuedPosts.clear();
            return;
        }
        queuedPosts.clear();

        if(listPosts.size() < mView.NUM_POST_TO_LOAD_ON_START && DatabaseHelper.FLAG_POSTS_LOADED_FROM_DB) {
            DatabaseHelper.FLAG_POSTS_LOADED_FROM_DB = false;
            fetchNextPosts(mView.NUM_POST_TO_LOAD_ON_START);
            
            return;
        }

        listPosts.addAll(getAllPosts());
        // Use an array to avoid concurrent modification exceptions todo this could be more beautiful
        Post[] posts = listPosts.toArray(new Post[listPosts.size()]);

        Observable.just(posts)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .filter(post -> post != null)
                .distinct()
                .toSortedList()
                .flatMap(Observable::from)
                .map(post -> {
                    if(post.getPostType() == TWITTER && (TwitterPresenter.firstTweetId < post.getId() || TwitterPresenter.firstTweetId == 0) ) TwitterPresenter.firstTweetId = post.getId();
                    if(post.getPostType() == TWITTER && (TwitterPresenter.lastTweetId > post.getId() || TwitterPresenter.lastTweetId == 0) ) TwitterPresenter.lastTweetId = post.getId();
                    return post;
                })
                .toSortedList()
                .subscribe(items -> {
                    allPosts.clear();
                    allPosts.addAll(items);
                    if(DatabaseHelper.FLAG_POSTS_LOADED_FROM_DB) {
                        DatabaseHelper.FLAG_POSTS_LOADED_FROM_DB = false;
                    } else {
                        //new DatabaseHelper(mView).insertPosts(items, mView);
                        //todo removed instant caching for performance + less bugs
                    }
                }, Throwable::printStackTrace, this::updateCurrentPosts);
    }

    /**
     * 1) Iterates through all posts
     * 2) Check if posts have be shown or not
     * 3) Adds all posts to the currentPosts list
     * 4) Notifies the adapter about the change
     * <p>
     * This should be called as few times as possible because it kills performance if it's called too often
     */
    public void updateCurrentPosts() {
        // Use an array to avoid concurrent modification exceptions todo this could be more beautiful
        Post[] posts = getAllPosts().toArray(new Post[getAllPosts().size()]);

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

    public void displayOnlyType(@AllTypes int postType) {
        for (int type : getPossibleTypes()) {
            if (type == postType) allowedTypes.put(type, true);
            else allowedTypes.put(type, false);
        }
        updateCurrentPosts();
    }

    /**
     * @return All fetched posts, whether they are currently shown or not
     */
    public List<Post> getAllPosts() {
        return allPosts;
    }

    /**
     * @return All posts that are displayed (the adapter is "linked" to this arrayList)
     */
    public List<Post> getPostsToDisplay() {
        return currentPosts;
    }

    /**
     * @param post Post object
     * @return returns true if the specified post is allowed (belongs to the currently shown categories / types)
     */
    private boolean isAllowedType(Post post) {
        Boolean allowed = allowedTypes.get(post.getPostType());
        if (allowed == null) {
            allowed = true;
        }
        return allowed;
    }

    private Date getFirstPostDate() {
        if (allPosts.isEmpty()) {
            return new Date(new Date().getTime() - 86400000);
            // setzte tag auf vorherigen
        } else {
            return allPosts.get(0).getDate();
        }
    }


    private Date getLastPostDate() {
        if (allPosts.isEmpty()) {
            return new Date();
        } else {
            return allPosts.get(allPosts.size() - 1).getDate();
        }
    }

    /**
     *      Root fetching Method to call all specific fetching methods for older Posts.
     *      @param numPosts int
     **/
    public void fetchNextPosts(int numPosts) {
        FETCH_DIRECTION_DOWN = true;
        //todo if this is called and fetchNewPosts too cancle all RxSubs
        numPostLoadCount = numPosts;
        mView.setRefreshAnim(true);
        //todo übergangslösung? da hier und in scrolllistener festgelegt
        new TwitterPresenter(mView).fetchPostsUntil(getLastPostDate(), numPosts);
        new YoutubePresenter(mView).fetchPostsUntil(getLastPostDate(), numPosts);
        new PietcastPresenter(mView).fetchPostsUntil(getLastPostDate(), numPosts);
        new FacebookPresenter(mView).fetchPostsUntil(getLastPostDate(), numPosts);
        new UploadplanPresenter(mView).fetchPostsUntil(getLastPostDate(), numPosts);
    }

    /**
     *      Root fetching Method to call all specific fetching methods for new Posts.
     *
     **/
    public void fetchNewPosts() {
        FETCH_DIRECTION_DOWN = false;
        mView.setRefreshAnim(true);
        new TwitterPresenter(mView).fetchPostsSince(getFirstPostDate());
        new YoutubePresenter(mView).fetchPostsSince(getFirstPostDate());
        new UploadplanPresenter(mView).fetchPostsSince(getFirstPostDate());
        new PietcastPresenter(mView).fetchPostsSince(getFirstPostDate());
        new FacebookPresenter(mView).fetchPostsSince(getFirstPostDate());
    }

    /**
     *      Returns true if all Posts are fetched or all fetchung tasks have been executed.
     *      @return Boolean ended
     **/
    public boolean getAllPostsFetched() {
        int isEnded = 0;
        for (Boolean aBoolean : fetchingEnded.values()) {
            if (aBoolean) {
                isEnded++;
            }
        }
        return fetchingEnded.size() == isEnded;
    }

    /**
     *      Gets the Post size of allPosts
     *      @return Integer size
     **/
    public int getAllPostsCount() {
        return allPosts.size();
    }

    /**
     *      Callback for all Fetching methods of each Presenter.
     *      Central point for using the input.
     *      @param listPosts List<Post>
     *      @param type int Presenter type
     **/
    public void onReadyFetch(List<Post> listPosts, @AllTypes int type) {
        PsLog.v("Finished fetching " + PostType.getName(type) + "...");
        if (listPosts != null && listPosts.size() > 0) {
            addPostsToQueue(listPosts, type);
        } else {
            fetchingEnded.put(type, true);
            PsLog.e("No Posts loaded in " + PostType.getName(type) + "...");
            //mView.showError("ERROR fetching " + PostType.getName(type));
            if (getAllPostsFetched()) {
                mView.setRefreshAnim(false);
                addPosts(queuedPosts);
            }


        }

    }


    private void addPostsToQueue(List<Post> listPosts, @AllTypes int type) {
        // ACHTUNG NEUES OBJEKT DA NUR POINTER ÜBERGEBEN!
        List<Post> lPosts = new ArrayList<>();
        lPosts.addAll(listPosts);

        if (lPosts.size() == 0) {
            PsLog.w("addPostsToQueue called with zero posts");
            return;
        }

        queuedPosts.addAll(lPosts);
        fetchingEnded.put(type, true);
        if(getAllPostsFetched()) {
            Post[] posts = queuedPosts.toArray(new Post[queuedPosts.size()]);

            Observable.just(posts)
                    .observeOn(Schedulers.io())
                    .onBackpressureBuffer()
                    .subscribeOn(Schedulers.io())
                    .flatMap(Observable::from)
                    .filter(post -> post != null)
                    .filter(post -> {
                        boolean b = post.getDate().before(getLastPostDate());
                        if (!b ) PsLog.v("!!! - >  A post in " + PostType.getName(type) + " is after last date...  -> TITLE: " + post.getTitle() + " Datum: " + post.getDate() + " letzter Post Datum: " + getLastPostDate());
                        if(b && FETCH_DIRECTION_DOWN) {
                            return b;
                        } else {
                            return true;
                        }
                    })
                    .distinct() // fixme -> does this delete Facebook reposts of Tweets etc?
                    .toSortedList()
                    .flatMap(Observable::from)
                    .take(numPostLoadCount)
                    .toList()
                    .subscribe(items -> {
                        queuedPosts.clear();
                        queuedPosts.addAll(items);
                    }, Throwable::printStackTrace, () -> {
                        // reset fetching is in UpdateAdapter in MainActivity k
                        addPosts(queuedPosts);
                    });
        }

    }

    /**
     *      Clears all Posts from the View.
     *
     **/
    public void clearPosts() {
        allPosts.clear();
        currentPosts.clear();
        TwitterPresenter.lastTweetId = 0;
        TwitterPresenter.firstTweetId = 0;
        updateCurrentPosts();

    }
}
