package de.pscom.pietsmiet.data.facebook;


import org.json.JSONObject;

import java.util.Date;

import io.reactivex.Observable;


interface FacebookDataStore {
    Observable<JSONObject> newPosts(Date firstPostDate, int numPosts);

    Observable<JSONObject> oldPosts(Date lastPostDate, int numPosts);
}
