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

    private final Context context;

    public NetworkUtil(Context context) {
        this.context = context;
    }

    /**
     * Uses the provided context to determine the current connectivity status.
     *
     * @return A non-null value defined in {@link Status}
     */
    private Status getConnectivityStatus() {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = conMan.getActiveNetworkInfo();

        switch (activeNetwork != null ? activeNetwork.getType() : CONST_NO_NETWORK) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_ETHERNET:
                return Status.WIFI;
            case ConnectivityManager.TYPE_MOBILE:
            case ConnectivityManager.TYPE_BLUETOOTH:
            case ConnectivityManager.TYPE_WIMAX:
                return Status.MOBILE;
            default:
                return Status.NONE;
        }
    }

    /**
     * Checks if the network is connected. An application context is said to have connection if
     * {@link #getConnectivityStatus()} does not equal {@link Status#NONE}.
     *
     * @return True if the application is connected, false if else.
     */
    public boolean isConnected() {
        return getConnectivityStatus() != Status.NONE;
    }

    /**
     * Checks if the network is connected to WiFi.
     *
     * @return True if the application is connected, false if else.
     */
    public boolean isConnectedWifi() {
        return getConnectivityStatus() == Status.WIFI;
    }

    private enum Status {
        WIFI,
        MOBILE,
        NONE
    }
}
