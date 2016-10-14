package de.pscom.pietsmiet.backend;

import de.pscom.pietsmiet.adapters.CardItem;
import de.pscom.pietsmiet.util.PsLog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.CardType.TYPE_UPLOAD_PLAN;
import static de.pscom.pietsmiet.util.RssUtil.loadRss;
import static de.pscom.pietsmiet.util.RssUtil.parseHtml;

public class UploadplanPresenter extends MainPresenter {
    private static final int DEFAULT_MAX = 1;
    private static String uploadplanUrl = "http://pietsmiet.de/news?format=feed&type=rss";


    public UploadplanPresenter() {
        super(TYPE_UPLOAD_PLAN);

        parseUploadplan(DEFAULT_MAX);
    }

    /**
     * Loads the latest uploadplan URLS and parses them
     *
     * @param max Max URLs to parse, should be as low as possible
     */
    public void parseUploadplan(int max) {
        Observable.defer(() -> Observable.just(loadRss(uploadplanUrl)))
                .subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(Observable::from)
                .doOnNext(element -> {
                    cardItem = new CardItem();
                    cardItem.setDatetime(element.getPubDate());
                    cardItem.setTitle(element.getTitle());
                })
                .map(element -> element.getLink().toString())
                .take(max)
                .flatMap(link -> Observable.defer(() -> Observable.just(parseHtml(link)))
                        .subscribeOn(Schedulers.io())
                        .onBackpressureBuffer()
                        .observeOn(AndroidSchedulers.mainThread()))
                .filter(content -> content != null)
                .subscribe(uploadplan -> {
                    cardItem.setDescription(uploadplan);
                    publish();
                }, Throwable::printStackTrace, () -> PsLog.v("Uploadplan geladen"));
    }


}
