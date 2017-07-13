package de.pscom.pietsmiet.stetho;
import android.content.Context;

import okhttp3.OkHttpClient;

public class StethoHelper {
    public static void init(Context context) {
        // Noop
    }

    public static OkHttpClient.Builder configureInterceptor(OkHttpClient.Builder httpClient) {
        return httpClient;
    }
}