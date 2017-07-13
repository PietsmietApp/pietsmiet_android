package de.pscom.pietsmiet.util;


import de.pscom.pietsmiet.json_model.twitchApi.Twitch;
import de.pscom.pietsmiet.json_model.twitchApi.TwitchApiInterface;
import de.pscom.pietsmiet.json_model.twitchApi.TwitchStream;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TwitchHelper {
    private final String urlTTVAPI = "https://api.twitch.tv/kraken/";
    private final TwitchApiInterface apiInterface;

    /*
     * Initiates the TwitchHelper Object with the Retrofit instance
     */
    public TwitchHelper() {
        if(SecretConstants.twitchClientId == null) {
            PsLog.w("No Twitch ClientID defined... Cannot load TwitchHelper");
        }

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlTTVAPI)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        apiInterface = retrofit.create(TwitchApiInterface.class);
    }

    /* Returns the Observable emitting the TwitchStream Object of channel
     * @param String channelID
     * @return Observable<TwitchStream>
     */
    public Observable<TwitchStream> getStreamStatus(String channelId) {
        return apiInterface.getStreamObject(channelId, SecretConstants.twitchClientId)
                .onBackpressureBuffer()
                .observeOn(AndroidSchedulers.mainThread())
                .map(Twitch::getStream);
    }

}
