package com.example.duan1.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.duan1.R;
import com.example.duan1.activities.LoginActivity;
import com.example.duan1.models.User;
import com.example.duan1.services.ApiClient;
import com.example.duan1.services.ApiService;
import com.example.duan1.services.UpdateProfileRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextInputEditText etEmail, etDisplayName;
    private Button btnSave, btnLogout, btnChangePass;
    private TextView tvStatTotalPlayers, tvStatTotalSquads, tvStatMostUsed;
    private ProgressBar pbLoading;

    private ApiService apiService;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        apiService = ApiClient.getApiService();

        initViews(view);
        setupListeners();
        loadProfileData();
    }

    private void initViews(View view) {
        etEmail = view.findViewById(R.id.et_email);
        etDisplayName = view.findViewById(R.id.et_display_name);
        btnSave = view.findViewById(R.id.btn_save_profile);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnChangePass = view.findViewById(R.id.btn_change_password);
        pbLoading = view.findViewById(R.id.pb_loading);

        View cardTotal = view.findViewById(R.id.stat_total_players);
        View cardSquads = view.findViewById(R.id.stat_favorite_team);
        View cardMost = view.findViewById(R.id.stat_most_used_player);

        tvStatTotalPlayers = cardTotal.findViewById(R.id.tv_stat_value);
        ((TextView) cardTotal.findViewById(R.id.tv_stat_label)).setText("Cầu thủ sở hữu");

        tvStatTotalSquads = cardSquads.findViewById(R.id.tv_stat_value);
        ((TextView) cardSquads.findViewById(R.id.tv_stat_label)).setText("Đội hình đã tạo");

        tvStatMostUsed = cardMost.findViewById(R.id.tv_stat_value);
        ((TextView) cardMost.findViewById(R.id.tv_stat_label)).setText("Hạng thành viên");
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> updateProfile());

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnChangePass.setOnClickListener(v -> showChangePasswordDialog());
    }

    private void loadProfileData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        pbLoading.setVisibility(View.VISIBLE);

        user.getIdToken(false).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                apiService.getProfile("Bearer " + token).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        pbLoading.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            displayData(response.body());
                        } else {
                            Toast.makeText(getContext(), "Lỗi tải hồ sơ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        pbLoading.setVisibility(View.GONE);
                        if(getContext() != null) Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void displayData(User user) {
        etEmail.setText(user.getEmail());
        etDisplayName.setText(user.getDisplayName());

        tvStatTotalPlayers.setText(String.valueOf(user.getPlayerCount()));
        tvStatTotalSquads.setText(String.valueOf(user.getSquadCount()));
        tvStatMostUsed.setText(user.getRole().toUpperCase());
    }

    private void updateProfile() {
        String newName = String.valueOf(etDisplayName.getText()).trim();
        if (newName.isEmpty()) {
            Toast.makeText(getContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        pbLoading.setVisibility(View.VISIBLE);

        user.getIdToken(false).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                apiService.updateProfile("Bearer " + token, new UpdateProfileRequest(newName)).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        pbLoading.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        pbLoading.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showChangePasswordDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);
        dialog.setContentView(view);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        EditText etOldPass = view.findViewById(R.id.et_old_pass);
        EditText etNewPass = view.findViewById(R.id.et_new_pass);
        EditText etConfirmPass = view.findViewById(R.id.et_confirm_new_pass);
        Button btnCancel = view.findViewById(R.id.btn_cancel_change_pass);
        Button btnSave = view.findViewById(R.id.btn_save_change_pass);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String oldPass = etOldPass.getText().toString();
            String newPass = etNewPass.getText().toString();
            String confirmPass = etConfirmPass.getText().toString();

            if (oldPass.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newPass.length() < 6) {
                Toast.makeText(getContext(), "Mật khẩu mới quá ngắn", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPass.equals(confirmPass)) {
                Toast.makeText(getContext(), "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            performChangePassword(oldPass, newPass, dialog);
        });

        dialog.show();
    }

    private void performChangePassword(String oldPass, String newPass, Dialog dialog) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPass).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Lỗi đổi mật khẩu", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}