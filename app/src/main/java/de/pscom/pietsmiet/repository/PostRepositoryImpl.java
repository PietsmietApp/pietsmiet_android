package de.pscom.pietsmiet.repository;

import java.util.Date;

import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.SettingsHelper;
import de.pscom.pietsmiet.view.MainActivity;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PostRepositoryImpl implements PostRepository {
    private final MainActivity view;

    public PostRepositoryImpl(MainActivity view) {
        this.view = view;
    }

    @Override
    public Observable<Post> fetchNextPosts(Date lastPostDate, int numPosts) {
        Observable<Post.PostBuilder> twitterObs = SettingsHelper.boolCategoryTwitter ?
                new TwitterRepository(view).fetchPostsUntilObservable(lastPostDate, numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> youtubeObs = SettingsHelper.boolCategoryYoutubeVideos ?
                new YoutubeRepository(view).fetchPostsUntilObservable(lastPostDate, numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> firebaseObs = (SettingsHelper.boolCategoryPietcast || SettingsHelper.boolCategoryPietsmietNews || SettingsHelper.boolCategoryPietsmietUploadplan || SettingsHelper.boolCategoryPietsmietVideos) ?
                new FirebaseRepository(view).fetchPostsUntilObservable(lastPostDate, numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> facebookObs = SettingsHelper.boolCategoryFacebook ?
                new FacebookRepository(view).fetchPostsUntilObservable(lastPostDate, numPosts)
                : Observable.empty();
        return manageEmittedPosts(Observable.mergeDelayError(twitterObs, youtubeObs, firebaseObs, facebookObs));
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
