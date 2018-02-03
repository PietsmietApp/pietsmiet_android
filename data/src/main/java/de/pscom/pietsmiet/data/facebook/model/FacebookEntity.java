package de.pscom.pietsmiet.data.facebook.model;

import com.google.gson.annotations.SerializedName;

public class FacebookEntity {
    @SerializedName("code")
    private int code;
    @SerializedName("body")
    private String body;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
