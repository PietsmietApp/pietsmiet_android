package de.pscom.pietsmiet.backend;

import android.graphics.drawable.Drawable;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.DrawableFetcher;
import rx.Observable;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.RssUtil.loadRss;

public class PietcastPresenter extends MainPresenter {
    private static final String pietcastUrl = "http://www.pietcast.de/pietcast/feed/podcast/";
    static final int MAX_COUNT = 15;

    public PietcastPresenter(MainActivity view) {
        super(view, PIETCAST);
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
                    post = new Post();
                    post.setDescription(element.getDescription());
                    post.setTitle(element.getTitle());
                    post.setUrl(element.getLink().toString());
                    post.setDatetime(element.getPubDate());
                    post.setThumbnail(thumb);
                    post.setPostType(PIETCAST);
                    posts.add(post);
                }, (throwable) -> {
                    throwable.printStackTrace();
                    view.showError("Pietcast parsing error");
                }, this::finished);
    }

}
