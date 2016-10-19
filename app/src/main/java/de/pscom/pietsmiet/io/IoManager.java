package de.pscom.pietsmiet.io;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 17.10.2016.
 */

public abstract class IoManager {

    protected final Context context;

    public IoManager(Context context) {
        this.context = context;
    }

    protected abstract File getDefaultDirectory();

    private File getActualFile(String path) {
        return new File(getDefaultDirectory(), path);
    }

    /**
     * Reads all the text from a file and returns it as a String
     *
     * @param path
     * @return
     */
    public String readText(String path) {
        BufferedReader bufferedReader = null;
        try {
            if (!getActualFile(path).exists())
                return "";
            bufferedReader = new BufferedReader(new FileReader(getActualFile(path)));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null)
                sb.append(line);
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Reads all the lines from a file and returns them as a String array
     *
     * @param path
     * @return
     */
    @Nullable
    public String[] readLines(String path) {
        BufferedReader bufferedReader = null;
        try {
            if (!getActualFile(path).exists())
                return null;
            bufferedReader = new BufferedReader(new FileReader(getActualFile(path)));
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = bufferedReader.readLine()) != null)
                lines.add(line);
            return lines.toArray(new String[]{});
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Write all text to a file
     *
     * @param path    Path to store the file to
     * @param content Content of the file
     */
    public void writeText(String path, String content) {
        BufferedWriter bufferedWriter = null;
        try {
            if (!getActualFile(path).exists()) {
                //noinspection ResultOfMethodCallIgnored
                getActualFile(path).createNewFile();
            }
            bufferedWriter = new BufferedWriter(new FileWriter(getActualFile(path)));
            bufferedWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Writes all lines to a file
     *
     * @param path
     * @param content
     */
    public void writeLines(String path, String[] content) {
        writeText(path, TextUtils.join(System.getProperty("line.separator"), content));
    }

    /**
     * appends all text to a file
     *
     * @param path
     * @param content
     */
    public void appendText(String path, String content) {
        BufferedWriter bufferedWriter = null;
        try {
            if (!getActualFile(path).exists()) {
                //noinspection ResultOfMethodCallIgnored
                getActualFile(path).createNewFile();
            }
            bufferedWriter = new BufferedWriter(new FileWriter(getActualFile(path), true));
            bufferedWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
