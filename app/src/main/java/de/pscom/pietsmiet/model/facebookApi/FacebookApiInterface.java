package de.pscom.pietsmiet.model.facebookApi;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

@SuppressWarnings("SameParameterValue")
public interface FacebookApiInterface {
    @FormUrlEncoded
    @POST ("v2.9")
    Observable<List<FacebookRoot>> getFBRootObject(@Query("access_token") String fb_api_token, @Field("include_headers") boolean b, @Field("batch") String batchReq);

}