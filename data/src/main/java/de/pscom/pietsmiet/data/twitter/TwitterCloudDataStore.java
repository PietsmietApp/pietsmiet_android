package de.pscom.pietsmiet.data.twitter;

import java.util.Date;

import de.pscom.pietsmiet.data.twitter.model.TwitterEntity;
import de.pscom.pietsmiet.data.twitter.model.TwitterStatus;
import de.pscom.pietsmiet.data.twitter.model.TwitterUser;
import io.reactivex.Observable;


class TwitterCloudDataStore implements TwitterDataStore {
    private final TwitterApiInterface apiInterface;
    private static final String query = "from:pietsmiet, " +
            "OR from:kessemak2, " +
            "OR from:jaypietsmiet, " +
            "OR from:brosator, " +
            "OR from:br4mm3n " +
            "exclude:replies";

    TwitterCloudDataStore(TwitterApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    @Override
    public Observable<TwitterStatus> newPosts(Date firstPostDate, int numPosts) {
        Observable<TwitterEntity> obs;
        if (false/*firstTweet != null*/) {
            obs = getToken().flatMap(bearer -> apiInterface.getTweetsSince(bearer, query, numPosts, 0L /*fixme*/));
        } else {
            obs = getToken().flatMap(bearer -> apiInterface.getTweets(bearer, query, numPosts));
        }
        return obs.flatMapIterable(root -> root.statuses);
    }

    /*todo
    retryWhen(throwable -> throwable.flatMap(error -> {
        if (error instanceof HttpException) {
            // Bearer is no longer valid; this happens rarely
            if (((HttpException) error).code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                //PsLog.w("Not authenticated / wrong bearer. Retrying");
                SettingsHelper.stringTwitterBearer = null;
                return Observable.just(null);
            }
        }
        // Unrelated error, throw it
        return Observable.error(error);
    }))*/

    @Override
    public Observable<TwitterStatus> oldPosts(Date lastPostDate, int numPosts) {
        Observable<TwitterEntity> obs;
        if (false/*lastTweet != null*/) {
            obs = getToken().flatMap(bearer -> apiInterface.getTweetsUntil(bearer, query, numPosts, 0L - 1/*fixme*/));
        } else {
            obs = getToken().flatMap(bearer -> apiInterface.getTweets(bearer, query, numPosts));
        }

        return obs.flatMapIterable(root -> root.statuses);
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

    private Observable<String> getToken() {
        return Observable.just("")
                .flatMap(ign -> {
                    // Use token from sharedPrefs if not empty
                    String token = /*fixme*/"SettingsHelper.stringTwitterBearer";
                    if (token != null && token != "") {
                        return Observable.just(token);
                    }
                    // Load new token
                    return apiInterface
                            .getToken("Basic " + /*fixme*/"SecretConstants.twitterSecret", "client_credentials")
                            .map(twitterToken -> {
                                if (twitterToken.tokenType.equals("bearer")) {
                                    //todo store token in shared
                                    //PsLog.d("Loading new twitter authentication bearer");
                                    return twitterToken.accessToken;
                                } else {
                                    // PsLog.e("Twitter did not return a bearer, critical!");
                                    //todo throw Exceptions.propagate(new Throwable("Twitter did not return a bearer, critical!"));
                                    return "";
                                }
                            });
                })
                .map(bearer -> "Bearer " + bearer);
    }

}
