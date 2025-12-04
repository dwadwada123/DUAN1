package com.example.duan1.services;

import com.google.gson.annotations.SerializedName;

public class SquadRequest {
    @SerializedName("squad_name")
    private String squadName;

    private String formation;

    public SquadRequest(String squadName, String formation) {
        this.squadName = squadName;
        this.formation = formation;
    }
}