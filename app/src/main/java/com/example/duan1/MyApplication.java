package com.example.duan1;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import android.util.Log;
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Dòng Debug: Kiểm tra xem MyApplication có được gọi không
        Log.d("APP_LIFECYCLE", "MyApplication onCreate is running.");

        try {
            FirebaseApp.initializeApp(this);
            Log.d("FIREBASE_INIT", "FirebaseApp initialized successfully.");
        } catch (Exception e) {
            // Nếu bạn thấy dòng này, có lỗi cấu hình!
            Log.e("FIREBASE_INIT", "Firebase initialization FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}