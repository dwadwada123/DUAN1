package com.example.duan1.models;
import com.google.gson.annotations.SerializedName;

public class UploadResponse {
    @SerializedName("url")
    private String url;

    public String getUrl() { return url; }
}