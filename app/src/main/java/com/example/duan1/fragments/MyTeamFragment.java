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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.R;
import com.example.duan1.activities.TacticsBoardActivity;
import com.example.duan1.adapters.SquadAdapter;
import com.example.duan1.dialogs.CreateSquadDialog;
import com.example.duan1.models.Squad;
import com.example.duan1.services.ApiClient;
import com.example.duan1.services.ApiService;
import com.example.duan1.services.SquadRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTeamFragment extends Fragment {

    private RecyclerView rvSquads;
    private LinearLayout layoutEmpty;
    private FloatingActionButton fabCreate;
    private ProgressBar pbLoading;

    private SquadAdapter adapter;
    private List<Squad> squadList = new ArrayList<>();
    private ApiService apiService;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_team, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        apiService = ApiClient.getApiService();

        rvSquads = view.findViewById(R.id.rv_squads);
        layoutEmpty = view.findViewById(R.id.layout_empty_state);
        fabCreate = view.findViewById(R.id.fab_create_squad);
        pbLoading = view.findViewById(R.id.pb_loading);

        setupRecyclerView();

        fabCreate.setOnClickListener(v -> showCreateDialog());

        loadSquads();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSquads();
    }

    private void setupRecyclerView() {
        rvSquads.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SquadAdapter(getContext(), squadList, new SquadAdapter.OnSquadActionListener() {
            @Override
            public void onSquadClick(Squad squad) {
                Intent intent = new Intent(getContext(), TacticsBoardActivity.class);
                intent.putExtra("squad_id", squad.getId());
                startActivity(intent);
            }

            @Override
            public void onSquadDelete(Squad squad) {
                showDeleteWarningDialog(squad);
            }
        });

        rvSquads.setAdapter(adapter);
    }

    private void showDeleteWarningDialog(Squad squad) {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_dismiss_warning, null);
        dialog.setContentView(view);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvTitle = view.findViewById(R.id.tv_warning_title);
        TextView tvMsg = view.findViewById(R.id.tv_warning_message);
        Button btnCancel = view.findViewById(R.id.btn_cancel_dismiss);
        Button btnConfirm = view.findViewById(R.id.btn_confirm_dismiss);

        tvTitle.setText("Xóa Đội hình");
        tvMsg.setText("Bạn có chắc chắn muốn xóa đội hình \"" + squad.getSquadName() + "\" không?");

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            deleteSquad(squad);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void deleteSquad(Squad squad) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        pbLoading.setVisibility(View.VISIBLE);

        user.getIdToken(false).addOnCompleteListener(task -> {
            String token = task.getResult().getToken();
            apiService.deleteSquad("Bearer " + token, squad.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    pbLoading.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Đã xóa đội hình", Toast.LENGTH_SHORT).show();
                        loadSquads();
                    } else {
                        Toast.makeText(getContext(), "Lỗi xóa: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadSquads() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        pbLoading.setVisibility(View.VISIBLE);

        user.getIdToken(false).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                apiService.getUserSquads("Bearer " + token).enqueue(new Callback<List<Squad>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Squad>> call, @NonNull Response<List<Squad>> response) {
                        pbLoading.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            squadList = response.body();
                            updateUI();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<Squad>> call, @NonNull Throwable t) {
                        pbLoading.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void showCreateDialog() {
        CreateSquadDialog.show(getContext(), (name, formation) -> createNewSquad(name, formation));
    }

    private void createNewSquad(String name, String formation) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        pbLoading.setVisibility(View.VISIBLE);

        user.getIdToken(false).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                SquadRequest request = new SquadRequest(name, formation);

                apiService.createSquad("Bearer " + token, request).enqueue(new Callback<Squad>() {
                    @Override
                    public void onResponse(@NonNull Call<Squad> call, @NonNull Response<Squad> response) {
                        pbLoading.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Tạo thành công", Toast.LENGTH_SHORT).show();
                            loadSquads();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<Squad> call, @NonNull Throwable t) {
                        pbLoading.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void updateUI() {
        if (squadList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvSquads.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvSquads.setVisibility(View.VISIBLE);
            adapter.setData(squadList);
        }
    }
}