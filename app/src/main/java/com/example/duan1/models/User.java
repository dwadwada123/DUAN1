package com.example.duan1.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("firebase_uid")
    private String userId;

    @SerializedName("email")
    private String email;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("role")
    private String role;

    @SerializedName("squad_count")
    private int squadCount;

    @SerializedName("player_count")
    private int playerCount;

    public User() {
    }

    public User(String userId, String email, String displayName, String role) {
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRole() {
        return role;
    }

    public int getSquadCount() {
        return squadCount;
    }

    public int getPlayerCount() {
        return playerCount;
    }
}