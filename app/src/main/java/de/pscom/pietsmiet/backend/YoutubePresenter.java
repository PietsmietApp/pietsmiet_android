package de.pscom.pietsmiet.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.model.YoutubeApiInterface;
import de.pscom.pietsmiet.model.YoutubeItem;
import de.pscom.pietsmiet.model.YoutubeRoot;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SecretConstants;
import de.pscom.pietsmiet.util.DrawableFetcher;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.VIDEO;

public class YoutubePresenter extends MainPresenter {

    private static final String urlYTAPI = "https://www.googleapis.com/youtube/v3/";
    static final int MAX_COUNT = 10;

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
        Observable.defer(() -> Observable.just(loadApi()))
                .subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .observeOn(Schedulers.io())
                .map(response -> response.body().getItems())
                .flatMap(Observable::from)
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

    private Response<YoutubeRoot> loadApi() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlYTAPI)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        YoutubeApiInterface apiService = retrofit.create(YoutubeApiInterface.class);
        Call<YoutubeRoot> call = apiService.getPlaylist(MAX_COUNT, SecretConstants.youtubeAPIkey, "UUqwGaUvq_l0RKszeHhZ5leA");
        Response<YoutubeRoot> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            PsLog.e(e.getMessage());
            view.showError("YouTube-API unreachable");
        }
        return response;
    }

}
