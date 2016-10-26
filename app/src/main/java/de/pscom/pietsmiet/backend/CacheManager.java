package de.pscom.pietsmiet.backend;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.util.PsLog;
import rx.Observable;
import rx.schedulers.Schedulers;

public class CacheManager {
    private MainActivity view;

    public CacheManager(MainActivity view) {
        this.view = view;
    }

    public void displayPostsFromCache() {
        Observable.defer(() -> Observable.just(new DatabaseHelper(view).getPostsFromCache()))
                .subscribeOn(Schedulers.io())
                .subscribe(list -> view.addNewPosts(list), Throwable::printStackTrace, () -> PsLog.v("Database finished loading"));
    }
}
