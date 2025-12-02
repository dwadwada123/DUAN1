package com.example.duan1.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duan1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;

    // Khai báo Firebase Authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo Firebase Auth instance
        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);

        btnRegister.setOnClickListener(v -> handleRegistration());

        tvLoginLink.setOnClickListener(v -> {
            finish();
        });
    }

    private void handleRegistration() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase yêu cầu mật khẩu tối thiểu 6 ký tự
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Thực hiện đăng ký User với Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Đăng ký Firebase thành công
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show();

                            // Tùy chọn: Gửi email xác minh ngay sau khi đăng ký (nên dùng)
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(verificationTask -> {
                                            if (verificationTask.isSuccessful()) {
                                                // Thông báo nếu gửi email xác minh thành công
                                            }
                                        });
                            }

                            finish(); // Quay lại màn hình Login
                        } else {
                            // Đăng ký thất bại (ví dụ: email đã tồn tại, format email sai,...)
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Lỗi đăng ký không xác định.";
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}