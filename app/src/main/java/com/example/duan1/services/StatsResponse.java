package com.example.duan1.services;

// Dùng cho GET /api/admin/stats
public class StatsResponse {
    private int userCount;
    private int playerCount;
    private int squadCount;

    public int getUserCount() { return userCount; }
    public int getPlayerCount() { return playerCount; }
    public int getSquadCount() { return squadCount; }
}