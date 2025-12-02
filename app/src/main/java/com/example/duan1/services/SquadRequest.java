package com.example.duan1.services;

import java.util.List;

// Dùng cho POST /api/squads (Tạo đội hình mới)
public class SquadRequest {
    private String name;
    private String formation;
    private List<String> initialPlayerIds; // Ví dụ: ID cầu thủ ban đầu

    public SquadRequest(String name, String formation, List<String> initialPlayerIds) {
        this.name = name;
        this.formation = formation;
        this.initialPlayerIds = initialPlayerIds;
    }
}