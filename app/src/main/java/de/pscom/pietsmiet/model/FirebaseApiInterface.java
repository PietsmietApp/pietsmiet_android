package de.pscom.pietsmiet.model;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

@SuppressWarnings("SameParameterValue")
public interface FirebaseApiInterface {
    // Get all scopes (with specific dates)
    @GET("new.json")
    Observable<Map<String, Map<String, FirebaseItem>>> getAll();

    @GET("new.json?orderBy=\"date\"")
    Observable<Map<String, Map<String, FirebaseItem>>> getAllSince(@Query("startAt") long fromTime);

    @GET("new.json?orderBy=\"date\"")
    Observable<Map<String, Map<String, FirebaseItem>>> getAllUntil(@Query("endAt") long untilTime,
                                                                   @Query("limitToLast") int limit);

    // Get a single scope (with specific dates)
    @GET("new/{scope}.json")
    Observable<Map<String, FirebaseItem>> getScopeAll(@Path("scope") String scope);

    @GET("new/{scope}.json?orderBy=\"date\"")
    Observable<Map<String, FirebaseItem>> getScopeSince(@Path("scope") String scope,
                                                        @Query("startAt") long fromTime);

    @GET("new/{scope}.json?orderBy=\"date\"")
    Observable<Map<String, FirebaseItem>> getScopeUntil(@Path("scope") String scope,
                                                        @Query("endAt") long untilTime,
                                                        @Query("limitToLast") int limit);
}
