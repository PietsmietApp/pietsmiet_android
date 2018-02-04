package de.pscom.pietsmiet.data.firebase;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.pscom.pietsmiet.data.BaseMapper;
import de.pscom.pietsmiet.data.firebase.model.FirebaseEntity;
import de.pscom.pietsmiet.domain.Post;

@Singleton
class FirebaseMapper extends BaseMapper<Post, FirebaseEntity> {
    @Inject
    FirebaseMapper() {
    }

    @Override
    public Post transform(FirebaseEntity item) {
        Post post = new Post();
        post.setTitle(item.title);
        post.setDescription(item.desc);
        post.setDate(new Date(item.date));
        post.setUrl(item.link);
        post.setThumbnailHDUrl(item.image_url);
        post.setThumbnailUrl(item.image_url);
        return post;
    }
}
