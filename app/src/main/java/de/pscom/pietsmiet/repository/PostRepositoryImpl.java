package de.pscom.pietsmiet.repository;

import android.content.Context;

import java.util.Date;

import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SettingsHelper;
import rx.Observable;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.NEWS;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.PS_VIDEO;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;

public class PostRepositoryImpl implements PostRepository {
    private final Context context;

    public PostRepositoryImpl(Context context) {
        this.context = context;
    }

    @Override
    public Observable<Post> fetchNextPosts(Date lastPostDate, int numPosts) {
        Observable<Post.PostBuilder> twitterObs = SettingsHelper.boolCategoryTwitter ?
                new TwitterRepository(context).fetchPostsUntilObservable(lastPostDate, numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> youtubeObs = SettingsHelper.boolCategoryYoutubeVideos ?
                new YoutubeRepository(context).fetchPostsUntilObservable(lastPostDate, numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> firebaseObs = (SettingsHelper.boolCategoryPietcast || SettingsHelper.boolCategoryPietsmietNews || SettingsHelper.boolCategoryPietsmietUploadplan || SettingsHelper.boolCategoryPietsmietVideos) ?
                new FirebaseRepository(context).fetchPostsUntilObservable(lastPostDate, numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> facebookObs = SettingsHelper.boolCategoryFacebook ?
                new FacebookRepository(context).fetchPostsUntilObservable(lastPostDate, numPosts)
                : Observable.empty();
        return manageEmittedPosts(Observable.mergeDelayError(twitterObs, youtubeObs, firebaseObs, facebookObs), numPosts, true, lastPostDate);
    }

    @Override
    public Observable<Post> fetchNewPosts(Date firstPostDate) {
        Observable<Post.PostBuilder> twitterObs = SettingsHelper.boolCategoryTwitter ?
                new TwitterRepository(context).fetchPostsSinceObservable(firstPostDate)
                : Observable.empty();
        Observable<Post.PostBuilder> youtubeObs = SettingsHelper.boolCategoryYoutubeVideos ?
                new YoutubeRepository(context).fetchPostsSinceObservable(firstPostDate)
                : Observable.empty();
        Observable<Post.PostBuilder> firebaseObs = (SettingsHelper.boolCategoryPietcast || SettingsHelper.boolCategoryPietsmietNews || SettingsHelper.boolCategoryPietsmietUploadplan || SettingsHelper.boolCategoryPietsmietVideos) ?
                new FirebaseRepository(context).fetchPostsSinceObservable(firstPostDate)
                : Observable.empty();
        Observable<Post.PostBuilder> facebookObs = SettingsHelper.boolCategoryFacebook ?
                new FacebookRepository(context).fetchPostsSinceObservable(firstPostDate)
                : Observable.empty();
        return manageEmittedPosts(Observable.mergeDelayError(twitterObs, youtubeObs, firebaseObs, facebookObs), 15, false, firstPostDate);
    }

    /**
     * Subscribes to the merged Observables emitting the loaded posts.
     * Filters the result and finally adds the selected posts to the allPost List with addPosts().
     *
     * @param postObs Observable<PostBuilder> emitting loaded posts from various sources.
     */
    private Observable<Post> manageEmittedPosts(Observable<Post.PostBuilder> postObs, int numPosts/*fixme not sure, ask tk*/, boolean fetchDirectionDown, Date date) {
        return postObs.observeOn(Schedulers.io(), true)
                .subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .filter(postBuilder -> postBuilder != null)
                .map(Post.PostBuilder::build)
                .filter(post -> post != null)
                .filter(post -> filterWrongPosts(post, fetchDirectionDown, date))
                .sorted()
                .take(numPosts);
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
