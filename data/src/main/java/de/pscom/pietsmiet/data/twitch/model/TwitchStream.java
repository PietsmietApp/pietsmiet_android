package de.pscom.pietsmiet.data.twitch.model;

import com.google.gson.annotations.SerializedName;

public class TwitchStream {

    @SerializedName("_id")
    private long id;
    @SerializedName("game")
    private String game;
    @SerializedName("viewers")
    private int viewers;
    @SerializedName("video_height")
    private int videoHeight;
    @SerializedName("average_fps")
    private float averageFps;
    @SerializedName("delay")
    private int delay;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("is_playlist")
    private boolean isPlaylist;
    @SerializedName("preview")
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
