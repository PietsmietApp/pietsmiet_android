package de.pscom.pietsmiet.presenter;

import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.pscom.pietsmiet.generic.DateTag;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.generic.ViewItem;
import de.pscom.pietsmiet.repository.PostRepository;
import de.pscom.pietsmiet.repository.TwitterRepository;
import de.pscom.pietsmiet.util.DatabaseHelper;
import de.pscom.pietsmiet.util.NetworkUtil;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SettingsHelper;
import de.pscom.pietsmiet.view.MainActivityView;
import rx.Observable;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.NEWS;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.PS_VIDEO;
import static de.pscom.pietsmiet.util.PostType.TWITTER;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;

public class PostPresenter {
    private static final int LOAD_MORE_ITEMS_COUNT = 25;
    private static final int LOAD_NEW_ITEMS_COUNT = 50;

    private final MainActivityView view;
    private final PostRepository postRepository;
    private final DatabaseHelper databaseHelper;
    private final NetworkUtil networkUtil;

    // All posts loaded
    @SuppressWarnings("CanBeFinal")
    private List<ViewItem> allPosts = new ArrayList<>();

    private Subscription subLoadingPosts;
    private Subscription subUpdatePosts;

    public PostPresenter(MainActivityView view, PostRepository postRepository, DatabaseHelper databaseHelper, NetworkUtil networkUtil) {
        this.view = view;
        this.postRepository = postRepository;
        this.databaseHelper = databaseHelper;
        this.networkUtil = networkUtil;
    }

    /**
     * Adds posts to the post list, where all posts are stored; removes duplicates and sorts it.
     * It also stores the last and first twitter Id
     */

