package de.pscom.pietsmiet.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import facebook4j.Post;
import twitter4j.MediaEntity;
import twitter4j.Status;

public class DrawableFetcher {

    /**
     * @param post A facebook posting
     * @return The drawable from the post, if available
     */
    @Nullable
    public static Drawable getDrawableFromPost(@Nullable Post post) {
        if (post != null) {
            URL imageUrl = post.getPicture();
            if (imageUrl != null) {
                return getDrawableFromUrl(imageUrl.toString());
            }
        }
        return null;
    }

    /**
     * @param post A facebook posting
     * @return The full drawable from the post, if available
     */
    @Nullable
    public static Drawable getFullDrawableFromPost(@Nullable Post post) {
        if (post != null) {
            URL imageUrl = post.getFullPicture();
            if (imageUrl != null) {
                return getDrawableFromUrl(imageUrl.toString());
            }
        }
        return null;
    }

    /**
     * @param status A tweet
     * @return The drawable from the tweet, if available
     */
    @Nullable
    public static Drawable getDrawableThumbFromTweet(@Nullable Status status) {
        if (status != null) {
            MediaEntity[] mediaEntities = status.getMediaEntities();
            if (mediaEntities != null && mediaEntities.length > 0 && mediaEntities[0].getMediaURL() != null) {
                return getDrawableFromUrl(mediaEntities[0].getMediaURL() + ":thumb");
            }
        }
        return null;
    }

    /**
     * @param status A tweet
     * @return The drawable from the tweet, if available
     */
    @Nullable
    public static Drawable getDrawableFromTweet(@Nullable Status status) {
        if (status != null) {
            MediaEntity[] mediaEntities = status.getMediaEntities();
            if (mediaEntities != null && mediaEntities.length > 0 && mediaEntities[0].getMediaURL() != null) {
                return getDrawableFromUrl(mediaEntities[0].getMediaURL());
            }
        }
        return null;
    }

    /**
     * @param url Url to the image
     * @return A BitmapDrawable from the url
     */
    @Nullable
    public static Drawable getDrawableFromUrl(@NonNull String url) {
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
    public static Drawable loadDrawableFromFile(Context context, String fileName) {
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
