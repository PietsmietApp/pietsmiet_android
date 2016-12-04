package de.pscom.pietsmiet.backend;

import java.text.SimpleDateFormat;
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

    static final int MAX_COUNT = 10;
    private static final String urlYTAPI = "https://www.googleapis.com/youtube/v3/";

    public YoutubePresenter(MainActivity view) {
        super(view, VIDEO);
        if (SecretConstants.youtubeAPIkey == null || SecretConstants.youtubeAPIkey == null) {
            PsLog.w("No Youtube API-key or token specified");
            return;
        }
        parsePlaylist();
    }

    /**
     * Parsing upload Playlist from YT
     * Adding found Videos to posts array
     */
    private void parsePlaylist() {
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlYTAPI)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        YoutubeApiInterface apiService = retrofit.create(YoutubeApiInterface.class);
        Observable<YoutubeRoot> call = apiService.getPlaylist(MAX_COUNT, SecretConstants.youtubeAPIkey, "UUqwGaUvq_l0RKszeHhZ5leA");

        call.subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .flatMapIterable(YoutubeRoot::getItems)
                .filter(result -> result != null)
                .doOnNext(item -> {
                    this.post = new Post();
                    String videoID = item.getContentDetails().getVideoId();
                    if (videoID != null && !videoID.isEmpty()) {
                        post.setUrl("http://www.youtube.com/watch?v=" + videoID);
                    }
                })
                .map(YoutubeItem::getSnippet)
                .subscribe(snippet -> {
                    try {
                        post.setThumbnail(DrawableFetcher.getDrawableFromUrl(snippet.getThumbnails().getDefault().getUrl()));
                        post.setTitle(snippet.getTitle());
                        post.setDescription(snippet.getDescription());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.GERMANY);
                        post.setDatetime(dateFormat.parse(snippet.getPublishedAt()));
                        post.setPostType(VIDEO);
                        posts.add(post);
                    } catch (Exception e) {
                        PsLog.e(e.getMessage());
                        view.showError("YouTube parsing error");
                    }
                }, e -> {
                    PsLog.e(e.toString());
                    view.showError("YouTube parsing error");
                }, this::finished);

    }

    @Override
    public void getNewPosts() {

    }

    @Override
    public void getPostsAfter() {

    }
}
