package de.pscom.pietsmiet.data.facebook;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.pscom.pietsmiet.data.util.RetrofitHelper;
import retrofit2.Retrofit;

@Singleton
class FacebookDataStoreFactory {
    private final Context context;

    @Inject
    FacebookDataStoreFactory(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * This will return the needed DataStore implementation based on what we need. In the future
     * this may return a cache implementation if everything is already cached
     *
     * @param loadNewer True if new posts are loaded, false if old posts are loaded
     * @return A FirebaseDataStore implementation based on context
     */
    public FacebookDataStore create(boolean loadNewer) {
        FacebookDataStore facebookDataStore;
        Retrofit retrofit = RetrofitHelper.getRetrofit("url"/*fixme*/);
        FacebookApiInterface apiInterface = retrofit.create(FacebookApiInterface.class);

        facebookDataStore = new FacebookCloudDataStore(apiInterface);
        return facebookDataStore;
    }
}
