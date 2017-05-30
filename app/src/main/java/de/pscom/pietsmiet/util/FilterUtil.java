package de.pscom.pietsmiet.util;

import android.content.Context;

import java.util.List;

import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_VIDEO_NOTIF_BLACKLIST;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.getSharedPreferenceList;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.setSharedPreferenceList;

public abstract class FilterUtil {
    public static boolean isGameAllowed(String game, Context context) {
        if (game == null || game == "") return true;

        List<String> forbiddenWords = getSharedPreferenceList(context, KEY_VIDEO_NOTIF_BLACKLIST);
        for (String forbidden : forbiddenWords) {
            // If name of the game contains a forbidden name, it's not allowed
            if (game.toLowerCase().contains(forbidden.toLowerCase())) return false;
        }
        return true;
    }

    public static void addStringToBlacklist(String forbidden, Context context) {
        if (forbidden == null || forbidden.length() <= 1) {
            PsLog.w("Invalid forbidden game");
            return;
        }
        List<String> forbiddenWords = getSharedPreferenceList(context, KEY_VIDEO_NOTIF_BLACKLIST);
        forbiddenWords.add(forbidden);
        setSharedPreferenceList(context, KEY_VIDEO_NOTIF_BLACKLIST, forbiddenWords);
    }
}
