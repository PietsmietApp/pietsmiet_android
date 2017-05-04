package de.pscom.pietsmiet.presenter;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.repository.PostRepository;
import de.pscom.pietsmiet.util.DatabaseHelper;
import de.pscom.pietsmiet.util.NetworkUtil;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.SettingsHelper;
import de.pscom.pietsmiet.view.MainActivityView;
import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PostPresenterTest {
    private static final long RANDOM_TIME = 1493910366000L;
    @Mock
    Context context;
    @Mock
    MainActivityView view;
    @Mock
    PostRepository repository;
    @Mock
    DatabaseHelper databaseHelper;
    @Mock
    NetworkUtil networkUtil;

    private PostPresenter presenter;

    @Before
    public void setUp() {
        presenter = new PostPresenter(view, repository, databaseHelper, networkUtil);
        RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.trampoline());
        SettingsHelper.boolCategoryPietsmietNews = true;
        SettingsHelper.boolCategoryPietsmietUploadplan = true;
        SettingsHelper.boolCategoryPietcast = true;
        SettingsHelper.boolCategoryPietsmietNews = true;
        SettingsHelper.boolCategoryFacebook = true;
        SettingsHelper.boolCategoryTwitter = true;
        SettingsHelper.boolCategoryYoutubeVideos = true;

        when(networkUtil.isConnected()).thenReturn(true);
    }

    @Test
    public void testNewPostsCompleted() {
        Post post1 = new Post.PostBuilder(PostType.FACEBOOK).title("Sep").date(new Date(RANDOM_TIME)).build();
        Post post2 = new Post.PostBuilder(PostType.TWITTER).title("Piet").date(new Date(RANDOM_TIME)).build();

        Observable<Post> obs = Observable.just(post1, post2);
        when(repository.fetchNewPosts(any(Date.class), anyInt())).thenReturn(obs);

        presenter.fetchNewPosts();

        verify(view).loadingStarted();
        verify(view).loadingCompleted();

        verify(view, never()).loadingFailed(anyString());
    }

    @Test
    public void testNextPostsCompleted() {
        Post post1 = new Post.PostBuilder(PostType.FACEBOOK).title("Sep").date(new Date(RANDOM_TIME)).build();
        Post post2 = new Post.PostBuilder(PostType.TWITTER).title("Piet").date(new Date(RANDOM_TIME)).build();

        Observable<Post> obs = Observable.just(post1, post2);
        when(repository.fetchNextPosts(any(Date.class), anyInt())).thenReturn(obs);

        presenter.fetchNextPosts();

        verify(view).loadingStarted();
        verify(view).loadingCompleted();

        verify(view, never()).loadingFailed(anyString());
    }

    @Test
    public void testNoNetworkError() {
        when(networkUtil.isConnected()).thenReturn(false);

        presenter.fetchNextPosts();

        verify(view).noNetworkError();
        verify(view, never()).loadingCompleted();
    }

    @Test
    public void testDuplicatePosts() {
        Post post1 = new Post.PostBuilder(PostType.FACEBOOK).title("Sep").date(new Date(RANDOM_TIME)).build();
        Post post2 = new Post.PostBuilder(PostType.FACEBOOK).title("Sep").date(new Date(RANDOM_TIME)).build();
        Post post3 = new Post.PostBuilder(PostType.FACEBOOK).title("Piet").date(new Date(RANDOM_TIME)).build();

        Observable<Post> obs = Observable.just(post1, post2, post3);
        when(repository.fetchNewPosts(any(Date.class), anyInt())).thenReturn(obs);

        presenter.fetchNewPosts();

        List<Post> shouldBe = Arrays.asList(post1, post3);
        assertThat(presenter.getPostsToDisplay(), is(shouldBe));
    }

    @Test
    public void testSorting() {
        Post post1 = new Post.PostBuilder(PostType.FACEBOOK).title("Sepjö").date(new Date(RANDOM_TIME + 3000)).build();
        Post post2 = new Post.PostBuilder(PostType.TWITTER).title("Raging Harti Harti").date(new Date(RANDOM_TIME)).build();
        Post post3 = new Post.PostBuilder(PostType.TWITTER).title("Piet").date(new Date(RANDOM_TIME - 20000)).build();
        Post post4 = new Post.PostBuilder(PostType.TWITTER).title("Piet").date(new Date(RANDOM_TIME - 30000)).build();

        Observable<Post> obs = Observable.just(post3, post1, post2, post4);
        when(repository.fetchNewPosts(any(Date.class), anyInt())).thenReturn(obs);

        presenter.fetchNewPosts();

        List<Post> shouldBe = Arrays.asList(post1, post2, post3, post4);
        assertThat(presenter.getPostsToDisplay(), is(shouldBe));
    }

    @Test
    public void testNewFiltering() {
        Post firstPost = new Post.PostBuilder(PostType.TWITTER).title("Piet").date(new Date(RANDOM_TIME - 20000)).build();

        Post post1 = new Post.PostBuilder(PostType.TWITTER).title("Raging Harti Harti").date(new Date(RANDOM_TIME)).build();
        Post post2 = new Post.PostBuilder(PostType.TWITTER).title("Piet").date(new Date(RANDOM_TIME - 30000)).build();

        presenter.getPostsToDisplay().add(firstPost);

        Observable<Post> obs = Observable.just(post1, post2);
        when(repository.fetchNewPosts(any(Date.class), anyInt())).thenReturn(obs);

        presenter.fetchNewPosts();

        List<Post> shouldBe = Arrays.asList(post1, firstPost);
        assertThat(presenter.getPostsToDisplay(), is(shouldBe));
    }

    @Test
    public void testNextFiltering() {
        Post lastPost = new Post.PostBuilder(PostType.TWITTER).title("Piet").date(new Date(RANDOM_TIME - 20000)).build();

        Post post1 = new Post.PostBuilder(PostType.TWITTER).title("Raging Harti Harti").date(new Date(RANDOM_TIME)).build();
        Post post2 = new Post.PostBuilder(PostType.TWITTER).title("Piet").date(new Date(RANDOM_TIME - 30000)).build();

        presenter.getPostsToDisplay().add(lastPost);

        Observable<Post> obs = Observable.just(post1, post2);
        when(repository.fetchNextPosts(any(Date.class), anyInt())).thenReturn(obs);

        presenter.fetchNextPosts();

        List<Post> shouldBe = Arrays.asList(lastPost, post2);
        assertThat(presenter.getPostsToDisplay(), is(shouldBe));
    }

    @Test
    public void testFirstPostDate() {
        Post post1 = new Post.PostBuilder(PostType.TWITTER).title("Raging Harti Harti").date(new Date(RANDOM_TIME)).build();
        Post post2 = new Post.PostBuilder(PostType.TWITTER).title("Piet").date(new Date(RANDOM_TIME - 3000)).build();
        Post post3 = new Post.PostBuilder(PostType.TWITTER).title("Piet").date(new Date(RANDOM_TIME - 5000)).build();

        presenter.getPostsToDisplay().addAll(Arrays.asList(post1, post2, post3));

        assertThat(presenter.getFirstPostDate().getTime(), is(RANDOM_TIME + 1000));
    }

    @Test
    public void testLastPostDate() {
        Post post1 = new Post.PostBuilder(PostType.TWITTER).title("Raging Harti Harti").date(new Date(RANDOM_TIME)).build();
        Post post2 = new Post.PostBuilder(PostType.TWITTER).title("Piet").date(new Date(RANDOM_TIME - 3000)).build();
        Post post3 = new Post.PostBuilder(PostType.TWITTER).title("Piet").date(new Date(RANDOM_TIME - 5000)).build();

        presenter.getPostsToDisplay().addAll(Arrays.asList(post1, post2, post3));

        assertThat(presenter.getLastPostDate().getTime(), is(RANDOM_TIME - 5000 - 1000));
    }


}
