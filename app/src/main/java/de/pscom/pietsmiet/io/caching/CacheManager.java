package de.pscom.pietsmiet.io.caching;

import android.content.Context;

import java.io.File;

import de.pscom.pietsmiet.io.IoManager;

public class CacheManager extends IoManager {

    public CacheManager(Context context) {
        super(context);
    }

    @Override
    protected File getDefaultDirectory() {
        return context.getCacheDir();
    }
}
