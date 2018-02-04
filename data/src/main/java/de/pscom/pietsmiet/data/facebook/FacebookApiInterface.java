package de.pscom.pietsmiet.data.facebook;

import java.util.List;

import de.pscom.pietsmiet.data.facebook.model.FacebookEntity;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

@SuppressWarnings("SameParameterValue")
interface FacebookApiInterface {
    @FormUrlEncoded
    @POST("v2.9")
    Observable<List<FacebookEntity>> getFBRootObject(@Query("access_token") String fb_api_token, @Field("include_headers") boolean b, @Field("batch") String batchReq);

}