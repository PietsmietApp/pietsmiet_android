package de.pscom.pietsmiet.model.twitterApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TwitterStatus {
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("id")
    @Expose
    public Long id;
    @SerializedName("id_str")
    @Expose
    public String idStr;
    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("truncated")
    @Expose
    public Boolean truncated;
    @SerializedName("entities")
    @Expose
    public TwitterEntities entities;
    @SerializedName("source")
    @Expose
    public String source;
    @SerializedName("user")
    @Expose
    public TwitterUser user;
    @SerializedName("retweeted_status")
    @Expose
    public TwitterStatus retweetedStatus;
    @SerializedName("is_quote_status")
    @Expose
    public Boolean isQuoteStatus;
    @SerializedName("possibly_sensitive")
    @Expose
    public Boolean possiblySensitive;

}
