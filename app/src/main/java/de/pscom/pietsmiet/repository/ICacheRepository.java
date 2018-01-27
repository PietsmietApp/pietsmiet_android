package de.pscom.pietsmiet.repository;

import java.util.Date;
import java.util.List;
import de.pscom.pietsmiet.generic.Post;

/**
 * Created by saibotk on 26.01.2018.
 */

interface ICacheRepository {
    /***
     * Returns all cached Posts related to the given Repository.
     * @param type  Repository to get the posts from
     * @return      The list of posts found in cache
     */
    List<Post> getCachedPosts(Class<? extends MainRepository> type);

    /***
     * Returns all cached Posts.
     * @return      The list of posts found in cache
     */
    List<Post> getCachedPosts();
    /***
     * Stores the given posts.
     * @param type       The associated Repository, the posts have been loaded from
     * @param posts     The posts to be stored
     */
    void storePosts(Class<? extends MainRepository> type, List<Post> posts);

    /***
     * Clears all cached posts.
     */
    void clear();

    /***
     * Trims the cache until the given Date, removes all newer elements.
     * @param date
     */
    void trimCache(Date date);
}
