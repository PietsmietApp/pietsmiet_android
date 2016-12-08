package de.pscom.pietsmiet.backend;

import android.graphics.drawable.Drawable;

import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.DrawableFetcher;
import rx.Observable;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.RssUtil.loadRss;

public class PietcastPresenter extends MainPresenter {
    static final int MAX_COUNT = 15;
    private static final String pietcastUrl = "http://www.pietcast.de/pietcast/feed/podcast/";

    public PietcastPresenter(MainActivity view) {
        super(view);
        parsePietcast();
    }

    /**
     * Loads the latests Piecasts
     */
    private void parsePietcast() {
        Observable.defer(() -> Observable.just(loadRss(pietcastUrl)))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(Observable::from)
                .take(MAX_COUNT)
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
                }, ()-> {});
    }

    @Override
    public void fetchNewPosts(Date dBefore) {

    }

    @Override
    public void fetchPostsBefore(Date dAfter, int numPosts) {

    }

}
