package com.example.duan1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.R;
import com.example.duan1.adapters.PlayerAdapter;
import com.example.duan1.dialogs.PlayerInfoDialog;
import com.example.duan1.models.Player;
import com.example.duan1.services.ApiClient;
import com.example.duan1.services.ApiService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerInventoryFragment extends Fragment {

    private RecyclerView rvMyPlayers;
    private LinearLayout layoutEmpty;
    private ProgressBar pbLoading;
    private PlayerAdapter adapter;

    private ApiService apiService;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        apiService = ApiClient.getApiService();

        rvMyPlayers = view.findViewById(R.id.rv_my_players);
        layoutEmpty = view.findViewById(R.id.layout_empty_state);
        pbLoading = view.findViewById(R.id.pb_loading);

        setupRecyclerView();
        loadInventory();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadInventory();
    }

    private void setupRecyclerView() {
        rvMyPlayers.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PlayerAdapter(getContext(), player -> {
            PlayerInfoDialog.show(getContext(), player, playerToDelete -> {
                requestDismissPlayer(playerToDelete);
            });
        });

        rvMyPlayers.setAdapter(adapter);
    }

    private void loadInventory() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        showLoading(true);

        user.getIdToken(false).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String token = task.getResult().getToken();
                callGetInventoryApi(token);
            } else {
                showLoading(false);
                Toast.makeText(getContext(), "Lỗi xác thực", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callGetInventoryApi(String token) {
        apiService.getMyInventory("Bearer " + token).enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(@NonNull Call<List<Player>> call, @NonNull Response<List<Player>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Player> players = response.body();
                    updateUI(players);
                } else {
                    Toast.makeText(getContext(), "Lỗi tải kho: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Player>> call, @NonNull Throwable t) {
                showLoading(false);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestDismissPlayer(Player player) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        Toast.makeText(getContext(), "Đang sa thải...", Toast.LENGTH_SHORT).show();

        user.getIdToken(false).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();

                apiService.dismissPlayer("Bearer " + token, player.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Đã sa thải " + player.getName(), Toast.LENGTH_SHORT).show();
                            loadInventory();
                        } else {
                            Toast.makeText(getContext(), "Lỗi sa thải: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void updateUI(List<Player> players) {
        if (players.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvMyPlayers.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvMyPlayers.setVisibility(View.VISIBLE);
            adapter.setData(players);
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            pbLoading.setVisibility(View.VISIBLE);
            rvMyPlayers.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
        } else {
            pbLoading.setVisibility(View.GONE);
        }
    }
}