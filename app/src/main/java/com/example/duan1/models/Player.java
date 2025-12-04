package com.example.duan1.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Player implements Serializable {

    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("team_name")
    private String team;

    @SerializedName("team_logo")
    private String teamLogo;

    @SerializedName("position")
    private String position;

    @SerializedName("nationality")
    private String nationality;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("height")
    private String height;

    @SerializedName("weight")
    private String weight;

    @SerializedName("description")
    private String description;

    @SerializedName("jersey_number")
    private int jerseyNumber;

    @SerializedName("is_active")
    private boolean isActive;

    public Player() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTeam() {
        return team;
    }

    public String getPosition() {
        return position;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getImage() {
        return imageUrl != null ? imageUrl : "";
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getDescription() {
        return description;
    }

    public String getTeamLogo() {
        return teamLogo;
    }

    public int getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(int jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setThumbUrl(String thumbUrl) {
        this.imageUrl = thumbUrl;
    }
}