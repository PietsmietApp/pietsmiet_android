package de.pscom.pietsmiet.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.pscom.pietsmiet.generic.Post;

/**
 * Created by saibotk on 26.01.2018
 */

class CacheRepository implements ICacheRepository {
    private HashMap<Class<? extends MainRepository>, List<Post>> cache;

    /***
     * Instanciates a CacheRepository instance.
     */
    CacheRepository() {
        cache = new HashMap<>();
    }

    /***
     * Returns all cached Posts related to the given Repository.
     * @param type  Class of the Repository to get the posts from
     * @return      The list of posts found in cache
     */
    @Override
    public List<Post> getCachedPosts(Class<? extends MainRepository> type) {
        if(cache.containsKey(type)) {
            return cache.get(type);
        }
        return Collections.emptyList();
    }

    /***
     * Returns all cached Posts.
     * @return The list of posts found in cache
     */
    @Override
    public List<Post> getCachedPosts() {
        List<Post> ltmp = new ArrayList<>();
        for (List<Post> posts : cache.values()) {
            ltmp.addAll(posts);
        }
        return ltmp;
    }

    /***
     * Stores the given posts.
     * @param posts     The posts to be stored
     *
     */
    @Override
    public void storePosts(Class<? extends MainRepository> type, List<Post> posts) {
        if(cache.containsKey(type)) {
            cache.get(type).addAll(posts);
        } else {
            cache.put(type, posts);
        }
    }

    /***
     * Clears all cached posts.
     */
    @Override
    public void clear() {
        cache.clear();
    }

    private List<Post> filterDate(List<Post> posts, Long time) {
        List<Post> tmp = new ArrayList<>();
        for (Post post : posts) {
            if(post.getDate().getTime() < time) {
                tmp.add(post);
            }
        }
        return tmp;
    }

    /***
     * Trims the cache until the given Date, removes all newer elements.

     * @param date
     */
    @Override
    public void trimCache(Date date) {
        for (Class<? extends MainRepository> aClass : cache.keySet()) {
            cache.put(aClass, filterDate(cache.get(aClass), date.getTime()));
        }

    }
}
