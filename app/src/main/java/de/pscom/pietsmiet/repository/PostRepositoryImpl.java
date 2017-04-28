package de.pscom.pietsmiet.repository;

import android.content.Context;

import java.util.Date;

import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.SettingsHelper;
import rx.Observable;
import rx.schedulers.Schedulers;

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
        return manageEmittedPosts(Observable.mergeDelayError(twitterObs, youtubeObs, firebaseObs, facebookObs));
    }

    @Override
    public Observable<Post> fetchNewPosts(Date firstPostDate, int numPosts) {
        Observable<Post.PostBuilder> twitterObs = SettingsHelper.boolCategoryTwitter ?
                new TwitterRepository(context).fetchPostsSinceObservable(firstPostDate, numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> youtubeObs = SettingsHelper.boolCategoryYoutubeVideos ?
                new YoutubeRepository(context).fetchPostsSinceObservable(firstPostDate, numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> firebaseObs = (SettingsHelper.boolCategoryPietcast || SettingsHelper.boolCategoryPietsmietNews || SettingsHelper.boolCategoryPietsmietUploadplan || SettingsHelper.boolCategoryPietsmietVideos) ?
                new FirebaseRepository(context).fetchPostsSinceObservable(firstPostDate, numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> facebookObs = SettingsHelper.boolCategoryFacebook ?
                new FacebookRepository(context).fetchPostsSinceObservable(firstPostDate, numPosts)
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
