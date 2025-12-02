package com.example.duan1.services;

// Dùng cho POST /api/admin/users/:id/ban
public class BanUserRequest {
    private String reason;

    public BanUserRequest(String reason) {
        this.reason = reason;
    }
}