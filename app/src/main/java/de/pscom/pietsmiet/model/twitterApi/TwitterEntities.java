package de.pscom.pietsmiet.model.twitterApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TwitterEntities {

    @SerializedName("media")
    @Expose
    public List<Media> media = null;

    public class Media {
        @SerializedName("id")
        @Expose
        public Long id;
        @SerializedName("id_str")
        @Expose
        public String idStr;
        @SerializedName("media_url")
        @Expose
        public String mediaUrl;
        @SerializedName("media_url_https")
        @Expose
        public String mediaUrlHttps;
        @SerializedName("url")
        @Expose
        public String url;
        @SerializedName("display_url")
        @Expose
        public String displayUrl;
        @SerializedName("expanded_url")
        @Expose
        public String expandedUrl;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("source_status_id")
        @Expose
        public Long sourceStatusId;
        @SerializedName("source_status_id_str")
        @Expose
        public String sourceStatusIdStr;
        @SerializedName("source_user_id")
        @Expose
        public Long sourceUserId;
        @SerializedName("source_user_id_str")
        @Expose
        public String sourceUserIdStr;

    }
}
