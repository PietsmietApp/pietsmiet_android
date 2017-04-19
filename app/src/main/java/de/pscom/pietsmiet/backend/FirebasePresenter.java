package de.pscom.pietsmiet.backend;

import java.util.Date;
import java.util.Map;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.model.FirebaseApiInterface;
import de.pscom.pietsmiet.model.FirebaseItem;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.NEWS;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;
import static de.pscom.pietsmiet.util.PostType.VIDEO;

public class FirebasePresenter extends MainPresenter {
    private static final String FIREBASE_URL = "https://pietsmiet-de5ff.firebaseio.com";
    private final FirebaseApiInterface apiInterface;

    public FirebasePresenter(MainActivity view) {
        super(view);
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FIREBASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        apiInterface = retrofit.create(FirebaseApiInterface.class);
    }

    private Observable<Post.PostBuilder> parsePostsFromDb(Observable<Map<String, Map<String, FirebaseItem>>> obs) {
        return Observable.defer(() -> obs)
                .filter(result -> result != null)
                .flatMapIterable(Map::values)
                .flatMapIterable(Map::values)
                .map(item -> {
                    int type;
                    switch (item.scope) {
                        case "uploadplan":
                            type = UPLOADPLAN;
                            break;
                        case "news":
                            type = NEWS;
                            break;
                        case "pietcast":
                            type = PIETCAST;
                            break;
                        case "video":
                            type = VIDEO;
                            break;
                        default:
                            type = -1;
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
    public Observable<Post.PostBuilder> fetchPostsSinceObservable(Date dBefore) {
        return parsePostsFromDb(apiInterface.getAll());

    }


    @Override
    public Observable<Post.PostBuilder> fetchPostsUntilObservable(Date dAfter, int numPosts) {
        return parsePostsFromDb(apiInterface.getAll());
    }
}
