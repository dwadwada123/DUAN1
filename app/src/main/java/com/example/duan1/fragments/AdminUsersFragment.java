package com.example.duan1.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.R;
import com.example.duan1.adapters.UserAdapter;
import com.example.duan1.models.User;
import com.example.duan1.services.ApiClient;
import com.example.duan1.services.ApiService;
import com.example.duan1.services.DeleteUserRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUsersFragment extends Fragment {

    private RecyclerView rvUsers;
    private UserAdapter adapter;
    private EditText etSearch;
    private List<User> allUsers = new ArrayList<>();

    private ApiService apiService;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        apiService = ApiClient.getApiService();

        etSearch = view.findViewById(R.id.et_search_user);
        rvUsers = view.findViewById(R.id.rv_user_list);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new UserAdapter(getContext(), new ArrayList<>(), this::showDeleteReasonDialog);
        rvUsers.setAdapter(adapter);

        loadUsers();
    }

    private void loadUsers() {
        FirebaseUser admin = mAuth.getCurrentUser();
        if (admin == null) return;

        admin.getIdToken(false).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                apiService.getAllUsers("Bearer " + token).enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            allUsers = response.body();
                            adapter.setData(allUsers);
                        } else {
                            if (response.code() == 403) {
                                Toast.makeText(getContext(), "Bạn không phải Admin", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                        if(getContext() != null)
                            Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showDeleteReasonDialog(User user) {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_reason, null);
        dialog.setContentView(view);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        EditText etReason = view.findViewById(R.id.et_deletion_reason);
        Button btnCancel = view.findViewById(R.id.btn_cancel_deletion);
        Button btnConfirm = view.findViewById(R.id.btn_confirm_deletion);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String reason = etReason.getText().toString().trim();
            if (reason.isEmpty()) {
                etReason.setError("Vui lòng nhập lý do");
                return;
            }
            deleteUser(user, reason);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void deleteUser(User user, String reason) {
        FirebaseUser admin = mAuth.getCurrentUser();
        if (admin == null) return;

        admin.getIdToken(false).addOnCompleteListener(task -> {
            String token = task.getResult().getToken();
            DeleteUserRequest request = new DeleteUserRequest(reason);

            apiService.deleteUser("Bearer " + token, user.getUserId(), request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Đã xóa User thành công", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    } else {
                        Toast.makeText(getContext(), "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}