package de.pscom.pietsmiet.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.io.IOException;
import java.util.List;

public class RssUtil {

    public static List<RSSItem> loadRss(String url) {
        RSSReader reader = new RSSReader();

        try {
            RSSFeed feed = reader.load(url);
            return feed.getItems();
        } catch (RSSReaderException rssException) {
            PsLog.e(rssException.toString());
            return null;
        }
    }

    public static String parseHtml(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/4.0")
                    .get();
            Elements content = doc.select("[itemprop=articleBody]");
            return content.toString();
        } catch (IOException e) {
            PsLog.e("Couldn't parse HTML: " + e.toString());
            return null;
        }
    }
}
