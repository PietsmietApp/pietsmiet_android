package de.pscom.pietsmiet.repository;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.json_model.facebookApi.FacebookApiInterface;
import de.pscom.pietsmiet.util.DrawableFetcher;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SecretConstants;
import de.pscom.pietsmiet.view.MainActivity;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.FACEBOOK;

class FacebookRepository extends MainRepository {
    FacebookApiInterface apiInterface;

    FacebookRepository(MainActivity view) {
        super(view);
        if (SecretConstants.facebookToken == null) {
            PsLog.w("No facebook secret or token specified");
            return;
        }
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://graph.facebook.com")
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
                        return new JSONObject(root.getBody()).getJSONArray("data");
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
                        PsLog.w("Konnte post nicht parsen", e);
                        throw Exceptions.propagate(e);
                    }
                })
                .filter(res -> res != null)
                .flatMapIterable(l -> l)
                .onErrorReturn(err -> {
                    PsLog.e("Couldn't load Facebook", err);
                    view.showMessage(view.getString(R.string.error_facebook_loading));
                    return null;
                })
                .filter(response -> response != null)
                .map(post -> {
                    Post.PostBuilder postBuilder = new Post.PostBuilder(FACEBOOK);
                    try {
                        if (!(post.has("from") && post.getJSONObject("from").has("name") && post.has("created_time"))) {
                            return postBuilder;
                        }
                        postBuilder.thumbnailUrl(DrawableFetcher.getThumbnailUrlFromFacebook(post, false));
                        postBuilder.thumbnailHDUrl(DrawableFetcher.getThumbnailUrlFromFacebook(post, true));
                        postBuilder.title(post.getJSONObject("from").getString("name"));
                        postBuilder.description((post.has("message")) ? post.getString("message") : "");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
                        postBuilder.date(dateFormat.parse(post.getString("created_time")));
                        if (post.has("id") && post.getString("id") != null && !post.getString("id").isEmpty()) {
                            postBuilder.url("http://www.facebook.com/" + post.getString("id"));
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
    public Observable<Post.PostBuilder> fetchPostsSinceObservable(Date dSince, int numPosts) {
        return parsePosts("&since=" + String.valueOf(dSince.getTime() / 1000), numPosts);
    }


    @Override
    public Observable<Post.PostBuilder> fetchPostsUntilObservable(Date dUntil, int numPosts) {
        return parsePosts("&until=" + String.valueOf(dUntil.getTime() / 1000), numPosts);
    }

}
