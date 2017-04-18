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
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.TWITTER;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;
import static de.pscom.pietsmiet.util.PostType.getPossibleTypes;


public class PostManager {
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
     * It also stores the last and first twitter Id
     */

    @SuppressWarnings("WeakerAccess")
    public void addPosts(List<Post> posts) {
        Observable.just(posts)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMapIterable(list -> {
                    list.addAll(allPosts);
                    return list;
                })
                .distinct()
                .doOnNext(post -> {
                    if (post.getPostType() == TWITTER && (TwitterPresenter.firstTweetId < post.getId() || TwitterPresenter.firstTweetId == 0))
                        TwitterPresenter.firstTweetId = post.getId();
                    if (post.getPostType() == TWITTER && (TwitterPresenter.lastTweetId > post.getId() || TwitterPresenter.lastTweetId == 0))
                        TwitterPresenter.lastTweetId = post.getId();
                })
                .toList()
                .subscribe(list -> {
                    allPosts.clear();
                    allPosts.addAll(list);
                    updateCurrentPosts();
                }, Throwable::printStackTrace);
    }

    /**
     * 1) Iterates through all posts
     * 2) Check if posts have be shown or not
     * 3) Adds all posts to the currentPosts list
     * 4) Notifies the adapter about the change
     */
    public void updateCurrentPosts() {
        Observable.just(allPosts)
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
     * Sets the allowedTypes only to the received postType, to display just one category.
     * @param postType
     */
    public void displayOnlyType(@AllTypes int postType) {
        for (int type : getPossibleTypes()) {
            if (type == postType) allowedTypes.put(type, true);
            else allowedTypes.put(type, false);
        }
        updateCurrentPosts();
    }

    /** Returns currentPosts.
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
        if (allowed == null) allowed = true;
        return allowed;
    }

    /**
     * Returns the date of the first post element in allPosts.
     * If no post is present, the returned date will be:
     * Current date - 1 Day
     * @return Date
     */
    private Date getFirstPostDate() {
        if (allPosts.isEmpty()) {
            return new Date(new Date().getTime() - 86400000);
        } else {
            return allPosts.get(0).getDate();
        }
    }

    /**
     * Returns the date of the last post element in allPosts.
     * If no post is present, the returned date will be the current date.
     * @return Date
     */
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
        postLoadCount = numPosts;
        mView.setRefreshAnim(true);
        Observable<Post.PostBuilder> twitterObs = new TwitterPresenter(mView).fetchPostsUntilObservable(getLastPostDate(), numPosts);
        Observable<Post.PostBuilder> youtubeObs = new YoutubePresenter(mView).fetchPostsUntilObservable(getLastPostDate(), numPosts);
        Observable<Post.PostBuilder> uploadplanObs = new PietcastPresenter(mView).fetchPostsUntilObservable(getLastPostDate(), numPosts);
        Observable<Post.PostBuilder> pietcastObs = new FacebookPresenter(mView).fetchPostsUntilObservable(getLastPostDate(), numPosts);
        Observable<Post.PostBuilder> facebookObs = new UploadplanPresenter(mView).fetchPostsUntilObservable(getLastPostDate(), numPosts);
        manageEmittedPosts(Observable.mergeDelayError(twitterObs, youtubeObs, uploadplanObs, pietcastObs, facebookObs));
    }

    /**
     *  Root fetching Method to call all specific fetching methods for new Posts.
     **/
    public void fetchNewPosts() {
        FETCH_DIRECTION_DOWN = false;
        mView.setRefreshAnim(true);
        Observable<Post.PostBuilder> twitterObs = new TwitterPresenter(mView).fetchPostsSinceObservable(getFirstPostDate());
        Observable<Post.PostBuilder> youtubeObs = new YoutubePresenter(mView).fetchPostsSinceObservable(getFirstPostDate());
        Observable<Post.PostBuilder> uploadplanObs = new UploadplanPresenter(mView).fetchPostsSinceObservable(getFirstPostDate());
        Observable<Post.PostBuilder> pietcastObs = new PietcastPresenter(mView).fetchPostsSinceObservable(getFirstPostDate());
        Observable<Post.PostBuilder> facebookObs = new FacebookPresenter(mView).fetchPostsSinceObservable(getFirstPostDate());
        manageEmittedPosts(Observable.mergeDelayError(twitterObs, youtubeObs, uploadplanObs, pietcastObs, facebookObs));
    }


    /**
     * Gets the Post size of allPosts
     *
     * @return Integer size
     **/
    public int getAllPostsCount() {
        return allPosts.size();
    }

    /**
     * Subscribes to the merged Observables emitting the loaded posts.
     * Filters the result and finally adds the selected posts to the allPost List with addPosts().
     * @param postObs Observable<PostBuilder> emitting loaded posts from various sources.
     */
    private void manageEmittedPosts(Observable<Post.PostBuilder> postObs) {
        postObs.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .map(Post.PostBuilder::build)
                .filter(post -> post != null)
                .sorted()
                .filter(this::filterWrongPosts)
                .take(postLoadCount)
                .toList()
                .subscribe(items -> {
                    addPosts(items);
                    mView.setRefreshAnim(false);
                    new DatabaseHelper(mView).insertPosts(items);
                }, e -> {
                    PsLog.w("Fehler bei Laden der Kategorie ", e);
                    mView.showError("Eine oder mehrere Kategorien konnten nicht geladen werden");
                    mView.setRefreshAnim(false);
                });
    }

    /**
     * Clears all posts from the view and resets variables.
     **/
    public void clearPosts() {
        allPosts.clear();
        currentPosts.clear();
        TwitterPresenter.lastTweetId = 0;
        TwitterPresenter.firstTweetId = 0;
        updateCurrentPosts();
    }

    /**
     * Checks if a post is after / before the fetching direction.
     * @param post Post object to check
     * @return boolean shouldFilter
     */
    private boolean filterWrongPosts(Post post) {
        boolean shouldFilter;
        if (FETCH_DIRECTION_DOWN) {
            shouldFilter = post.getDate().before(getLastPostDate());
            if (!shouldFilter && post.getPostType() != UPLOADPLAN && post.getPostType() != PIETCAST) {
                PsLog.w("A post in " + PostType.getName(post.getPostType()) + " is after last date:  " +
                        " Titel: " + post.getTitle() +
                        " Datum: " + post.getDate() +
                        " letzter (ältester) Post Datum: " + getLastPostDate());
            }
        } else {
            shouldFilter = post.getDate().after(getFirstPostDate());
            if (!shouldFilter && post.getPostType() != UPLOADPLAN && post.getPostType() != PIETCAST) {
                PsLog.w("A post in " + PostType.getName(post.getPostType()) + " is before last date:  " +
                        " Titel: " + post.getTitle() +
                        " Datum: " + post.getDate() +
                        " letzter (neuster) Post Datum: " + getFirstPostDate());
            }
        }
        return shouldFilter;
    }
}
