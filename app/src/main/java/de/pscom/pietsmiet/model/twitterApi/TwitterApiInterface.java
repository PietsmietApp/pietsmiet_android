package de.pscom.pietsmiet.model.twitterApi;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

@SuppressWarnings("SameParameterValue")
public interface TwitterApiInterface {
    @POST("oauth2/token")
    @FormUrlEncoded
    Observable<TwitterToken> getToken(@Header("Authorization") String authorization, @Field("grant_type") String body);

    @GET("1.1/search/tweets.json?result_type=recent")
    Observable<TwitterRoot> getTweetsSince(@Header("Authorization") String authorization,
                                           @Query("q") String searchQuery,
                                           @Query("count") int count,
                                           @Query("since_id") long since_id);


    @GET("1.1/search/tweets.json?result_type=recent")
    Observable<TwitterRoot> getTweetsUntil(@Header("Authorization") String authorization,
                                           @Query("q") String searchQuery,
                                           @Query("count") int count,
                                           @Query("max_id") long max_id);

    @GET("1.1/search/tweets.json?result_type=recent")
    Observable<TwitterRoot> getTweets(@Header("Authorization") String authorization,
                                           @Query("q") String searchQuery,
                                           @Query("count") int count);
}
