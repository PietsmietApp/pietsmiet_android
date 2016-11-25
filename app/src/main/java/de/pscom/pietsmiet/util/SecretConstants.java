package de.pscom.pietsmiet.util;

import android.content.Context;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SecretConstants {
    @Nullable
    public static String twitterSecret;
    @Nullable
    public static String rssUrl;
    @Nullable
    public static String facebookToken;
    @Nullable
    public static String facebookSecret;
    @Nullable
    public static String youtubeAPIkey;

    public SecretConstants(Context context) {
        try {
            InputStream input = context.getAssets().open("secrets.properties");
            Properties properties = new Properties();
            properties.load(input);

            twitterSecret = properties.getProperty("twitterSecret");
            rssUrl = properties.getProperty("rssUrl");
            facebookToken = properties.getProperty("facebookToken");
            facebookSecret = properties.getProperty("facebookSecret");
            youtubeAPIkey = properties.getProperty("youtubeAPIkey");
        } catch (IOException e) {
            PsLog.w("You haven't included a '/app/src/main/assets/secrets.properties' file in your project with the API-Keys and RSS-URLs!\n" +
                    "=> Twitter, Facebook & Uploadplan won't work");
        }
    }

}
