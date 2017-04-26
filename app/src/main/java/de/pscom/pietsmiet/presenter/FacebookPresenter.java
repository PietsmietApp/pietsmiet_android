package de.pscom.pietsmiet.presenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.model.facebookApi.FacebookApiInterface;
import de.pscom.pietsmiet.util.DrawableFetcher;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SecretConstants;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.FACEBOOK;

public class FacebookPresenter extends MainPresenter {

    private static final String strNoFacebookSecretOrTokenSpecified = "No facebook secret or token specified";
    private static final String strGraphDotFacebookDotCom = "https://graph.facebook.com";
    private static final String strData = "data";
    private static final String strKonntePostNichtParsen = "Konnte post nicht parsen";
    private static final String strCouldntLoadFacebook = "Couldn't load Facebook";
    private static final String strFacebookKonnteNichtGeladenWerden = "Facebook konnte nicht geladen werden";
    private static final String strFrom = "from";
    private static final String strName = "name";
    private static final String strMessage = "message";
    private static final String strCreatedTime = "created_time";
    private static final String strSimpleDateFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String strId = "id";
    private static final String strWwwDotFacebookDotCom = "http://www.facebook.com/";

    FacebookApiInterface apiInterface;

    public FacebookPresenter(MainActivity view) {
        super(view);
        if (SecretConstants.facebookToken == null) {
            PsLog.w(strNoFacebookSecretOrTokenSpecified);
            return;
        }
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(strGraphDotFacebookDotCom)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        apiInterface = retrofit.create(FacebookApiInterface.class);
    }

    private Observable<Post.PostBuilder> parsePosts(String strTime, int numPosts) {
        return Observable.defer(() -> apiInterface.getFBRootObject(SecretConstants.facebookToken, false, getBatchString(strTime, numPosts)))
                .filter(result -> result != null)
                .flatMapIterable(res -> res)
                .map(root -> {
                    try {
                        return new JSONObject(root.getBody()).getJSONArray(strData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        throw Exceptions.propagate(e);
                    }
                })
                .filter(rt -> rt != null)
                .map(rawData -> {
                    List<JSONObject> jl = new ArrayList<>();
                    try {
                        for (int i = 0; i < rawData.length(); i++) {
                            jl.add(i, rawData.getJSONObject(i));
                        }
                        return jl;
                    } catch (JSONException e) {
                        PsLog.w(strKonntePostNichtParsen, e);
                        throw Exceptions.propagate(e);
                    }
                })
                .filter(res -> res != null)
                .flatMapIterable(l -> l)
                .onErrorReturn(err -> {
                    PsLog.e(strCouldntLoadFacebook, err);
                    view.showSnackbar(strFacebookKonnteNichtGeladenWerden);
                    return null;
                })
                .filter(response -> response != null)
                .map(post -> {

                    postBuilder = new Post.PostBuilder(FACEBOOK);
                    try {
                        if (!(post.has(strFrom) && post.getJSONObject(strFrom).has(strName) && post.has(strMessage) && post.has(strCreatedTime))) {
                            return postBuilder;
                        }
                        postBuilder.thumbnailUrl(DrawableFetcher.getThumbnailUrlFromFacebook(post, false));
                        postBuilder.thumbnailHDUrl(DrawableFetcher.getThumbnailUrlFromFacebook(post, true));
                        postBuilder.title(post.getJSONObject(strFrom).getString(strName));
                        postBuilder.description((post.has(strMessage)) ? post.getString(strMessage) : "");
                        SimpleDateFormat dateFormat = new SimpleDateFormat(strSimpleDateFormat, Locale.ENGLISH);
                        postBuilder.date(dateFormat.parse(post.getString(strCreatedTime)));
                        if (post.has(strId) && post.getString(strId) != null && !post.getString(strId).isEmpty()) {
                            postBuilder.url(strWwwDotFacebookDotCom+ post.getString(strId));
                        }
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                    return postBuilder;
                });
    }

    /**
     * @return String with BatchRequest for FB posts from Team Pietsmiet
     */

    private String getBatchString(String strTime, int numPosts) {
        String strFetch = "/posts?limit=" + numPosts + strTime + "&fields=from,created_time,message,full_picture,picture\",\"body\":\"\"";
        String batch = "[";
        //Piet
        batch += "{\"method\":\"GET\",\"relative_url\":\"pietsmittie" + strFetch + "},";
        //Chris
        batch += "{\"method\":\"GET\",\"relative_url\":\"brosator" + strFetch + "},";
        //Jay
        batch += "{\"method\":\"GET\",\"relative_url\":\"icetea3105" + strFetch + "},";
        //Sep
        batch += "{\"method\":\"GET\",\"relative_url\":\"kessemak88" + strFetch + "},";
        //Brammen
        batch += "{\"method\":\"GET\",\"relative_url\":\"br4mm3n" + strFetch + "}";
        batch += "]";
        return batch;
    }

    @Override
    public Observable<Post.PostBuilder> fetchPostsSinceObservable(Date dSince) {
        return parsePosts("&since=" + String.valueOf(dSince.getTime() / 1000), 50);
    }


    @Override
    public Observable<Post.PostBuilder> fetchPostsUntilObservable(Date dUntil, int numPosts) {
        return parsePosts("&until=" + String.valueOf(dUntil.getTime() / 1000), numPosts);
    }

}
