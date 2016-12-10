package de.pscom.pietsmiet.util;

import android.content.Context;

import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NEWS_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.getSharedPreferenceBoolean;

public class SettingsHelper {
    public static boolean uploadPlanNotification;

    public static void loadAllSettings(Context context){
        uploadPlanNotification = getSharedPreferenceBoolean(context, KEY_NEWS_SETTING,true);
    }

}
