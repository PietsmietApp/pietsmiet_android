package de.pscom.pietsmiet.model;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

@SuppressWarnings("SameParameterValue")
public interface FirebaseApiInterface {
    @GET("{scope}.json")
    Observable<FirebaseItem> getScope(@Path("scope") String scope);
}
