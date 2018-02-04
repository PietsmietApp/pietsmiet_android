package de.pscom.pietsmiet.data.firebase;

import java.util.Date;
import java.util.Map;

import de.pscom.pietsmiet.data.firebase.model.FirebaseEntity;
import io.reactivex.Observable;


class FirebaseCloudDataStore implements FirebaseDataStore {
    // todo extract
    private static final String TOPIC_VIDEO = "video";
    private static final String TOPIC_UPLOADPLAN = "uploadplan";
    private static final String TOPIC_NEWS = "news";
    private static final String TOPIC_PIETCAST = "pietcast";

    private final FirebaseApiInterface apiInterface;

    FirebaseCloudDataStore(FirebaseApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    @Override
    public Observable<FirebaseEntity> newPosts(Date firstPostDate) {
        Observable<Map<String, FirebaseEntity>> toReturn = Observable.empty();
        if (/*boolCategoryPietsmietVideos*/ true) {
            toReturn.mergeWith(apiInterface.getScopeSince(TOPIC_VIDEO, firstPostDate.getTime()));
        }
        if (/*boolCategoryPietsmietUploadplan*/ true) {
            toReturn.mergeWith(apiInterface.getScopeSince(TOPIC_UPLOADPLAN, firstPostDate.getTime()));
        }
        if (/*boolCategoryPietsmietNews*/ true) {
            toReturn.mergeWith(apiInterface.getScopeSince(TOPIC_NEWS, firstPostDate.getTime()));
        }
        if (/*boolCategoryPietcast*/ true) {
            toReturn.mergeWith(apiInterface.getScopeSince(TOPIC_PIETCAST, firstPostDate.getTime()));
        }
        return toReturn.flatMapIterable(Map::values);
    }

    @Override
    public Observable<FirebaseEntity> oldPosts(Date lastPostDate, int numPosts) {
        Observable<Map<String, FirebaseEntity>> toReturn = Observable.empty();
        if (/*boolCategoryPietsmietVideos*/ true) {
            toReturn.mergeWith(apiInterface.getScopeUntil(TOPIC_VIDEO, lastPostDate.getTime(), numPosts));
        }
        if (/*boolCategoryPietsmietUploadplan*/ true) {
            toReturn.mergeWith(apiInterface.getScopeUntil(TOPIC_UPLOADPLAN, lastPostDate.getTime(), numPosts));
        }
        if (/*boolCategoryPietsmietNews*/ true) {
            toReturn.mergeWith(apiInterface.getScopeUntil(TOPIC_NEWS, lastPostDate.getTime(), numPosts));
        }
        if (/*boolCategoryPietcast*/ true) {
            toReturn.mergeWith(apiInterface.getScopeUntil(TOPIC_PIETCAST, lastPostDate.getTime(), numPosts));
        }
        return toReturn.flatMapIterable(Map::values);
    }
}
