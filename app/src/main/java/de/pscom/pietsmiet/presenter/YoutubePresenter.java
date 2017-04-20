package de.pscom.pietsmiet.presenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.model.youtubeApi.YoutubeApiInterface;
import de.pscom.pietsmiet.model.youtubeApi.YoutubeItem;
import de.pscom.pietsmiet.model.youtubeApi.YoutubeRoot;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SecretConstants;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.YOUTUBE;

public class YoutubePresenter extends MainPresenter {
    private static final String urlYTAPI = "https://www.googleapis.com/youtube/v3/";

    YoutubeApiInterface apiInterface;

    public YoutubePresenter(MainActivity view) {
        super(view);
        if (!checkForKeys()) return;
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlYTAPI)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();

        apiInterface = retrofit.create(YoutubeApiInterface.class);
    }

    protected boolean checkForKeys() {
        if (SecretConstants.youtubeAPIkey == null) {
            PsLog.w("No Youtube API-key or token specified");
            return false;
        }
        return true;
    }

    @Override
    public Observable<Post.PostBuilder> fetchPostsSinceObservable(Date dSince) {

        String dateFormatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(dSince);
        Observable<YoutubeRoot> callObs = apiInterface.getPlaylistSinceDate(50, SecretConstants.youtubeAPIkey, "UCqwGaUvq_l0RKszeHhZ5leA", dateFormatted);

        return fetchData(callObs);
    }

    @Override
    public Observable<Post.PostBuilder> fetchPostsUntilObservable(Date dUntil, int numPosts) {
        String dateFormatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(dUntil);
        Observable<YoutubeRoot> callObs = apiInterface.getPlaylistUntilDate(numPosts, SecretConstants.youtubeAPIkey, "UCqwGaUvq_l0RKszeHhZ5leA", dateFormatted);

        return fetchData(callObs);
    }


    private Observable<Post.PostBuilder> fetchData(Observable<YoutubeRoot> call) {
        return Observable.defer(() -> call)
                .onErrorReturn(err -> {
                    PsLog.e("Couldn't fetch Youtube: ", err);
                    view.showError("Youtube konnte nicht geladen werden");
                    return null;
                })
                .filter(result -> result != null)
                .flatMapIterable(YoutubeRoot::getItems)
                .filter(result -> result != null)
                .doOnNext(item -> {
                    this.postBuilder = new Post.PostBuilder(YOUTUBE);
                    String videoID = item.getId().getVideoId();
                    if (videoID != null && !videoID.isEmpty()) {
                        postBuilder.url("http://www.youtube.com/watch?v=" + videoID);
                    }
                })
                .map(YoutubeItem::getSnippet)
                .map(snippet -> {
                    postBuilder.thumbnailUrl(snippet.getThumbnails().getMedium().getUrl());
                    postBuilder.title(snippet.getTitle());
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
                        postBuilder.date(dateFormat.parse(snippet.getPublishedAt()));
                    } catch (ParseException e) {
                        // Post will be automatically filtered as it's null (when no date in postbuilder is given)
                        PsLog.w("YouTube date parsing error", e);
                        view.showError("Einige Youtube-Posts konnte nicht geladen werden");
                    }
                    return postBuilder;
                });
    }

}
