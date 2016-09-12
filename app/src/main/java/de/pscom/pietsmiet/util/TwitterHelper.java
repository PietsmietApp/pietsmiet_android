package de.pscom.pietsmiet.util;

import android.content.Context;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterApiException;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;


public class TwitterHelper {
    TwitterApiClient twitterApiClient;
    long lastTweetId;

    public TwitterHelper(Context mContext) {
        TwitterAuthConfig authConfig = new TwitterAuthConfig("px2E2wOhxNrs4tsr8JqojB2yp", "zyVTNh7x2BUCDChlsZ6OStSqhFBBI8nEBWDGKv2HXcIfMbmLJg");
        Fabric.with(mContext, new TwitterCore(authConfig));
        twitterApiClient = TwitterCore.getInstance().getGuestApiClient();
    }

    public List<Tweet> fetchTweets(int count) {
        final List<Tweet> tweetsToReturn = new ArrayList<>();
        try {
            SearchService searchService = twitterApiClient.getSearchService();
            Call<Search> call = searchService.tweets("from:pietsmiet, " +
                            "OR from:kessemak2, " +
                            "OR from:jaypietsmiet, " +
                            "OR from:brosator, " +
                            "OR from:br4mm3n"
                    , null, null, null, "recent", count, null, null, null, false);
            call.enqueue(new Callback<Search>() {
                @Override
                public void success(Result<Search> result) {
                    for (Tweet tweet : result.data.tweets) {
                        PsLog.v(tweet.text);
                        lastTweetId = tweet.id;
                        tweetsToReturn.add(tweet);
                    }
                }

                public void failure(TwitterException exception) {
                    throw exception;
                }
            });
            return tweetsToReturn;
        } catch (TwitterApiException apiException) {
            PsLog.e("Twitter API-Exception: " +
                    "\nStatus Code: " + apiException.getStatusCode() +
                    "\n Error message: " + apiException.getErrorMessage());
            return null;
        } catch (TwitterAuthException authException) {
            PsLog.e("Twitter Auth-Exception: " + authException.getMessage());
            return null;
        }
    }
}
