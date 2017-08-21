package de.pscom.pietsmiet.util;

import android.content.Context;

import java.io.File;

// Source: http://stackoverflow.com/questions/6898090/how-to-clear-cache-android/6898278#6898278
public class CacheUtil {
    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            PsLog.e("CLEARING CACHE FAILED: " + e.getMessage());
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir != null && dir.delete();
    }
}
