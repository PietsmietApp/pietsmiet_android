package de.pscom.pietsmiet.backend;

import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SecretConstants;
import de.pscom.pietsmiet.util.SharedPreferenceHelper;
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
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.FACEBOOK;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_FACEBOOK_DATE;

public class FacebookPresenter extends MainPresenter {
    public static final int LIMIT_PER_USER = 4;
    private Facebook mFacebook;

    private String lastFetchedTime;

    public FacebookPresenter() {
        super(FACEBOOK);
        if (SecretConstants.facebookToken == null || SecretConstants.facebookSecret == null) {
            PsLog.w("No facebook secret or token specified");
            return;
        }
        mFacebook = new FacebookFactory().getInstance();
        mFacebook.setOAuthAppId("664158170415954", SecretConstants.facebookSecret);
        mFacebook.setOAuthAccessToken(new AccessToken(SecretConstants.facebookToken, null));
        parsePosts();
    }

    @Override
    public void onTakeView(MainActivity view) {
        super.onTakeView(view);
        if (view != null && SharedPreferenceHelper.shouldUseCache) {
            lastFetchedTime = SharedPreferenceHelper.getSharedPreferenceString(view, KEY_FACEBOOK_DATE, "");
        }
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
                        PsLog.w(e.getMessage());
                        return null;
                    }
                })
                .filter(response -> response != null)
                .subscribe(post -> {
                    //Drawable thumb = DrawableFetcher.getDrawableFromPost(post);
                    this.post = new Post();
                    //this.post.setThumbnail(thumb);
                    this.post.setTitle(post.getFrom().getName());
                    this.post.setDescription(post.getMessage());
                    this.post.setDatetime(post.getCreatedTime());
                    this.post.setPostType(FACEBOOK);
                    posts.add(this.post);
                }, e -> PsLog.e(e.toString()), () -> {
                    finished();
                    lastFetchedTime = String.valueOf(new Date().getTime() / 1000);
                    if (view != null) {
                        SharedPreferenceHelper.setSharedPreferenceString(view, KEY_FACEBOOK_DATE, lastFetchedTime);
                    }
                });
    }

    /**
     * @return List of unparsed posts from Team Pietsmiet
     */
    private List<BatchResponse> loadPosts() {
        String sinceTime = "";
        if (lastFetchedTime != null && !lastFetchedTime.isEmpty()) {
            sinceTime = "&since=" + lastFetchedTime;
        }

        try {
            BatchRequests<BatchRequest> batch = new BatchRequests<>();
            //Piet
            batch.add(new BatchRequest(RequestMethod.GET, "pietsmittie/posts?limit=" + LIMIT_PER_USER + "&fields=from,created_time,message,picture" + sinceTime));
            //Chris
            batch.add(new BatchRequest(RequestMethod.GET, "brosator/posts?limit=" + LIMIT_PER_USER + "&fields=from,created_time,message,picture" + sinceTime));
            //Jay
            batch.add(new BatchRequest(RequestMethod.GET, "icetea3105/posts?limit=" + LIMIT_PER_USER + "&fields=from,created_time,message,picture" + sinceTime));
            //Sep
            batch.add(new BatchRequest(RequestMethod.GET, "kessemak88/posts?limit=" + LIMIT_PER_USER + "&fields=from,created_time,message,picture" + sinceTime));
            //Brammen
            batch.add(new BatchRequest(RequestMethod.GET, "br4mm3n/posts?limit=" + LIMIT_PER_USER + "&fields=from,created_time,message,picture" + sinceTime));

            return mFacebook.executeBatch(batch);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
