package de.pscom.pietsmiet.data.facebook.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FacebookEntity {

    @SerializedName("data")
    private List<FacebookData> data = null;

    public List<FacebookData> getData() {
        return data;
    }

    public void setData(List<FacebookData> data) {
        this.data = data;
    }

}
