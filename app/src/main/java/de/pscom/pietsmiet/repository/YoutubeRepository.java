package de.pscom.pietsmiet.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.json_model.youtubeApi.YoutubeApiInterface;
import de.pscom.pietsmiet.json_model.youtubeApi.YoutubeRoot;
import de.pscom.pietsmiet.json_model.youtubeApi.YoutubeSnippet;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.RetrofitHelper;
import de.pscom.pietsmiet.util.SecretConstants;
import de.pscom.pietsmiet.view.MainActivity;
import retrofit2.Retrofit;
import rx.Observable;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.generic.Post.PostType.YOUTUBE;

public class YoutubeRepository extends MainRepository {
    private static final String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/";

    YoutubeApiInterface apiInterface;

    YoutubeRepository(MainActivity view) {
        super(view);
        if (SecretConstants.youtubeAPIkey == null) {
            PsLog.w("No Youtube API-key or token specified");
            return;
        }
        Retrofit retrofit = RetrofitHelper.getRetrofit(YOUTUBE_API_URL);
        apiInterface = retrofit.create(YoutubeApiInterface.class);
    }

    @Override
    public Observable<Post.PostBuilder> fetchPostsSinceObservable(Date dSince, int numPosts) {

        String dateFormatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(dSince);
        Observable<YoutubeRoot> callObs = apiInterface.getPlaylistSinceDate(numPosts, SecretConstants.youtubeAPIkey, "UCqwGaUvq_l0RKszeHhZ5leA", dateFormatted);

        return fetchData(callObs.observeOn(Schedulers.io()));
    }

    @Override
    public Observable<Post.PostBuilder> fetchPostsUntilObservable(Date dUntil, int numPosts) {
        String dateFormatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(dUntil);
        Observable<YoutubeRoot> callObs = apiInterface.getPlaylistUntilDate(numPosts, SecretConstants.youtubeAPIkey, "UCqwGaUvq_l0RKszeHhZ5leA", dateFormatted);

        return fetchData(callObs.observeOn(Schedulers.io()));
    }


    private Observable<Post.PostBuilder> fetchData(Observable<YoutubeRoot> call) {
        return Observable.defer(() -> call)
                .onErrorReturn(err -> {
                    PsLog.e("Couldn't fetch Youtube: ", err);
                    view.showMessage(view.getString(R.string.error_youtube_loading));
                    return null;
                })
                .filter(result -> result != null)
                .flatMapIterable(YoutubeRoot::getItems)
                .filter(result -> result != null)
                .map(item -> {
                    Post.PostBuilder postBuilder = new Post.PostBuilder(YOUTUBE);
                    if (item.getId() != null) {
                        String videoID = item.getId().getVideoId();
                        if (videoID != null && !videoID.isEmpty()) {
                            postBuilder.url("http://www.youtube.com/watch?v=" + videoID);
                        }
                    }
                    YoutubeSnippet snippet = item.getSnippet();

                    if (snippet != null) {
                        postBuilder.thumbnailUrl(snippet.getThumbnails().getMedium().getUrl());
                        postBuilder.thumbnailHDUrl(snippet.getThumbnails().getMedium().getUrl());
                        postBuilder.title(snippet.getTitle());
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
                            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                            postBuilder.date(dateFormat.parse(snippet.getPublishedAt()));
                        } catch (ParseException e) {
                            // Post will be automatically filtered as it's null (when no date in postbuilder is given)
                            PsLog.w("YouTube date parsing error", e);
                            view.showMessage(view.getString(R.string.error_youtube_loading_partial));
                        }
                    }
                    return postBuilder;
                });
    }

}
