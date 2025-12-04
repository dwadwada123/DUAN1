package com.example.duan1.services;

import com.google.gson.annotations.SerializedName;

public class StatsResponse {
    @SerializedName("total_users")
    private int userCount;

    @SerializedName("total_players_recruited")
    private int playerCount;

    @SerializedName("total_squads")
    private int squadCount;

    public int getUserCount() { return userCount; }
    public int getPlayerCount() { return playerCount; }
    public int getSquadCount() { return squadCount; }
}