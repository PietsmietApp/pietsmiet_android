package de.pscom.pietsmiet.data.twitter.model;

import com.google.gson.annotations.SerializedName;

public class TwitterUser {
    @SerializedName("id")
    public Long id;
    @SerializedName("id_str")
    public String idStr;
    @SerializedName("name")
    public String name;
    @SerializedName("screen_name")
    public String screenName;
}
