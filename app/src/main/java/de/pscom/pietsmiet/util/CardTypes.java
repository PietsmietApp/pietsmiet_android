package de.pscom.pietsmiet.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CardTypes {
    public static final int TYPE_VIDEO = 0;
    public static final int TYPE_STREAM = 1;
    public static final int TYPE_PIETCAST = 2;
    public static final int TYPE_TWITTER = 3;
    public static final int TYPE_FACEBOOK = 4;
    public static final int TYPE_UPLOAD_PLAN = 5;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_FACEBOOK, TYPE_TWITTER, TYPE_UPLOAD_PLAN})
    public @interface ItemTypeNoThumbnail {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_VIDEO, TYPE_FACEBOOK, TYPE_TWITTER, TYPE_PIETCAST, TYPE_STREAM})
    public @interface ItemTypeThumbnail {
    }
}