package de.pscom.pietsmiet.model.twitchApi;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

@SuppressWarnings("SameParameterValue")
public interface TwitchApiInterface {

    @GET("streams/{id}")
    Observable<Twitch> getStreamObject(@Path("id") String channelId, @Query("client_id") String clientId);

}
