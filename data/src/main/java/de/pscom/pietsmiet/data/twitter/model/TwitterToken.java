package de.pscom.pietsmiet.data.twitter.model;

import com.google.gson.annotations.SerializedName;

public class TwitterToken {
    @SerializedName("token_type")
    public String tokenType;
    @SerializedName("access_token")
    public String accessToken;
}
