package de.pscom.pietsmiet.backend;

import android.graphics.drawable.Drawable;

import java.util.Date;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.DrawableFetcher;
import rx.Observable;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.RssUtil.loadRss;

public class PietcastPresenter extends MainPresenter {
    private static final String pietcastUrl = "http://www.pietsmiet.de/pietcast/feed/podcast/";

    public PietcastPresenter(MainActivity view) {
        super(view);
    }

    /**
     * Loads the latests Pietcasts
     */
    private void parsePietcast(int num) {
        Observable.defer(() -> Observable.just(loadRss(pietcastUrl)))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(Observable::from)
                .take(num)
                .onBackpressureBuffer()
                .subscribe(element -> {
                    Drawable thumb = DrawableFetcher.getDrawableFromRss(element);
                    postBuilder = new Post.PostBuilder(PIETCAST);
                    postBuilder.description(element.getDescription());
                    postBuilder.title(element.getTitle());
                    postBuilder.url(element.getLink().toString());
                    postBuilder.date(element.getPubDate());
                    postBuilder.thumbnail(thumb);
                    posts.add(postBuilder.build());
                }, (throwable) -> {
                    throwable.printStackTrace();
                    view.showError("Pietcast parsing error");
                    view.getPostManager().onReadyFetch(posts, PIETCAST);
                }, () -> view.getPostManager().onReadyFetch(posts, PIETCAST));
    }

    @Override
    public void fetchPostsSince(Date dSince) {
        parsePietcast(2);
        //todo efficiency / logic
        //fixme wrong check for date
    }


    @Override
    public void fetchPostsUntil(Date dUntil, int numPosts) {
        parsePietcast(20);
        // todo mit datum arbeiten!
    }

}
