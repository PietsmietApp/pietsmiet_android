package de.pscom.pietsmiet.model;

import java.util.HashMap;
import java.util.Map;

public class YoutubeContent {

    private String videoId;
    private Map<String, Object> additionalProperties = new HashMap<>();

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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
