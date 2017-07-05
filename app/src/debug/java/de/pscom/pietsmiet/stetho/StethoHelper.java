package de.pscom.pietsmiet.stetho;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import de.pscom.pietsmiet.BuildConfig;
import okhttp3.OkHttpClient;

public class StethoHelper {
    public static void init(Context context) {
        if (!BuildConfig.DEBUG) return;
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                        .build());
    }

    public static OkHttpClient.Builder configureInterceptor(OkHttpClient.Builder httpClient) {
        // Additional check to avoid that we are in release, should never happen here
        if (BuildConfig.DEBUG){
            return httpClient.addNetworkInterceptor(new StethoInterceptor());
        } else {
            return httpClient;
        }

    }

}
