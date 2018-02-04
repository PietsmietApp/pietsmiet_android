package de.pscom.pietsmiet.data.facebook;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;


class FacebookCloudDataStore implements FacebookDataStore {
    private final FacebookApiInterface apiInterface;

    FacebookCloudDataStore(FacebookApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    @Override
    public Observable<JSONObject> newPosts(Date firstPostDate, int numPosts) {
        return parsePosts("&since=" + String.valueOf(firstPostDate.getTime() / 1000), numPosts);
    }

    @Override
    public Observable<JSONObject> oldPosts(Date lastPostDate, int numPosts) {
        return parsePosts("&until=" + String.valueOf(lastPostDate.getTime() / 1000), numPosts);
    }

    // TODO: 04.02.2018 Rewrite the whole thing to also use GSON
    // This removes the need for this code
    private Observable<JSONObject> parsePosts(String strTime, int numPosts) {
        return apiInterface.getFBRootObject("token" /*fixme*/, false, getBatchString(strTime, numPosts))
                .flatMapIterable(res -> res)
                .map(root -> {
                    try {
                        return new JSONObject(root.getBody()).getJSONArray("data");
                    } catch (JSONException e) {
                        //todo
                        return null;
                    }
                })
                .map(rawData -> {
                    List<JSONObject> jl = new ArrayList<>();
                    try {
                        for (int i = 0; i < rawData.length(); i++) {
                            jl.add(i, rawData.getJSONObject(i));
                        }
                        return jl;
                    } catch (JSONException e) {
                        //todo
                        return null;
                    }
                }).flatMapIterable(l -> l);
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
        batch += "{\"method\":\"GET\",\"relative_url\":\"SepPietSmiet" + strFetch + "},";
        //Brammen
        batch += "{\"method\":\"GET\",\"relative_url\":\"br4mm3n" + strFetch + "}";
        batch += "]";
        return batch;
    }
}
