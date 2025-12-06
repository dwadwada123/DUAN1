package com.example.duan1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.R;
import com.example.duan1.activities.PlayerDetailActivity;
import com.example.duan1.adapters.PlayerAdapter;
import com.example.duan1.models.Player;
import com.example.duan1.services.ApiClient;
import com.example.duan1.services.ApiService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private EditText etSearch;
    private Button btnFilterPlayer, btnFilterTeam, btnSearch;
    private RecyclerView rvSearchResults;
    private ProgressBar pbLoading;
    private LinearLayout layoutEmptyState;
    private TextView tvEmptyMessage;

    private boolean isSearchByPlayer = true;
    private PlayerAdapter adapter;
    private ApiService apiService;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        apiService = ApiClient.getApiService();

        initViews(view);
        setupRecyclerView();
        setupListeners();
        updateFilterUI();

        performSearch("");
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.et_search);
        btnFilterPlayer = view.findViewById(R.id.btn_filter_player);
        btnFilterTeam = view.findViewById(R.id.btn_filter_team);
        btnSearch = view.findViewById(R.id.btn_search);
        rvSearchResults = view.findViewById(R.id.rv_search_results);
        pbLoading = view.findViewById(R.id.pb_loading);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        tvEmptyMessage = view.findViewById(R.id.tv_empty_message);
    }

    private void setupRecyclerView() {
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlayerAdapter(getContext(), this::onPlayerClick);
        rvSearchResults.setAdapter(adapter);
    }

    private void setupListeners() {
        btnFilterPlayer.setOnClickListener(v -> {
            isSearchByPlayer = true;
            updateFilterUI();
        });

        btnFilterTeam.setOnClickListener(v -> {
            isSearchByPlayer = false;
            updateFilterUI();
        });

        btnSearch.setOnClickListener(v -> handleSearch());

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                handleSearch();
                return true;
            }
            return false;
        });
    }

    private void handleSearch() {
        String query = etSearch.getText().toString().trim();
        performSearch(query);
    }

    private void performSearch(String query) {
        showLoading(true);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            showLoading(false);
            return;
        }

        user.getIdToken(false).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String token = task.getResult().getToken();
                callSearchApi(token, query);
            } else {
                showLoading(false);
                Toast.makeText(getContext(), "Lỗi xác thực Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callSearchApi(String token, String query) {
        apiService.getPlayers("Bearer " + token, query).enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(@NonNull Call<List<Player>> call, @NonNull Response<List<Player>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Player> players = response.body();
                    if (players.isEmpty()) {
                        showEmptyState("Không tìm thấy kết quả nào.");
                    } else {
                        showResults(players);
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Player>> call, @NonNull Throwable t) {
                showLoading(false);
                Log.e("SEARCH_ERROR", "Error: " + t.getMessage());
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateFilterUI() {
        if (isSearchByPlayer) {
            btnFilterPlayer.setBackgroundResource(R.drawable.button_active_background);
            btnFilterPlayer.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

            btnFilterTeam.setBackgroundResource(R.drawable.button_inactive_background);
            btnFilterTeam.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_text));

            etSearch.setHint("Nhập tên cầu thủ...");
        } else {
            btnFilterTeam.setBackgroundResource(R.drawable.button_active_background);
            btnFilterTeam.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

            btnFilterPlayer.setBackgroundResource(R.drawable.button_inactive_background);
            btnFilterPlayer.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_text));

            etSearch.setHint("Nhập tên đội bóng...");
        }
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            pbLoading.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.GONE);
        } else {
            pbLoading.setVisibility(View.GONE);
        }
    }

    private void showEmptyState(String message) {
        rvSearchResults.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);
        tvEmptyMessage.setText(message);
    }

    private void showResults(List<Player> players) {
        layoutEmptyState.setVisibility(View.GONE);
        rvSearchResults.setVisibility(View.VISIBLE);
        adapter.setData(players);
    }

    private void onPlayerClick(Player player) {
        Intent intent = new Intent(getContext(), PlayerDetailActivity.class);
        intent.putExtra("player_data", player);
        startActivity(intent);
    }
}