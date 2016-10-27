package de.pscom.pietsmiet.backend;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
import rx.Observable;
import rx.schedulers.Schedulers;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION_NUMBER = 1;

    private static final String DATABASE_NAME = "PietSmiet.db";
    private static final String POST_TABLE_NAME = "posts";
    private static final String POSTS_COLUMN_ID = "id";
    private static final String POSTS_COLUMN_TITLE = "title";
    private static final String POSTS_COLUMN_DESC = "desc";
    private static final String POSTS_COLUMN_TYPE = "type";
    private static final String POSTS_COLUMN_TIME = "time";
    private static final String POSTS_COLUMN_DURATION = "duration";
    private static final String POSTS_COLUMN_HAS_THUMBNAIL = "thumbnail";

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + POST_TABLE_NAME + " (" +
                        POSTS_COLUMN_ID + " INTEGER PRIMARY KEY," +
                        POSTS_COLUMN_TITLE + " TINY_TEXT, " +
                        POSTS_COLUMN_DESC + " TEXT," +
                        POSTS_COLUMN_TYPE + " INT," +
                        POSTS_COLUMN_TIME + " INT," +
                        POSTS_COLUMN_DURATION + " INT," +
                        POSTS_COLUMN_HAS_THUMBNAIL + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + POST_TABLE_NAME);
        onCreate(db);
    }

    private void deleteTable() {
        getWritableDatabase().delete(POST_TABLE_NAME, null, null);
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
                    contentValues.put(POSTS_COLUMN_TYPE, post.getPostType());
                    contentValues.put(POSTS_COLUMN_TIME, dateFormat.format(post.getDate()));
                    contentValues.put(POSTS_COLUMN_DURATION, post.getDuration());
                    contentValues.put(POSTS_COLUMN_HAS_THUMBNAIL, post.hasThumbnail());
                    db.insert(POST_TABLE_NAME, null, contentValues);
                }, Throwable::printStackTrace, () -> {
                    PsLog.v("Stored posts in db");
                    this.close();
                });
    }

    /**
     * Loads all post objects from the database and displays it
     *
     * @param context For loading the drawable & displaying the post after finished loading
     */
    @SuppressWarnings("WeakerAccess")
    public void displayPostsFromCache(MainActivity context) {
        List<Post> toReturn = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + POST_TABLE_NAME, null);

        Observable.empty()
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
                    } finally {
                        res.close();
                        this.close();
                    }
                    if (context != null) context.addNewPosts(toReturn);
                    else PsLog.e("Context is null!");
                });
    }
}
