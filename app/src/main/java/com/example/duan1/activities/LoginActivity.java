package com.example.duan1.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duan1.R;
import com.example.duan1.models.User;
import com.example.duan1.services.ApiClient;
import com.example.duan1.services.ApiService;
import com.example.duan1.services.SyncUserRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private CheckBox cbRemember;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        apiService = ApiClient.getApiService();
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        initViews();
        setupListeners();

        boolean isRemembered = sharedPreferences.getBoolean("REMEMBER_ME", true);
        cbRemember.setChecked(isRemembered);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        boolean isRemembered = sharedPreferences.getBoolean("REMEMBER_ME", false);

        if (currentUser != null) {
            if (isRemembered) {
                fetchTokenAndSync(currentUser);
            } else {
                mAuth.signOut();
            }
        }
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        tvForgotPassword = findViewById(R.id.tv_forgot_pass);
        cbRemember = findViewById(R.id.cb_remember);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void handleLogin() {
        String email = String.valueOf(etEmail.getText()).trim();
        String password = String.valueOf(etPassword.getText()).trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập Email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập Mật khẩu");
            return;
        }

        setLoading(true);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("REMEMBER_ME", cbRemember.isChecked());
                        editor.apply();

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            fetchTokenAndSync(user);
                        }
                    } else {
                        setLoading(false);
                        String errorMessage = getFirebaseErrorMessage(task.getException());
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String getFirebaseErrorMessage(Exception exception) {
        if (exception == null) return "Đăng nhập thất bại.";

        if (exception instanceof FirebaseAuthInvalidUserException) {
            String errorCode = ((FirebaseAuthInvalidUserException) exception).getErrorCode();
            if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                return "Tài khoản không tồn tại.";
            } else if (errorCode.equals("ERROR_USER_DISABLED")) {
                return "Tài khoản đã bị vô hiệu hóa.";
            } else {
                return "Lỗi tài khoản không hợp lệ.";
            }
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            String errorCode = ((FirebaseAuthInvalidCredentialsException) exception).getErrorCode();
            if (errorCode.equals("ERROR_INVALID_EMAIL")) {
                return "Định dạng email không hợp lệ.";
            } else if (errorCode.equals("ERROR_WRONG_PASSWORD")) {
                return "Mật khẩu không chính xác.";
            } else {
                return "Email hoặc mật khẩu không chính xác.";
            }
        }
        return "Email hoặc mật khẩu không chính xác.";
    }

    private void fetchTokenAndSync(FirebaseUser user) {
        setLoading(true);
        user.getIdToken(false).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String token = task.getResult().getToken();
                syncUserWithBackend(token);
            } else {
                setLoading(false);
                mAuth.signOut();
                Toast.makeText(LoginActivity.this, "Không thể xác thực phiên đăng nhập", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void syncUserWithBackend(String token) {
        String authHeader = "Bearer " + token;
        apiService.syncUser(authHeader, new SyncUserRequest()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    User backendUser = response.body();
                    navigateBasedOnRole(backendUser.getRole());
                } else {
                    if (response.code() == 403) {
                        Toast.makeText(LoginActivity.this, "Tài khoản của bạn đã bị khóa bởi Admin", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Lỗi đồng bộ dữ liệu (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                    mAuth.signOut();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, "Không thể kết nối đến Server. Vui lòng kiểm tra mạng.", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
            }
        });
    }

    private void navigateBasedOnRole(String role) {
        Intent intent;
        if ("admin".equalsIgnoreCase(role)) {
            intent = new Intent(LoginActivity.this, AdminActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            etEmail.setEnabled(false);
            etPassword.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            etEmail.setEnabled(true);
            etPassword.setEnabled(true);
        }
    }

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
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Đã gửi email khôi phục. Vui lòng kiểm tra hộp thư.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Gửi thất bại. Email không tồn tại hoặc lỗi mạng.", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    });
        });

        dialog.show();
    }
}