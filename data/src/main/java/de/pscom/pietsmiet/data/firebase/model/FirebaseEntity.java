package de.pscom.pietsmiet.data.firebase.model;

import com.google.gson.annotations.SerializedName;

public class FirebaseEntity {
    @SerializedName("date")
    public Long date;
    @SerializedName("desc")
    public String desc;
    @SerializedName("link")
    public String link;
    @SerializedName("image_url")
    public String image_url;
    @SerializedName("scope")
    public String scope;
    @SerializedName("title")
    public String title;
}
