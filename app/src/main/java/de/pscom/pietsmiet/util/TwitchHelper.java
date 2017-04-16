package de.pscom.pietsmiet.util;


import de.pscom.pietsmiet.model.Twitch;
import de.pscom.pietsmiet.model.TwitchApiInterface;
import de.pscom.pietsmiet.model.TwitchStream;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class TwitchHelper {
    private Subscription sub;
    private final String urlTTVAPI = "https://api.twitch.tv/kraken/";
    private final TwitchApiInterface apiInterface;
    public TwitchHelper() {
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlTTVAPI)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        apiInterface = retrofit.create(TwitchApiInterface.class);
    }

    public Observable<TwitchStream> getStreamStatus() {
        return apiInterface.getStreamObject("pietsmiet", SecretConstants.twitchClientId)
                .subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .map(Twitch::getStream);
    }

}
