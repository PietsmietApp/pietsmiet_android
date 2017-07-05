package de.pscom.pietsmiet.repository;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.json_model.twitterApi.TwitterApiInterface;
import de.pscom.pietsmiet.json_model.twitterApi.TwitterRoot;
import de.pscom.pietsmiet.json_model.twitterApi.TwitterUser;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.RetrofitHelper;
import de.pscom.pietsmiet.util.SecretConstants;
import de.pscom.pietsmiet.util.SettingsHelper;
import de.pscom.pietsmiet.util.SharedPreferenceHelper;
import de.pscom.pietsmiet.view.MainActivity;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import rx.Observable;
import rx.exceptions.Exceptions;

import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_TWITTER_BEARER;

public class TwitterRepository extends MainRepository { //todo make package only
    public static Post firstTweet, lastTweet;
    private static final String TWITTER_API_URL = "https://api.twitter.com";
    TwitterApiInterface apiInterface;
    private final String query = "from:pietsmiet, " +
            "OR from:kessemak2, " +
            "OR from:jaypietsmiet, " +
            "OR from:brosator, " +
            "OR from:br4mm3n " +
            "exclude:replies";

    TwitterRepository(MainActivity view) {
        super(view);
        if (SecretConstants.twitterSecret == null) {
            PsLog.w("No twitter secret specified");
            return;
        }

        Retrofit retrofit = RetrofitHelper.getRetrofit(TWITTER_API_URL);
        apiInterface = retrofit.create(TwitterApiInterface.class);
    }


    private Observable<Post.PostBuilder> parseTweets(Observable<TwitterRoot> obs) {
        return obs
                .retryWhen(throwable -> throwable.flatMap(error -> {
                    if (error instanceof HttpException) {
                        // Bearer is no longer valid; this happens rarely
                        if (((HttpException) error).code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            PsLog.w("Not authenticated / wrong bearer. Retrying");
                            SettingsHelper.stringTwitterBearer = null;
                            return Observable.just(null);
                        }
                    }
                    // Unrelated error, throw it
                    return Observable.error(error);
                }))
                .filter(root -> root != null)
                .flatMapIterable(root -> root.statuses)
                .onErrorReturn(err -> {
                    PsLog.e("Couldn't load Twitter", err);
                    view.showMessage(view.getString(R.string.error_twitter_loading));
                    return null;
                })
                .filter(status -> status != null)
                .map(status -> {
                    Post.PostBuilder postBuilder = new Post.PostBuilder(PostType.TWITTER);
                    try {
                        postBuilder.description(status.text)
                                .date(getTwitterDate(status.createdAt))
                                .url("https://twitter.com/" + status.user.screenName + "/status/" + status.idStr)
                                .id(status.id)
                                .username(status.user.screenName)
                                .title(getDisplayName(status.user));
                        if (status.entities.media != null && status.entities.media.size() > 0) {
                            postBuilder.thumbnailHDUrl(status.entities.media.get(0).mediaUrlHttps)
                                    .thumbnailUrl(status.entities.media.get(0).mediaUrlHttps + ":thumb");
                        }
                    } catch (ParseException e) {
                        //todo
                        PsLog.w("Twitter Date parsing failed", e);
                    }
                    return postBuilder;
                });
    }

    private static Date getTwitterDate(String date) throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("EE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
        return sf.parse(date);
    }

    private Observable<String> getToken() {
        return Observable.just("")
                .flatMap(ign -> {
                    // Use token from sharedPrefs if not empty
                    String token = SettingsHelper.stringTwitterBearer;
                    if (token != null && token != "") {
                        return Observable.just(token);
                    }
                    // Load new token
                    return apiInterface
                            .getToken("Basic " + SecretConstants.twitterSecret, "client_credentials")
                            .map(twitterToken -> {
                                PsLog.d("Loading new twitter authentication bearer");
                                if (twitterToken.tokenType.equals("bearer")) {
                                    SharedPreferenceHelper.setSharedPreferenceString(view,
                                            KEY_TWITTER_BEARER, twitterToken.accessToken);
                                    return twitterToken.accessToken;
                                } else {
                                    PsLog.e("Twitter did not return a bearer, critical!");
                                    throw Exceptions.propagate(new Throwable("Twitter did not return a bearer, critical!"));
                                }
                            });
                })
                .map(bearer -> "Bearer " + bearer);
    }

    /**
     * @return A more human readable and static user name
     */
    private String getDisplayName(TwitterUser user) {
        int userId = (int) Math.max(Math.min(Integer.MAX_VALUE, user.id), Integer.MIN_VALUE);
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
                return user.screenName;
        }
    }


    @Override
    public Observable<Post.PostBuilder> fetchPostsSinceObservable(Date dBefore, int numPosts) {
        Observable<TwitterRoot> obs;
        if (firstTweet != null) {
            obs = getToken().flatMap(bearer -> apiInterface.getTweetsSince(bearer, query, numPosts, firstTweet.getId()));
        } else {
            obs = getToken().flatMap(bearer -> apiInterface.getTweets(bearer, query, numPosts));
        }
        return parseTweets(obs);
    }

    @Override
    public Observable<Post.PostBuilder> fetchPostsUntilObservable(Date dAfter, int numPosts) {
        Observable<TwitterRoot> obs;
        if (lastTweet != null) {
            obs = getToken().flatMap(bearer -> apiInterface.getTweetsUntil(bearer, query, numPosts, lastTweet.getId() - 1));
        } else {
            obs = getToken().flatMap(bearer -> apiInterface.getTweets(bearer, query, numPosts));
        }

        return parseTweets(obs);
    }

}
