package de.pscom.pietsmiet.backend;

import java.util.List;

import de.pscom.pietsmiet.BuildConfig;
import de.pscom.pietsmiet.util.PsLog;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterHelper {
    //TODO: Store id!
    private long lastTweetId;

    private Twitter twitterInstance;
    private static final int maxCount = 10;
    public Subscription mTwitterSubscription;

    public TwitterHelper() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setApplicationOnlyAuthEnabled(true);
        if (BuildConfig.DEBUG) builder.setDebugEnabled(true);
        TwitterFactory tf = new TwitterFactory(builder.build());
        twitterInstance = tf.getInstance();
        twitterInstance.setOAuthConsumer("btEhqyrrGF96AYQXP20Wwul4n", "uDRVzqrNQm4zAjjVnix7w2KglZe0A7K95iCoJNPqXnbe2YAFdH");
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
        getToken();
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

    private void getToken(){
        try {
            twitterInstance.getOAuth2Token();
        } catch (TwitterException e) {
            PsLog.e("error getting token: " + e.getErrorMessage());
        } catch (IllegalStateException e){
            PsLog.d("Token already instantiated");
        }
    }

    private void getRateLimit() {
        try {
            RateLimitStatus status = twitterInstance.getRateLimitStatus("search").get("/search/tweets");
            PsLog.v("Limit: " + status.getLimit());
            PsLog.v("Remaining: " + status.getRemaining());
            PsLog.v("ResetTimeInSeconds: " + status.getResetTimeInSeconds());
            PsLog.v("SecondsUntilReset: " + status.getSecondsUntilReset());

        } catch (TwitterException e) {
            PsLog.w("Couldn't get rate limit: " + e.getErrorMessage());
        }
    }
}
