package de.pscom.pietsmiet.data.facebook;


import java.util.Date;

import de.pscom.pietsmiet.data.facebook.model.FacebookData;
import io.reactivex.Observable;


interface FacebookDataStore {
    Observable<FacebookData> newPosts(Date firstPostDate, int numPosts);

    Observable<FacebookData> oldPosts(Date lastPostDate, int numPosts);
}
