package de.pscom.pietsmiet.io;

import android.content.Context;

import de.pscom.pietsmiet.io.caching.CacheManager;
import de.pscom.pietsmiet.io.files.FileManager;


public class Managers {

    private static Context context;
    private static FileManager fileManager;
    private static CacheManager cacheManager;

    public static void initialize(Context context) {
        Managers.context = context;
    }

    public static FileManager getFileManager() {
        return fileManager != null ? fileManager : context != null ? new FileManager(context) : null;
    }

    public static CacheManager getCacheManager() {
        return cacheManager != null ? cacheManager : context != null ? new CacheManager(context) : null;
    }


}
