package de.pscom.pietsmiet.data.youtube;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.pscom.pietsmiet.data.youtube.model.YoutubeEntity;
import de.pscom.pietsmiet.data.youtube.model.YoutubeItem;
import io.reactivex.Observable;


class YoutubeCloudDataStore implements YoutubeDataStore {
    private static final String CHANNEL_ID_PIETSMIET = "UCqwGaUvq_l0RKszeHhZ5leA";
    private static final String YOUTUBE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private final YoutubeApiInterface apiInterface;

    YoutubeCloudDataStore(YoutubeApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    @Override
    public Observable<YoutubeItem> newPosts(Date firstPostDate, int numPosts) {
        return apiInterface.getPlaylistSinceDate(numPosts, "todo" /*fixme*/, CHANNEL_ID_PIETSMIET, getFormattedDateForApi(firstPostDate))
                .flatMapIterable(YoutubeEntity::getItems);
    }

    @Override
    public Observable<YoutubeItem> oldPosts(Date lastPostDate, int numPosts) {
        return apiInterface.getPlaylistUntilDate(numPosts, "todo" /*fixme*/, CHANNEL_ID_PIETSMIET, getFormattedDateForApi(lastPostDate))
                .flatMapIterable(YoutubeEntity::getItems);
    }

    private String getFormattedDateForApi(Date toFormat) {
        return new SimpleDateFormat(YOUTUBE_DATE_FORMAT, Locale.getDefault()).format(toFormat);
    }

}
