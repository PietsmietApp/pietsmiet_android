package de.pscom.pietsmiet.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CardTypes {
    public static final int TYPE_VIDEO = 0x1;
    public static final int TYPE_STREAM = 0x2;
    public static final int TYPE_PIETCAST = 0x4;
    public static final int TYPE_TWITTER = 0x8;
    public static final int TYPE_FACEBOOK = 0x10;
    public static final int TYPE_UPLOAD_PLAN = 0x20;

    public static final int TYPE_IS_VIDEO = TYPE_VIDEO | TYPE_STREAM | TYPE_PIETCAST;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_FACEBOOK, TYPE_TWITTER, TYPE_UPLOAD_PLAN})
    public @interface ItemTypeNoThumbnail {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_VIDEO, TYPE_FACEBOOK, TYPE_TWITTER, TYPE_PIETCAST, TYPE_STREAM})
    public @interface ItemTypeThumbnail {
    }
}