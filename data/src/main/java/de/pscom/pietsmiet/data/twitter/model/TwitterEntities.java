package de.pscom.pietsmiet.data.twitter.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TwitterEntities {

    @SerializedName("media")
    public List<Media> media = null;

    public class Media {
        @SerializedName("id")
        public Long id;
        @SerializedName("id_str")
        public String idStr;
        @SerializedName("media_url")
        public String mediaUrl;
        @SerializedName("media_url_https")
        public String mediaUrlHttps;
        @SerializedName("url")
        public String url;
        @SerializedName("display_url")
        public String displayUrl;
        @SerializedName("expanded_url")
        public String expandedUrl;
        @SerializedName("type")
        public String type;
        @SerializedName("source_status_id")
        public Long sourceStatusId;
        @SerializedName("source_status_id_str")
        public String sourceStatusIdStr;
        @SerializedName("source_user_id")
        public Long sourceUserId;
        @SerializedName("source_user_id_str")
        public String sourceUserIdStr;

    }
}
