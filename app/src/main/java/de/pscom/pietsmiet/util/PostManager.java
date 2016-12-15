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
import static de.pscom.pietsmiet.util.PostType.getPossibleTypes;


public class PostManager {
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
    // todo efficienty
    public void addPosts(List<Post> lPosts) {
        List<Post> listPosts = new ArrayList<>();
        listPosts.addAll(lPosts);
        // ACHTUNG !!! DA HIER NUR DER POINTER ÜBERGEBEN WIRD BRAUCHT MAN EIN NEUES OBJEKT! todo MERKEN! SPART ZEIT ;)
        if (listPosts.size() == 0) {
            //todo warum?
            PsLog.w("addPosts called with zero posts");
            resetFetchingEnded();
            mView.setRefreshAnim(false);
            queuedPosts.clear();
            return;
        }
        queuedPosts.clear();
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
                .subscribe(items -> {
                    allPosts.clear();
                    allPosts.addAll(items);
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
            return new Date();
            // todo sinnvoll? Nein setzte tag auf vorherigen
        } else {
            return allPosts.get(0).getDate();
        }
    }

    private Date getLastPostDate() {
        if (allPosts.isEmpty()) {
            Date d = new Date();
            return d;
        } else {
            return allPosts.get(allPosts.size() - 1).getDate();
        }
    }

    public void fetchNextPosts(int numPosts) {
        numPostLoadCount = numPosts;
        mView.setRefreshAnim(true);
        //todo übergangslösung? da hier und in scrolllistener festgelegt
        new TwitterPresenter(mView).fetchPostsUntil(getLastPostDate(), numPosts);
        new YoutubePresenter(mView).fetchPostsUntil(getLastPostDate(), numPosts);
        new PietcastPresenter(mView).fetchPostsUntil(getLastPostDate(), numPosts);
        new FacebookPresenter(mView).fetchPostsUntil(getLastPostDate(), numPosts);
        new UploadplanPresenter(mView).fetchPostsUntil(getLastPostDate(), numPosts);
    }

    public void fetchNewPosts() {
        mView.setRefreshAnim(true);
        new TwitterPresenter(mView).fetchPostsSince(getFirstPostDate());
        new YoutubePresenter(mView).fetchPostsSince(getFirstPostDate());
        new UploadplanPresenter(mView).fetchPostsSince(getFirstPostDate());
        new PietcastPresenter(mView).fetchPostsSince(getFirstPostDate());
        new FacebookPresenter(mView).fetchPostsSince(getFirstPostDate());
    }

    public boolean getAllPostsFetched() {
        int isEnded = 0;
        for (Boolean aBoolean : fetchingEnded.values()) {
            if (aBoolean) {
                isEnded++;
            }
        }
        return fetchingEnded.size() == isEnded;
    }

    //todo add documentation
    public int getAllPostsCount() {
        return allPosts.size();
    }

    public void onReadyFetch(List<Post> listPosts, @AllTypes int type) {
        if (listPosts != null && listPosts.size() > 0) {
            addPostsToQueue(listPosts, type);
        } else {
            fetchingEnded.put(type, true);
            //fixme error meldung nicht zeigen, wenn keine posts geladen werden konnten weil keine mehr da sind
            //bei pull to refresh zB
            PsLog.e("No Posts loaded in " + PostType.getName(type) + " Category");
            mView.showError("ERROR fetching " + PostType.getName(type));
            if (getAllPostsFetched()) {
                mView.setRefreshAnim(false);
                //testweise
                //queuedPosts.clear();
                addPosts(queuedPosts);
            }
            //todo bessere Fehlermeldungen überall! und to top button

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

        lPosts.addAll(queuedPosts);
        // Use an array to avoid concurrent modification exceptions todo this could be more beautiful
        Post[] posts = lPosts.toArray(new Post[lPosts.size()]);
        Observable.just(posts)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .filter(post -> post != null)
                .filter(post -> {
                    boolean b = post.getDate().before(getLastPostDate());
                    if (!b) PsLog.v("posts is after last date");
                    return b;
                }) //provisorisch da getlastpostdate nicht async ist -.- kann aber so funktionieren
                .distinct()
                .toSortedList()
                .flatMap(Observable::from)
                .take(numPostLoadCount)
                .toSortedList()
                .subscribe(items -> {
                    queuedPosts.clear();
                    queuedPosts.addAll(items);
                }, Throwable::printStackTrace, () -> {
                    fetchingEnded.put(type, true);
                    if (getAllPostsFetched()) {
                        // reset fetching is in UpdateAdapter in MainActivity k
                        addPosts(queuedPosts);
                    }
                });
    }

    public void clearPosts() {
        allPosts.clear();
        currentPosts.clear();
    }
}
