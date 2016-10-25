package de.pscom.pietsmiet.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.generic.Post;
import rx.Observable;


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
                .subscribe(post -> {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(POSTS_COLUMN_TITLE, post.getTitle());
                    contentValues.put(POSTS_COLUMN_DESC, post.getDescription());
                    contentValues.put(POSTS_COLUMN_TYPE, post.getPostType());
                    contentValues.put(POSTS_COLUMN_TIME, Long.valueOf(post.getDate().getTime()).intValue());
                    contentValues.put(POSTS_COLUMN_DURATION, post.getDuration());
                    //contentValues.put(POSTS_COLUMN_THUMBNAIL, post.getThumbnail().toString()); //// FIXME: 25.10.2016
                    db.insert(POST_TABLE_NAME, null, contentValues);
                });
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, POST_TABLE_NAME);
    }

    public List<Post> getPostsFromCache() {

        List<Post> toReturn = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + POST_TABLE_NAME, null);
        try {

            res.moveToFirst();

            while (!res.isAfterLast()) {
                Post post = new Post();
                post.setTitle(res.getString(res.getColumnIndex(POSTS_COLUMN_TITLE)));
                post.setDescription(res.getString(res.getColumnIndex(POSTS_COLUMN_DESC)));
                post.setPostType(res.getInt(res.getColumnIndex(POSTS_COLUMN_TYPE)));
                post.setDatetime(new Date(res.getInt(res.getColumnIndex(POSTS_COLUMN_TIME))));
                post.setDuration(res.getInt(res.getColumnIndex(POSTS_COLUMN_DURATION)));
                res.moveToNext();
                toReturn.add(post);
            }
        } finally {
            res.close();
        }
        return toReturn;
    }
}
