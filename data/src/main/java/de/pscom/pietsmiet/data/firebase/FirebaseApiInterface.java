package de.pscom.pietsmiet.data.firebase;

import java.util.Map;

import de.pscom.pietsmiet.data.firebase.model.FirebaseEntity;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

@SuppressWarnings("SameParameterValue")
public interface FirebaseApiInterface {
    // Get all scopes (with specific dates)
    @GET(".json")
    Observable<Map<String, Map<String, FirebaseEntity>>> getAll();

    @GET(".json?orderBy=\"date\"")
    Observable<Map<String, Map<String, FirebaseEntity>>> getAllSince(@Query("startAt") long fromTime);

    @GET(".json?orderBy=\"date\"")
    Observable<Map<String, Map<String, FirebaseEntity>>> getAllUntil(@Query("endAt") long untilTime,
                                                                     @Query("limitToLast") int limit);

    // Get a single scope (with specific dates)
    @GET("{scope}.json")
    Observable<Map<String, FirebaseEntity>> getScopeAll(@Path("scope") String scope);

    @GET("{scope}.json?orderBy=\"date\"")
    Observable<Map<String, FirebaseEntity>> getScopeSince(@Path("scope") String scope,
                                                          @Query("startAt") long fromTime);

    @GET("{scope}.json?orderBy=\"date\"")
    Observable<Map<String, FirebaseEntity>> getScopeUntil(@Path("scope") String scope,
                                                          @Query("endAt") long untilTime,
                                                          @Query("limitToLast") int limit);
}
