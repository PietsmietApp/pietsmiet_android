package de.pscom.pietsmiet.backend;

import android.graphics.drawable.Drawable;

import java.util.Date;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.DrawableFetcher;
import rx.Observable;

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
    private Observable<Post.PostBuilder> parsePietcast(int num) {
        return Observable.defer(() -> Observable.just(loadRss(pietcastUrl)))
                .filter(result -> result != null)
                .flatMap(Observable::from)
                .take(num)
                .onBackpressureBuffer()
                .map(element -> {
                    Drawable thumb = DrawableFetcher.getDrawableFromRss(element);
                    postBuilder = new Post.PostBuilder(PIETCAST);
                    postBuilder.description(element.getDescription());
                    postBuilder.title(element.getTitle());
                    postBuilder.url(element.getLink().toString());
                    postBuilder.date(element.getPubDate());
                    postBuilder.thumbnail(thumb);
                    return postBuilder;
                });
    }

    @Override
    public Observable<Post.PostBuilder> fetchPostsSinceObservable(Date dSince) {
        return parsePietcast(2);
        //todo efficiency / logic
    }


    @Override
    public Observable<Post.PostBuilder> fetchPostsUntilObservable(Date dUntil, int numPosts) {
        return parsePietcast(20);
    }

}
