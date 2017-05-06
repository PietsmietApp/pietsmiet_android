package de.pscom.pietsmiet.json_model.twitterApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TwitterRoot {

    @SerializedName("statuses")
    @Expose
    public List<TwitterStatus> statuses = null;

    @SerializedName("search_metadata")
    @Expose
    public SearchMetadata searchMetadata;

    public class SearchMetadata {
        // Highest ID
        @SerializedName("max_id")
        @Expose
        public Long maxId;
        @SerializedName("count")
        @Expose
        public Long count;
        @SerializedName("since_id")
        @Expose
        public Long sinceId;
    }
}
