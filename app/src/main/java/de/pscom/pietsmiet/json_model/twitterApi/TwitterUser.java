package de.pscom.pietsmiet.json_model.twitterApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TwitterUser {
    @SerializedName("id")
    @Expose
    public Long id;
    @SerializedName("id_str")
    @Expose
    public String idStr;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("screen_name")
    @Expose
    public String screenName;
}
