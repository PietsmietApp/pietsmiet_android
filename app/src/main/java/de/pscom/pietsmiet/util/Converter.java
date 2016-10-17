package de.pscom.pietsmiet.util;

import static de.pscom.pietsmiet.util.CardTypes.FACEBOOK;
import static de.pscom.pietsmiet.util.CardTypes.PIETCAST;
import static de.pscom.pietsmiet.util.CardTypes.STREAM;
import static de.pscom.pietsmiet.util.CardTypes.TWITTER;
import static de.pscom.pietsmiet.util.CardTypes.UPLOAD_PLAN;
import static de.pscom.pietsmiet.util.CardTypes.VIDEO;

/**
 * Created by User on 17.10.2016.
 */

/**
 * Convers stuff
 */
public class Converter {

    /**
     * Converts a generic String type to a int card type
     *
     * @param type
     * @return
     */
    public static int convertType(String type) {
        switch (type) {
            case "twitter":
                return TWITTER;
            case "facebook":
                return FACEBOOK;
            case "video":
                return VIDEO;
            case "stream":
                return STREAM;
            case "pietcast":
                return PIETCAST;
            case "uploadplan":
                return UPLOAD_PLAN;
        }
        return -1;
    }

    /**
     * Converts a int card type to a generic String type
     *
     * @param type
     * @return
     */
    public static String convertType(int type) {
        switch (type) {
            case TWITTER:
                return "twitter";
            case FACEBOOK:
                return "facebook";
            case VIDEO:
                return "video";
            case STREAM:
                return "stream";
            case PIETCAST:
                return "pietcast";
            case UPLOAD_PLAN:
                return "uploadplan";
        }
        return "";
    }

}
