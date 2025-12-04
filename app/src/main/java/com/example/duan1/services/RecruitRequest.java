package com.example.duan1.services;

import com.google.gson.annotations.SerializedName;

public class RecruitRequest {
    @SerializedName("player_id")
    private String playerId;

    public RecruitRequest(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}