package de.pscom.pietsmiet.backend;

import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.DrawableFetcher;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.RssUtil.loadRss;

public class PietcastPresenter extends MainPresenter {
    private static String pietcastUrl = "http://www.pietcast.de/pietcast/feed/podcast/";

    public PietcastPresenter() {
        super(PIETCAST);
        parsePietcast();
    }

    /**
     * Loads the latests Piecasts
     */
    private void parsePietcast() {
        Observable.defer(() -> Observable.just(loadRss(pietcastUrl)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(Observable::from)
                .onBackpressureBuffer()
                .subscribe(element -> Observable.defer(() -> Observable.just(DrawableFetcher.getDrawableFromRss(element)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(drawable -> {
                            post = new Post();
                            post.setDescription(element.getDescription());
                            post.setTitle(element.getTitle());
                            post.setDatetime(element.getPubDate());
                            post.setThumbnail(drawable);
                            publish();
                        }), Throwable::printStackTrace, this::finished);
    }

}
