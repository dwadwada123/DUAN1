package com.example.duan1.services;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {
    @SerializedName("display_name")
    private String displayName;

    public UpdateProfileRequest(String displayName) {
        this.displayName = displayName;
    }
}