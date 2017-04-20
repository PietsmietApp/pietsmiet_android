package de.pscom.pietsmiet.util;

import org.junit.Test;
import org.mockito.Mock;

import java.util.Date;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.presenter.FirebasePresenterTest;
import de.pscom.pietsmiet.presenter.YoutubePresenterTest;
import rx.Observable;
import rx.observers.TestSubscriber;

public class PostManagerTest {
    @Mock
    MainActivity mainActivity;

    @Test
    public void buildAndSortPosts() throws Exception {
        PostManager postManager = new PostManager(mainActivity);
        TestSubscriber<Post> testSubscriber = new TestSubscriber<>();
        Observable<Post.PostBuilder> youtubeObs = new YoutubePresenterTest().preparePresenter().fetchPostsSinceObservable(new Date());
        //Observable<Post.PostBuilder> facebookObs = new FacebookPresenterTest().preparePresenter().fetchPostsSinceObservable(new Date());
        //Observable<Post.PostBuilder> twitterObs = new TwitterPresenterTest().preparePresenter().fetchPostsSinceObservable(new Date());
        Observable<Post.PostBuilder> firebaseObs = new FirebasePresenterTest().preparePresenter().fetchPostsSinceObservable(new Date());
        Observable.mergeDelayError(youtubeObs, firebaseObs)
                .map(Post.PostBuilder::build)
                .filter(post -> post != null)
                .sorted()
                .take(20)
                .subscribe(testSubscriber);
        for (Post post :
                testSubscriber.getOnNextEvents()) {
            System.out.println(post.getTitle());
        }
        //todo add tests if Twitter has tests
    }

}