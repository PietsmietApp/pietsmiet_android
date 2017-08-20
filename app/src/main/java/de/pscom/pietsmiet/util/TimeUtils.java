package de.pscom.pietsmiet.util;

import android.content.Context;

import java.util.Date;

import de.pscom.pietsmiet.R;

/**
 * Source: https://github.com/ccrama/Slide/blob/master/app/src/main/java/me/ccrama/redditslide/TimeUtils.java
 */
public class TimeUtils {

    public static final long SECOND_MILLIS = 1000;
    public static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final long DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeSince(Date date, Context c) {
        long time = date.getTime();

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return c.getString(R.string.time_just_now);
        }


        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return c.getString(R.string.time_just_now);
        } else if (diff < HOUR_MILLIS) {
            int minutes = longToInt(diff / MINUTE_MILLIS);
            return c.getString(R.string.time_minutes_short, minutes);
        } else if (diff < DAY_MILLIS) {
            int hours = longToInt(diff / HOUR_MILLIS);
            return c.getString(R.string.time_hours_short, hours);
        } else {
            int days = longToInt(diff / DAY_MILLIS);
            return c.getString(R.string.time_days_short, days);
        }
    }

    private static Integer longToInt(Long temp) {
        return temp.intValue();
    }
}
