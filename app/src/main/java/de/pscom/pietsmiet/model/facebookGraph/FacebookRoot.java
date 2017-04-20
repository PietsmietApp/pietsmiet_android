
package de.pscom.pietsmiet.model.facebookGraph;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FacebookRoot {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("body")
    @Expose
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
