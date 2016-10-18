package de.pscom.pietsmiet.io.caching;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.io.Managers;
import de.pscom.pietsmiet.util.PsLog;

/**
 * Created by User on 17.10.2016.
 */

public class PostCache {

    private static String lineSeparator = "\r\n";
    private static int postId = 0;

    /**
     * Loads all posts from the cache (/data/data/de.pscom.pietsmiet/cache/posts.txt)
     *
     * @return the loaded posts
     */
    public static List<Post> getPosts() {
        List<Post> posts = new ArrayList<>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        //Highly dangerous
        //Do not try this at home
        //Crashes on malformed but not empty files
        String[] lines = Managers.getCacheManager().readLines("posts.txt");

        for (int i = 0; i < (lines != null ? lines.length : 0); i++) {
            try {
                switch (lines[i]) {
                    case "video":
                        posts.add(new Post(normalize(lines[++i]), normalize(lines[++i]), simpleDateFormat.parse(normalize(lines[++i])), drawableFromFile(normalize(lines[++i])), Integer.parseInt(normalize(lines[++i])), Integer.parseInt(normalize(lines[++i]))));
                        i++;
                        break;
                    case "thumbnail":
                        posts.add(new Post(normalize(lines[++i]), normalize(lines[++i]), simpleDateFormat.parse(normalize(lines[++i])), drawableFromFile(normalize(lines[++i])), Integer.parseInt(normalize(lines[++i]))));
                        i++;
                        break;
                    default:
                        posts.add(new Post(normalize(lines[++i]), normalize(lines[++i]), simpleDateFormat.parse(normalize(lines[++i])), Integer.parseInt(normalize(lines[++i]))));
                        ++i;
                        break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return posts;
    }

    /**
     * Saves all the posts to the cache (/data/data/de.pscom.pietsmiet/cache/posts.txt)
     *
     * @param posts The posts to cache
     */
    public static void setPosts(List<Post> posts) {
        StringBuilder stringBuilder = new StringBuilder();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);

        for (Post post : posts) {
            stringBuilder.append(post.isVideoView() ? "video" : post.getThumbnail() != null ? "thumbnail" : "post").append(lineSeparator);
            stringBuilder.append(unionize(post.getTitle())).append(lineSeparator);
            stringBuilder.append(unionize(post.getDescription())).append(lineSeparator);
            stringBuilder.append(unionize(simpleDateFormat.format(post.getDate()))).append(lineSeparator);

            if (post.getThumbnail() != null) {
                stringBuilder.append(unionize(drawableToFile(post.getThumbnail()))).append(lineSeparator);
            }
            if (post.isVideoView()) {
                stringBuilder.append(unionize(String.valueOf(post.getDuration()))).append(lineSeparator);
            }

            stringBuilder.append(unionize(Integer.toString(post.getPostType()))).append(lineSeparator);

            stringBuilder.append(lineSeparator);
        }
        try {
            Managers.getCacheManager().writeText("posts.txt", stringBuilder.toString());
        } catch (Exception e) {
            PsLog.w("Couldn't write file: " + e.getMessage());
        }
    }

    /**
     * Forces all text on a single line
     *
     * @param input Text to put on a line
     * @return Text on a single ine
     */
    private static String unionize(String input) {
        return input == null ? null : input.replace("&", "&amp;").replace("\r", "&cr;").replace("\n", "&lf;");
    }

    /**
     * Split text on multiple lines
     *
     * @param input Text to split
     * @return Splitted text
     */
    private static String normalize(String input) {
        return input == null ? null : input.replace("&lf;", "\n").replace("&cr;", "\r").replace("&amp;", "&");
    }

    private static Drawable drawableFromFile(String name) {
        if (!name.startsWith("cache://"))
            return null;
        String id = name.substring("cache://".length());

        //Bitmap bitmap = BitmapFactory.decodeFile(new File(Managers.getCacheManager().getDefaultDirectory(), id + ".png").getAbsolutePath());

        return BitmapDrawable.createFromPath(new File(Managers.getCacheManager().getDefaultDirectory(), id + ".png").getAbsolutePath());
    }

    private static String drawableToFile(Drawable drawable) {
        String id = String.valueOf(postId++);

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(Managers.getCacheManager().getDefaultDirectory(), id + ".png"));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "cache://" + id;
    }

}
