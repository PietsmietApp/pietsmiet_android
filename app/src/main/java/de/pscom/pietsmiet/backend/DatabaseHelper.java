package de.pscom.pietsmiet.backend;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.DrawableFetcher;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SharedPreferenceHelper;
import rx.Observable;
import rx.schedulers.Schedulers;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION_NUMBER = 1;

    private static final String DATABASE_NAME = "PietSmiet.db";
    private static final String TABLE_POSTS = "posts";
    private static final String POSTS_COLUMN_ID = "id";
    private static final String POSTS_COLUMN_TITLE = "title";
    private static final String POSTS_COLUMN_DESC = "desc";
    private static final String POSTS_COLUMN_URL = "url";
    private static final String POSTS_COLUMN_TYPE = "type";
    private static final String POSTS_COLUMN_TIME = "time";
    private static final String POSTS_COLUMN_DURATION = "duration";
    private static final String POSTS_COLUMN_HAS_THUMBNAIL = "thumbnail";

    private static final int MAX_ADDITIONAL_POSTS_STORED = 50;

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_POSTS + " (" +
                        POSTS_COLUMN_ID + " INTEGER PRIMARY KEY," +
                        POSTS_COLUMN_TITLE + " TINY_TEXT, " +
                        POSTS_COLUMN_DESC + " TEXT," +
                        POSTS_COLUMN_URL + " TEXT," +
                        POSTS_COLUMN_TYPE + " INT," +
                        POSTS_COLUMN_TIME + " INT," +
                        POSTS_COLUMN_DURATION + " INT," +
                        POSTS_COLUMN_HAS_THUMBNAIL + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        onCreate(db);
    }

    private void deleteTable() {
        getWritableDatabase().delete(TABLE_POSTS, null, null);
    }

    /**
     * Adds posts to the database (and stores their thumbnails). This is done asynchronous
     *
     * @param posts   Posts to store
     * @param context Context for storing thumbnails
     */
    public void insertPosts(List<Post> posts, Context context) {
        deleteTable();
        SQLiteDatabase db = getWritableDatabase();
        Observable.just(posts)
                .flatMap(Observable::from)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(post -> {
                    if (post.hasThumbnail()) {
                        DrawableFetcher.saveDrawableToFile(post.getThumbnail(), context, Integer.toString(post.hashCode()));
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(POSTS_COLUMN_ID, post.hashCode());
                    contentValues.put(POSTS_COLUMN_TITLE, post.getTitle());
                    contentValues.put(POSTS_COLUMN_DESC, post.getDescription());
                    contentValues.put(POSTS_COLUMN_URL, post.getUrl());
                    contentValues.put(POSTS_COLUMN_TYPE, post.getPostType());
                    contentValues.put(POSTS_COLUMN_TIME, dateFormat.format(post.getDate()));
                    contentValues.put(POSTS_COLUMN_DURATION, post.getDuration());
                    contentValues.put(POSTS_COLUMN_HAS_THUMBNAIL, post.hasThumbnail());
                    db.insert(TABLE_POSTS, null, contentValues);
                }, (throwable) -> {
                    throwable.printStackTrace();
                    db.close();
                }, () -> {
                    PsLog.v("Stored " + posts.size() + " posts in db");
                    db.close();
                });
    }

    private int getPostsInDbCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long cnt = DatabaseUtils.queryNumEntries(db, TABLE_POSTS);
        db.close();
        return (int) Math.max(Math.min(Integer.MAX_VALUE, cnt), Integer.MIN_VALUE);
    }

    /**
     * Loads all post objects from the database and displays it
     * Clears the database if it's too big
     *
     * @param context For loading the drawable & displaying the post after finished loading
     */
    @SuppressWarnings("WeakerAccess")
    public void displayPostsFromCache(MainActivity context) {
        List<Post> toReturn = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_POSTS, null);

        Observable.just("")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(ignored -> {
                    try {
                        res.moveToFirst();

                        while (!res.isAfterLast()) {
                            try {
                                Post post = new Post();
                                post.setTitle(res.getString(res.getColumnIndex(POSTS_COLUMN_TITLE)));
                                post.setDescription(res.getString(res.getColumnIndex(POSTS_COLUMN_DESC)));
                                post.setUrl(res.getString(res.getColumnIndex(POSTS_COLUMN_URL)));
                                post.setPostType(res.getInt(res.getColumnIndex(POSTS_COLUMN_TYPE)));
                                post.setDuration(res.getInt(res.getColumnIndex(POSTS_COLUMN_DURATION)));
                                post.setDatetime(dateFormat.parse(res.getString(res.getColumnIndex(POSTS_COLUMN_TIME))));
                                if (res.getInt(res.getColumnIndex(POSTS_COLUMN_HAS_THUMBNAIL)) == 1) {
                                    Drawable thumb = DrawableFetcher.loadDrawableFromFile(context, Integer.toString(post.hashCode()));
                                    if (thumb != null) {
                                        post.setThumbnail(thumb);
                                    }
                                }
                                toReturn.add(post);
                            } catch (ParseException e) {
                                PsLog.w("Couldn't parse date: " + e.getMessage());
                            }
                            res.moveToNext();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        res.close();
                        db.close();
                    }
                    int postsInDb = getPostsInDbCount();
                    if (postsInDb != toReturn.size()) {
                        // Reload all posts when not all posts from db not all posts are stored in db (/ db defect).
                        PsLog.w("Loading all posts this time because database was incomplete.\n" +
                                " Posts in DB: " + postsInDb +
                                ", Posts loaded from DB: " + toReturn.size());
                        SharedPreferenceHelper.shouldUseCache = false;
                        deleteTable();
                        this.close();
                    } else if (toReturn.size() < getPostsLoadedCount()) {
                        // Reload all posts when not all posts from db are loaded / not all posts are stored in db.
                        // The loaded posts from db are applied nevertheless.
                        PsLog.w("Loading all posts this time because database was incomplete.\n" +
                                " Posts in DB: " + postsInDb +
                                ", Should have loaded at least: " + getPostsLoadedCount());
                        SharedPreferenceHelper.shouldUseCache = false;
                    }
                    // Clear db when it's too big / old
                    if (postsInDb > (getPostsLoadedCount() + MAX_ADDITIONAL_POSTS_STORED)) {
                        PsLog.i("Db cleared because it was too big (" + postsInDb + " entries)\n" +
                                "Loading all posts this time.");
                        SharedPreferenceHelper.shouldUseCache = false;
                        deleteTable();
                        this.close();
                        return;
                    }
                    // Apply posts otherwise
                    if (context != null) {
                        PsLog.v("Applying " + toReturn.size() + " posts from db");
                        context.addNewPosts(toReturn);
                    } else {
                        PsLog.e("Context is null!");
                    }


                });
    }

    private int getPostsLoadedCount() {
        return TwitterPresenter.MAX_COUNT + PietcastPresenter.MAX_COUNT + FacebookPresenter.LIMIT_PER_USER * 5 + YoutubePresenter.MAX_COUNT;
    }
}
