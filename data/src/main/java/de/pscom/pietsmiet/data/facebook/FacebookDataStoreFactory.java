package de.pscom.pietsmiet.data.facebook;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.pscom.pietsmiet.data.facebook.model.FacebookEntity;
import de.pscom.pietsmiet.data.util.RetrofitHelper;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        Type listType = new TypeToken<List<FacebookEntity>>() {
        }.getType();
        Gson gson = new GsonBuilder().registerTypeAdapter(listType, new FacebookResponseDeserializer()).create();
        Retrofit retrofit = RetrofitHelper.getRetrofit("url"/*fixme*/, GsonConverterFactory.create(gson));
        FacebookApiInterface apiInterface = retrofit.create(FacebookApiInterface.class);

        facebookDataStore = new FacebookCloudDataStore(apiInterface);
        return facebookDataStore;
    }
}
