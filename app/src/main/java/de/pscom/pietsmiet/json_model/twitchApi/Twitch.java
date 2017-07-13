
package de.pscom.pietsmiet.json_model.twitchApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Twitch {

    @SerializedName("stream")
    @Expose
    private TwitchStream stream;

    public TwitchStream getStream() {
        return stream;
    }

    public void setStream(TwitchStream stream) {
        this.stream = stream;
    }

}
