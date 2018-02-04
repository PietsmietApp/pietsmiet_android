package de.pscom.pietsmiet.data.twitch;

import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.data.twitch.model.TwitchEntity;
import io.reactivex.Observable;

interface TwitchDataStore {
    Observable<List<TwitchEntity>> newPosts(Date firstPostDate, int numPosts);
    Observable<List<TwitchEntity>> oldPosts(Date lastPostDate, int numPosts);
}
