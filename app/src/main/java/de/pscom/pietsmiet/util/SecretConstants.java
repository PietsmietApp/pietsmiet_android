package de.pscom.pietsmiet.util;

import android.content.Context;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/* Create a secret.properties file in app/src/main/assets with this content:
    twitterSecret=Your Twitter Secret
    facebookSecret=Your Facebook Secret
    facebookToken=Your Facebook Token
    youtubeAPIkey=Your Youtube API key
*/
public class SecretConstants {
    @Nullable
    public static String twitterSecret;
    @Nullable
    public static String facebookToken;
    @Nullable
    public static String youtubeAPIkey;
    @Nullable
    public static String twitchClientId;

    public SecretConstants(Context context) {
        try {
            InputStream input = context.getAssets().open("secrets.properties");
            Properties properties = new Properties();
            properties.load(input);

            twitterSecret = properties.getProperty("twitterSecret");
            facebookToken = properties.getProperty("facebookToken");
            youtubeAPIkey = properties.getProperty("youtubeAPIkey");
            twitchClientId = properties.getProperty("twitchClientId");
        } catch (IOException e) {
            PsLog.e("You haven't included a '/app/src/main/assets/secrets.properties' file in your project with the API-Keys!\n" +
                    "=> Twitter, Facebook, Twitch & Youtube won't work");
        }
    }

}
