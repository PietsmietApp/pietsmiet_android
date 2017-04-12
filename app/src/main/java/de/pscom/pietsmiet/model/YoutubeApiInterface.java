package de.pscom.pietsmiet.model;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

@SuppressWarnings("SameParameterValue")
public interface YoutubeApiInterface {
    //todo order=date funktioniert bei sincedate nur solange nicht mehr als 50 neue Videos geladen werden müssen osnst werden welche übersprungen, da die order abwärts ist und nihct aufsteigend!
    @GET("search?part=snippet%2Cid&order=date&fields=items(id(videoId),snippet(title,publishedAt,thumbnails(default(url))))")
    Observable<YoutubeRoot> getPlaylistUntilDate(@Query("maxResults") int maxResults, @Query("key") String key, @Query("channelId") String channelId, @Query("publishedBefore") String dBefore);

    @GET("search?part=snippet%2Cid&order=date&fields=items(id(videoId),snippet(title,publishedAt,thumbnails(default(url))))")
    Observable<YoutubeRoot> getPlaylistSinceDate(@Query("maxResults") int maxResults, @Query("key") String key, @Query("channelId") String channelId, @Query("publishedAfter") String dAfter);
}
