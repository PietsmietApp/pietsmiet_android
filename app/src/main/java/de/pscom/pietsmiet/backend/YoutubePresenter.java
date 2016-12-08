package de.pscom.pietsmiet.backend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.VIDEO;

public class YoutubePresenter extends MainPresenter {

    static final int MAX_COUNT = 10;
    private static final String urlYTAPI = "https://www.googleapis.com/youtube/v3/";

    public YoutubePresenter(MainActivity view) {
        super(view);
        //todo sinnvoll?
        if (SecretConstants.youtubeAPIkey == null || SecretConstants.youtubeAPIkey == null) {
            PsLog.w("No Youtube API-key or token specified");
            return;
        }

    }

    @Override
    public void fetchNewPosts(Date dAfter) {
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlYTAPI)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        YoutubeApiInterface apiService = retrofit.create(YoutubeApiInterface.class);
        String dateFormatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.GERMANY).format(dAfter);
        Observable<YoutubeRoot> call = apiService.getPlaylistAfterDate(50, SecretConstants.youtubeAPIkey, "UCqwGaUvq_l0RKszeHhZ5leA", dateFormatted);


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
                        postBuilder.description(snippet.getDescription());
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
                }, ()->view.getPostManager().onReadyFetch(posts));
    }

    @Override
    public void fetchPostsBefore(Date dBefore, int numPosts) {
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlYTAPI)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        String dateFormatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.GERMANY).format(dBefore);
        YoutubeApiInterface apiService = retrofit.create(YoutubeApiInterface.class);
        Observable<YoutubeRoot> call = apiService.getPlaylistBeforeDate(numPosts, SecretConstants.youtubeAPIkey, "UCqwGaUvq_l0RKszeHhZ5leA", dateFormatted);


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
                        postBuilder.description(snippet.getDescription());
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
                }, ()->view.getPostManager().onReadyFetch(posts));
    }

}
