package de.pscom.pietsmiet.backend;

import de.pscom.pietsmiet.adapters.CardItem;
import de.pscom.pietsmiet.util.CardType;
import de.pscom.pietsmiet.util.DrawableFetcher;
import de.pscom.pietsmiet.util.PsLog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.RssUtil.loadRss;

public class PietcastPresenter extends MainPresenter {
    private static String pietcastUrl = "http://www.pietcast.de/pietcast/feed/podcast/";

    public PietcastPresenter() {
        super(CardType.TYPE_PIETCAST);
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
                .subscribe(element -> Observable.defer(() -> Observable.just(DrawableFetcher.getDrawableFromUrl(element.getThumbnails().get(0).toString())))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(drawable -> {
                            cardItem = new CardItem();
                            cardItem.setDescription(element.getDescription());
                            cardItem.setTitle(element.getTitle());
                            cardItem.setDatetime(element.getPubDate());
                            cardItem.setThumbnail(drawable);
                            publish();
                        }), Throwable::printStackTrace, () -> PsLog.v("Pietcasts geladen"));
    }

}
