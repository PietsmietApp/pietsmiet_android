package de.pscom.pietsmiet;

import android.content.Context;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.interfaces.PostRepository;
import de.pscom.pietsmiet.presenter.FacebookPresenter;
import de.pscom.pietsmiet.presenter.FirebasePresenter;
import de.pscom.pietsmiet.presenter.TwitterPresenter;
import de.pscom.pietsmiet.presenter.YoutubePresenter;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SettingsHelper;
import rx.Observable;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

public class PostRepositoryImpl implements PostRepository {
    private final Context context;

    public PostRepositoryImpl(Context context) {
        this.context = context;
    }

    @Override
    public Observable<Post> fetchNextPosts(Date lastPostDate, int numPosts) {

        Observable<Post.PostBuilder> twitterObs = SettingsHelper.boolCategoryTwitter ?
                new TwitterPresenter(context).fetchPostsUntilObservable(lastPostDate, numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> youtubeObs = SettingsHelper.boolCategoryYoutubeVideos ?
                new YoutubePresenter(context).fetchPostsUntilObservable(lastPostDate, numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> firebaseObs = (SettingsHelper.boolCategoryPietcast || SettingsHelper.boolCategoryPietsmietNews || SettingsHelper.boolCategoryPietsmietUploadplan || SettingsHelper.boolCategoryPietsmietVideos) ?
                new FirebasePresenter(context).fetchPostsUntilObservable(lastPostDate, numPosts)
                : Observable.empty();
        Observable<Post.PostBuilder> facebookObs = SettingsHelper.boolCategoryFacebook ?
                new FacebookPresenter(context).fetchPostsUntilObservable(lastPostDate, numPosts)
                : Observable.empty();
        return manageEmittedPosts(Observable.mergeDelayError(twitterObs, youtubeObs, firebaseObs, facebookObs));
    }

    @Override
    public Observable<Post> fetchNewPosts(Date firstPostDate) {

        Observable<Post.PostBuilder> twitterObs = SettingsHelper.boolCategoryTwitter ?
                new TwitterPresenter(context).fetchPostsSinceObservable(firstPostDate)
                : Observable.empty();
        Observable<Post.PostBuilder> youtubeObs = SettingsHelper.boolCategoryYoutubeVideos ?
                new YoutubePresenter(context).fetchPostsSinceObservable(firstPostDate)
                : Observable.empty();
        Observable<Post.PostBuilder> firebaseObs = (SettingsHelper.boolCategoryPietcast || SettingsHelper.boolCategoryPietsmietNews || SettingsHelper.boolCategoryPietsmietUploadplan || SettingsHelper.boolCategoryPietsmietVideos) ?
                new FirebasePresenter(context).fetchPostsSinceObservable(firstPostDate)
                : Observable.empty();
        Observable<Post.PostBuilder> facebookObs = SettingsHelper.boolCategoryFacebook ?
                new FacebookPresenter(context).fetchPostsSinceObservable(firstPostDate)
                : Observable.empty();
        return manageEmittedPosts(Observable.mergeDelayError(twitterObs, youtubeObs, firebaseObs, facebookObs));
    }

    /**
     * Subscribes to the merged Observables emitting the loaded posts.
     * Filters the result and finally adds the selected posts to the allPost List with addPosts().
     *
     * @param postObs Observable<PostBuilder> emitting loaded posts from various sources.
     */
    private Observable<Post> manageEmittedPosts(Observable<Post.PostBuilder> postObs) {
        return postObs.observeOn(Schedulers.io(), true)
                .subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .filter(postBuilder -> postBuilder != null)
                .map(Post.PostBuilder::build)
                .filter(post -> post != null)
                .sorted()
                //fixme.take(15)
                .retryWhen(attempts -> attempts.zipWith(Observable.range(1, 2), (throwable, attempt) -> {
                    if (attempt == 2) throw Exceptions.propagate(throwable);
                    else {
                        //fixme mView.loadingFailed("Kritischer Fehler. Ein neuer Ladeversuch wird gestartet...");
                        PsLog.w("Krititischer Fehler, neuer Versuch: ", throwable);
                        return attempt;
                    }
                }).flatMap(retryCount -> Observable.timer((long) Math.pow(2, retryCount), TimeUnit.SECONDS)));
    }
}
