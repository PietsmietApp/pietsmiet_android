package de.pscom.pietsmiet.model.firebaseApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FirebaseItem {
    @SerializedName("date")
    @Expose
    public Long date;
    @SerializedName("desc")
    @Expose
    public String desc;
    @SerializedName("link")
    @Expose
    public String link;
    @SerializedName("scope")
    @Expose
    public String scope;
    @SerializedName("title")
    @Expose
    public String title;
}
