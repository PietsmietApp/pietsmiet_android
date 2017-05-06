package de.pscom.pietsmiet.presenter;

import android.content.Context;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.pscom.pietsmiet.R;
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
    private final Context context;

    // All posts loaded
    @SuppressWarnings("CanBeFinal")
    private List<ViewItem> allPosts = new ArrayList<>();

    private Subscription subLoadingPosts;
    private Subscription subUpdatePosts;

    public PostPresenter(MainActivityView view,
                         PostRepository postRepository,
                         DatabaseHelper databaseHelper,
                         NetworkUtil networkUtil,
                         Context context) {
        this.view = view;
        this.postRepository = postRepository;
        this.databaseHelper = databaseHelper;
        this.networkUtil = networkUtil;
        this.context = context;
    }

    /**
     * Sorts allPosts and removes Posts that are not allowed with the current filter settings
     * It also removes and readds all DateTags so there are no DateTags with no posts below them
     * <p>
     * It calls adapter.notifyDataSetChanged because we don't know which items changed
     */
    public void updateSettingsFilters() {
        List<ViewItem> lVItem = new ArrayList<>(); // todo investigate might fix concurrent modification error
        lVItem.addAll(allPosts);
        subUpdatePosts = Observable.just(lVItem)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .filter(viewItem -> viewItem.getType() == ViewItem.TYPE_POST)
                .map(item -> (Post) item)
                .filter(post -> SettingsHelper.getSettingsValueForType(post.getPostType()))
                .toList()
                .compose(addDateTags(LOAD_TYPE.NEW))
                .subscribe(list -> {
                    allPosts.clear();
                    allPosts.addAll(list);
                }, throwable -> {
                    PsLog.e("Couldn't update current posts: ", throwable);
                    view.loadingFailed(context.getString(R.string.error_updating_posts));
                }, view::freshLoadingCompleted);
    }

    /**
     * Takes an Observable of new posts (from APIs or database), removes duplicates,
     * makes sure they are allowed (current Filter settings) and sorts them
     *
     * @return An Observable of a List of ViewItems that can be passed to the view
     */
    public Observable.Transformer<Post, List<Post>> sortAndFilterNewPosts() {
        return observable -> observable.map(l -> l)
                .distinct()
                .filter(post -> SettingsHelper.getSettingsValueForType(post.getPostType()))
                .doOnNext(post -> {
                    if (post.getPostType() == TWITTER && (TwitterRepository.firstTweet == null || TwitterRepository.firstTweet.getId() < post.getId()))
                        TwitterRepository.firstTweet = post;
                    if (post.getPostType() == TWITTER && (TwitterRepository.lastTweet == null || TwitterRepository.lastTweet.getId() > post.getId()))
                        TwitterRepository.lastTweet = post;
                })
                .toSortedList();
    }

    /**
     * Adds DateTags (day separators) to the List of *Posts*
     *
     * @return A Observable of a List with ViewItems
     */
    public Observable.Transformer<List<Post>, List<ViewItem>> addDateTags(LOAD_TYPE fetchDirection) {
        return listObservable -> listObservable.map(list -> {
            List<ViewItem> viewItems = new ArrayList<>();
            viewItems.addAll(list);
            if (list.isEmpty()) return viewItems;

            // Avoid that some date tags aren't show if the loading starts at a new day
            if (fetchDirection == LOAD_TYPE.DOWN && getLastPost() != null) {
                // Fetching down => Add the last post (from allPosts) to the top to compare
                list.add(0, getLastPost());
            } else if (fetchDirection == LOAD_TYPE.UP && getFirstPost() != null) {
                //Fetching up => Add the first post (from allPosts) to the bottom to compare
                list.add(getFirstPost());
            }

            SimpleDateFormat dayFormatter = new SimpleDateFormat("dd", Locale.getDefault());
            Date lastPostDate = list.get(0).getDate();
            for (Post post : list) {
                if (post.getDate().before(lastPostDate)) {
                    if (!dayFormatter.format(post.getDate()).equals(dayFormatter.format(lastPostDate))) {
                        if (viewItems.contains(post)) {
                            viewItems.add(viewItems.indexOf(post), new DateTag(post.getDate()));
                        } else if (post == getFirstPost()) {
                            // Add the dateTag to the bottom of the list
                            viewItems.add(new DateTag(post.getDate()));
                        }
                    }
                    lastPostDate = post.getDate();
                }
            }

            return viewItems;
        });
    }

    /**
     * @return All posts that are displayed (the adapter is "linked" to this arrayList)
     */
    public List<ViewItem> getPostsToDisplay() {
        return allPosts;
    }

    /**
     * @return First post in allPosts or null if empty
     */
    @Nullable
    private synchronized Post getFirstPost() {
        if (!allPosts.isEmpty()) {
            for (ViewItem vi : allPosts) {
                if (vi.getType() == ViewItem.TYPE_POST) return (Post) vi;
            }
        }
        return null;
    }

    /**
     * @return Last post in allPosts or null if empty
     */
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
    Date getFirstPostDate() {
        Post post = getFirstPost();
        return (post == null) ? new Date(new Date().getTime() - 864000000) : new Date(post.getDate().getTime() + 1000);
    }

    /**
     * Returns the date of the last post element in allPosts.
     * If no post is present, the returned date will be the current date.
     *
     * @return Date
     */
    Date getLastPostDate() {
        Post post = getLastPost();
        return (post == null) ? new Date() : new Date(post.getDate().getTime() - 1000);
    }

    /**
     * Root fetching Method to call all specific fetching methods for older Posts.
     **/
    public void fetchNextPosts() {
        PsLog.v("Loading the next " + LOAD_MORE_ITEMS_COUNT + " posts");
        if (!setupLoading()) return;
        subscribeLoadedPosts(postRepository.fetchNextPosts(getLastPostDate(), LOAD_MORE_ITEMS_COUNT), true, LOAD_MORE_ITEMS_COUNT);
    }

    /**
     * Root fetching Method to call all specific fetching methods for new Posts.
     **/
    public void fetchNewPosts() {
        PsLog.v("Loading new posts");
        if (!setupLoading()) return;
        subscribeLoadedPosts(postRepository.fetchNewPosts(getFirstPostDate(), LOAD_NEW_ITEMS_COUNT), false, LOAD_NEW_ITEMS_COUNT);
    }

    /**
     * /**
     * Subscribes to the load Post observable and pass the finished Items to the view
     * Error handling for loading failed is included
     *
     * @param observable         FetchNext or FetchNew observable to subscribe on
     * @param fetchDirectionDown In which direction the observable is fetching (for the filter and for notifyAdapterItemRangeInserted)
     *                           true means FetchNextPosts (=> down), false means FetchNewPosts (=> up)
     * @param numPosts           Max Number of post to take, rest is discarded
     */

    private void subscribeLoadedPosts(Observable<Post> observable, boolean fetchDirectionDown, int numPosts) {
        subLoadingPosts = observable
                .filter(post -> filterWrongPosts(post, fetchDirectionDown))
                .sorted()
                .take(numPosts)
                .retryWhen(attempts -> attempts.zipWith(Observable.range(1, 2), (throwable, attempt) -> {
                    if (attempt == 2) throw Exceptions.propagate(throwable);
                    else {
                        view.loadingFailed(context.getString(R.string.error_loading_all_retry));
                        PsLog.w("Krititischer Fehler, neuer Versuch: ", throwable);
                        return Observable.timer(3L, TimeUnit.SECONDS);
                    }
                }))
                .compose(sortAndFilterNewPosts())
                .compose(addDateTags(fetchDirectionDown ? LOAD_TYPE.DOWN : LOAD_TYPE.UP))
                .subscribe(items -> {
                    if (fetchDirectionDown) {
                        // Fetched next posts => Add it to the bottom
                        int position = allPosts.size();
                        allPosts.addAll(items);
                        view.loadingItemRangeInserted(position, items.size());
                    } else {
                        // Fetched new posts => Add it to top
                        allPosts.addAll(0, items);
                        view.loadingItemRangeInserted(0, items.size());
                    }
                    PsLog.v("Finished with " + items.size() + " Posts");
                    databaseHelper.insertPosts(items);
                }, e -> {
                    if (e instanceof TimeoutException) {
                        PsLog.w("Laden dauerte zu lange, Abbruch...");
                        view.loadingFailed(context.getString(R.string.error_loading_all_timeout));
                    } else {
                        PsLog.e("Kritischer Fehler beim Laden: ", e);
                        view.loadingFailed(context.getString(R.string.error_loading_all) +
                                "Der Fehler wurde den Entwicklern gemeldet");
                    }
                });
    }

    /**
     * This is the callback for the database fetching method
     * It also starts fetching of newer posts
     *
     * @param posts Sorted ViewItems that are directly passed to the view
     */
    public void addNewPostsToView(List<ViewItem> posts) {
        allPosts.addAll(posts);
        view.freshLoadingCompleted();
        fetchNewPosts();
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
    private boolean filterWrongPosts(Post post, boolean fetchDirectionDown) {
        boolean shouldFilter;
        if (fetchDirectionDown) {
            shouldFilter = post.getDate().before(getLastPostDate());
            if (!shouldFilter && post.getPostType() != UPLOADPLAN && post.getPostType() != PIETCAST && post.getPostType() != PS_VIDEO && post.getPostType() != NEWS) {
                PsLog.w("A post in " + PostType.getName(post.getPostType()) + " is before last date:  " +
                        " Titel: " + post.getTitle() +
                        " Datum: " + post.getDate() +
                        " letzter (ältester) Post Datum: " + getLastPostDate());
            }
        } else {
            shouldFilter = post.getDate().after(getFirstPostDate());
            if (!shouldFilter && post.getPostType() != UPLOADPLAN && post.getPostType() != PIETCAST && post.getPostType() != PS_VIDEO && post.getPostType() != NEWS) {
                PsLog.w("A post in " + PostType.getName(post.getPostType()) + " is before after date:  " +
                        " Titel: " + post.getTitle() +
                        " Datum: " + post.getDate() +
                        "\n letzter (neuster) Post: Datum: " + getFirstPostDate());
            }
        }
        return shouldFilter;
    }

    public enum LOAD_TYPE {
        DOWN,
        UP,
        NEW
    }
}
