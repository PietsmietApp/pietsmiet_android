package de.pscom.pietsmiet.util;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * Created by Anwender on 08.09.2016.
 */
public class TwitterUtil {
    Twitter twitter;

    public TwitterUtil() {
        twitter = new TwitterFactory().getInstance();
    }

    public List<Status> getTweets() {
        try {
            Query query = new Query("from:pietsmiet, " +
                    "OR from:kessemak2, " +
                    "OR from:jaypietsmiet, " +
                    "OR from:brosator, " +
                    "OR from:br4mm3n");
            QueryResult result = twitter.search(query);
            return result.getTweets();
        } catch (TwitterException te) {
            LogUtil.e("Could not fetch tweets: " + te.getErrorMessage());
            return null;
        }
    }
}
