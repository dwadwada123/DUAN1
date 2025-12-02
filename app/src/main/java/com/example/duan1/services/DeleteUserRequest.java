package com.example.duan1.services;

// Dùng cho DELETE /api/admin/users/:id
public class DeleteUserRequest {
    private String reason;

    public DeleteUserRequest(String reason) {
        this.reason = reason;
    }
}