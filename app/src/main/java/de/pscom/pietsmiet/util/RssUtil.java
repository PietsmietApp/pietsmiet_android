package de.pscom.pietsmiet.util;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.util.List;

public class RssUtil {

    /**
     * @param url A rss feed url
     * @return A list of RssItems from the specified feed
     */
    public static List<RSSItem> loadRss(String url) {
        RSSReader reader = new RSSReader();

        try {
            RSSFeed feed = reader.load(url);
            return feed.getItems();
        } catch (RSSReaderException rssException) {
            PsLog.e("Couldn't read rss", rssException);
            return null;
        }
    }
}
