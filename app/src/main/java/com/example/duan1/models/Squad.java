package com.example.duan1.models;

import java.util.HashMap;
import java.util.Map;

public class Squad {
    private String squadId;
    private String userId;
    private String squadName;
    private String formation;

    private Map<String, String> positions = new HashMap<>();

    public Squad() {
    }

    public Squad(String squadId, String userId, String squadName, String formation) {
        this.squadId = squadId;
        this.userId = userId;
        this.squadName = squadName;
        this.formation = formation;
    }

    public String getSquadId() {
        return squadId;
    }

    public void setSquadId(String squadId) {
        this.squadId = squadId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSquadName() {
        return squadName;
    }

    public void setSquadName(String squadName) {
        this.squadName = squadName;
    }

    public String getFormation() {
        return formation;
    }

    public void setFormation(String formation) {
        this.formation = formation;
    }

    public Map<String, String> getPositions() {
        return positions;
    }

    public void setPositions(Map<String, String> positions) {
        this.positions = positions;
    }
}