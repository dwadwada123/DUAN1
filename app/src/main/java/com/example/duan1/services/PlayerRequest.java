package com.example.duan1.services;

import com.google.gson.annotations.SerializedName;

public class PlayerRequest {
    private String name;

    @SerializedName("team_name")
    private String teamName;

    private String position;
    private String nationality;
    private String height;
    private String weight;
    private String description;

    @SerializedName("team_logo")
    private String teamLogo;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("jersey_number")
    private int jerseyNumber;

    public PlayerRequest(String name, String teamName, String position, String nationality, String height, String weight, String description, String imageUrl, int jerseyNumber) {
        this.name = name;
        this.teamName = teamName;
        this.position = position;
        this.nationality = nationality;
        this.height = height;
        this.weight = weight;
        this.description = description;
        this.imageUrl = imageUrl;
        this.jerseyNumber = jerseyNumber;
        this.teamLogo = "";
    }
}