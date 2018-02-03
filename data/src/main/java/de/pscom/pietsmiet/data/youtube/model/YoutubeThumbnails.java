package de.pscom.pietsmiet.data.youtube.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class YoutubeThumbnails {
    @SerializedName("default")
    private YoutubeThumbnail _default;

    private YoutubeThumbnail medium;
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * @return The _default
     */
    public YoutubeThumbnail getDefault() {
        return _default;
    }

    /**
     * @param _default The default
     */
    public void setDefault(YoutubeThumbnail _default) {
        this._default = _default;
    }

    /**
     * @return The medium
     */
    public YoutubeThumbnail getMedium() {
        return medium;
    }

    /**
     * @param medium The medium
     */
    public void setMedium(YoutubeThumbnail medium) {
        this.medium = medium;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

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
}