    @SuppressWarnings("WeakerAccess")
    public Observable.Transformer<ViewItem, List<ViewItem>> addPosts(boolean fetchDirectionDown /*todo notifyRange*/) {
        return postObservable -> postObservable
                .distinct()
                .filter((post) -> post.getType() != ViewItem.TYPE_POST || SettingsHelper.getSettingsValueForType(((Post) post).getPostType()))
                .doOnNext(item -> {
                    if (item.getType() == ViewItem.TYPE_POST) {
                        Post post = (Post) item;
                        if (post.getPostType() == TWITTER && (TwitterRepository.firstTweet == null || TwitterRepository.firstTweet.getId() < post.getId()))
                            TwitterRepository.firstTweet = post;
                        if (post.getPostType() == TWITTER && (TwitterRepository.lastTweet == null || TwitterRepository.lastTweet.getId() > post.getId()))
                            TwitterRepository.lastTweet = post;
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
        List<ViewItem> lVItem = new ArrayList<>(); // todo investigate might fix concurrent modification error
        lVItem.addAll(allPosts);
        subUpdatePosts = Observable.just(lVItem)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .filter((ViewItem post) -> post.getType() == ViewItem.TYPE_POST && SettingsHelper.getSettingsValueForType(((Post) post).getPostType()))
                .distinct()
                .toSortedList()
                .map(list -> {
                    if (list == null || list.isEmpty()) return list;
                    List<ViewItem> listV = new ArrayList<>();
                    listV.addAll(list);
                    if (listV.size() == 0) {
                        return list;
                    }
                    Date lastPostDate_ = listV.get(0).getDate();
                    for (ViewItem vi : listV) {
                        if (vi.getType() == ViewItem.TYPE_POST && vi.getDate().before(lastPostDate_)) {
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
                }, throwable -> {
                    PsLog.e("Couldn't update current posts: ", throwable);
                    view.loadingFailed("Posts konnten nicht aktualisiert werden");
                }, view::loadingCompleted);
    }

    /**
     * @return All posts that are displayed (the adapter is "linked" to this arrayList)
     */
    public List<ViewItem> getPostsToDisplay() {
        return allPosts;
    }

    @Nullable
    private synchronized Post getFirstPost() {
        if (!allPosts.isEmpty()) {
            for (ViewItem vi : allPosts) {
                if (vi.getType() == ViewItem.TYPE_POST) return (Post) vi;
            }
        }
        return null;
    }

    @Nullable
    private synchronized Post getLastPost() {
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
        Post post = getFirstPost();
        return (post == null) ? new Date(new Date().getTime() - 864000000) : new Date(post.getDate().getTime() + 1000);
    }

    /**
     * Returns the date of the last post element in allPosts.
     * If no post is present, the returned date will be the current date.
     *
     * @return Date
     */
    private Date getLastPostDate() {
        Post post = getLastPost();
        return (post == null) ? new Date() : new Date(post.getDate().getTime() - 1000);
    }

    /**
     * Root fetching Method to call all specific fetching methods for older Posts.
     **/
    public void fetchNextPosts() {
        PsLog.v("Loading the next " + LOAD_MORE_ITEMS_COUNT + " posts");
        if (!setupLoading()) return;
        subscribeLoadedPosts(postRepository.fetchNextPosts(getLastPostDate(), LOAD_MORE_ITEMS_COUNT), true, getLastPostDate(), LOAD_MORE_ITEMS_COUNT);
    }

    /**
     * Root fetching Method to call all specific fetching methods for new Posts.
     **/
    public void fetchNewPosts() {
        PsLog.v("Loading new posts");
        if (!setupLoading()) return;
        subscribeLoadedPosts(postRepository.fetchNewPosts(getFirstPostDate(), LOAD_NEW_ITEMS_COUNT), false, getFirstPostDate(), LOAD_NEW_ITEMS_COUNT);
    }

    /**
     * Subscribes to the load Post observable and does the error handling
     *
     * @param observable         Fetch next or Fetch new observable to subscribe on
     * @param fetchDirectionDown In which direction the observable is fetching (for the filter and later for notifyAdapter)
     */
    private void subscribeLoadedPosts(Observable<Post> observable, boolean fetchDirectionDown, Date date, int numPosts) {
        subLoadingPosts = observable
                .filter(post -> filterWrongPosts(post, fetchDirectionDown, date))
                .sorted()
                .take(numPosts)
                .retryWhen(attempts -> attempts.zipWith(Observable.range(1, 2), (throwable, attempt) -> {
                    if (attempt == 2) throw Exceptions.propagate(throwable);
                    else {
                        view.loadingFailed("Kritischer Fehler. Ein neuer Ladeversuch wird gestartet...");
                        PsLog.w("Krititischer Fehler, neuer Versuch: ", throwable);
                        return Observable.timer(3L, TimeUnit.SECONDS);
                    }
                }))
                .compose(addPosts(fetchDirectionDown))
                .subscribe(items -> {
                    updateCurrentPosts();
                    PsLog.v("Finished with " + items.size() + " Posts");
                    databaseHelper.insertPosts(items);
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

    /**
     * Checks if network's available and informs the view about the started loading
     */
    private boolean setupLoading() {
        if (!networkUtil.isConnected()) {
            view.noNetworkError();
            return false;
        }
        view.loadingStarted();
        return true;
    }

    /**
     * Clears / unsubscribes all subscriptions
     * DONT CALL IT TOO OFTEN!
     * Should be called if the App gets closed.
     */
    public void stopSubscriptions() {
        if (subLoadingPosts != null && !subLoadingPosts.isUnsubscribed())
            subLoadingPosts.unsubscribe();
        if (subUpdatePosts != null && !subUpdatePosts.isUnsubscribed())
            subUpdatePosts.unsubscribe();
    }

    /**
     * Clears all posts from the view and resets variables.
     **/
    public void clearPosts() {
        allPosts.clear();
        TwitterRepository.lastTweet = null;
        TwitterRepository.firstTweet = null;
    }

    /**
     * Checks if a post is after / before the fetching direction
     * and overrides the previous check if needed.
     *
     * @param post Post object to check
     * @return boolean shouldFilter
     */
    private boolean filterWrongPosts(Post post, boolean fetchDirectionDown, Date date) {
        boolean shouldFilter;
        if (fetchDirectionDown) {
            shouldFilter = post.getDate().before(date);
            if (!shouldFilter && post.getPostType() != UPLOADPLAN && post.getPostType() != PIETCAST && post.getPostType() != PS_VIDEO && post.getPostType() != NEWS) {
                PsLog.w("A post in " + PostType.getName(post.getPostType()) + " is after last date:  " +
                        " Titel: " + post.getTitle() +
                        " Datum: " + post.getDate() +
                        " letzter (ältester) Post Datum: " + date);
            }
        } else {
            shouldFilter = post.getDate().after(date);
            if (!shouldFilter && post.getPostType() != UPLOADPLAN && post.getPostType() != PIETCAST && post.getPostType() != PS_VIDEO && post.getPostType() != NEWS) {
                PsLog.w("A post in " + PostType.getName(post.getPostType()) + " is before last date:  " +
                        " Titel: " + post.getTitle() +
                        " Datum: " + post.getDate() +
                        "\n letzter (neuster) Post: Datum: " + date);
            }
        }
        return shouldFilter;
    }
}
