package de.pscom.pietsmiet.json_model.youtubeApi;


import java.util.HashMap;
import java.util.Map;


public class YoutubeSnippet {

    private String publishedAt;
    private String title;
    private String description;
    private YoutubeThumbnails thumbnails;
    private String channelTitle;
    private String playlistId;
    private Integer position;
    private Map<String, Object> additionalProperties = new HashMap<>();

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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}