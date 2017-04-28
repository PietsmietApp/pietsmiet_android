package de.pscom.pietsmiet.repository;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.pscom.pietsmiet.presenter.PostPresenter;
import de.pscom.pietsmiet.view.MainActivityView;
@RunWith(MockitoJUnitRunner.class)
public class PostRepositoryImplTest {
    @Mock
    Context context;
    @Mock
    MainActivityView view;
    @Mock
    PostRepository repository;

    private PostPresenter presenter;

    @Before
    public void setUp() {
        presenter = new PostPresenter(view, repository, context);
    }

    @Test
    public void testDuplicatePosts() {
        /*Post post1 = new Post.PostBuilder(PostType.FACEBOOK).title("hi").date(new Date()).build();
        Post post2 = new Post.PostBuilder(PostType.FACEBOOK).title("hi").date(new Date()).build();
        Observable<Post> observable = Observable.just(post1, post2);
        when(repository.fetchNewPosts(any(Date.class), anyInt())).thenReturn(observable);

        presenter.fetchNewPosts();

        verify(view).loadingCompleted();*/
    }
}