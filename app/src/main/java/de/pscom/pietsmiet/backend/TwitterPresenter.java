package de.pscom.pietsmiet.backend;

import android.graphics.drawable.Drawable;

import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.BuildConfig;
import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.DrawableFetcher;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SecretConstants;
import de.pscom.pietsmiet.util.SharedPreferenceHelper;
import rx.Observable;
import rx.Single;
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

import static de.pscom.pietsmiet.util.PostType.TWITTER;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_TWITTER_ID;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_TWITTER_ID_NEWEST;

public class TwitterPresenter extends MainPresenter {
    private Twitter twitterInstance;
    private long lastTweetId, firstTweetId;

    public TwitterPresenter(MainActivity view) {
        super(view);
        if (view != null && SharedPreferenceHelper.shouldUseCache) {
            lastTweetId = SharedPreferenceHelper.getSharedPreferenceLong(view, KEY_TWITTER_ID, 0);
            firstTweetId = SharedPreferenceHelper.getSharedPreferenceLong(view, KEY_TWITTER_ID_NEWEST, 0);
        }
        if (SecretConstants.twitterSecret == null) {
            PsLog.w("No twitter secret specified");
            return;
        }
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setApplicationOnlyAuthEnabled(true);
        if (BuildConfig.DEBUG) builder.setDebugEnabled(true);
        TwitterFactory tf = new TwitterFactory(builder.build());
        twitterInstance = tf.getInstance();
        twitterInstance.setOAuthConsumer("btEhqyrrGF96AYQXP20Wwul4n", SecretConstants.twitterSecret);


    }

    private void parseTweets(List<Status> tweetList) {
        Observable.defer(() -> Observable.just(tweetList))
                .subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .flatMap(Observable::from)
                .subscribe(tweet -> {
                    Drawable thumb = DrawableFetcher.getDrawableFromTweet(tweet);
                    postBuilder = new Post.PostBuilder(TWITTER);
                    postBuilder.thumbnail(thumb);
                    postBuilder.title(getDisplayName(tweet.getUser()));
                    postBuilder.description(tweet.getText());
                    postBuilder.date(tweet.getCreatedAt());
                    if (tweet.getUser() != null && tweet.getId() != 0) {
                        postBuilder.url("https://twitter.com/" + tweet.getUser().getScreenName()
                                + "/status/" + tweet.getId());
                    }
                    posts.add(postBuilder.build());

                }, (throwable) -> {
                    throwable.printStackTrace();
                    view.showError("Twitter parsing error");
                    view.getPostManager().onReadyFetch(posts, TWITTER);
                }, () -> {
                    if (view != null) {
                        view.getPostManager().onReadyFetch(posts, TWITTER);
                    }
                });
    }

    /**
     * Fetch a list of tweets
     *
     * @return List of Tweets
     */
    private List<Status> fetchTweets(Query psTwitt) {
        QueryResult result;
        try {
            result = twitterInstance.search(psTwitt);
        } catch (TwitterException e) {
            PsLog.e("Couldn't fetch tweets: " + e.getMessage());
            view.showError("Twitter unreachable");
            return null;
        }

        return result.getTweets();
    }

    /**
     * @return A query to fetch only tweets from Team Pietsmiets. It excludes replies,
     */
    private Query pietsmietTweets( int numPosts, long lastTweetId ) {
        return new Query("from:pietsmiet, " +
                "OR from:kessemak2, " +
                "OR from:jaypietsmiet, " +
                "OR from:brosator, " +
                "OR from:br4mm3n " +
                "exclude:replies")
                .count(numPosts)
                .sinceId(lastTweetId)
                .resultType(Query.ResultType.recent);
    }

    /**
     * @param user User to get the name for
     * @return A more human readable and static user name
     */
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

    /**
     * This fetches the token and calls the query
     */
    private void getTokenAndFetch(Query q) {
        Single.just(null)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe((s) -> {
                    try {
                        twitterInstance.getOAuth2Token();
                    } catch (TwitterException e) {
                        PsLog.e("error getting token: " + e.getErrorMessage());
                    } catch (IllegalStateException e) {
                        PsLog.d("Token already instantiated");
                    } finally {
                        parseTweets(fetchTweets(q));
                    }

                });
    }

    /**
     * Show the remaining calls to the search api with this app's token.
     */
    private void getRateLimit() {
        try {
            RateLimitStatus status = twitterInstance.getRateLimitStatus("search").get("/search/tweets");
            PsLog.v("Limit: " + status.getLimit());
            PsLog.v("Remaining: " + status.getRemaining());
            PsLog.v("ResetTimeInSeconds: " + status.getResetTimeInSeconds());
            PsLog.v("SecondsUntilReset: " + status.getSecondsUntilReset());

        } catch (TwitterException e) {
            PsLog.w("Couldn't get rate limit: " + e.getErrorMessage());
            view.showError("Twitter error");
        }
    }

    @Override
    public void fetchPostsSince( Date dBefore ) {



        Query q = new Query("from:pietsmiet, " +
                "OR from:kessemak2, " +
                "OR from:jaypietsmiet, " +
                "OR from:brosator, " +
                "OR from:br4mm3n " +
                "exclude:replies")
                .sinceId(firstTweetId)
                .count(5)
                .resultType(Query.ResultType.recent);

        getTokenAndFetch(q);
    }


    protected void fetchData(Observable call) {

    }

    @Override
    public void fetchPostsUntil( Date dAfter, int numPosts ) {
        Query q = new Query("from:pietsmiet, " +
                "OR from:kessemak2, " +
                "OR from:jaypietsmiet, " +
                "OR from:brosator, " +
                "OR from:br4mm3n " +
                "exclude:replies");
        if(lastTweetId != 0) {
                q.maxId(lastTweetId);
        }
        q.count(numPosts)
         .resultType(Query.ResultType.recent);

        getTokenAndFetch(q);
    }

}
