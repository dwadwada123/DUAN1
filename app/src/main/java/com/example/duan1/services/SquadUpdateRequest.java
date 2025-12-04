package com.example.duan1.services;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SquadUpdateRequest {
    @SerializedName("squad_name")
    private String squadName;
    private String formation;
    private List<SquadItemRequest> items;

    public SquadUpdateRequest(String squadName, String formation, List<SquadItemRequest> items) {
        this.squadName = squadName;
        this.formation = formation;
        this.items = items;
    }

    public static class SquadItemRequest {
        @SerializedName("slot_index")
        private int slotIndex;

        @SerializedName("player_id")
        private String playerId;

        @SerializedName("position_label")
        private String positionLabel;

        public SquadItemRequest(int slotIndex, String playerId, String positionLabel) {
            this.slotIndex = slotIndex;
            this.playerId = playerId;
            this.positionLabel = positionLabel;
        }
    }
}