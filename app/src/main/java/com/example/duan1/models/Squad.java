package com.example.duan1.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Squad implements Serializable {

    @SerializedName("_id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("squad_name")
    private String squadName;

    @SerializedName("formation")
    private String formation;

    @SerializedName("items")
    private List<SquadItem> items;

    public Squad(String squadName, String formation) {
        this.squadName = squadName;
        this.formation = formation;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getSquadName() { return squadName; }
    public String getFormation() { return formation; }
    public List<SquadItem> getItems() { return items; }

    public void setFormation(String formation) { this.formation = formation; }
    public void setSquadName(String squadName) { this.squadName = squadName; }
    public void setItems(List<SquadItem> items) { this.items = items; }
}