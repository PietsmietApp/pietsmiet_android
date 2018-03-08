package de.pscom.pietsmiet.data.facebook;

import java.util.Date;

import de.pscom.pietsmiet.data.facebook.model.FacebookData;
import de.pscom.pietsmiet.data.facebook.model.FacebookEntity;
import io.reactivex.Observable;


class FacebookCloudDataStore implements FacebookDataStore {
    private final FacebookApiInterface apiInterface;

    FacebookCloudDataStore(FacebookApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    @Override
    public Observable<FacebookData> newPosts(Date firstPostDate, int numPosts) {
        return parsePosts("&since=" + String.valueOf(firstPostDate.getTime() / 1000), numPosts);
    }

    @Override
    public Observable<FacebookData> oldPosts(Date lastPostDate, int numPosts) {
        return parsePosts("&until=" + String.valueOf(lastPostDate.getTime() / 1000), numPosts);
    }


    private Observable<FacebookData> parsePosts(String strTime, int numPosts) {
        return apiInterface.getFBRootObject("token" /*fixme*/, false, getBatchString(strTime, numPosts))
                .flatMapIterable(l -> l)
                .map(FacebookEntity::getData)
                .flatMapIterable(l -> l);
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