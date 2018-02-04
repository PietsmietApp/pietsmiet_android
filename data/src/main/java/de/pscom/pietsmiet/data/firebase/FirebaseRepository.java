package de.pscom.pietsmiet.data.firebase;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.pscom.pietsmiet.domain.Post;
import de.pscom.pietsmiet.domain.repository.PostRepository;
import io.reactivex.Observable;

@Singleton
public class FirebaseRepository implements PostRepository {
    private final FirebaseDataStoreFactory firebaseDataStoreFactory;
    private final FirebaseMapper firebaseMapper;

    @Inject
    FirebaseRepository(FirebaseDataStoreFactory firebaseDataStoreFactory,
                       FirebaseMapper firebaseMapper) {
        this.firebaseDataStoreFactory = firebaseDataStoreFactory;
        this.firebaseMapper = firebaseMapper;
    }

    @Override
    public Observable<Post> getOlderPosts(Date lastPostDate, int numPosts) {
        final FirebaseDataStore firebaseDataStore = firebaseDataStoreFactory.create(false);
        return firebaseDataStore.oldPosts(lastPostDate, numPosts).map(firebaseMapper::transform);
    }

    @Override
    public Observable<Post> getNewerPosts(Date firstPostDate, int numPosts) {
        final FirebaseDataStore firebaseDataStore = firebaseDataStoreFactory.create(true);
        return firebaseDataStore.newPosts(firstPostDate).map(firebaseMapper::transform);
    }
}
