package de.pscom.pietsmiet.data.twitch;

import de.pscom.pietsmiet.data.twitch.model.TwitchEntity;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

@SuppressWarnings("SameParameterValue")
public interface TwitchApiInterface {

    @GET("streams/{id}")
    Observable<TwitchEntity> getStreamObject(@Path("id") String channelId, @Query("client_id") String clientId);

}
