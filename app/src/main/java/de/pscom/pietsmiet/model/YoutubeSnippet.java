package de.pscom.pietsmiet.model;


import java.util.HashMap;
import java.util.Map;


public class YoutubeSnippet {

    private String publishedAt;
    private String channelId;
    private String title;
    private String description;
    private YoutubeThumbnails thumbnails;
    private String channelTitle;
    private String playlistId;
    private Integer position;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The publishedAt
     */
    public String getPublishedAt() {
        return publishedAt;
    }

    /**
     * @param publishedAt The publishedAt
     */
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    /**
     * @return The channelId
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * @param channelId The channelId
     */
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The thumbnails
     */
    public YoutubeThumbnails getThumbnails() {
        return thumbnails;
    }

    /**
     * @param thumbnails The thumbnails
     */
    public void setThumbnails(YoutubeThumbnails thumbnails) {
        this.thumbnails = thumbnails;
    }

    /**
     * @return The channelTitle
     */
    public String getChannelTitle() {
        return channelTitle;
    }

    /**
     * @param channelTitle The channelTitle
     */
    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    /**
     * @return The playlistId
     */
    public String getPlaylistId() {
        return playlistId;
    }

    /**
     * @param playlistId The playlistId
     */
    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    /**
     * @return The position
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * @param position The position
     */
    public void setPosition(Integer position) {
        this.position = position;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}