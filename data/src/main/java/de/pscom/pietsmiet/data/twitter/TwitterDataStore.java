package de.pscom.pietsmiet.data.twitter;

import java.util.Date;

import de.pscom.pietsmiet.data.twitter.model.TwitterStatus;
import io.reactivex.Observable;

interface TwitterDataStore {
    Observable<TwitterStatus> newPosts(Date firstPostDate, int numPosts);
    Observable<TwitterStatus> oldPosts(Date lastPostDate, int numPosts);
}
