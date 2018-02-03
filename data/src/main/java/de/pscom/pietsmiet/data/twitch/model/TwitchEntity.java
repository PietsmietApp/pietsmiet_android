package de.pscom.pietsmiet.data.twitch.model;

import com.google.gson.annotations.SerializedName;

/**
 * Source: https://github.com/ihorvitruk/buddysearch/blob/6a01a90ec667b70c6591eaaf33d8aab21f31faf9/library/src/main/java/com/buddysearch/android/library/data/mapper/BaseMapper.java
 */
public class TwitchEntity {
    @SerializedName("stream")
    private TwitchStream stream;

    public TwitchStream getStream() {
        return stream;
    }

    public void setStream(TwitchStream stream) {
        this.stream = stream;
    }
}
