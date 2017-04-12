package de.pscom.pietsmiet.backend;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.model.YoutubeApiInterface;
import de.pscom.pietsmiet.model.YoutubeItem;
import de.pscom.pietsmiet.model.YoutubeRoot;
import de.pscom.pietsmiet.util.DrawableFetcher;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SecretConstants;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.VIDEO;

public class YoutubePresenter extends MainPresenter {
    private static final String urlYTAPI = "https://www.googleapis.com/youtube/v3/";

    private final YoutubeApiInterface apiInterface;

    public YoutubePresenter(MainActivity view) {
        super(view);

        if (SecretConstants.youtubeAPIkey == null || SecretConstants.youtubeAPIkey == null) {
            PsLog.w("No Youtube API-key or token specified");
        }

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlYTAPI)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        apiInterface = retrofit.create(YoutubeApiInterface.class);
    }

    @Override
    public void fetchPostsSince(Date dSince) {

        String dateFormatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.GERMANY).format(dSince);
        Observable<YoutubeRoot> callObs = apiInterface.getPlaylistSinceDate(50, SecretConstants.youtubeAPIkey, "UCqwGaUvq_l0RKszeHhZ5leA", dateFormatted);

        fetchData(callObs);
    }

    @Override
    public void fetchPostsUntil(Date dUntil, int numPosts) {
        String dateFormatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.GERMANY).format(dUntil);
        Observable<YoutubeRoot> callObs = apiInterface.getPlaylistUntilDate(numPosts, SecretConstants.youtubeAPIkey, "UCqwGaUvq_l0RKszeHhZ5leA", dateFormatted);

        fetchData(callObs);
    }


    private void fetchData(Observable<YoutubeRoot> call) {
        call.subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .flatMapIterable(YoutubeRoot::getItems)
                .filter(result -> result != null)
                .doOnNext(item -> {
                    this.postBuilder = new Post.PostBuilder(VIDEO);
                    String videoID = item.getId().getVideoId();
                    if (videoID != null && !videoID.isEmpty()) {
                        postBuilder.url("http://www.youtube.com/watch?v=" + videoID);
                    }
                })
                .map(YoutubeItem::getSnippet)
                .subscribe(snippet -> {
                    try {
                        postBuilder.thumbnail(DrawableFetcher.getDrawableFromUrl(snippet.getThumbnails().getDefault().getUrl()));
                        postBuilder.title(snippet.getTitle());
                        postBuilder.description("");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.GERMANY);
                        postBuilder.date(dateFormat.parse(snippet.getPublishedAt()));
                        posts.add(postBuilder.build());
                    } catch (Exception e) {
                        PsLog.e(e.getMessage());
                        view.showError("YouTube parsing error");
                    }
                }, e -> {
                    PsLog.e(e.toString());
                    view.showError("YouTube parsing error");
                    view.getPostManager().onReadyFetch(posts, VIDEO);
                }, () -> view.getPostManager().onReadyFetch(posts, VIDEO));
    }

}
