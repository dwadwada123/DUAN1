package com.example.duan1.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.duan1.R;
import com.example.duan1.models.Player;
import com.example.duan1.services.ApiClient;
import com.example.duan1.services.ApiService;
import com.example.duan1.services.PlayerRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPlayerEditActivity extends AppCompatActivity {

    private TextInputEditText etName, etHeight, etWeight, etDesc, etJerseyNumber;
    private Spinner spTeam, spPosition, spNationality;
    private MaterialButton btnSave, btnDelete, btnSelectImage;
    private TextView tvTitle;
    private ImageView ivAvatarPreview;

    private ApiService apiService;
    private FirebaseAuth mAuth;

    private String currentPlayerId = null;
    private Player currentPlayer = null;
    private String currentImageUrl = "https://www.thesportsdb.com/images/media/player/thumb/placeholder.jpg";

    private List<String> teamList = new ArrayList<>();
    private List<String> nationList = new ArrayList<>();
    private List<String> positionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_player_edit);

        mAuth = FirebaseAuth.getInstance();
        apiService = ApiClient.getApiService();

        setupToolbar();
        initViews();

        loadSpinnerData();
    }

    private void loadSpinnerData() {
        apiService.getTeams().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    teamList = response.body();
                    setupSpinnerAdapter(spTeam, teamList);
                    checkIfAllDataLoaded();
                }
            }
            @Override public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {}
        });

        apiService.getNations().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    nationList = response.body();
                    setupSpinnerAdapter(spNationality, nationList);
                    checkIfAllDataLoaded();
                }
            }
            @Override public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {}
        });

        apiService.getPositions().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    positionList = response.body();
                    setupSpinnerAdapter(spPosition, positionList);
                    checkIfAllDataLoaded();
                }
            }
            @Override public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {}
        });
    }

    private void setupSpinnerAdapter(Spinner spinner, List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void checkIfAllDataLoaded() {
        if (!teamList.isEmpty() && !nationList.isEmpty() && !positionList.isEmpty()) {
            processIntentData();
        }
    }

    private void processIntentData() {
        if (getIntent().hasExtra("player_id")) {
            currentPlayerId = getIntent().getStringExtra("player_id");
            currentPlayer = (Player) getIntent().getSerializableExtra("player_data");
            setupEditMode();
        } else {
            setupCreateMode();
        }

        btnSave.setOnClickListener(v -> handleSave());
        btnDelete.setOnClickListener(v -> confirmDelete());
        btnSelectImage.setOnClickListener(v -> showImageInputDialog());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_form_title);
        etName = findViewById(R.id.et_player_name);
        etHeight = findViewById(R.id.et_height);
        etWeight = findViewById(R.id.et_weight);
        etJerseyNumber = findViewById(R.id.et_jersey_number);
        etDesc = findViewById(R.id.et_description);
        ivAvatarPreview = findViewById(R.id.iv_player_avatar_edit);

        spTeam = findViewById(R.id.spinner_team);
        spPosition = findViewById(R.id.spinner_position);
        spNationality = findViewById(R.id.spinner_nationality);

        btnSave = findViewById(R.id.btn_save_player);
        btnDelete = findViewById(R.id.btn_delete_player);
        btnSelectImage = findViewById(R.id.btn_select_image);
    }

    private void setupEditMode() {
        tvTitle.setText("Chỉnh sửa Cầu thủ");
        btnDelete.setVisibility(View.VISIBLE);

        if (currentPlayer != null) {
            etName.setText(currentPlayer.getName());
            etHeight.setText(currentPlayer.getHeight());
            etWeight.setText(currentPlayer.getWeight());
            etDesc.setText(currentPlayer.getDescription());
            etJerseyNumber.setText(String.valueOf(currentPlayer.getJerseyNumber()));

            if (currentPlayer.getImage() != null && !currentPlayer.getImage().isEmpty()) {
                currentImageUrl = currentPlayer.getImage();
                Glide.with(this).load(currentImageUrl).into(ivAvatarPreview);
            }

            setSpinnerSelection(spTeam, currentPlayer.getTeam());
            setSpinnerSelection(spPosition, currentPlayer.getPosition());
            setSpinnerSelection(spNationality, currentPlayer.getNationality());
        }
    }

    private void setupCreateMode() {
        tvTitle.setText("Thêm Cầu thủ Mới");
        btnDelete.setVisibility(View.GONE);
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null) return;
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (adapter == null) return;

        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void showImageInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập đường dẫn ảnh (URL)");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        input.setText(currentImageUrl);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String url = input.getText().toString().trim();
            if (!url.isEmpty()) {
                currentImageUrl = url;
                Glide.with(this).load(currentImageUrl).placeholder(R.drawable.ic_placeholder_player).into(ivAvatarPreview);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void handleSave() {
        String name = Objects.requireNonNull(etName.getText()).toString().trim();

        if (name.isEmpty()) {
            etName.setError("Tên không được để trống");
            return;
        }

        if (spTeam.getSelectedItem() == null) {
            Toast.makeText(this, "Đang tải dữ liệu...", Toast.LENGTH_SHORT).show();
            return;
        }

        String team = spTeam.getSelectedItem().toString();
        String position = spPosition.getSelectedItem().toString();
        String nationality = spNationality.getSelectedItem().toString();
        String height = Objects.requireNonNull(etHeight.getText()).toString();
        String weight = Objects.requireNonNull(etWeight.getText()).toString();
        String desc = Objects.requireNonNull(etDesc.getText()).toString();

        int jerseyNum = 0;
        try {
            jerseyNum = Integer.parseInt(Objects.requireNonNull(etJerseyNumber.getText()).toString());
        } catch (NumberFormatException e) {
            jerseyNum = 0;
        }

        PlayerRequest request = new PlayerRequest(name, team, position, nationality, height, weight, desc, currentImageUrl, jerseyNum);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        user.getIdToken(false).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                if (currentPlayerId == null) {
                    createPlayer(token, request);
                } else {
                    updatePlayer(token, currentPlayerId, request);
                }
            }
        });
    }

    private void createPlayer(String token, PlayerRequest request) {
        apiService.createPlayer("Bearer " + token, request).enqueue(new Callback<Player>() {
            @Override
            public void onResponse(@NonNull Call<Player> call, @NonNull Response<Player> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminPlayerEditActivity.this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AdminPlayerEditActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(@NonNull Call<Player> call, @NonNull Throwable t) {
                Toast.makeText(AdminPlayerEditActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePlayer(String token, String id, PlayerRequest request) {
        apiService.updatePlayer("Bearer " + token, id, request).enqueue(new Callback<Player>() {
            @Override
            public void onResponse(@NonNull Call<Player> call, @NonNull Response<Player> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminPlayerEditActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AdminPlayerEditActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(@NonNull Call<Player> call, @NonNull Throwable t) {
                Toast.makeText(AdminPlayerEditActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa Cầu thủ")
                .setMessage("Bạn có chắc chắn muốn xóa cầu thủ này khỏi hệ thống?")
                .setPositiveButton("Xóa", (dialog, which) -> deletePlayer())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deletePlayer() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        user.getIdToken(false).addOnCompleteListener(task -> {
            String token = task.getResult().getToken();
            apiService.deletePlayerSoft("Bearer " + token, currentPlayerId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminPlayerEditActivity.this, "Đã xóa cầu thủ", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AdminPlayerEditActivity.this, "Lỗi xóa: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(AdminPlayerEditActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}