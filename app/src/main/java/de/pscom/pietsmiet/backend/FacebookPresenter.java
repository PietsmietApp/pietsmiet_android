package de.pscom.pietsmiet.backend;

import java.util.List;

import de.pscom.pietsmiet.adapters.CardItem;

import de.pscom.pietsmiet.util.DrawableFetcher;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SecretConstants;
import facebook4j.BatchRequest;
import facebook4j.BatchRequests;
import facebook4j.BatchResponse;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;
import facebook4j.internal.http.RequestMethod;
import facebook4j.json.DataObjectFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.CardType.FACEBOOK;

public class FacebookPresenter extends MainPresenter {
    Facebook mFacebook;

    public FacebookPresenter() {
        super(FACEBOOK);
        if (SecretConstants.facebookToken == null || SecretConstants.twitterSecret == null) {
            PsLog.w("No twitter secret specified");
            return;
        }
        PsLog.v(SecretConstants.facebookToken);
        mFacebook = new FacebookFactory().getInstance();
        mFacebook.setOAuthAppId("664158170415954", SecretConstants.facebookSecret);
        //facebook.setOAuthPermissions("public_profile");
        mFacebook.setOAuthAccessToken(new AccessToken(SecretConstants.facebookToken, null));
        parsePosts();
    }

    private void parsePosts() {
        Observable.defer(() -> Observable.just(loadPosts()))
                .subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
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
                .subscribe(post -> Observable.defer(() -> Observable.just(DrawableFetcher.getDrawableFromPost(post)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(drawable -> {
                            cardItem = new CardItem();
                            cardItem.setThumbnail(drawable);
                            cardItem.setTitle(post.getFrom().getName());
                            cardItem.setDescription(post.getMessage());
                            cardItem.setDatetime(post.getCreatedTime());
                            publish();
                        }), e -> PsLog.e(e.toString()), () -> PsLog.v("facebook posts geladen"));
    }

    /**
     * @return List of unparsed posts from Team Pietsmiet
     */
    private List<BatchResponse> loadPosts() {
        try {
            BatchRequests<BatchRequest> batch = new BatchRequests<>();
            //Piet
            batch.add(new BatchRequest(RequestMethod.GET, "pietsmittie/posts?limit=5&fields=from,created_time,message,picture"));
            //Chris
            batch.add(new BatchRequest(RequestMethod.GET, "brosator/posts?limit=5&fields=from,created_time,message,picture"));
            //Jay
            batch.add(new BatchRequest(RequestMethod.GET, "icetea3105/posts?limit=5&fields=from,created_time,message,picture"));
            //Sep
            batch.add(new BatchRequest(RequestMethod.GET, "kessemak88/posts?limit=5&fields=from,created_time,message,picture"));
            //Brammen
            batch.add(new BatchRequest(RequestMethod.GET, "br4mm3n/posts?limit=5&fields=from,created_time,message,picture"));

            return mFacebook.executeBatch(batch);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
