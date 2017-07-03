package de.pscom.pietsmiet.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Source: https://github.com/nickescobedo/Android-Shared-Preferences-Helper/blob/master/SharedPreferenceHelper.java
 */
@SuppressWarnings("SameParameterValue")
public class SharedPreferenceHelper {
    public static final String KEY_APP_FIRST_RUN = "KEY_APP_FIRST_RUN";

    public static final String KEY_NOTIFY_UPLOADPLAN_SETTING = "KEY_NOTIFY_UPLOADPLAN_SETTING";
    public static final String KEY_NOTIFY_VIDEO_SETTING = "KEY_NOTIFY_VIDEO_SETTING";
    public static final String KEY_NOTIFY_NEWS_SETTING = "KEY_NOTIFY_NEWS_SETTING";
    public static final String KEY_NOTIFY_PIETCAST_SETTING = "KEY_NOTIFY_PIETCAST_SETTING";
    public static final String KEY_TWITTER_BEARER = "KEY_TWITTER_BEARER";
    public static final String KEY_QUALITY_IMAGE_LOAD_HD_SETTING = "KEY_QUALITY_IMAGE_LOAD_HD_SETTING";

    public static final String KEY_CATEGORY_YOUTUBE_VIDEOS = "KEY_CATEGORY_YOUTUBE_VIDEOS";
    public static final String KEY_CATEGORY_PIETSMIET_VIDEOS = "KEY_CATEGORY_PIETSMIET_VIDEOS";
    public static final String KEY_CATEGORY_PIETSMIET_NEWS = "KEY_CATEGORY_PIETSMIET_NEWS";
    public static final String KEY_CATEGORY_PIETSMIET_UPLOADPLAN = "KEY_CATEGORY_PIETSMIET_UPLOADPLAN";
    public static final String KEY_CATEGORY_PIETCAST = "KEY_CATEGORY_PIETCAST";
    public static final String KEY_CATEGORY_TWITTER = "KEY_CATEGORY_TWITTER";
    public static final String KEY_CATEGORY_FACEBOOK = "KEY_CATEGORY_FACEBOOK";

    private final static String PREF_FILE = "PREF";

    /**
     * Set a integer shared preference
     * @param key - Key to set shared preference
     * @param value - Value for the key
     */
    public static void setSharedPreferenceInt(Context context, String key, int value){
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
        FirebaseAnalytics.getInstance(context).setUserProperty(((key.length() > 24) ? key.substring(0, 24) : key), value + "");
    }

    /**
     * Set a string shared preference
     *
     * @param key   - Key to set shared preference
     * @param value - Value for the key
     */
    public static void setSharedPreferenceString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Set a long shared preference
     *
     * @param key   - Key to set shared preference
     * @param value - Value for the key
     */
    public static void setSharedPreferenceLong(Context context, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * Set a Boolean shared preference
     *
     * @param key   - Key to set shared preference
     * @param value - Value for the key
     */
    public static void setSharedPreferenceBoolean(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
        FirebaseAnalytics.getInstance(context).setUserProperty(((key.length() > 24) ? key.substring(0, 24) : key), value ? "true" : "false");
    }

    /**
     * Get a integer shared preference
     * @param key - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    public static int getSharedPreferenceInt(Context context, String key, int defValue){
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        return settings.getInt(key, defValue);
    }

    /**
     * Get a string shared preference
     *
     * @param key      - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    public static String getSharedPreferenceString(Context context, String key, String defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        return settings.getString(key, defValue);
    }

    /**
     * Get a long shared preference
     *
     * @param key      - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    public static long getSharedPreferenceLong(Context context, String key, long defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        return settings.getLong(key, defValue);
    }

    /**
     * Get a boolean shared preference
     *
     * @param key      - Key to look up in shared preferences.
     * @param defValue - Default value to be returned if shared preference isn't found.
     * @return value - String containing value of the shared preference if found.
     */
    public static boolean getSharedPreferenceBoolean(Context context, String key, boolean defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        return settings.getBoolean(key, defValue);
    }
}

