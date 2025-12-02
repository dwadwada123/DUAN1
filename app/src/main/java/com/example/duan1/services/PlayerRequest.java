package com.example.duan1.services;

public class PlayerRequest {
    // Các trường dữ liệu cần thiết để tạo/sửa Player
    private String name;
    private String position;
    private int age;
    private String photoUrl; // Hoặc dùng Multipart cho file upload

    // Constructor, Getters, Setters (hoặc chỉ Getters nếu dùng Gson)
    public PlayerRequest(String name, String position, int age) {
        this.name = name;
        this.position = position;
        this.age = age;
    }
}