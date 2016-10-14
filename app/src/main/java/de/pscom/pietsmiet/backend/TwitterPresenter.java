package de.pscom.pietsmiet.backend;

import java.util.List;

import de.pscom.pietsmiet.BuildConfig;
import de.pscom.pietsmiet.adapters.CardItem;
import de.pscom.pietsmiet.util.CardType;
import de.pscom.pietsmiet.util.DrawableFetcher;
import de.pscom.pietsmiet.util.PsLog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterPresenter extends MainPresenter {
    //TODO: Store id!
    private long lastTweetId;

    private Twitter twitterInstance;
    private static final int maxCount = 10;

    public TwitterPresenter() {
        super(CardType.TYPE_TWITTER);
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setApplicationOnlyAuthEnabled(true);
        if (BuildConfig.DEBUG) builder.setDebugEnabled(true);
        TwitterFactory tf = new TwitterFactory(builder.build());
        twitterInstance = tf.getInstance();
        twitterInstance.setOAuthConsumer("btEhqyrrGF96AYQXP20Wwul4n", "uDRVzqrNQm4zAjjVnix7w2KglZe0A7K95iCoJNPqXnbe2YAFdH");

        parseTweets();
    }

    private void parseTweets() {
        Observable.defer(() -> Observable.just(fetchTweets(maxCount)))
                .subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .flatMap(Observable::from)
                .doOnNext(tweet -> lastTweetId = tweet.getId())
                .subscribe(tweet -> Observable.defer(() -> Observable.just(DrawableFetcher.getDrawableFromTweet(tweet)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(drawable -> {
                            cardItem = new CardItem();
                            cardItem.setThumbnail(drawable);
                            cardItem.setTitle(getDisplayName(tweet.getUser()));
                            cardItem.setDescription(tweet.getText());
                            cardItem.setDatetime(tweet.getCreatedAt());
                            publish();
                        }), Throwable::printStackTrace, () -> PsLog.v("Tweets geladen"));
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
                "OR from:br4mm3n " +
                "exclude:replies")
                .count(count)
                .sinceId(lastTweetId)
                .resultType(Query.ResultType.recent);
    }

    private String getDisplayName(User user) {
        int userId = (int) Math.max(Math.min(Integer.MAX_VALUE, user.getId()), Integer.MIN_VALUE);
        switch (userId) {
            case 109850283:
                return "Piet";
            case 832560607:
                return "Sep";
            case 120150508:
                return "Jay";
            case 400567148:
                return "Chris";
            case 394250799:
                return "Brammen";
            default:
                return user.getName();
        }
    }

    private void getToken() {
        try {
            twitterInstance.getOAuth2Token();
        } catch (TwitterException e) {
            PsLog.e("error getting token: " + e.getErrorMessage());
        } catch (IllegalStateException e) {
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
