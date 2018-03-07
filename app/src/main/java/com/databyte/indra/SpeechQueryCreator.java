package com.databyte.indra;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SpeechQueryCreator {

    @SerializedName("timestamp")
    @Expose
    private Long timestamp;
    @SerializedName("user")
    @Expose
    private Integer user;
    @SerializedName("speech")
    @Expose
    private String speech;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }

}
