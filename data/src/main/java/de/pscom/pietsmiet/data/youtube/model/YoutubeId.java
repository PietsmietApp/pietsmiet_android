package de.pscom.pietsmiet.data.youtube.model;

import com.google.gson.annotations.SerializedName;

public class YoutubeId {

    @SerializedName("kind")
    private String kind;
    @SerializedName("videoId")
    private String videoId;

    /**
     * @return The kind
     */
    public String getKind() {
        return kind;
    }

    /**
     * @param kind The kind
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * @return The videoId
     */
    public String getVideoId() {
        return videoId;
    }

    /**
     * @param videoId The videoId
     */
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

}
