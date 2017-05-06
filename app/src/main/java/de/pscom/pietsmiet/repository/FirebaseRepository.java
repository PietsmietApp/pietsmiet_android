package de.pscom.pietsmiet.repository;

import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Map;

import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.json_model.firebaseApi.FirebaseApiInterface;
import de.pscom.pietsmiet.json_model.firebaseApi.FirebaseItem;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SettingsHelper;
import de.pscom.pietsmiet.view.MainActivity;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.FirebaseUtil.TOPIC_NEWS;
import static de.pscom.pietsmiet.util.FirebaseUtil.TOPIC_PIETCAST;
import static de.pscom.pietsmiet.util.FirebaseUtil.TOPIC_UPLOADPLAN;
import static de.pscom.pietsmiet.util.FirebaseUtil.TOPIC_VIDEO;
import static de.pscom.pietsmiet.util.PostType.NEWS;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.PS_VIDEO;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;

public class FirebaseRepository extends MainRepository {
    FirebaseApiInterface apiInterface;

    FirebaseRepository(MainActivity view) {
        super(view);
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SettingsHelper.stringFirebaseDbUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();
        apiInterface = retrofit.create(FirebaseApiInterface.class);
    }

    private Observable<Post.PostBuilder> parsePostsFromDb(Observable<Map<String, Map<String, FirebaseItem>>> obs) {
        return Observable.defer(() -> obs)
                .retryWhen(throwable -> throwable.flatMap(error -> {
                    if (error instanceof SocketTimeoutException) {
                        PsLog.w("Firebase Timeout", error);
                        view.showMessage(view.getString(R.string.error_firebase_timeout));
                        return Observable.just(null);
                    }
                    // Unrelated error, throw it
                    return Observable.error(error);
                }))
                .onErrorReturn(err -> {
                    PsLog.e("Couldn't load Firebase", err);
                    view.showMessage(view.getString(R.string.error_firebase_loading));
                    return null;
                })
                .filter(result -> result != null)
                .flatMapIterable(Map::values)
                .flatMapIterable(Map::values)
                .map(item -> {
                    int type;
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
                            type = -1;
                    }
                    Post.PostBuilder postBuilder = new Post.PostBuilder(type);
                    if (!SettingsHelper.boolCategoryPietsmietVideos && type == PS_VIDEO) {
                        return postBuilder.empty();
                    } else if (!SettingsHelper.boolCategoryPietsmietUploadplan && type == UPLOADPLAN) {
                        return postBuilder.empty();
                    } else if (!SettingsHelper.boolCategoryPietsmietNews && type == NEWS) {
                        return postBuilder.empty();
                    } else if (!SettingsHelper.boolCategoryPietcast && type == PIETCAST) {
                        return postBuilder.empty();
                    }
                    postBuilder = new Post.PostBuilder(type);
                    postBuilder.title(item.title);
                    postBuilder.description(item.desc);
                    postBuilder.date(new Date(item.date));
                    postBuilder.url(item.link);
                    return postBuilder;
                });
    }

    @Override
    public Observable<Post.PostBuilder> fetchPostsSinceObservable(Date dBefore, int numPosts) {
        return parsePostsFromDb(apiInterface.getAll());

    }


    @Override
    public Observable<Post.PostBuilder> fetchPostsUntilObservable(Date dAfter, int numPosts) {
        return parsePostsFromDb(apiInterface.getAll());
    }
}
