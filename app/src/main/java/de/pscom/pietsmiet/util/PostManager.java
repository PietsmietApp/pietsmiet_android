package de.pscom.pietsmiet.util;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.DateTag;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.generic.ViewItem;
import de.pscom.pietsmiet.presenter.FacebookPresenter;
import de.pscom.pietsmiet.presenter.FirebasePresenter;
import de.pscom.pietsmiet.presenter.TwitterPresenter;
import de.pscom.pietsmiet.presenter.YoutubePresenter;
import rx.Observable;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.AllTypes;
import static de.pscom.pietsmiet.util.PostType.NEWS;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.PS_VIDEO;
import static de.pscom.pietsmiet.util.PostType.TWITTER;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;
import static de.pscom.pietsmiet.util.PostType.getPossibleTypes;

public class PostManager {
    private static boolean FETCH_DIRECTION_DOWN = false;

    private final MainActivity mView;

    // All posts loaded
    @SuppressWarnings("CanBeFinal")
    private List<ViewItem> allPosts = new ArrayList<>();

    private int postLoadCount = 15;

    private Subscription subLoadingPosts;
    private Subscription subAddingPosts;
    private Subscription subUpdatePosts;

    public PostManager(MainActivity view) {
        mView = view;
    }

    /**
     * Adds posts to the post list, where all posts are stored; removes duplicates and sorts it.
     * It also stores the last and first twitter Id
     */

    @SuppressWarnings("WeakerAccess")
    public void addPosts(List<Post> posts) {
        List<ViewItem> lposts = new ArrayList<>();
        lposts.addAll(posts);
        subAddingPosts = Observable.just(lposts)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMapIterable(list -> list)
                .distinct()
                .filter((post) -> post.getType() != ViewItem.TYPE_POST || SettingsHelper.getSettingsValueForType(((Post) post).getPostType()))
                .doOnNext(post_ -> {
                    if (post_.getType() == ViewItem.TYPE_POST) {
                        Post post = (Post) post_;
                        if (post.getPostType() == TWITTER && (TwitterPresenter.firstTweet == null || TwitterPresenter.firstTweet.getId() < post.getId()))
                            TwitterPresenter.firstTweet = post;
                        if (post.getPostType() == TWITTER && (TwitterPresenter.lastTweet == null || TwitterPresenter.lastTweet.getId() > post.getId()))
                            TwitterPresenter.lastTweet = post;
                    }
                })
                .toSortedList()
                .subscribe(list -> {
                    allPosts.addAll(list);
                }, (throwable) -> PsLog.e("Couldn't update all posts!", throwable), this::updateCurrentPosts);
    }

    /**
     * 1) Iterates through all posts
     * 2) Check if posts have to be shown
     * 3) Adds these posts to the currentPosts list
     * 4) Notifies the adapter about the change
     */
    public void updateCurrentPosts() {
        final Date lastPostDate = getFirstPostDate();
        subUpdatePosts = Observable.just(allPosts)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .filter((ViewItem post) -> post.getType() == ViewItem.TYPE_POST && SettingsHelper.getSettingsValueForType(((Post) post).getPostType()))
                .toSortedList()
                .map(list -> {
                    List<ViewItem> listV = new ArrayList<>();
                    listV.addAll(list);
                    Date lastPostDate_ = lastPostDate;
                    for(ViewItem vi: listV) {
                        if(vi.getType() == ViewItem.TYPE_POST && vi.getDate().before(lastPostDate_) ) {
                            if (!new SimpleDateFormat("dd", Locale.getDefault()).format(vi.getDate()).equals(new SimpleDateFormat("dd", Locale.getDefault()).format(lastPostDate_)))
                                list.add(list.indexOf(vi), new DateTag(vi.getDate()));
                            lastPostDate_ = vi.getDate();
                        }
                    }
                    return list;
                })
                .subscribe(list -> {
                    allPosts.clear();
                    allPosts.addAll(list);
                }, throwable -> PsLog.e("Couldn't update current posts: ", throwable), () -> {
                    if (mView != null) mView.updateAdapter();
                });
    }

    /**
     * Sets the allowedTypes only to the received postType, to display just one category.
     *
     * @param postType Type to display
     */
    public void displayOnlyType(@AllTypes int postType) {
        for (int type : getPossibleTypes()) {
            if (type == postType)
                SharedPreferenceHelper.setSharedPreferenceBoolean(mView, SettingsHelper.getSharedPreferenceKeyForType(type), true);
            else
                SharedPreferenceHelper.setSharedPreferenceBoolean(mView, SettingsHelper.getSharedPreferenceKeyForType(type), false);
        }
        SettingsHelper.loadAllSettings(mView);
        updateCurrentPosts();
    }

