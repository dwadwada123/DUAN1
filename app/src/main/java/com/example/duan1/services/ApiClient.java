package com.example.duan1.services;

import com.example.duan1.services.ApiService; // Import ApiService từ package services
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // ⚠️⚠️ THAY THẾ BẰNG BASE URL CỦA API BACKEND CỦA BẠN ⚠️⚠️
    // Sử dụng 10.0.2.2 nếu chạy trên Emulator và Backend là localhost.
    private static final String BASE_URL = "http://your.api.backend.com/";

    private static Retrofit retrofit = null;

    /**
     * Khởi tạo và trả về instance Retrofit (Singleton Pattern).
     * Đảm bảo chỉ có một instance Retrofit được sử dụng trong toàn bộ ứng dụng.
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * Trả về implementation của interface ApiService.
     */
    public static ApiService getApiService() {
        // Dùng Retrofit instance để tạo ra đối tượng triển khai (implementation) của ApiService
        return getClient().create(ApiService.class);
    }
}