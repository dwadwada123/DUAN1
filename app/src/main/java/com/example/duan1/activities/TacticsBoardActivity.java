package com.example.duan1.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.duan1.R;
import com.example.duan1.adapters.PlayerAdapter;
import com.example.duan1.models.Player;
import com.example.duan1.models.Squad;
import com.example.duan1.models.SquadItem;
import com.example.duan1.services.ApiClient;
import com.example.duan1.services.ApiService;
import com.example.duan1.services.SquadUpdateRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TacticsBoardActivity extends AppCompatActivity {

    private final int[] SLOT_IDS = {
            R.id.slot_0, R.id.slot_1, R.id.slot_2, R.id.slot_3, R.id.slot_4,
            R.id.slot_5, R.id.slot_6, R.id.slot_7, R.id.slot_8, R.id.slot_9, R.id.slot_10
    };

    private Spinner spinnerFormation;
    private String currentSquadId;
    private Squad currentSquad;
    private Map<Integer, Player> positionsMap = new HashMap<>();

    private ApiService apiService;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tactics_board);

        mAuth = FirebaseAuth.getInstance();
        apiService = ApiClient.getApiService();

        spinnerFormation = findViewById(R.id.spinner_formation);

        if (getIntent().hasExtra("squad_id")) {
            currentSquadId = getIntent().getStringExtra("squad_id");
            loadSquadDetail();
        } else {
            Toast.makeText(this, "Thiếu ID đội hình", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupSlotClickListeners();

        findViewById(R.id.btn_save_squad).setOnClickListener(v -> saveSquadChanges());
    }

    private void setupSlotClickListeners() {
        for (int i = 0; i < SLOT_IDS.length; i++) {
            int slotIndex = i;
            View slotView = findViewById(SLOT_IDS[i]);
            TextView tvPos = slotView.findViewById(R.id.tv_position_label);
            if (i == 0) tvPos.setText("GK");
            else tvPos.setText("P" + i);

            slotView.setOnClickListener(v -> showInventorySheet(slotIndex));
        }
    }

    private void loadSquadDetail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        user.getIdToken(false).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                apiService.getSquadDetail("Bearer " + token, currentSquadId).enqueue(new Callback<Squad>() {
                    @Override
                    public void onResponse(@NonNull Call<Squad> call, @NonNull Response<Squad> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            currentSquad = response.body();
                            renderSquadOnBoard();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Squad> call, @NonNull Throwable t) {
                        Toast.makeText(TacticsBoardActivity.this, "Lỗi tải đội hình", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void renderSquadOnBoard() {
        positionsMap.clear();
        if (currentSquad.getItems() != null) {
            for (SquadItem item : currentSquad.getItems()) {
                if (item.getPlayer() != null) {
                    positionsMap.put(item.getSlotIndex(), item.getPlayer());
                    updateSlotUI(item.getSlotIndex(), item.getPlayer());
                }
            }
        }
    }

    private void showInventorySheet(int targetSlotIndex) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_select_player, null);
        dialog.setContentView(view);

        RecyclerView rvPlayers = view.findViewById(R.id.rv_available_players);
        rvPlayers.setLayoutManager(new LinearLayoutManager(this));

        loadInventoryForSheet(rvPlayers, dialog, targetSlotIndex);

        dialog.show();
    }

    private void loadInventoryForSheet(RecyclerView recyclerView, Dialog dialog, int targetSlotIndex) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        user.getIdToken(false).addOnCompleteListener(task -> {
            String token = task.getResult().getToken();
            apiService.getMyInventory("Bearer " + token).enqueue(new Callback<List<Player>>() {
                @Override
                public void onResponse(@NonNull Call<List<Player>> call, @NonNull Response<List<Player>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Player> inventory = response.body();

                        PlayerAdapter adapter = new PlayerAdapter(TacticsBoardActivity.this, selectedPlayer -> {
                            checkAndPlacePlayer(targetSlotIndex, selectedPlayer, dialog);
                        });
                        adapter.setData(inventory);
                        recyclerView.setAdapter(adapter);
                    }
                }
                @Override
                public void onFailure(@NonNull Call<List<Player>> call, @NonNull Throwable t) { }
            });
        });
    }

    private void checkAndPlacePlayer(int targetSlotIndex, Player selectedPlayer, Dialog sheetDialog) {
        Integer existingSlotIndex = null;
        for (Map.Entry<Integer, Player> entry : positionsMap.entrySet()) {
            if (entry.getValue().getId().equals(selectedPlayer.getId())) {
                existingSlotIndex = entry.getKey();
                break;
            }
        }

        if (existingSlotIndex != null) {
            if (existingSlotIndex == targetSlotIndex) {
                sheetDialog.dismiss();
                return;
            }

            int oldSlot = existingSlotIndex;
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận đổi vị trí")
                    .setMessage(selectedPlayer.getName() + " đang ở vị trí khác. Bạn có muốn chuyển cầu thủ này sang vị trí mới không?")
                    .setPositiveButton("Đổi", (dialog, which) -> {
                        removePlayerFromSlot(oldSlot);
                        placePlayerToSlot(targetSlotIndex, selectedPlayer);
                        sheetDialog.dismiss();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        } else {
            placePlayerToSlot(targetSlotIndex, selectedPlayer);
            sheetDialog.dismiss();
        }
    }

    private void placePlayerToSlot(int slotIndex, Player player) {
        positionsMap.put(slotIndex, player);
        updateSlotUI(slotIndex, player);
    }

    private void removePlayerFromSlot(int slotIndex) {
        positionsMap.remove(slotIndex);
        clearSlotUI(slotIndex);
    }

    private void updateSlotUI(int slotIndex, Player player) {
        View slotView = findViewById(SLOT_IDS[slotIndex]);
        ImageView ivAvatar = slotView.findViewById(R.id.iv_player_avatar);
        TextView tvName = slotView.findViewById(R.id.tv_player_name);

        tvName.setText(player.getName());
        Glide.with(this).load(player.getImage()).into(ivAvatar);
    }

    private void clearSlotUI(int slotIndex) {
        View slotView = findViewById(SLOT_IDS[slotIndex]);
        ImageView ivAvatar = slotView.findViewById(R.id.iv_player_avatar);
        TextView tvName = slotView.findViewById(R.id.tv_player_name);
        tvName.setText("Trống");
        ivAvatar.setImageResource(R.drawable.shape_circle_gray);
    }

    private void saveSquadChanges() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        user.getIdToken(false).addOnCompleteListener(task -> {
            String token = task.getResult().getToken();

            List<SquadUpdateRequest.SquadItemRequest> items = new ArrayList<>();
            for (Map.Entry<Integer, Player> entry : positionsMap.entrySet()) {
                items.add(new SquadUpdateRequest.SquadItemRequest(entry.getKey(), entry.getValue().getId(), ""));
            }

            String formation = spinnerFormation.getSelectedItem().toString();
            String name = currentSquad != null ? currentSquad.getSquadName() : "My Squad";

            SquadUpdateRequest request = new SquadUpdateRequest(name, formation, items);

            apiService.updateSquad("Bearer " + token, currentSquadId, request).enqueue(new Callback<Squad>() {
                @Override
                public void onResponse(@NonNull Call<Squad> call, @NonNull Response<Squad> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(TacticsBoardActivity.this, "Đã lưu đội hình!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(TacticsBoardActivity.this, "Lỗi lưu: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Squad> call, @NonNull Throwable t) {
                    Toast.makeText(TacticsBoardActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}