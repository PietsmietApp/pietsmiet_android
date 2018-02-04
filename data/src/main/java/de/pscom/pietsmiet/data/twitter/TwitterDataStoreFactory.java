package de.pscom.pietsmiet.data.twitter;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.pscom.pietsmiet.data.twitter.TwitterApiInterface;
import de.pscom.pietsmiet.data.twitter.TwitterDataStore;
import de.pscom.pietsmiet.data.util.RetrofitHelper;
import retrofit2.Retrofit;

@Singleton
class TwitterDataStoreFactory {
    private final Context context;

    @Inject
    TwitterDataStoreFactory(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * This will return the needed DataStore implementation based on what we need. In the future
     * this may return a cache implementation if everything is already cached
     *
     * @param loadNewer True if new posts are loaded, false if old posts are loaded
     * @return A FirebaseDataStore implementation based on context
     */
    public TwitterDataStore create(boolean loadNewer) {
        TwitterDataStore twitterDataStore;
        Retrofit retrofit = RetrofitHelper.getRetrofit("url"/*fixme*/);
        TwitterApiInterface apiInterface = retrofit.create(TwitterApiInterface.class);

        twitterDataStore = new TwitterCloudDataStore(apiInterface);
        return twitterDataStore;
    }
}
