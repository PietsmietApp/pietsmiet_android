package de.pscom.pietsmiet.backend;

import android.graphics.drawable.Drawable;

import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.DrawableFetcher;
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
    private Facebook mFacebook;

    public FacebookPresenter(MainActivity view) {
        super(view);
        if (SecretConstants.facebookToken == null || SecretConstants.facebookSecret == null) {
            PsLog.w("No facebook secret or token specified");
            return;
        }
        mFacebook = new FacebookFactory().getInstance();
        mFacebook.setOAuthAppId("664158170415954", SecretConstants.facebookSecret);
        mFacebook.setOAuthAccessToken(new AccessToken(SecretConstants.facebookToken, null));
    }

    private void parsePosts(String strTime, int numPosts) {
        Observable.defer(() -> Observable.just(loadPosts(strTime, numPosts)))
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
                        view.showError("Facebook parsing error");
                        return null;
                    }
                })
                .filter(response -> response != null)
                .subscribe(post -> {
                    Drawable thumb = DrawableFetcher.getDrawableFromPost(post);
                    postBuilder = new Post.PostBuilder(FACEBOOK);
                    postBuilder.thumbnail(thumb);
                    postBuilder.title(post.getFrom().getName());
                    postBuilder.description(post.getMessage());
                    postBuilder.date(post.getCreatedTime());
                    if (post.getId() != null && !post.getId().isEmpty()) {
                        postBuilder.url("http://www.facebook.com/" + post.getId());
                    }
                    posts.add(this.postBuilder.build());
                }, e -> {
                    PsLog.e(e.toString());
                    view.showError("Facebook parsing error");
                    view.getPostManager().onReadyFetch(posts, FACEBOOK);
                }, () -> {
                    view.getPostManager().onReadyFetch(posts, FACEBOOK);
                });
    }

    /**
     * @return List of unparsed posts from Team Pietsmiet
     */
    private List<BatchResponse> loadPosts( String strTime,  int numPosts) {
        try {
            BatchRequests<BatchRequest> batch = new BatchRequests<>();
            //Piet
            batch.add(new BatchRequest(RequestMethod.GET, "pietsmittie/posts?limit=" + numPosts + "&fields=from,created_time,message,full_picture" + strTime));
            //Chris
            batch.add(new BatchRequest(RequestMethod.GET, "brosator/posts?limit=" + numPosts + "&fields=from,created_time,message,full_picture" + strTime));
            //Jay
            batch.add(new BatchRequest(RequestMethod.GET, "icetea3105/posts?limit=" + numPosts + "&fields=from,created_time,message,full_picture" + strTime));
            //Sep
            batch.add(new BatchRequest(RequestMethod.GET, "kessemak88/posts?limit=" + numPosts + "&fields=from,created_time,message,full_picture" + strTime));
            //Brammen
            batch.add(new BatchRequest(RequestMethod.GET, "br4mm3n/posts?limit=" + numPosts + "&fields=from,created_time,message,full_picture" + strTime));

            return mFacebook.executeBatch(batch);
        } catch (Exception e) {
            view.showError("Facebook API unreachable");
            PsLog.e(e.getMessage());
        }
        return null;
    }

    @Override
    public void fetchNewPosts(Date dBefore) {
        parsePosts("&since=" + String.valueOf(dBefore.getTime() / 1000), 50);
    }


    @Override
    public void fetchPostsBefore(Date dAfter, int numPosts) {
        parsePosts("&until=" + String.valueOf(dAfter.getTime() / 1000), numPosts);
    }

}
