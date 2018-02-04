package de.pscom.pietsmiet.data.twitter;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.pscom.pietsmiet.domain.Post;
import de.pscom.pietsmiet.domain.repository.PostRepository;
import io.reactivex.Observable;

@Singleton
public class TwitterRepository implements PostRepository {
    private final TwitterDataStoreFactory twitterDataStoreFactory;
    private final TwitterMapper twitterMapper;

    @Inject
    TwitterRepository(TwitterDataStoreFactory twitterDataStoreFactory,
                       TwitterMapper twitterMapper) {
        this.twitterDataStoreFactory = twitterDataStoreFactory;
        this.twitterMapper = twitterMapper;
    }

    @Override
    public Observable<Post> getOlderPosts(Date lastPostDate, int numPosts) {
        final TwitterDataStore twitterDataStore = twitterDataStoreFactory.create(false);
        return twitterDataStore.oldPosts(lastPostDate, numPosts).map(twitterMapper::transform);
    }

    @Override
    public Observable<Post> getNewerPosts(Date firstPostDate, int numPosts) {
        final TwitterDataStore twitterDataStore = twitterDataStoreFactory.create(true);
        return twitterDataStore.newPosts(firstPostDate, numPosts).map(twitterMapper::transform);
    }
}
