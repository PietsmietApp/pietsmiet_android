package de.pscom.pietsmiet.data.twitter.model;

import com.google.gson.annotations.SerializedName;

public class TwitterStatus {
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("id")
    public Long id;
    @SerializedName("id_str")
    public String idStr;
    @SerializedName("text")
    public String text;
    @SerializedName("truncated")
    public Boolean truncated;
    @SerializedName("entities")
    public TwitterEntities entities;
    @SerializedName("source")
    public String source;
    @SerializedName("user")
    public TwitterUser user;
    @SerializedName("retweeted_status")
    public TwitterStatus retweetedStatus;
    @SerializedName("is_quote_status")
    public Boolean isQuoteStatus;
    @SerializedName("possibly_sensitive")
    public Boolean possiblySensitive;

}
