package de.pscom.pietsmiet.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import twitter4j.MediaEntity;
import twitter4j.Status;

public class DrawableFetcher {
    public static final String LOAD_FROM_CACHE = "LOAD_FROM_CACHE";

    /**
     * @param post A facebook posting
     * @return The full drawable from the post, if available
     */
    public static String getThumbnailUrlFromFacebook(@Nullable JSONObject post, boolean isHD) throws JSONException {
        if (post != null) {
            if (!isHD && post.has("picture") && post.get("picture") != null) {
                return post.get("picture").toString();
            }
            if (isHD && post.has("full_picture") && post.get("full_picture") != null) {
                return post.get("full_picture").toString();
            }
        }
        return null;
    }

    /**
     * @param status A tweet
     * @return The drawable from the tweet, if available
     */
    @Nullable
    public static String getThumbnailUrlFromTweet(@Nullable Status status, boolean isHd) {
        if (status != null) {
            MediaEntity[] mediaEntities = status.getMediaEntities();
            if (mediaEntities != null && mediaEntities.length > 0 && mediaEntities[0].getMediaURL() != null) {
                return mediaEntities[0].getMediaURL() + (isHd ? "" : ":thumb");
            }
        }
        return null;
    }

    /**
     * @param url Url to the image
     * @return A BitmapDrawable from the url
     */
    @Nullable
    private static Drawable getDrawableFromUrl(@NonNull String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
            Drawable toReturn = new BitmapDrawable(Resources.getSystem(), bitmap);
            if (toReturn.getMinimumHeight() > 0 && toReturn.getMinimumWidth() > 0) {
                return toReturn;
            }
        } catch (Exception e) {
            PsLog.w("Couldn't fetch thumbnail: " + e.toString());
        }
        return null;
    }

    public static void loadThumbnailIntoView(de.pscom.pietsmiet.generic.Post post, Context c, ImageView view) {
        Single.just(post.getThumbnailUrl())
                .subscribeOn(Schedulers.io())
                .map(url -> {
                    Drawable drawable = null;
                    if (url == LOAD_FROM_CACHE) {
                        drawable = loadDrawableFromFile(c, post.hashCode() + "");
                    } else if (url != null) {
                        drawable = getDrawableFromUrl(url);
                    }
                    return drawable;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(drawable -> {
                    if (drawable != null) {
                        if (view != null) {
                            view.setVisibility(View.VISIBLE);
                            view.setImageDrawable(drawable);
                        }
                        post.setThumbnail(drawable);
                    }
                }, Throwable::printStackTrace);
    }

    /**
     * Converts a BitmapDrawable to a bitmap and stores it
     * <p>
     * Source: http://stackoverflow.com/a/673014/4026792
     *
     * @param drawable BitmapDrawable to store
     * @param context  Context for getting the dir
     * @param fileName Filename to store to
     */
    public static void saveDrawableToFile(Drawable drawable, Context context, String fileName) {
        FileOutputStream out = null;
        try {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            File path = context.getCacheDir();
            out = new FileOutputStream(path.getAbsolutePath() + fileName);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            // PNG is a lossless format, the compression factor (100) is ignored; it's just for saving
        } catch (Exception e) {
            PsLog.w("Couldn't save drawable: " + e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * Loads a Bitmap from file and convert it to BitmapDrawable
     * <p>
     * Source: http://stackoverflow.com/a/8711059/4026792
     *
     * @param context  Context for getting the directory
     * @param fileName Filename
     * @return BitmapDrawable from the file
     */
    private static Drawable loadDrawableFromFile(Context context, String fileName) {
        Bitmap bitmap;

        File path = context.getCacheDir();
        File f = new File(path.getAbsolutePath() + fileName);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            return new BitmapDrawable(context.getResources(), bitmap);
        } catch (FileNotFoundException e) {
            PsLog.i("Couldn't find thumbnail: " + f.getAbsolutePath());
        }
        return null;
    }


}
