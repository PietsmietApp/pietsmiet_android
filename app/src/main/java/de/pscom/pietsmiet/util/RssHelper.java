package de.pscom.pietsmiet.util;

import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class RssHelper {
    Context mContext;
    static String uploadplanUrl = "http://pietsmiet.de/news?format=feed&type=rss";
    static String pietcastUrl = "http://www.pietcast.de/pietcast/feed/podcast/";
    public Subscription mPlanSubscription;
    public Subscription mPietcastSubscription;

    public RssHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void displayUploadplan() {
        mPlanSubscription = Observable.defer(() -> Observable.just(loadRss(uploadplanUrl)))
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .map(element -> element.getLink().toString())
                .flatMap(link -> Observable.defer(() -> Observable.just(parseHtml(link)))
                        .subscribeOn(Schedulers.io()))
                .subscribe(content -> Log.v("TAG", content), Throwable::printStackTrace);
    }

    public void displayPietcast() {
        mPietcastSubscription = Observable.defer(() -> Observable.just(loadRss(pietcastUrl)))
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .subscribe(element -> Log.v("TAG", element.getTitle()), Throwable::printStackTrace);
    }

    private List<RSSItem> loadRss(String url) {
        RSSReader reader = new RSSReader();

        try {
            RSSFeed feed = reader.load(url);
            return feed.getItems();
        } catch (RSSReaderException rssException) {
            Log.v("Tag", rssException.toString());
        }
        return null;
    }

    private String parseHtml(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/4.0")
                    .get();
            Elements content = doc.select("[itemprop=articleBody]");
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
