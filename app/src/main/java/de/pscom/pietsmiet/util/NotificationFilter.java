package de.pscom.pietsmiet.util;

/**
 * A (static) utility class to filter notifications based on e.g. the game
 */
public class NotificationFilter {

    /**
     * Don't do this!<br />
     * This is a static class.
     */
    private NotificationFilter()
    {
        throw new IllegalStateException("Don't do this!");
    }

    /**
     * Determines whether or not any of the filters apply to the given content
     * @param content The content to apply the filter to
     * @param filter The filters to check
     * @return Whether or not the filter applies to the content
     */
    public boolean contains(String content, String filter)
    {
        /*
         * .toLowerCase() because contains does not have the ability to ignore the case
         */
        content = content.toLowerCase();
        /*
         * same argument here
         */
        String[] filters = filter.toLowerCase().split(",");
        /*
         * check if any filter applied
         */
        for (String current : filters) {
            /*
             * if filter matches, return true
             */
            if(content.contains(current))
                return true;
        }
        /*
         * no filter matches, return false
         */
        return false;
    }

}
