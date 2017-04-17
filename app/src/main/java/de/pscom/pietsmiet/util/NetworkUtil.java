package de.pscom.pietsmiet.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Source: https://github.com/ccrama/Slide/blob/master/app/src/main/java/me/ccrama/redditslide/util/NetworkUtil.java
 */

public class NetworkUtil {

    // Assigned a random value that is not a value of ConnectivityManager.TYPE_*
    private static final int CONST_NO_NETWORK = 534522;


    /**
     * Uses the provided context to determine the current connectivity status.
     *
     * @param context A context used to retrieve connection information from
     * @return A non-null value defined in {@link Status}
     */
    public static Status getConnectivityStatus(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = conMan.getActiveNetworkInfo();

        switch (activeNetwork != null ? activeNetwork.getType() : CONST_NO_NETWORK) {
            case ConnectivityManager.TYPE_WIFI: case ConnectivityManager.TYPE_ETHERNET:
                return Status.WIFI;
            case ConnectivityManager.TYPE_MOBILE: case ConnectivityManager.TYPE_BLUETOOTH: case ConnectivityManager.TYPE_WIMAX:
                return Status.MOBILE;
            default:
                return Status.NONE;
        }
    }

    /**
     * Checks if the network is connected. An application context is said to have connection if
     * {@link #getConnectivityStatus(Context)} does not equal {@link Status#NONE}.
     *
     * @param context The context used to retrieve connection information
     * @return True if the application is connected, false if else.
     */
    public static boolean isConnected(Context context) {
        return getConnectivityStatus(context) != Status.NONE;
    }

    /**
     * Checks if the network is connected to WiFi.
     *
     * @param context The context used to retrieve connection information
     * @return True if the application is connected, false if else.
     */
    public static boolean isConnectedWifi(Context context) {
        return getConnectivityStatus(context) == Status.WIFI;
    }

    private enum Status {
        WIFI,
        MOBILE,
        NONE
    }
}
