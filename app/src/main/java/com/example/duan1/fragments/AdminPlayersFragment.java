package com.example.duan1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.R;
import com.example.duan1.activities.AdminPlayerEditActivity;
import com.example.duan1.adapters.PlayerAdapter;
import com.example.duan1.models.Player;
import com.example.duan1.services.ApiClient;
import com.example.duan1.services.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPlayersFragment extends Fragment {

    private EditText etSearch;
    private RecyclerView rvPlayers;
    private FloatingActionButton fabAdd;
    private PlayerAdapter adapter;

    private ApiService apiService;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_players, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        apiService = ApiClient.getApiService();

        etSearch = view.findViewById(R.id.et_admin_player_search);
        rvPlayers = view.findViewById(R.id.rv_admin_players);
        fabAdd = view.findViewById(R.id.fab_add_new_player);

        setupRecyclerView();
        setupListeners();

        loadPlayers("");
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPlayers(etSearch.getText().toString());
    }

    private void setupRecyclerView() {
        rvPlayers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlayerAdapter(getContext(), player -> {
            Intent intent = new Intent(getContext(), AdminPlayerEditActivity.class);
            intent.putExtra("player_id", player.getId());
            intent.putExtra("player_data", player);
            startActivity(intent);
        });

        rvPlayers.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AdminPlayerEditActivity.class);
            startActivity(intent);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadPlayers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadPlayers(String query) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        user.getIdToken(false).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                apiService.getPlayers("Bearer " + token, query).enqueue(new Callback<List<Player>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Player>> call, @NonNull Response<List<Player>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            adapter.setData(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Player>> call, @NonNull Throwable t) {
                        if(getContext() != null)
                            Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}