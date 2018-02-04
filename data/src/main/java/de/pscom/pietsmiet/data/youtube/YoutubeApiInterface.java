package de.pscom.pietsmiet.data.youtube;

import de.pscom.pietsmiet.data.youtube.model.YoutubeEntity;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

@SuppressWarnings("SameParameterValue")
interface YoutubeApiInterface {
    //todo order=date funktioniert bei sincedate nur solange nicht mehr als 50 neue Videos geladen werden müssen sonst werden welche übersprungen, da die order abwärts ist und nicht aufsteigend!
    @GET("search?part=snippet%2Cid&order=date&fields=items(id(videoId),snippet(title,publishedAt,thumbnails(medium(url))))")
    Observable<YoutubeEntity> getPlaylistUntilDate(@Query("maxResults") int maxResults, @Query("key") String key, @Query("channelId") String channelId, @Query("publishedBefore") String dBefore);

    @GET("search?part=snippet%2Cid&order=date&fields=items(id(videoId),snippet(title,publishedAt,thumbnails(medium(url))))")
    Observable<YoutubeEntity> getPlaylistSinceDate(@Query("maxResults") int maxResults, @Query("key") String key, @Query("channelId") String channelId, @Query("publishedAfter") String dAfter);
}
