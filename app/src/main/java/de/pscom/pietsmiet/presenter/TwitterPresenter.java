package de.pscom.pietsmiet.presenter;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.model.twitterApi.TwitterApiInterface;
import de.pscom.pietsmiet.model.twitterApi.TwitterRoot;
import de.pscom.pietsmiet.model.twitterApi.TwitterUser;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SecretConstants;
import de.pscom.pietsmiet.util.SettingsHelper;
import de.pscom.pietsmiet.util.SharedPreferenceHelper;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_TWITTER_BEARER;

public class TwitterPresenter extends MainPresenter {
    public static Post firstTweet, lastTweet;
    TwitterApiInterface apiInterface;
    private final String query = "from:pietsmiet, " +
            "OR from:kessemak2, " +
            "OR from:jaypietsmiet, " +
            "OR from:brosator, " +
            "OR from:br4mm3n " +
            "exclude:replies";

    public TwitterPresenter(MainActivity view) {
        super(view);
        if (!checkForKeys()) return;
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.twitter.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        apiInterface = retrofit.create(TwitterApiInterface.class);
        getToken();
    }

    protected boolean checkForKeys() {
        if (SecretConstants.twitterSecret == null) {
            PsLog.w("No twitter secret specified");
            return false;
        }
        return true;
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
                    view.showError("Twitter konnte nicht geladen werden");
                    return null;
                })
                .map(status -> {
                    postBuilder = new Post.PostBuilder(PostType.TWITTER);
                    try {
                        postBuilder.description(status.text)
                                .date(getTwitterDate(status.createdAt))
                                .url("https://twitter.com/" + status.user.screenName + "/status/" + status.idStr)
                                .id(status.id)
                                .title(getDisplayName(status.user));
                        if (status.entities.media != null && status.entities.media.size() > 0) {
                            postBuilder.thumbnailHDUrl(status.entities.media.get(0).mediaUrlHttps)
                                    .thumbnailUrl(status.entities.media.get(0).mediaUrlHttps + ":thumb");
                        }
                    } catch (ParseException e) {
                        //todo
                        PsLog.w("you suck", e);
                    }
                    return postBuilder;
                });
    }

    private static Date getTwitterDate(String date) throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("EE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
        return sf.parse(date);
    }

    protected Observable<String> getToken() {
        return Observable.just("")
                .flatMap(ign -> {
                    // Use token from sharedPrefs if not empty
                    String token = SettingsHelper.stringTwitterBearer;
                    if (token != null) {
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
                });
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
    public Observable<Post.PostBuilder> fetchPostsSinceObservable(Date dBefore) {
        Observable<TwitterRoot> obs;
        if (firstTweet != null) {
            obs = getToken().flatMap(bearer -> apiInterface.getTweetsSince("Bearer " + bearer, query, 50, firstTweet.getId()));
        } else {
            obs = getToken().flatMap(bearer -> apiInterface.getTweets("Bearer " + bearer, query, 50));
        }
        return parseTweets(obs);
    }

    @Override
    public Observable<Post.PostBuilder> fetchPostsUntilObservable(Date dAfter, int numPosts) {
        Observable<TwitterRoot> obs;
        if (lastTweet != null) {
            obs = getToken().flatMap(bearer -> apiInterface.getTweetsUntil("Bearer " + bearer, query, numPosts, lastTweet.getId()));
        } else {
            obs = getToken().flatMap(bearer -> apiInterface.getTweets("Bearer " + bearer, query, numPosts));
        }

        return parseTweets(obs);
    }

}
