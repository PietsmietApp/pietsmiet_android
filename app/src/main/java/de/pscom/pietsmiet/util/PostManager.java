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
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.AllTypes;
import static de.pscom.pietsmiet.util.PostType.TWITTER;
import static de.pscom.pietsmiet.util.PostType.getPossibleTypes;


public class PostManager {
    public static boolean CLEAR_CACHE_FLAG = false;
    private static boolean FETCH_DIRECTION_DOWN = false;

    private final MainActivity mView;
    public Map<Integer, Boolean> allowedTypes = new HashMap<>();
    // Posts that are currently displayed in adapter
    @SuppressWarnings("CanBeFinal")
    private List<Post> currentPosts = new ArrayList<>();
    // All posts loaded
    @SuppressWarnings("CanBeFinal")
    private List<Post> allPosts = new ArrayList<>();

    private int postLoadCount = 15;


    public PostManager(MainActivity view) {
        mView = view;
    }

    /**
     * Adds posts to the post list, where all posts are stored; removes duplicates and sorts it.
     * This happens on a background thread
     *
     * @param lPosts posts to add
     */

    public void addPosts(List<Post> lPosts) {
        // Neues Objekt erstellen, da ansonsten nur Pointer übergeben wird
        List<Post> listPosts = new ArrayList<>();
        listPosts.addAll(lPosts);

        if (listPosts.size() == 0) {
            PsLog.w("addPosts called with zero posts");
            return;
        }

        if (listPosts.size() < mView.NUM_POST_TO_LOAD_ON_START && DatabaseHelper.FLAG_POSTS_LOADED_FROM_DB) {
            DatabaseHelper.FLAG_POSTS_LOADED_FROM_DB = false;
            fetchNextPosts(mView.NUM_POST_TO_LOAD_ON_START);

            return;
        }

        listPosts.addAll(getAllPosts());

        Observable.just(listPosts)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .distinct()
                .sorted()
                .doOnNext(post -> {
                    if (post.getPostType() == TWITTER && (TwitterPresenter.firstTweetId < post.getId() || TwitterPresenter.firstTweetId == 0))
                        TwitterPresenter.firstTweetId = post.getId();
                    if (post.getPostType() == TWITTER && (TwitterPresenter.lastTweetId > post.getId() || TwitterPresenter.lastTweetId == 0))
                        TwitterPresenter.lastTweetId = post.getId();
                })
                .toList()
                .subscribe(items -> {
                    allPosts.clear();
                    allPosts.addAll(items);
                    if (DatabaseHelper.FLAG_POSTS_LOADED_FROM_DB) {
                        DatabaseHelper.FLAG_POSTS_LOADED_FROM_DB = false;
                    } else {
                        new DatabaseHelper(mView).insertPosts(items, mView);
                    }
                }, Throwable::printStackTrace, this::updateCurrentPosts);
    }

    /**
     * 1) Iterates through all posts
     * 2) Check if posts have be shown or not
     * 3) Adds all posts to the currentPosts list
     * 4) Notifies the adapter about the change
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
    private List<Post> getAllPosts() {
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
     * Root fetching Method to call all specific fetching methods for older Posts.
     *
     * @param numPosts int
     **/
    public void fetchNextPosts(int numPosts) {
        FETCH_DIRECTION_DOWN = true;
        //todo if this is called and fetchNewPosts too cancel all RxSubs
        postLoadCount = numPosts;
        mView.setRefreshAnim(true);
        Observable<Post.PostBuilder> twitterObs = new TwitterPresenter(mView).fetchPostsUntilObservable(getLastPostDate(), numPosts);
        Observable<Post.PostBuilder> youtubeObs = new YoutubePresenter(mView).fetchPostsUntilObservable(getLastPostDate(), numPosts);
        //Observable<Post.PostBuilder> uploadplanObs = new PietcastPresenter(mView).fetchPostsUntilObservable(getLastPostDate(), numPosts);
        Observable<Post.PostBuilder> pietcastObs = new FacebookPresenter(mView).fetchPostsUntilObservable(getLastPostDate(), numPosts);
        Observable<Post.PostBuilder> facebookObs = new UploadplanPresenter(mView).fetchPostsUntilObservable(getLastPostDate(), numPosts);

        Observable.mergeDelayError(twitterObs, youtubeObs, /*uploadplanObs, */pietcastObs, facebookObs)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .onBackpressureBuffer()
                .map(Post.PostBuilder::build)
                .filter(post -> post != null)
                .distinct()
                .toSortedList()
                .subscribe(this::addPostsToQueue, e -> {
                    PsLog.e(e.toString());
                    mView.showError("Eine oder mehrere Kategorien konnten nicht geladen werden");
                });
    }