    /**
     * Returns currentPosts.
     *
     * @return All posts that are displayed (the adapter is "linked" to this arrayList)
     */
    public List<ViewItem> getPostsToDisplay() {
        return allPosts;
    }

    @Nullable
    private Post getFirstPost() {
        if (!allPosts.isEmpty()) {
            for (ViewItem vi : allPosts) {
                if(vi.getType() == ViewItem.TYPE_POST) return (Post) vi;
            }
        }
        return null;
    }

    @Nullable
    private Post getLastPost() {
        Post lastPost = null;
        if (!allPosts.isEmpty()) {
            for (ViewItem vi : allPosts) {
                if(vi.getType() == ViewItem.TYPE_POST) lastPost = (Post) vi;
            }
        }
        return lastPost;
    }

    /**
     * Returns the date of the first post element in allPosts.
     * If no post is present, the returned date will be:
     * Current date - 1 Day
     *
     * @return Date
     */
    private Date getFirstPostDate() {
        Post p = getFirstPost();
        return (p == null) ? new Date(new Date().getTime() - 864000000) : p.getDate();
    }

    /**
     * Returns the date of the last post element in allPosts.
     * If no post is present, the returned date will be the current date.
     *
     * @return Date
     */
    private Date getLastPostDate() {
        Post lPost = getLastPost();
        return (lPost == null) ? new Date() : lPost.getDate();
    }

