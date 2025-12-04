package com.example.duan1.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class SquadItem implements Serializable {

    @SerializedName("slot_index")
    private int slotIndex;

    @SerializedName("player_id")
    private Player player;

    @SerializedName("position_label")
    private String positionLabel;

    public SquadItem(int slotIndex, Player player, String positionLabel) {
        this.slotIndex = slotIndex;
        this.player = player;
        this.positionLabel = positionLabel;
    }

    public int getSlotIndex() { return slotIndex; }
    public Player getPlayer() { return player; }
    public String getPositionLabel() { return positionLabel; }
}