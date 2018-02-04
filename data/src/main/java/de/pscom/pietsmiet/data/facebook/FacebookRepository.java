package de.pscom.pietsmiet.data.facebook;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.pscom.pietsmiet.domain.Post;
import de.pscom.pietsmiet.domain.repository.PostRepository;
import io.reactivex.Observable;

@Singleton
public class FacebookRepository implements PostRepository {
    private final FacebookDataStoreFactory facebookDataStoreFactory;
    private final FacebookMapper facebookMapper;

    @Inject
    FacebookRepository(FacebookDataStoreFactory facebookDataStoreFactory,
                       FacebookMapper facebookMapper) {
        this.facebookDataStoreFactory = facebookDataStoreFactory;
        this.facebookMapper = facebookMapper;
    }

    @Override
    public Observable<Post> getOlderPosts(Date lastPostDate, int numPosts) {
        final FacebookDataStore facebookDataStore = facebookDataStoreFactory.create(false);
        return facebookDataStore.oldPosts(lastPostDate, numPosts).map(facebookMapper::transform);
    }

    @Override
    public Observable<Post> getNewerPosts(Date firstPostDate, int numPosts) {
        final FacebookDataStore facebookDataStore = facebookDataStoreFactory.create(true);
        return facebookDataStore.newPosts(firstPostDate, numPosts).map(facebookMapper::transform);
    }
}
