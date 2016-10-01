package de.pscom.pietsmiet.backend;

import java.util.List;

import de.pscom.pietsmiet.util.PsLog;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterHelper {
    //TODO: Store id!
    private long lastTweetId;

    Twitter twitterInstance;
    private static final int maxCount = 10;
    public Subscription mTwitterSubscription;

    public TwitterHelper() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("px2E2wOhxNrs4tsr8JqojB2yp")
                .setOAuthConsumerSecret("zyVTNh7x2BUCDChlsZ6OStSqhFBBI8nEBWDGKv2HXcIfMbmLJg")
                .setOAuthAccessToken("2563910439-Spw0P4vTwqfxAhvegQGXcDxPuGIFpv9cxHeLn8N")
                .setOAuthAccessTokenSecret("IDYvhgDMyka6oEWVJgZbBTVyWIk1njDYyjPUnVlRLlgZe");
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitterInstance = tf.getInstance();
    }

    public void displayTweets() {
        mTwitterSubscription = Observable.defer(() -> Observable.just(fetchTweets(maxCount)))
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .filter(tweet -> !tweet.isRetweeted())
                .doOnNext(tweet -> lastTweetId = tweet.getId())
                .map(Status::getText)
                .subscribe(PsLog::v, Throwable::printStackTrace);
    }


    private List<Status> fetchTweets(int count) {
        QueryResult result;
        try {
            result = twitterInstance.search(pietsmietTweets(count, lastTweetId));
        } catch (TwitterException e) {
            PsLog.e("Couldn't fetch tweets: " + e.getMessage());
            return null;
        }

        return result.getTweets();
    }

    private Query pietsmietTweets(int count, long sinceId) {
        return new Query("from:pietsmiet, " +
                "OR from:kessemak2, " +
                "OR from:jaypietsmiet, " +
                "OR from:brosator, " +
                "OR from:br4mm3n")
                .count(count)
                .sinceId(lastTweetId)
                .resultType(Query.ResultType.recent);
    }
}
