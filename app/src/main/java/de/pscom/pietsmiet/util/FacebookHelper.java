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
                .flatMapIterable(l -> l)
                .flatMapIterable(result -> {
                    try {
                        return result.asResponseList();
                    } catch (FacebookException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .map(rawPost -> {
                    try {
                        return DataObjectFactory.createPost(rawPost.toString());
                    } catch (FacebookException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(response -> response != null)
                .toSortedList(FacebookHelper::comparePosts)
                .flatMapIterable(l -> l)
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
            batch.add(new BatchRequest(RequestMethod.GET, "pietsmittie/posts?limit=5&fields=from,created_time,message"));
            //Chris
            batch.add(new BatchRequest(RequestMethod.GET, "brosator/posts?limit=5&fields=from,created_time,message"));
            //Jay
            batch.add(new BatchRequest(RequestMethod.GET, "icetea3105/posts?limit=5&fields=from,created_time,message"));
            //Sep
            batch.add(new BatchRequest(RequestMethod.GET, "kessemak88/posts?limit=5&fields=from,created_time,message"));
            //Brammen
            batch.add(new BatchRequest(RequestMethod.GET, "br4mm3n/posts?limit=5&fields=from,created_time,message"));

            return facebook.executeBatch(batch);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Integer comparePosts(Post post1, Post post2) {
        return post1.getCreatedTime().compareTo(post2.getCreatedTime());
    }
}
