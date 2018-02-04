package de.pscom.pietsmiet.data.firebase;

import java.util.Date;

import de.pscom.pietsmiet.data.firebase.model.FirebaseEntity;
import io.reactivex.Observable;

interface FirebaseDataStore {
    Observable<FirebaseEntity> newPosts(Date firstPostDate);
    Observable<FirebaseEntity> oldPosts(Date lastPostDate, int numPosts);
}
