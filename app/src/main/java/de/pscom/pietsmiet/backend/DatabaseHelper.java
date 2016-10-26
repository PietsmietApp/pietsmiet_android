package de.pscom.pietsmiet.backend;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.pscom.pietsmiet.generic.Post;
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
    private static final String POSTS_COLUMN_THUMBNAIL = "thumbnail";

    @SuppressLint("SimpleDateFormat")
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + POST_TABLE_NAME + " (" +
                        POSTS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        POSTS_COLUMN_TITLE + " TINY_TEXT, " +
                        POSTS_COLUMN_DESC + " TEXT," +
                        POSTS_COLUMN_TYPE + " INT," +
                        POSTS_COLUMN_TIME + " INT," +
                        POSTS_COLUMN_DURATION + " INT," +
                        POSTS_COLUMN_THUMBNAIL + " TEXT)"
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

    public void insertPosts(List<Post> posts) {
        deleteTable();
        SQLiteDatabase db = getWritableDatabase();
        Observable.just(posts)
                .flatMap(Observable::from)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(post -> {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(POSTS_COLUMN_TITLE, post.getTitle());
                    contentValues.put(POSTS_COLUMN_DESC, post.getDescription());
                    contentValues.put(POSTS_COLUMN_TYPE, post.getPostType());
                    contentValues.put(POSTS_COLUMN_TIME, dateFormat.format(post.getDate()));
                    contentValues.put(POSTS_COLUMN_DURATION, post.getDuration());
                    //contentValues.put(POSTS_COLUMN_THUMBNAIL, post.getThumbnail().toString()); //// FIXME: 25.10.2016
                    db.insert(POST_TABLE_NAME, null, contentValues);
                }, Throwable::printStackTrace, () -> PsLog.v("Stored posts in db"));
    }

    @SuppressWarnings("WeakerAccess")
    public List<Post> getPostsFromCache() {

        List<Post> toReturn = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + POST_TABLE_NAME, null);
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

                    toReturn.add(post);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                res.moveToNext();
            }
        } finally {
            res.close();
        }
        return toReturn;
    }
}
