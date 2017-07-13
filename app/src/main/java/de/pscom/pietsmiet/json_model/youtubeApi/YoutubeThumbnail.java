package de.pscom.pietsmiet.json_model.youtubeApi;

import java.util.HashMap;
import java.util.Map;

public class YoutubeThumbnail {

    private String url;
    private Integer width;
    private Integer height;
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}