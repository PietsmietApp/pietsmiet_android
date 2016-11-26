package de.pscom.pietsmiet.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YoutubeApiInterface {
    @GET("playlistItems?part=snippet%2CcontentDetails%2Cstatus+")
    Call<YoutubeRoot> getPlaylist(@Query("maxResults") int maxResults, @Query("key") String key, @Query("playlistId") String playlistId);
}
