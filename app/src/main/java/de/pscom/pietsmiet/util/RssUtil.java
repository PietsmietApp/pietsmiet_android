package de.pscom.pietsmiet.util;

import android.content.Context;
import android.util.Log;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RssUtil {
    Context mContext;

    public RssUtil(Context mContext) {
        this.mContext = mContext;
    }

    public void displayRss() {
        Subscription mSubscription = Observable.defer(() -> Observable.just(loadRss()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(Observable::from)
                .map(result -> result.getLink().toString())
                .subscribe(result -> Log.v("TAG", result)
                        , e -> Log.v("Error", e.toString()));
    }

    private List<RSSItem> loadRss() {
        RSSReader reader = new RSSReader();
        String uri = "http://pietsmiet.de/news?format=feed&type=rss";
        try {
            RSSFeed feed = reader.load(uri);
            return feed.getItems();
        } catch (RSSReaderException rssException) {
            Log.v("Tag", rssException.toString());
        }
        return null;
    }

}
