package de.pscom.pietsmiet.backend;

import java.util.Date;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.model.FirebaseApiInterface;
import de.pscom.pietsmiet.util.PostType;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

public class UploadplanPresenter extends MainPresenter {
    private static final String FIREBASE_URL = "https://pietsmiet-de5ff.firebaseio.com";
    private final FirebaseApiInterface apiInterface;

    public UploadplanPresenter(MainActivity view) {
        super(view);
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FIREBASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        apiInterface = retrofit.create(FirebaseApiInterface.class);
    }

    private Observable<Post.PostBuilder> parseUploadplanFromDb() {
        Observable<Post.PostBuilder> obs = Observable.defer(() -> apiInterface.getScope("uploadplan"))
                .map(item -> {
                    postBuilder = new Post.PostBuilder(PostType.UPLOADPLAN);
                    postBuilder.title(item.getTitle());
                    postBuilder.description(item.getDesc());
                    postBuilder.date(new Date(item.getDate()));
                    postBuilder.url(item.getLink());
                    return postBuilder;
                });
        return obs;
    }

    @Override
    public Observable<Post.PostBuilder> fetchPostsSinceObservable(Date dBefore) {
        return parseUploadplanFromDb();

    }


    @Override
    public Observable<Post.PostBuilder> fetchPostsUntilObservable(Date dAfter, int numPosts) {
        return parseUploadplanFromDb();
    }
}
