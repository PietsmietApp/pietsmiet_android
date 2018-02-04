package de.pscom.pietsmiet.data.youtube;

import java.util.Date;

import de.pscom.pietsmiet.data.youtube.model.YoutubeItem;
import io.reactivex.Observable;

interface YoutubeDataStore {
    Observable<YoutubeItem> newPosts(Date firstPostDate, int numPosts);
    Observable<YoutubeItem> oldPosts(Date lastPostDate, int numPosts);
}
