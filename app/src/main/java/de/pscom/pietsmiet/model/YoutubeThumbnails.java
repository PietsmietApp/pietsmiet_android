package de.pscom.pietsmiet.model;

import java.util.HashMap;
import java.util.Map;

public class YoutubeThumbnails {

    private YoutubeThumbnailDefault _default;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The _default
     */
    public YoutubeThumbnailDefault getDefault() {
        return _default;
    }

    /**
     * @param _default The default
     */
    public void setDefault(YoutubeThumbnailDefault _default) {
        this._default = _default;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
