package de.pscom.pietsmiet.model.twitterApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TwitterToken {
    @SerializedName("token_type")
    @Expose
    public String tokenType;
    @SerializedName("access_token")
    @Expose
    public String accessToken;
}
