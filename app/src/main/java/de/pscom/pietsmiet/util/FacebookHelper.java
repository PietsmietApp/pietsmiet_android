package de.pscom.pietsmiet.util;

import android.content.Context;

import java.util.List;

import facebook4j.BatchRequest;
import facebook4j.BatchRequests;
import facebook4j.BatchResponse;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Post;
import facebook4j.auth.AccessToken;
import facebook4j.internal.http.RequestMethod;
import facebook4j.json.DataObjectFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

public class FacebookHelper {
    Context mContext;

    public FacebookHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void loadPosts() {
        Observable.defer(() -> Observable.just(getAllPosts()))
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .map(result -> {
                    try {
                        return result.asResponseList();
                    } catch (FacebookException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(response -> response != null)
                .flatMap(Observable::from)
                .map(rawPost -> {
                    try {
                        return DataObjectFactory.createPost(rawPost.toString());
                    } catch (FacebookException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(response -> response != null)
                .toSortedList(FacebookHelper::compareModel)
                .flatMap(Observable::from)
                .map(Post::getMessage)
                .filter(string -> string != null)
                .subscribe(PsLog::v, e -> PsLog.e(e.toString()));
    }

    private List<BatchResponse> getAllPosts() {
        try {
            Facebook facebook = new FacebookFactory().getInstance();
            facebook.setOAuthAppId("664158170415954", "48b0d6be3acddd1a9959943b76acce31");
            facebook.setOAuthPermissions("public_profile");
            facebook.setOAuthAccessToken(new AccessToken("664158170415954|eLOYv9Ms5CriMN8yB0s3vJ6vZZ4", null));

            BatchRequests<BatchRequest> batch = new BatchRequests<>();
            //Piet
            batch.add(new BatchRequest(RequestMethod.GET, "174416892612899/feed?limit=5"));
            //Chris
            batch.add(new BatchRequest(RequestMethod.GET, "276775629094183/feed?limit=5"));
            //Jay
            batch.add(new BatchRequest(RequestMethod.GET, "275192789211423/feed?limit=5"));
            //Sep
            batch.add(new BatchRequest(RequestMethod.GET, "411585615549330/feed?limit=5"));
            //Brammen
            batch.add(new BatchRequest(RequestMethod.GET, "298837886825395/feed?limit=5"));
            
            return facebook.executeBatch(batch);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Integer compareModel(Post car1, Post car2) {
        return car1.getCreatedTime().compareTo(car2.getCreatedTime());
    }
}
