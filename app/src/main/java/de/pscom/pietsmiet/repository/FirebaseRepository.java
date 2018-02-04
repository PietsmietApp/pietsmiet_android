package de.pscom.pietsmiet.repository;

import java.util.Date;
import java.util.Map;

import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.json_model.firebaseApi.FirebaseApiInterface;
import de.pscom.pietsmiet.json_model.firebaseApi.FirebaseItem;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.RetrofitHelper;
import de.pscom.pietsmiet.util.SettingsHelper;
import de.pscom.pietsmiet.view.MainActivity;
import retrofit2.Retrofit;
import rx.Observable;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.generic.Post.PostType;
import static de.pscom.pietsmiet.generic.Post.PostType.NEWS;
import static de.pscom.pietsmiet.generic.Post.PostType.PIETCAST;
import static de.pscom.pietsmiet.generic.Post.PostType.PS_VIDEO;
import static de.pscom.pietsmiet.generic.Post.PostType.UPLOADPLAN;
import static de.pscom.pietsmiet.util.FirebaseUtil.TOPIC_NEWS;
import static de.pscom.pietsmiet.util.FirebaseUtil.TOPIC_PIETCAST;
import static de.pscom.pietsmiet.util.FirebaseUtil.TOPIC_UPLOADPLAN;
import static de.pscom.pietsmiet.util.FirebaseUtil.TOPIC_VIDEO;
import static de.pscom.pietsmiet.util.SettingsHelper.boolCategoryPietcast;
import static de.pscom.pietsmiet.util.SettingsHelper.boolCategoryPietsmietNews;
import static de.pscom.pietsmiet.util.SettingsHelper.boolCategoryPietsmietUploadplan;
import static de.pscom.pietsmiet.util.SettingsHelper.boolCategoryPietsmietVideos;

public class FirebaseRepository extends MainRepository {
    FirebaseApiInterface apiInterface;

    FirebaseRepository(MainActivity view) {
        super(view);
        Retrofit retrofit = RetrofitHelper.getRetrofit(SettingsHelper.stringFirebaseDbUrl);
        apiInterface = retrofit.create(FirebaseApiInterface.class);
    }

    private Observable<Post.PostBuilder> parsePostsFromDb(Observable<Map<String, FirebaseItem>> obs) {
        return Observable.defer(() -> obs)
                .onErrorReturn(err -> {
                    PsLog.e("Couldn't load Firebase", err);
                    view.showMessage(view.getString(R.string.error_firebase_loading));
                    return null;
                })
                .filter(result -> result != null)
                .flatMapIterable(Map::values)
                .map(item -> {
                    PostType type;
                    switch (item.scope) {
                        case TOPIC_UPLOADPLAN:
                            type = UPLOADPLAN;
                            break;
                        case TOPIC_NEWS:
                            type = NEWS;
                            break;
                        case TOPIC_PIETCAST:
                            type = PIETCAST;
                            break;
                        case TOPIC_VIDEO:
                            type = PS_VIDEO;
                            break;
                        default:
                            type = null;
                    }
                    Post.PostBuilder postBuilder = new Post.PostBuilder(type);
                    if (!boolCategoryPietsmietVideos && type == PS_VIDEO) {
                        return postBuilder.empty();
                    } else if (!boolCategoryPietsmietUploadplan && type == UPLOADPLAN) {
                        return postBuilder.empty();
                    } else if (!boolCategoryPietsmietNews && type == NEWS) {
                        return postBuilder.empty();
                    } else if (!boolCategoryPietcast && type == PIETCAST) {
                        return postBuilder.empty();
                    }
                    postBuilder = new Post.PostBuilder(type);
                    postBuilder.title(item.title);
                    postBuilder.description(item.desc);
                    postBuilder.date(new Date(item.date));
                    postBuilder.url(item.link);
                    postBuilder.thumbnailHDUrl(item.image_url);
                    postBuilder.thumbnailUrl(item.image_url);
                    return postBuilder;
                });
    }

    @Override
    public Observable<Post.PostBuilder> fetchPostsSinceObservable(Date dBefore, int numPosts) {
        Observable<Map<String, FirebaseItem>> video = Observable.empty();
        Observable<Map<String, FirebaseItem>> uploadplan = Observable.empty();
        Observable<Map<String, FirebaseItem>> pietcast = Observable.empty();
        Observable<Map<String, FirebaseItem>> news = Observable.empty();
        if (boolCategoryPietsmietVideos) {
            video = apiInterface.getScopeSince(TOPIC_VIDEO, dBefore.getTime()).sorted().takeLast(numPosts);
        }
        if (boolCategoryPietsmietUploadplan) {
            uploadplan = apiInterface.getScopeSince(TOPIC_UPLOADPLAN, dBefore.getTime()).sorted().takeLast(numPosts);
        }
        if (boolCategoryPietsmietNews) {
            news = apiInterface.getScopeSince(TOPIC_NEWS, dBefore.getTime()).sorted().takeLast(numPosts);
        }
        if (boolCategoryPietcast) {
            pietcast = apiInterface.getScopeSince(TOPIC_PIETCAST, dBefore.getTime()).sorted().takeLast(numPosts);
        }
        return parsePostsFromDb(Observable.merge(video, news, uploadplan, pietcast).observeOn(Schedulers.io())).sorted().takeLast(numPosts);
    }


    @Override
    public Observable<Post.PostBuilder> fetchPostsUntilObservable(Date dAfter, int numPosts) {
        Observable<Map<String, FirebaseItem>> video = Observable.empty();
        Observable<Map<String, FirebaseItem>> uploadplan = Observable.empty();
        Observable<Map<String, FirebaseItem>> pietcast = Observable.empty();
        Observable<Map<String, FirebaseItem>> news = Observable.empty();
        if (boolCategoryPietsmietVideos) {
            video = apiInterface.getScopeUntil(TOPIC_VIDEO, dAfter.getTime(), numPosts);
        }
        if (boolCategoryPietsmietUploadplan) {
            uploadplan = apiInterface.getScopeUntil(TOPIC_UPLOADPLAN, dAfter.getTime(), numPosts);
        }
        if (boolCategoryPietsmietNews) {
            news = apiInterface.getScopeUntil(TOPIC_NEWS, dAfter.getTime(), numPosts);
        }
        if (boolCategoryPietcast) {
            pietcast = apiInterface.getScopeUntil(TOPIC_PIETCAST, dAfter.getTime(), numPosts);
        }
        return parsePostsFromDb(Observable.merge(video, news, uploadplan, pietcast).observeOn(Schedulers.io())).sorted().take(numPosts);
    }
}
