package de.pscom.pietsmiet.util;

/**
 * Created by User on 18.10.2016.
 */

public enum Types {

    NONE(0, "none"),
    VIDEO(NONE.getId() + 1, "video"),
    STREAM(VIDEO.getId() + 1, "stream"),
    PIETCAST(STREAM.getId() + 1, "pietCast"),
    TWITTER(PIETCAST.getId() + 1, "twitter"),
    FACEBOOK(TWITTER.getId() + 1, "facebook"),
    UPLOAD_PLAN(FACEBOOK.getId() + 1, "uploadPlan");

    private final int id;
    private final String name;

    Types(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Types fromId(int id) {
        for (Types type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return NONE;
    }

    public static Types fromName(String name) {
        for (Types type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return NONE;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