    /**
     * Root fetching Method to call all specific fetching methods for new Posts.
     **/
    public void fetchNewPosts() {
        FETCH_DIRECTION_DOWN = false;
        mView.setRefreshAnim(true);
        Observable<Post.PostBuilder> twitterObs = new TwitterPresenter(mView).fetchPostsSinceObservable(getFirstPostDate());
        Observable<Post.PostBuilder> youtubeObs = new YoutubePresenter(mView).fetchPostsSinceObservable(getFirstPostDate());
        //Observable<Post.PostBuilder> uploadplanObs = new UploadplanPresenter(mView).fetchPostsSinceObservable(getFirstPostDate());
        Observable<Post.PostBuilder> pietcastObs = new PietcastPresenter(mView).fetchPostsSinceObservable(getFirstPostDate());
        Observable<Post.PostBuilder> facebookObs = new FacebookPresenter(mView).fetchPostsSinceObservable(getFirstPostDate());

        Observable.mergeDelayError(twitterObs, youtubeObs, /*uploadplanObs, */pietcastObs, facebookObs)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .onBackpressureBuffer()
                .map(Post.PostBuilder::build)
                .distinct()
                .toSortedList()
                .subscribe(this::addPostsToQueue, e -> {
                    PsLog.e(e.toString());
                    mView.showError("Eine oder mehrere Kategorien konnten nicht geladen werden");
                });
    }


    /**
     * Gets the Post size of allPosts
     *
     * @return Integer size
     **/
    public int getAllPostsCount() {
        return allPosts.size();
    }


    private void addPostsToQueue(List<Post> listPosts) {

        if (listPosts.size() == 0) {
            PsLog.w("addPostsToQueue called with zero posts");
            mView.setRefreshAnim(false);
            return;
        }
        Observable.just(listPosts)
                .observeOn(Schedulers.io())
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.io())
                .flatMapIterable(l -> l)
                .filter(post -> {
                    boolean b = false;
                    if (FETCH_DIRECTION_DOWN) {
                        b = post.getDate().before(getLastPostDate());
                        if (!b)
                            PsLog.w("A post in " + PostType.getName(post.getPostType()) + " is after last date:  " +
                                    " Titel: " + post.getTitle() +
                                    " Datum: " + post.getDate() +
                                    " letzter (ältester) Post Datum: " + getLastPostDate());
                        return b;
                    } else {
                        b = post.getDate().after(getFirstPostDate());
                        if (!b)
                            PsLog.w("A post in " + PostType.getName(post.getPostType()) + " is before last date:  " +
                                    " Titel: " + post.getTitle() +
                                    " Datum: " + post.getDate() +
                                    " letzter (neuster) Post Datum: " + getFirstPostDate());
                        return b;
                    }
                })
                .take(postLoadCount)
                .toList()
                .doOnNext(ignored -> mView.setRefreshAnim(false))
                .subscribe(this::addPosts, Throwable::printStackTrace);
    }

    /**
     * Clears all Posts from the View.
     **/
    public void clearPosts() {
        allPosts.clear();
        currentPosts.clear();
        TwitterPresenter.lastTweetId = 0;
        TwitterPresenter.firstTweetId = 0;
        updateCurrentPosts();
    }
}
