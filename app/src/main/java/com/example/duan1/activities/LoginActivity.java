package com.example.duan1.activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar; // Thêm ProgressBar
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duan1.R;
import com.example.duan1.services.ApiClient;
import com.example.duan1.services.ApiService;
import com.example.duan1.services.SyncUserRequest;
import com.example.duan1.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private CheckBox cbRemember;
    private ProgressBar progressBar; // Khai báo ProgressBar

    private FirebaseAuth mAuth;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo các thành phần
        mAuth = FirebaseAuth.getInstance();
        apiService = ApiClient.getApiService();

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        tvForgotPassword = findViewById(R.id.tv_forgot_pass);
        cbRemember = findViewById(R.id.cb_remember);
        progressBar = findViewById(R.id.progressBar); // Đảm bảo bạn có ProgressBar trong layout

        btnLogin.setOnClickListener(v -> handleLogin());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
    }

    /**
     * FIX: Kiểm tra xem người dùng đã đăng nhập chưa khi Activity bắt đầu.
     */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Nếu đã đăng nhập, cố gắng đồng bộ lại để lấy Role và chuyển màn hình.
            Log.d(TAG, "User already logged in. Attempting to sync token.");
            showProgress(true);

            currentUser.getIdToken(true).addOnCompleteListener(tokenTask -> {
                showProgress(false);
                if (tokenTask.isSuccessful() && tokenTask.getResult() != null) {
                    syncUserWithBackend(tokenTask.getResult().getToken());
                } else {
                    // Nếu không lấy được token, hiển thị màn hình đăng nhập
                    Toast.makeText(this, "Lỗi lấy token cũ. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                }
            });
        }
    }

    // -----------------------------------------------------------------
    // LOGIC ĐĂNG NHẬP (FIREBASE VÀ API BACKEND)
    // -----------------------------------------------------------------

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(true); // Hiển thị loading

        // 1. Đăng nhập với Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // 2. Lấy Firebase ID Token (Luôn lấy token mới khi đăng nhập thành công)
                            user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                                if (tokenTask.isSuccessful() && tokenTask.getResult() != null) {
                                    String idToken = tokenTask.getResult().getToken();
                                    // 3. Đồng bộ hóa User với Backend
                                    syncUserWithBackend(idToken);
                                } else {
                                    showProgress(false);
                                    String errorMessage = tokenTask.getException() != null ? tokenTask.getException().getMessage() : "Không thể lấy token.";
                                    Toast.makeText(LoginActivity.this, "Lỗi lấy ID Token: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    mAuth.signOut(); // Thất bại ở bước token cũng cần đăng xuất
                                }
                            });
                        }
                    } else {
                        // Đăng nhập thất bại
                        showProgress(false);
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định.";
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Firebase Login Failed: " + errorMessage);
                    }
                });
    }

    private void syncUserWithBackend(String idToken) {
        String authHeader = "Bearer " + idToken;

        // Không gọi showProgress(true) ở đây vì đã gọi trong handleLogin() hoặc onStart()

        apiService.syncUser(authHeader, new SyncUserRequest()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                showProgress(false); // Ẩn loading khi nhận được phản hồi

                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    String role = user.getRole();
                    navigateToNextScreen(role);
                } else {
                    // Lỗi đồng bộ (Backend từ chối User, ví dụ: Role không hợp lệ)
                    Toast.makeText(LoginActivity.this, "Lỗi đồng bộ User với máy chủ: Mã lỗi " + response.code(), Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                showProgress(false); // Ẩn loading khi thất bại
                Toast.makeText(LoginActivity.this, "Lỗi kết nối API/Mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "API Sync Failed", t);
                mAuth.signOut();
            }
        });
    }

    private void navigateToNextScreen(String role) {
        Intent intent;
        String targetActivityName;

        if ("Admin".equalsIgnoreCase(role)) {
            intent = new Intent(LoginActivity.this, AdminActivity.class);
            targetActivityName = "AdminActivity";
        } else {
            intent = new Intent(LoginActivity.this, MainActivity.class);
            targetActivityName = "MainActivity";
        }

        try {
            startActivity(intent);
            finish();
        } catch (ActivityNotFoundException e) {
            // Xử lý lỗi Activity không được tìm thấy
            Toast.makeText(this, "LỖI CẤU HÌNH: Activity " + targetActivityName + " chưa được khai báo trong AndroidManifest.xml!", Toast.LENGTH_LONG).show();
            Log.e(TAG, "ActivityNotFoundException: " + targetActivityName + " not declared.", e);
            mAuth.signOut();
        }
    }

    // -----------------------------------------------------------------
    // CẢI TIẾN: HÀM HIỂN THỊ TIẾN TRÌNH
    // -----------------------------------------------------------------

    private void showProgress(boolean show) {
        // Vô hiệu hóa nút và hiển thị/ẩn ProgressBar
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
        }
    }
    // -----------------------------------------------------------------
    // LOGIC QUÊN MẬT KHẨU (FIREBASE)
    // -----------------------------------------------------------------
    private void showForgotPasswordDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_forgot_password, null);
        dialog.setContentView(view);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        EditText etResetEmail = view.findViewById(R.id.et_reset_email);
        Button btnCancel = view.findViewById(R.id.btn_cancel_reset);
        Button btnSend = view.findViewById(R.id.btn_send_reset);
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSend.setOnClickListener(v -> {
            String email = etResetEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email để khôi phục", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi email đặt lại mật khẩu bằng Firebase
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Đã gửi liên kết đặt lại mật khẩu tới: " + email, Toast.LENGTH_LONG).show();
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định.";
                            Toast.makeText(LoginActivity.this, "Gửi thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    });
        });

        dialog.show();
    }
}
