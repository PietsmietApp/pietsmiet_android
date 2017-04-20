package de.pscom.pietsmiet.util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import rx.Observable;
import rx.schedulers.Schedulers;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION_NUMBER = 3;
    private static final String DATABASE_NAME = "PietSmiet.db";
    private static final String TABLE_POSTS = "posts";
    private static final String POSTS_COLUMN_ID = "id";
    private static final String POSTS_COLUMN_API_ID = "api_id";
    private static final String POSTS_COLUMN_TITLE = "title";
    private static final String POSTS_COLUMN_DESC = "desc";
    private static final String POSTS_COLUMN_URL = "url";
    private static final String POSTS_COLUMN_TYPE = "type";
    private static final String POSTS_COLUMN_TIME = "time";
    private static final String POSTS_COLUMN_DURATION = "duration";
    private static final String POSTS_COLUMN_HAS_THUMBNAIL = "thumbnail";

    // MAX_AGE_DAYS defines the maxium age of the stored posts, before it gets cleared
    private static final int MAX_AGE_DAYS = 5;

    private final Context mContext;

    @SuppressLint("SimpleDateFormat")
    //private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /** Initializes a new DatabaseHelper object
     *  @param context Context reference to access PostManager etc
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
        mContext = context;
    }

    /**
     * Creates the default table
     * @param db SQLiteDatabase link
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_POSTS + " (" +
                        POSTS_COLUMN_ID + " INTEGER PRIMARY KEY," +
                        POSTS_COLUMN_API_ID + " INTEGER," +
                        POSTS_COLUMN_TITLE + " TINY_TEXT, " +
                        POSTS_COLUMN_DESC + " TEXT," +
                        POSTS_COLUMN_URL + " TEXT," +
                        POSTS_COLUMN_TYPE + " INT," +
                        POSTS_COLUMN_TIME + " INT," +
                        POSTS_COLUMN_DURATION + " INT," +
                        POSTS_COLUMN_HAS_THUMBNAIL + " TEXT)"
        );
    }

    /**
     *  Called if the database was updated eg from an older version / after an update.
     *  Resets the database.
     * @param db SQLiteDatabase current database link
     * @param i int
     * @param i1 int
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        onCreate(db);
    }

    /**
     *  Deletes the table TABLE_POSTS.
     */
    private void deleteTable() {
        getWritableDatabase().delete(TABLE_POSTS, null, null);
    }

    /**
     * Adds posts to the database (and stores their thumbnails). This is done asynchronous
     *
     * @param posts Posts to store
     */
    @SuppressWarnings("WeakerAccess")
    public void insertPosts(List<Post> posts) {
        deleteTable();
        SQLiteDatabase db = getWritableDatabase();
        Observable.just(posts)
                .flatMap(Observable::from)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(post -> {
                    if (post.hasThumbnail()) {
                        DrawableFetcher.saveDrawableToFile(post.getThumbnail(), mContext, Integer.toString(post.hashCode()));
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(POSTS_COLUMN_ID, post.hashCode());
                    contentValues.put(POSTS_COLUMN_API_ID, post.getId());
                    contentValues.put(POSTS_COLUMN_TITLE, post.getTitle());
                    contentValues.put(POSTS_COLUMN_DESC, post.getDescription());
                    contentValues.put(POSTS_COLUMN_URL, post.getUrl());
                    contentValues.put(POSTS_COLUMN_TYPE, post.getPostType());
                    contentValues.put(POSTS_COLUMN_TIME, post.getDate().getTime());
                    contentValues.put(POSTS_COLUMN_DURATION, post.getDuration());
                    contentValues.put(POSTS_COLUMN_HAS_THUMBNAIL, post.hasThumbnail());
                    db.replace(TABLE_POSTS, null, contentValues);
                }, (throwable) -> {
                    throwable.printStackTrace();
                    db.close();
                }, () -> {
                    PsLog.v("Stored " + getPostsInDbCount() + " posts in db");
                    db.close();
                });
    }

    /**
     *  Returns the amount of posts currently stored in the database.
     * @return int Count posts stored in the database.
     */
    private int getPostsInDbCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long cnt = DatabaseUtils.queryNumEntries(db, TABLE_POSTS);
        db.close();
        return (int) Math.max(Math.min(Integer.MAX_VALUE, cnt), Integer.MIN_VALUE);
    }


    /**
     *  Clears the entire Database.
     */
    public void clearDB() {
        deleteTable();
        this.close();
    }

    /**
     * Loads all post objects from the database and displays it
     * Clears the database if it's too old
     *
     * @param context For loading the drawable & displaying the post after finished loading
     */
    @SuppressWarnings("WeakerAccess")
    public void displayPostsFromCache(MainActivity context) {
        if (context == null) {
            return;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        // Don't retrieve posts older than two days
        long time = new Date(System.currentTimeMillis() - (MAX_AGE_DAYS * DAY_IN_MS)).getTime();
        PsLog.v("LOADING CACHE STARTED...");
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_POSTS + " " +
                "WHERE " + POSTS_COLUMN_TIME + " > " + time + " " +
                "ORDER BY " + POSTS_COLUMN_TIME + " DESC ", null);
        PostManager pm = context.postManager;
        Observable.just(res)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .onBackpressureBuffer()
                .filter(Cursor::moveToFirst)
                .map(cursor -> {
                    List<Post> toReturn = new ArrayList<>();
                    try {
                        do {
                            int old_hashcode = cursor.getInt(cursor.getColumnIndex(POSTS_COLUMN_ID));

                            Post.PostBuilder postBuilder = new Post.PostBuilder(cursor.getInt(cursor.getColumnIndex(POSTS_COLUMN_TYPE)))
                                    .title(cursor.getString(cursor.getColumnIndex(POSTS_COLUMN_TITLE)))
                                    .id(cursor.getLong(cursor.getColumnIndex(POSTS_COLUMN_API_ID)))
                                    .description(cursor.getString(cursor.getColumnIndex(POSTS_COLUMN_DESC)))
                                    .url(cursor.getString(cursor.getColumnIndex(POSTS_COLUMN_URL)))
                                    .duration(cursor.getInt(cursor.getColumnIndex(POSTS_COLUMN_DURATION)))
                                    .date(new Date(cursor.getLong(cursor.getColumnIndex(POSTS_COLUMN_TIME))));
                            if (cursor.getInt(cursor.getColumnIndex(POSTS_COLUMN_HAS_THUMBNAIL)) == 1) {
                                postBuilder.thumbnailUrl(DrawableFetcher.LOAD_FROM_CACHE);
                            }
                            Post post = postBuilder.build();

                            if (post != null && post.hashCode() == old_hashcode) {
                                toReturn.add(postBuilder.build());
                            } else {
                                PsLog.w("Post in db has a different hashcode than before or Post is null, not using it");
                            }
                        } while (cursor.moveToNext());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        cursor.close();
                        db.close();
                    }
                    return toReturn;

                })
                .subscribe(items -> {
                    this.close();
                    PsLog.v("Loaded " + items.size() + " posts from DB");
                    pm.addPosts(items);
                }, e -> {
                    this.close();
                    PsLog.e("Could not load posts from DB: ", e);
                    pm.fetchNewPosts();
                }, pm::fetchNewPosts);
    }

}
