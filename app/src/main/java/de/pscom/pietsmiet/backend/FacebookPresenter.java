package de.pscom.pietsmiet.backend;

import android.graphics.drawable.Drawable;

import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.DrawableFetcher;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SecretConstants;
import de.pscom.pietsmiet.util.SettingsHelper;
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

import static de.pscom.pietsmiet.util.PostType.FACEBOOK;

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

    private Observable<Post.PostBuilder> parsePosts(String strTime, int numPosts) {
        return Observable.defer(() -> Observable.just(loadPosts(strTime, numPosts)))
                .flatMapIterable(l -> l)
                .map(result -> {
                    try {
                        return result.asResponseList();
                    } catch (FacebookException e) {
                        PsLog.w("Konnte result nicht parsen", e);
                        return null;
                    }
                })
                .filter(result -> result != null)
                .flatMapIterable(l -> l)
                .map(rawPost -> {
                    try {
                        return DataObjectFactory.createPost(rawPost.toString());
                    } catch (FacebookException e) {
                        PsLog.w("Konnte post nicht parsen", e);
                        view.showError("Facebook parsing error");
                        return null;
                    }
                })
                .filter(response -> response != null)
                .map(post -> {
                    Drawable thumb = (SettingsHelper.shouldLoadHDImages(view) ? DrawableFetcher.getFullDrawableFromPost(post) : DrawableFetcher.getDrawableFromPost(post));
                    postBuilder = new Post.PostBuilder(FACEBOOK);
                    postBuilder.thumbnail(thumb);
                    postBuilder.title(post.getFrom().getName());
                    postBuilder.description(post.getMessage());
                    postBuilder.date(post.getCreatedTime());
                    if (post.getId() != null && !post.getId().isEmpty()) {
                        postBuilder.url("http://www.facebook.com/" + post.getId());
                    }
                    return postBuilder;
                });
    }

    /**
     * @return List of unparsed posts from Team Pietsmiet
     */
    private List<BatchResponse> loadPosts(String strTime, int numPosts) {
        String strFetch = "/posts?limit=" + numPosts + "&fields=from,created_time,message," + (SettingsHelper.shouldLoadHDImages(view) ? "full_" : "") + "picture" + strTime;
        try {
            BatchRequests<BatchRequest> batch = new BatchRequests<>();
            //Piet
            batch.add(new BatchRequest(RequestMethod.GET, "pietsmittie" + strFetch));
            //Chris
            batch.add(new BatchRequest(RequestMethod.GET, "brosator" + strFetch));
            //Jay
            batch.add(new BatchRequest(RequestMethod.GET, "icetea3105" + strFetch));
            //Sep
            batch.add(new BatchRequest(RequestMethod.GET, "kessemak88" + strFetch));
            //Brammen
            batch.add(new BatchRequest(RequestMethod.GET, "br4mm3n" + strFetch));

            return mFacebook.executeBatch(batch);
        } catch (Exception e) {
            view.showError("Facebook API unreachable");
            PsLog.e("Facebook Api Error: ", e);
        }
        return null;
    }

    @Override
    public Observable<Post.PostBuilder> fetchPostsSinceObservable(Date dSince) {
        return parsePosts("&since=" + String.valueOf(dSince.getTime() / 1000), 50);
    }


    @Override
    public Observable<Post.PostBuilder> fetchPostsUntilObservable(Date dUntil, int numPosts) {
        return parsePosts("&until=" + String.valueOf(dUntil.getTime() / 1000), numPosts);
        //todo watch this if working correctly -> because many Posts are rejected
    }

}
