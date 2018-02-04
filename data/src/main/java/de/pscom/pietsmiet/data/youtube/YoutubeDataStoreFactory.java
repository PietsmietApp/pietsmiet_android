package de.pscom.pietsmiet.data.youtube;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.pscom.pietsmiet.data.util.RetrofitHelper;
import retrofit2.Retrofit;

@Singleton
class YoutubeDataStoreFactory {
    private final Context context;

    @Inject
    YoutubeDataStoreFactory(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * This will return the needed DataStore implementation based on what we need. In the future
     * this may return a cache implementation if everything is already cached
     *
     * @param loadNewer True if new posts are loaded, false if old posts are loaded
     * @return A FirebaseDataStore implementation based on context
     */
    public YoutubeDataStore create(boolean loadNewer) {
        YoutubeDataStore youtubeDataStore;
        Retrofit retrofit = RetrofitHelper.getRetrofit("url"/*fixme*/);
        YoutubeApiInterface apiInterface = retrofit.create(YoutubeApiInterface.class);

        youtubeDataStore = new YoutubeCloudDataStore(apiInterface);
        return youtubeDataStore;
    }
}
