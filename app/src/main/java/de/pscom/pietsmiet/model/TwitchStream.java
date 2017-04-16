
package de.pscom.pietsmiet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TwitchStream {

    @SerializedName("_id")
    @Expose
    private long id;
    @SerializedName("game")
    @Expose
    private String game;
    @SerializedName("viewers")
    @Expose
    private int viewers;
    @SerializedName("video_height")
    @Expose
    private int videoHeight;
    @SerializedName("average_fps")
    @Expose
    private float averageFps;
    @SerializedName("delay")
    @Expose
    private int delay;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("is_playlist")
    @Expose
    private boolean isPlaylist;
    @SerializedName("preview")
    @Expose
    private TwitchPreview preview;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public int getViewers() {
        return viewers;
    }

    public void setViewers(int viewers) {
        this.viewers = viewers;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public float getAverageFps() {
        return averageFps;
    }

    public void setAverageFps(float averageFps) {
        this.averageFps = averageFps;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isIsPlaylist() {
        return isPlaylist;
    }

    public void setIsPlaylist(boolean isPlaylist) {
        this.isPlaylist = isPlaylist;
    }

    public TwitchPreview getPreview() {
        return preview;
    }

    public void setPreview(TwitchPreview preview) {
        this.preview = preview;
    }

}
