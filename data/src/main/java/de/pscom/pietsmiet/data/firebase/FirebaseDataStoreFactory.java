package de.pscom.pietsmiet.data.firebase;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.pscom.pietsmiet.data.util.RetrofitHelper;
import retrofit2.Retrofit;

@Singleton
class FirebaseDataStoreFactory {
    private final Context context;

    @Inject
    FirebaseDataStoreFactory(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * This will return the needed DataStore implementation based on what we need. In the future
     * this may return a cache implementation if everything is already cached
     *
     * @param loadNewer True if new posts are loaded, false if old posts are loaded
     * @return A FirebaseDataStore implementation based on context
     */
    public FirebaseDataStore create(boolean loadNewer) {
        FirebaseDataStore firebaseDataStore;
        Retrofit retrofit = RetrofitHelper.getRetrofit("url"/*fixme*/);
        FirebaseApiInterface apiInterface = retrofit.create(FirebaseApiInterface.class);

        firebaseDataStore = new FirebaseCloudDataStore(apiInterface);
        return firebaseDataStore;
    }
}
