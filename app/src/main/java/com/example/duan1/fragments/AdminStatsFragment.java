package com.example.duan1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.duan1.R;
import com.example.duan1.activities.LoginActivity;
import com.example.duan1.services.ApiClient;
import com.example.duan1.services.ApiService;
import com.example.duan1.services.StatsResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminStatsFragment extends Fragment {

    private TextView tvUsersCount, tvSquadsCount;
    private Button btnLogout;

    private ApiService apiService;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        apiService = ApiClient.getApiService();

        btnLogout = view.findViewById(R.id.btn_admin_logout);
        View cardUsers = view.findViewById(R.id.stat_card_users);
        View cardSquads = view.findViewById(R.id.stat_card_squads);

        if (cardUsers != null) {
            ((TextView) cardUsers.findViewById(R.id.tv_stat_label)).setText("Tổng User");
            tvUsersCount = cardUsers.findViewById(R.id.tv_stat_value);
            tvUsersCount.setText("-");
        }

        if (cardSquads != null) {
            ((TextView) cardSquads.findViewById(R.id.tv_stat_label)).setText("Tổng Squad");
            tvSquadsCount = cardSquads.findViewById(R.id.tv_stat_value);
            tvSquadsCount.setText("-");
        }

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        loadStats();
    }

    private void loadStats() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        user.getIdToken(false).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                apiService.getStats("Bearer " + token).enqueue(new Callback<StatsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<StatsResponse> call, @NonNull Response<StatsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            StatsResponse stats = response.body();
                            if (tvUsersCount != null)
                                tvUsersCount.setText(String.valueOf(stats.getUserCount()));
                            if (tvSquadsCount != null)
                                tvSquadsCount.setText(String.valueOf(stats.getSquadCount()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<StatsResponse> call, @NonNull Throwable t) {
                        if (getContext() != null)
                            Toast.makeText(getContext(), "Lỗi tải thống kê", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}