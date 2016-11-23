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
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.FACEBOOK;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_FACEBOOK_DATE;

public class FacebookPresenter extends MainPresenter {
    public static final int LIMIT_PER_USER = 4;
    private Facebook mFacebook;

    private String lastFetchedTime;

    public FacebookPresenter(MainActivity view) {
        super(view, FACEBOOK);
        if (view != null && SharedPreferenceHelper.shouldUseCache) {
            lastFetchedTime = SharedPreferenceHelper.getSharedPreferenceString(view, KEY_FACEBOOK_DATE, "");
        }
        if (SecretConstants.facebookToken == null || SecretConstants.facebookSecret == null) {
            PsLog.w("No facebook secret or token specified");
            return;
        }
        mFacebook = new FacebookFactory().getInstance();
        mFacebook.setOAuthAppId("664158170415954", SecretConstants.facebookSecret);
        mFacebook.setOAuthAccessToken(new AccessToken(SecretConstants.facebookToken, null));
        parsePosts();
    }

    private void parsePosts() {
        Observable.defer(new Func0<Observable<List<BatchResponse>>>() {
            @Override
            public Observable<List<BatchResponse>> call() {
                return Observable.just(FacebookPresenter.this.loadPosts());
            }
        })
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
                .subscribe(new Action1<facebook4j.Post>() {
                    @Override
                    public void call(facebook4j.Post post) {
                        //Drawable thumb = DrawableFetcher.getDrawableFromPost(post);
                        FacebookPresenter.this.post = new Post();
                        //this.post.setThumbnail(thumb);
                        FacebookPresenter.this.post.setTitle(post.getFrom().getName());
                        FacebookPresenter.this.post.setDescription(post.getMessage());
                        FacebookPresenter.this.post.setDatetime(post.getCreatedTime());
                        FacebookPresenter.this.post.setPostType(FACEBOOK);
                        if (post.getId() != null && !post.getId().isEmpty()) {
                            FacebookPresenter.this.post.setUrl("http://www.facebook.com/" + post.getId());
                        }
                        posts.add(FacebookPresenter.this.post);
                    }
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
