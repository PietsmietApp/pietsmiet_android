package de.pscom.pietsmiet.data.youtube;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.pscom.pietsmiet.domain.Post;
import de.pscom.pietsmiet.domain.repository.PostRepository;
import io.reactivex.Observable;

@Singleton
public class YoutubeRepository implements PostRepository {
    private final YoutubeDataStoreFactory youtubeDataStoreFactory;
    private final YoutubeMapper youtubeMapper;

    @Inject
    YoutubeRepository(YoutubeDataStoreFactory youtubeDataStoreFactory,
                       YoutubeMapper youtubeMapper) {
        this.youtubeDataStoreFactory = youtubeDataStoreFactory;
        this.youtubeMapper = youtubeMapper;
    }

    @Override
    public Observable<Post> getOlderPosts(Date lastPostDate, int numPosts) {
        final YoutubeDataStore youtubeDataStore = youtubeDataStoreFactory.create(false);
        return youtubeDataStore.oldPosts(lastPostDate, numPosts).map(youtubeMapper::transform);
    }

    @Override
    public Observable<Post> getNewerPosts(Date firstPostDate, int numPosts) {
        final YoutubeDataStore youtubeDataStore = youtubeDataStoreFactory.create(true);
        return youtubeDataStore.newPosts(firstPostDate, numPosts).map(youtubeMapper::transform);
    }
}
