package de.pscom.pietsmiet.util;

import android.util.Log;


/**
 * Source: https://github.com/ccrama/Slide/blob/master/app/src/main/java/me/ccrama/redditslide/util/LogUtil.java
 */
@SuppressWarnings("SameParameterValue")
public class PsLog {
    private static final int CALLING_METHOD_INDEX;

    /**
     * Get the stacktrace index of the method that called this class
     *
     * Variation of http://stackoverflow.com/a/8592871/4026792
     */
    static {
        int i = 1;
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            i++;
            if (ste.getClassName().equals(PsLog.class.getName())) {
                break;
            }
        }
        CALLING_METHOD_INDEX = i;
    }

    /**
     * Source: http://stackoverflow.com/a/24586896/4026792
     *
     * @return Log tag in format (CLASSNAME.java:LINENUMBER); which makes it clickable in logcat
     */
    private static String getTag() {
        try {
            final StackTraceElement ste = Thread.currentThread().getStackTrace()[CALLING_METHOD_INDEX];
            String name = ste.getFileName();
            int line = ste.getLineNumber();
            if (name == null && line == -1) return "Pietsmiet_App";
            return "(" + ste.getFileName() + (line != -1 && line != 0 ? ":" + ste.getLineNumber() + ")" : "" + ")");

        } catch (Exception e) {
            return "Pietsmiet_App";
        }
    }

    public static void v(String message) {
        Log.v(getTag(), message);
    }

    public static void d(String message) {
        Log.d(getTag(), message);
    }

    public static void i(String message) {
        Log.i(getTag(), message);
    }

    public static void w(String message) {
        Log.w(getTag(), message);
    }

    public static void e(String message) {
        Log.e(getTag(), message);
    }

    public static void e(Throwable tr, String message) {
        Log.e(getTag(), message, tr);
    }
}

