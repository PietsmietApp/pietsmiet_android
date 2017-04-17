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
import de.pscom.pietsmiet.util.SettingsHelper;
import rx.Observable;
import rx.exceptions.Exceptions;
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

public class TwitterPresenter extends MainPresenter {
    public static long lastTweetId, firstTweetId;
    private Twitter twitterInstance;

    public TwitterPresenter(MainActivity view) {
        super(view);

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

    private Observable<Post.PostBuilder> parseTweets(Query q) {
        return Observable.defer(() -> Observable.just(q))
                .compose(getToken())
                .flatMapIterable(this::fetchTweets)
                .map(tweet -> {
                    Drawable thumb = null;

                    if(SettingsHelper.shouldLoadHDImages(view)) {
                        thumb = DrawableFetcher.getDrawableFromTweet(tweet);
                    } else {
                        thumb = DrawableFetcher.getDrawableThumbFromTweet(tweet);
                    }
                    postBuilder = new Post.PostBuilder(TWITTER);
                    postBuilder.thumbnail(thumb);
                    postBuilder.title(getDisplayName(tweet.getUser()));
                    postBuilder.description(tweet.getText());
                    postBuilder.id(tweet.getId());
                    postBuilder.date(tweet.getCreatedAt());
                    if (tweet.getUser() != null && tweet.getId() != 0) {
                        postBuilder.url("https://twitter.com/" + tweet.getUser().getScreenName()
                                + "/status/" + tweet.getId());
                    }
                    return postBuilder;
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
    private Observable.Transformer<Query, Query> getToken() {
        return queryObservable -> queryObservable.map(query -> {
            try {
                twitterInstance.getOAuth2Token();
            } catch (TwitterException e) {
                PsLog.e("error getting token: " + e.getErrorMessage());
                throw Exceptions.propagate(e);
            } catch (IllegalStateException ignored) {
                PsLog.d("Token already instantiated");
            }
            return query;
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
    public Observable<Post.PostBuilder> fetchPostsSinceObservable(Date dBefore) {
        Query q = new Query("from:pietsmiet, " +
                "OR from:kessemak2, " +
                "OR from:jaypietsmiet, " +
                "OR from:brosator, " +
                "OR from:br4mm3n " +
                "exclude:replies")
                .sinceId(firstTweetId)
                .count(50)
                .resultType(Query.ResultType.recent);

        return parseTweets(q);
    }

    @Override
    public Observable<Post.PostBuilder> fetchPostsUntilObservable(Date dAfter, int numPosts) {
        Query q = new Query("from:pietsmiet, " +
                "OR from:kessemak2, " +
                "OR from:jaypietsmiet, " +
                "OR from:brosator, " +
                "OR from:br4mm3n " +
                "exclude:replies");
        if (lastTweetId != 0) {
            q.maxId(lastTweetId);
        }
        q.count(numPosts)
                .resultType(Query.ResultType.recent);

        return parseTweets(q);
    }

}
