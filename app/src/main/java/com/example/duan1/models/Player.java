package com.example.duan1.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Player implements Serializable {

    @SerializedName("idPlayer")
    private String idPlayer;

    @SerializedName("strPlayer")
    private String name;

    @SerializedName("strTeam")
    private String team;

    @SerializedName("strNationality")
    private String nationality;

    @SerializedName("strPosition")
    private String position;

    @SerializedName("strThumb")
    private String thumbUrl;

    @SerializedName("strCutout")
    private String cutoutUrl;

    @SerializedName("strHeight")
    private String height;

    @SerializedName("strWeight")
    private String weight;

    @SerializedName("strDescriptionEN")
    private String description;

    public Player() {
    }

    public String getImage() {
        if (cutoutUrl != null && !cutoutUrl.isEmpty()) return cutoutUrl;
        return thumbUrl;
    }

    public String getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(String idPlayer) {
        this.idPlayer = idPlayer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getCutoutUrl() {
        return cutoutUrl;
    }

    public void setCutoutUrl(String cutoutUrl) {
        this.cutoutUrl = cutoutUrl;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}