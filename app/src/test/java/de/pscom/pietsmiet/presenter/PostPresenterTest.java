package de.pscom.pietsmiet.presenter;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.generic.ViewItem;
import de.pscom.pietsmiet.repository.PostRepository;
import de.pscom.pietsmiet.util.DatabaseHelper;
import de.pscom.pietsmiet.util.NetworkUtil;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.SettingsHelper;
import de.pscom.pietsmiet.view.MainActivityView;
import rx.Observable;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PostPresenterTest {
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
    public void testDuplicatePosts() {
        Post post1 = new Post.PostBuilder(PostType.FACEBOOK).title("hi2").date(new Date(new Date().getTime() - 5000)).url("s").build();
        Post post2 = new Post.PostBuilder(PostType.TWITTER).title("hi1").date(new Date()).url("s").build();

        Observable<Post> observable = Observable.just(post1, post2);
        when(repository.fetchNewPosts(any(Date.class), anyInt())).thenReturn(observable);

        presenter.fetchNewPosts();

        verify(view).loadingStarted();
        verify(view).loadingCompleted();

        for (ViewItem item : presenter.getPostsToDisplay()) {
            if (item instanceof Post) {
                System.out.println(((Post) item).getTitle());
            }
        }
    }
}