    /**
     * Root fetching Method to call all specific fetching methods for older Posts.
     *
     * @param numPosts int
     **/
    public void fetchNextPosts(int numPosts) {
        if (!NetworkUtil.isConnected(mView)) {
            mView.showSnackbar("Keine Netzwerkverbindung");
            mView.setRefreshAnim(false);
            mView.scrollListener.resetState();
            return;
        }
        FETCH_DIRECTION_DOWN = true;
        postLoadCount = numPosts;
        mView.setRefreshAnim(true);
        PsLog.v("Loading the next " + postLoadCount + " posts");
        Observable<Post.PostBuilder> twitterObs = SettingsHelper.boolCategoryTwitter ?
                new TwitterPresenter(mView).fetchPostsUntilObservable(getLastPostDate(), numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> youtubeObs = SettingsHelper.boolCategoryYoutubeVideos ?
                new YoutubePresenter(mView).fetchPostsUntilObservable(getLastPostDate(), numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> firebaseObs = (SettingsHelper.boolCategoryPietcast || SettingsHelper.boolCategoryPietsmietNews || SettingsHelper.boolCategoryPietsmietUploadplan || SettingsHelper.boolCategoryPietsmietVideos) ?
                new FirebasePresenter(mView).fetchPostsUntilObservable(getLastPostDate(), numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> facebookObs = SettingsHelper.boolCategoryFacebook ?
                new FacebookPresenter(mView).fetchPostsUntilObservable(getLastPostDate(), numPosts)
                : Observable.empty();
        manageEmittedPosts(Observable.mergeDelayError(twitterObs, youtubeObs, firebaseObs, facebookObs));
    }

    /**
     * Root fetching Method to call all specific fetching methods for new Posts.
     **/
    public void fetchNewPosts() {
        if (!NetworkUtil.isConnected(mView)) {
            mView.showSnackbar("Keine Netzwerkverbindung");
            mView.setRefreshAnim(false);
            mView.scrollListener.resetState();
            return;
        }
        FETCH_DIRECTION_DOWN = false;
        mView.setRefreshAnim(true);
        PsLog.v("Loading new posts");
        Observable<Post.PostBuilder> twitterObs = SettingsHelper.boolCategoryTwitter ?
                new TwitterPresenter(mView).fetchPostsSinceObservable(getFirstPostDate())
                : Observable.empty();
        Observable<Post.PostBuilder> youtubeObs = SettingsHelper.boolCategoryYoutubeVideos ?
                new YoutubePresenter(mView).fetchPostsSinceObservable(getFirstPostDate())
                : Observable.empty();
        Observable<Post.PostBuilder> firebaseObs = (SettingsHelper.boolCategoryPietcast || SettingsHelper.boolCategoryPietsmietNews || SettingsHelper.boolCategoryPietsmietUploadplan || SettingsHelper.boolCategoryPietsmietVideos) ?
                new FirebasePresenter(mView).fetchPostsSinceObservable(getFirstPostDate())
                : Observable.empty();
        Observable<Post.PostBuilder> facebookObs = SettingsHelper.boolCategoryFacebook ?
                new FacebookPresenter(mView).fetchPostsSinceObservable(getFirstPostDate())
                : Observable.empty();
        manageEmittedPosts(Observable.mergeDelayError(twitterObs, youtubeObs, firebaseObs, facebookObs));
    }

    /**
     * Subscribes to the merged Observables emitting the loaded posts.
     * Filters the result and finally adds the selected posts to the allPost List with addPosts().
     *
     * @param postObs Observable<PostBuilder> emitting loaded posts from various sources.
     */
    private void manageEmittedPosts(Observable<Post.PostBuilder> postObs) {
        subLoadingPosts = postObs.observeOn(Schedulers.io(), true)
                .subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .filter(postBuilder -> postBuilder != null)
                .map(Post.PostBuilder::build)
                .filter(post -> post != null)
                .filter(this::filterWrongPosts)
                .sorted()
                .take(postLoadCount)
                .toList()
                .retryWhen(attempts -> attempts.zipWith(Observable.range(1, 2), (throwable, attempt) -> {
                    if (attempt == 2) throw Exceptions.propagate(throwable);
                    else {
                        mView.showSnackbar("Kritischer Fehler. Ein neuer Ladeversuch wird gestartet...");
                        PsLog.w("Krititischer Fehler, neuer Versuch: ", throwable);
                        return attempt;
                    }
                }).flatMap(retryCount -> Observable.timer((long) Math.pow(2, retryCount), TimeUnit.SECONDS)))
                .timeout(15, TimeUnit.SECONDS)
                .subscribe(items -> {
                    addPosts(items);
                    mView.setRefreshAnim(false);
                    PsLog.v("Finished with " + items.size() + " Posts");
                    new DatabaseHelper(mView).insertPosts(items);
                }, e -> {
                    if (e instanceof TimeoutException) {
                        PsLog.w("Laden dauerte zu lange, Abbruch...");
                        mView.showSnackbar("Konnte Posts nicht laden (Timeout)", Snackbar.LENGTH_INDEFINITE);
                    } else {
                        PsLog.e("Kritischer Fehler beim Laden: ", e);
                        mView.showSnackbar("Kritischer Fehler beim Laden. " +
                                "Der Fehler wurde den Entwicklern gemeldet", Snackbar.LENGTH_INDEFINITE);
                    }
                    mView.setRefreshAnim(false);
                }, () -> mView.setRefreshAnim(false));
    }

    /**
     * Clears / unsubscribes all subscriptions
     * DONT CALL IT TOO OFTEN!
     * Should be called if the App gets closed.
     */
    public void clearSubscriptions() {
        if (subLoadingPosts != null && !subLoadingPosts.isUnsubscribed())
            subLoadingPosts.unsubscribe();
        if (subAddingPosts != null && !subAddingPosts.isUnsubscribed())
            subAddingPosts.unsubscribe();
        if (subUpdatePosts != null && !subUpdatePosts.isUnsubscribed())
            subUpdatePosts.unsubscribe();
    }

    /**
     * Clears all posts from the view and resets variables.
     **/
    public void clearPosts() {
        allPosts.clear();
        TwitterPresenter.lastTweet = null;
        TwitterPresenter.firstTweet = null;
        updateCurrentPosts();
        mView.scrollListener.resetState();
    }

    /**
     * First checks if a video posts is from a unallowed category
     * <p>
     * Then checks if a post is after / before the fetching direction
     * and overrides the previous check if needed.
     *
     * @param post Post object to check
     * @return boolean shouldFilter
     */
    private boolean filterWrongPosts(Post post) {

        boolean shouldFilter;
        if (FETCH_DIRECTION_DOWN) {
            shouldFilter = post.getDate().before(getLastPostDate());
            if (!shouldFilter && post.getPostType() != UPLOADPLAN && post.getPostType() != PIETCAST && post.getPostType() != PS_VIDEO && post.getPostType() != NEWS) {
                Post lPost = getLastPost();

                if(lPost != null) PsLog.w("A post in " + PostType.getName(post.getPostType()) + " is after last date:  " +
                        " Titel: " + post.getTitle() +
                        " Datum: " + post.getDate() +
                        " letzter (ältester) Post " + lPost.getTitle() + " Datum: " + getLastPostDate());
            }
        } else {
            shouldFilter = post.getDate().after(getFirstPostDate());
            if (!shouldFilter && post.getPostType() != UPLOADPLAN && post.getPostType() != PIETCAST && post.getPostType() != PS_VIDEO && post.getPostType() != NEWS) {
                Post firstPost = getFirstPost();

                if(firstPost != null) PsLog.w("A post in " + PostType.getName(post.getPostType()) + " is before last date:  " +
                        " Titel: " + post.getTitle() +
                        " Datum: " + post.getDate() +
                        "\n letzter (neuster) Post " + getFirstPost().getTitle() + " Datum: " + getFirstPostDate());
            }
        }
        return shouldFilter;
    }
}
