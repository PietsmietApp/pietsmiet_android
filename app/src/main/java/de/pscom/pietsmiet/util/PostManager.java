package de.pscom.pietsmiet.util;

import android.content.Context;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import de.pscom.pietsmiet.generic.DateTag;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.generic.ViewItem;
import de.pscom.pietsmiet.interfaces.MainActivityView;
import de.pscom.pietsmiet.interfaces.PostRepository;
import de.pscom.pietsmiet.presenter.TwitterPresenter;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.NEWS;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.PS_VIDEO;
import static de.pscom.pietsmiet.util.PostType.TWITTER;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;

public class PostManager {
    private static boolean FETCH_DIRECTION_DOWN = false;

    private final MainActivityView view;
    private final PostRepository postRepository;
    private final Context context;

    // All posts loaded
    @SuppressWarnings("CanBeFinal")
    private List<ViewItem> allPosts = new ArrayList<>();

    private int postLoadCount = 15;

    private Subscription subLoadingPosts;
    private Subscription subAddingPosts;
    private Subscription subUpdatePosts;

    public PostManager(MainActivityView view, PostRepository postRepository, Context context) {
        this.view = view;
        this.postRepository = postRepository;
        this.context = context;
    }

    /**
     * Adds posts to the post list, where all posts are stored; removes duplicates and sorts it.
     * It also stores the last and first twitter Id
     */

    @SuppressWarnings("WeakerAccess")
    public Observable.Transformer<ViewItem, List<ViewItem>> addPosts() {
        return postObservable -> postObservable
                .distinct()
                .filter(this::filterWrongPosts)
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
                .doOnNext(list -> allPosts.addAll(list));
    }

    /**
     * 1) Iterates through all posts
     * 2) Check if posts have to be shown
     * 3) Adds these posts to the currentPosts list
     * 4) Notifies the adapter about the change
     */
    public void updateCurrentPosts() {
        subUpdatePosts = Observable.just(allPosts)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .filter((ViewItem post) -> post.getType() == ViewItem.TYPE_POST && SettingsHelper.getSettingsValueForType(((Post) post).getPostType()))
                .distinct()
                .toSortedList()
                .map(list -> {
                    List<ViewItem> listV = new ArrayList<>();
                    listV.addAll(list);
                    Date lastPostDate_ = listV.get(0).getDate(); //todo ? not null
                    for (ViewItem vi : listV) {
                        if (vi.getType() == ViewItem.TYPE_POST && vi.getDate().before(lastPostDate_)) {
                            if (!new SimpleDateFormat("dd", Locale.getDefault()).format(vi.getDate()).equals(new SimpleDateFormat("dd", Locale.getDefault()).format(lastPostDate_)))
                                list.add(list.indexOf(vi), new DateTag(vi.getDate()));
                            lastPostDate_ = vi.getDate();
                        }
                    }
                    return list;
                })
                .subscribe(view::loadingCompleted, throwable -> PsLog.e("Couldn't update current posts: ", throwable));
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
                if (vi.getType() == ViewItem.TYPE_POST) return (Post) vi;
            }
        }
        return null;
    }

    @Nullable
    private Post getLastPost() {
        Post lastPost = null;
        if (!allPosts.isEmpty()) {
            for (ViewItem vi : allPosts) {
                if (vi.getType() == ViewItem.TYPE_POST) lastPost = (Post) vi;
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
        PsLog.v("Loading the next " + numPosts + " posts");
        setupLoading();
        subscribeLoadedPosts(postRepository.fetchNextPosts(getLastPostDate(), numPosts), true);
    }

    /**
     * Root fetching Method to call all specific fetching methods for new Posts.
     **/
    public void fetchNewPosts() {
        PsLog.v("Loading new posts");
        setupLoading();
        subscribeLoadedPosts(postRepository.fetchNewPosts(getFirstPostDate()), false);
    }

    public void subscribeLoadedPosts(Observable<Post> observable, boolean fetchDirectionDown){
        subLoadingPosts = observable
                .compose(addPosts())
                .subscribe(items -> {
                    updateCurrentPosts();
                    PsLog.v("Finished with " + items.size() + " Posts");
                    new DatabaseHelper(context).insertPosts(items);
                }, e -> {
                    if (e instanceof TimeoutException) {
                        PsLog.w("Laden dauerte zu lange, Abbruch...");
                        view.loadingFailed("Konnte Posts nicht laden (Timeout)");
                    } else {
                        PsLog.e("Kritischer Fehler beim Laden: ", e);
                        view.loadingFailed("Kritischer Fehler beim Laden. " +
                                "Der Fehler wurde den Entwicklern gemeldet");
                    }
                });
    }

    private void setupLoading() {
        if (!NetworkUtil.isConnected(context)) {
            view.noNetworkError();
            return;
        }
        view.loadingStarted();
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
    }

    /**
     * First checks if a video posts is from a unallowed category
     * <p>
     * Then checks if a post is after / before the fetching direction
     * and overrides the previous check if needed.
     *
     * @param item ViewItem object to check
     * @return boolean shouldFilter
     */
    private boolean filterWrongPosts(ViewItem item) {
        if (!(item instanceof Post)) {
            return true;
        }
        Post post = (Post) item;
        boolean shouldFilter;
        if (FETCH_DIRECTION_DOWN) {
            shouldFilter = post.getDate().before(getLastPostDate());
            if (!shouldFilter && post.getPostType() != UPLOADPLAN && post.getPostType() != PIETCAST && post.getPostType() != PS_VIDEO && post.getPostType() != NEWS) {
                Post lPost = getLastPost();

                if (lPost != null)
                    PsLog.w("A post in " + PostType.getName(post.getPostType()) + " is after last date:  " +
                            " Titel: " + post.getTitle() +
                            " Datum: " + post.getDate() +
                            " letzter (ältester) Post " + lPost.getTitle() + " Datum: " + getLastPostDate());
            }
        } else {
            shouldFilter = post.getDate().after(getFirstPostDate());
            if (!shouldFilter && post.getPostType() != UPLOADPLAN && post.getPostType() != PIETCAST && post.getPostType() != PS_VIDEO && post.getPostType() != NEWS) {
                Post firstPost = getFirstPost();

                if (firstPost != null)
                    PsLog.w("A post in " + PostType.getName(post.getPostType()) + " is before last date:  " +
                            " Titel: " + post.getTitle() +
                            " Datum: " + post.getDate() +
                            "\n letzter (neuster) Post " + getFirstPost().getTitle() + " Datum: " + getFirstPostDate());
            }
        }
        return shouldFilter;
    }
}
