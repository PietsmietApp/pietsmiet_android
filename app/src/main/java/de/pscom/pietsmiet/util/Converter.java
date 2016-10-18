package de.pscom.pietsmiet.util;

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
        return Types.fromName(type).getId();
    }

    /**
     * Converts a int card type to a generic String type
     *
     * @param type
     * @return
     */
    public static String convertType(int type) {
        return Types.fromId(type).getName();
    }

}
