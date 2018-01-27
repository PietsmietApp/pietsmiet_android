package de.pscom.pietsmiet.repository;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SettingsHelper;
import de.pscom.pietsmiet.view.MainActivity;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PostRepositoryImpl implements PostRepository {
    private final MainActivity view;
    private final ICacheRepository cache;

    public PostRepositoryImpl(@NonNull MainActivity view) {
        this.view = view;
        this.cache = new CacheRepository();
    }

    private Post getNewestPost(@NonNull List<Post> posts) {
        Post n = null;
        for (Post post : posts) {
            if(n == null || post != null && post.getDate().getTime() > n.getDate().getTime()) {
                n = post;
            }
        }
        return n;
    }

    private Post getOldestPost(@NonNull List<Post> posts) {
        Post old = null;
        for (Post post : posts) {
            if(old == null || post != null && post.getDate().getTime() < old.getDate().getTime()) {
                old = post;
            }
        }
        return old;
    }

    private List<Post> filterDate(List<Post> posts, Long time, boolean up) {
        List<Post> tmp = new ArrayList<>();
        for (Post post : posts) {
            if(up && post.getDate().getTime() < time || !up && post.getDate().getTime() > time) {
                tmp.add(post);
            }
        }
        return tmp;
    }

    public void clear() {
        cache.clear();
        TwitterRepository.lastTweet = null;
        TwitterRepository.firstTweet = null;
    }

    public void cachePosts(List<Post> posts) {
        HashMap<Class<? extends MainRepository>, List<Post>> map = new HashMap<>();
        for (Post post : posts) {
            Class<? extends MainRepository> x = PostTypeToRepository(post.getPostType());
            if(x != null) {
                if(map.containsKey(x)) {
                    map.get(x).add(post);
                } else {
                    List<Post> lp = new ArrayList<>();
                    lp.add(post);
                    map.put(x, lp);
                }
            }
        }
        for (Class<? extends MainRepository> aClass : map.keySet()) {
            cache.storePosts(aClass, map.get(aClass));
        }
    }

    private Class<? extends MainRepository> PostTypeToRepository(int i) {
        switch (i) {
            case PostType.FACEBOOK:
                return FacebookRepository.class;
            case PostType.TWITTER:
                return TwitterRepository.class;
            case PostType.YOUTUBE:
                return YoutubeRepository.class;
            case PostType.UPLOADPLAN:
                return FirebaseRepository.class;
            case PostType.NEWS:
                return FirebaseRepository.class;
            case PostType.PIETCAST:
                return FirebaseRepository.class;
            case PostType.PS_VIDEO:
                return FirebaseRepository.class;
            default:
                return null;
        }
    }

    @Override
    public Observable<Post> fetchNextPosts(Date lastPostDate, int numPosts) {
        cache.trimCache(lastPostDate);
        ArrayList<Post> cachedOldest = new ArrayList<>();

        List<Post> postTwitter = filterDate(cache.getCachedPosts(TwitterRepository.class), lastPostDate.getTime(), true);
        Post oldTwitt = getOldestPost(postTwitter);
        cachedOldest.add(oldTwitt);
        if(oldTwitt != null) {
            TwitterRepository.lastTweet = oldTwitt;
        }

        List<Post> postYoutube = filterDate(cache.getCachedPosts(YoutubeRepository.class), lastPostDate.getTime(), true);
        cachedOldest.add(getOldestPost(postYoutube));

        List<Post> postFacebook = filterDate(cache.getCachedPosts(FacebookRepository.class), lastPostDate.getTime(), true);
        cachedOldest.add(getOldestPost(postFacebook));

        List<Post> postFirebase = filterDate(cache.getCachedPosts(FirebaseRepository.class), lastPostDate.getTime(), true);
        cachedOldest.add(getOldestPost(postFirebase));

        Post newestOfOldestPost = getNewestPost(cachedOldest);
        List<Post> lfiltered = Collections.emptyList();
        if(newestOfOldestPost != null) {
            lfiltered = filterDate(cache.getCachedPosts(), newestOfOldestPost.getDate().getTime(), false);
            lfiltered = filterDate(lfiltered, lastPostDate.getTime(), true);
        }

        Observable<Post.PostBuilder> twitterObs = Observable.empty();
        Observable<Post.PostBuilder> youtubeObs = Observable.empty();
        Observable<Post> firebaseObs = Observable.empty();
        Observable<Post.PostBuilder> facebookObs = Observable.empty();
        Observable<Post> cachedObs = Observable.from(lfiltered).sorted();

        int numToLoad = numPosts - postTwitter.size();
        if(numToLoad > 0 && SettingsHelper.boolCategoryTwitter) {
            PsLog.v("Twitter: Fetching " + numToLoad + " new Posts from API.");
            twitterObs = new TwitterRepository(view).fetchPostsUntilObservable(lastPostDate, numToLoad);
        }

        numToLoad = numPosts - postYoutube.size();
        if(numToLoad > 0 && SettingsHelper.boolCategoryYoutubeVideos) {
            PsLog.v("Youtube: Fetching " + numToLoad + " new Posts from API.");
            youtubeObs = new YoutubeRepository(view).fetchPostsUntilObservable(lastPostDate, numToLoad);
        }
        // TODO WRITE TESTS for Caching!!!
        // Fixme throwing away some already loaded Firebase posts because of the 4 streams merged together internally by the FirebaseRepo
        numToLoad = numPosts - postFirebase.size();
        if(numToLoad > 0 && (SettingsHelper.boolCategoryPietcast || SettingsHelper.boolCategoryPietsmietNews || SettingsHelper.boolCategoryPietsmietUploadplan || SettingsHelper.boolCategoryPietsmietVideos)) {
            PsLog.v("Firebase: Fetching " + numToLoad + " new Posts from API.");
            firebaseObs = manageEmittedPosts( new FirebaseRepository(view).fetchPostsUntilObservable(lastPostDate, numToLoad) ).sorted().take(numToLoad);
        }

        numToLoad = numPosts - postFacebook.size();
        if(numToLoad > 0 && SettingsHelper.boolCategoryFacebook) {
            PsLog.v("Facebook: Fetching " + numToLoad + " new Posts from API.");
            facebookObs = new FacebookRepository(view).fetchPostsUntilObservable(lastPostDate, numToLoad);
        }

        return Observable.mergeDelayError( cachedObs, firebaseObs, manageEmittedPosts(Observable.mergeDelayError(twitterObs, youtubeObs, facebookObs)));
    }

    @Override
    public Observable<Post> fetchNewPosts(Date firstPostDate, int numPosts) {
        Observable<Post.PostBuilder> twitterObs = SettingsHelper.boolCategoryTwitter ?
                new TwitterRepository(view).fetchPostsSinceObservable(firstPostDate, numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> youtubeObs = SettingsHelper.boolCategoryYoutubeVideos ?
                new YoutubeRepository(view).fetchPostsSinceObservable(firstPostDate, numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> firebaseObs = (SettingsHelper.boolCategoryPietcast || SettingsHelper.boolCategoryPietsmietNews || SettingsHelper.boolCategoryPietsmietUploadplan || SettingsHelper.boolCategoryPietsmietVideos) ?
                new FirebaseRepository(view).fetchPostsSinceObservable(firstPostDate, numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> facebookObs = SettingsHelper.boolCategoryFacebook ?
                new FacebookRepository(view).fetchPostsSinceObservable(firstPostDate, numPosts)
                : Observable.empty();
        return manageEmittedPosts(Observable.mergeDelayError(twitterObs, youtubeObs, firebaseObs, facebookObs));
    }

    /**
     * Builds the post and filters null objects
     *
     * @param postObs Observable<PostBuilder> emitting loaded posts from various sources.
     */
    private Observable<Post> manageEmittedPosts(Observable<Post.PostBuilder> postObs) {
        return postObs
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .filter(postBuilder -> postBuilder != null)
                .map(Post.PostBuilder::build)
                .filter(post -> post != null);
    }
}
