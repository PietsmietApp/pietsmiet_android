package de.pscom.pietsmiet.io.files;

import android.content.Context;

import java.io.File;

import de.pscom.pietsmiet.io.IoManager;

public class FileManager extends IoManager {

    public FileManager(Context context) {
        super(context);
    }

    @Override
    protected File getDefaultDirectory() {
        return context.getFilesDir();
    }
}
