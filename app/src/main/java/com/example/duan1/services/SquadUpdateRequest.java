package com.example.duan1.services;

import java.util.List;

public class SquadUpdateRequest {
    private String squadName; // Tùy chọn sửa tên
    private List<String> playerIds; // Danh sách ID cầu thủ mới (để cập nhật lại)

    public SquadUpdateRequest(String squadName, List<String> playerIds) {
        this.squadName = squadName;
        this.playerIds = playerIds;
    }
}