package de.pscom.pietsmiet.util;

import org.junit.Test;
import org.mockito.Mock;

import java.util.Date;

import de.pscom.pietsmiet.view.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.presenter.FacebookPresenterTest;
import de.pscom.pietsmiet.presenter.FirebasePresenterTest;
import de.pscom.pietsmiet.presenter.PostPresenter;
import de.pscom.pietsmiet.presenter.TwitterPresenterTest;
import de.pscom.pietsmiet.presenter.YoutubePresenterTest;
import rx.Observable;
import rx.observers.TestSubscriber;

public class PostPresenterTest {
    @Mock
    MainActivity mainActivity;

    @Test
    public void buildAndSortPosts() throws Exception {
        PostPresenter postPresenter = new PostPresenter(mainActivity);
        TestSubscriber<Post> testSubscriber = new TestSubscriber<>();
        Observable<Post.PostBuilder> youtubeObs = new YoutubePresenterTest().preparePresenter().fetchPostsSinceObservable(new Date());
        Observable<Post.PostBuilder> facebookObs = new FacebookPresenterTest().preparePresenter().fetchPostsSinceObservable(new Date());
        Observable<Post.PostBuilder> twitterObs = new TwitterPresenterTest().preparePresenter().fetchPostsSinceObservable(new Date());
        Observable<Post.PostBuilder> firebaseObs = new FirebasePresenterTest().preparePresenter().fetchPostsSinceObservable(new Date());
        Observable.mergeDelayError(youtubeObs, firebaseObs, facebookObs, twitterObs)
                .map(Post.PostBuilder::build)
                .filter(post -> post != null)
                .sorted()
                .take(20)
                .subscribe(testSubscriber);
        for (Post post :
                testSubscriber.getOnNextEvents()) {
            System.out.println(post.getPostType());
        }
        for (Throwable tr :
                testSubscriber.getOnErrorEvents()) {
            System.out.println(tr.getMessage());
        }
        //todo add tests to assure correct ordering
    }

}