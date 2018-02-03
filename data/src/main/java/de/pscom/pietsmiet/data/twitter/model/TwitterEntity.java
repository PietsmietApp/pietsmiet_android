package de.pscom.pietsmiet.data.twitter.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TwitterEntity {

    @SerializedName("statuses")
    public List<TwitterStatus> statuses = null;

    @SerializedName("search_metadata")
    public SearchMetadata searchMetadata;

    public class SearchMetadata {
        // Highest ID
        @SerializedName("max_id")
        public Long maxId;
        @SerializedName("count")
        public Long count;
        @SerializedName("since_id")
        public Long sinceId;
    }
}
