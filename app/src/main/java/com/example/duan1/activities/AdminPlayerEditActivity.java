package com.example.duan1.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.duan1.R;
import com.example.duan1.models.Player;
import com.example.duan1.models.UploadResponse;
import com.example.duan1.services.ApiClient;
import com.example.duan1.services.ApiService;
import com.example.duan1.services.PlayerRequest;
import com.example.duan1.utils.FileUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPlayerEditActivity extends AppCompatActivity {

    private TextInputEditText etName, etHeight, etWeight, etDesc, etJerseyNumber;
    private AutoCompleteTextView actTeam, actPosition, actNationality;
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

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    uploadImageToLocalServer(uri);
                }
            }
    );

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

    private void uploadImageToLocalServer(Uri imageUri) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải ảnh lên Server...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        File file = FileUtils.getFileFromUri(this, imageUri);
        if (file == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Lỗi đọc file ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        apiService.uploadImage(body).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(@NonNull Call<UploadResponse> call, @NonNull Response<UploadResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    currentImageUrl = response.body().getUrl();

                    Glide.with(AdminPlayerEditActivity.this)
                            .load(currentImageUrl)
                            .placeholder(R.drawable.ic_placeholder_player)
                            .into(ivAvatarPreview);

                    Toast.makeText(AdminPlayerEditActivity.this, "Upload thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminPlayerEditActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UploadResponse> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AdminPlayerEditActivity.this, "Lỗi kết nối upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSpinnerData() {
        apiService.getTeams().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    teamList = response.body();
                    setupAutoComplete(actTeam, teamList);
                    checkIfAllDataLoaded();
                }
            }
            @Override public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {}
        });

        apiService.getNations().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    nationList = response.body();
                    setupAutoComplete(actNationality, nationList);
                    checkIfAllDataLoaded();
                }
            }
            @Override public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {}
        });

        apiService.getPositions().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    positionList = response.body();
                    setupAutoComplete(actPosition, positionList);
                    checkIfAllDataLoaded();
                }
            }
            @Override public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {}
        });
    }

    private void setupAutoComplete(AutoCompleteTextView autoComplete, List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, data);
        autoComplete.setAdapter(adapter);
        autoComplete.setOnClickListener(v -> autoComplete.showDropDown());
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
        btnSelectImage.setOnClickListener(v -> showImageOptionDialog());
    }

    private void showImageOptionDialog() {
        String[] options = {"Chọn từ thư viện", "Nhập link ảnh (URL)"};
        new AlertDialog.Builder(this)
                .setTitle("Ảnh đại diện")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        pickImageLauncher.launch("image/*");
                    } else {
                        showUrlInputDialog();
                    }
                })
                .show();
    }

    private void showUrlInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập URL");
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
        actTeam = findViewById(R.id.act_team);
        actPosition = findViewById(R.id.act_position);
        actNationality = findViewById(R.id.act_nationality);
        btnSave = findViewById(R.id.btn_save_player);
        btnDelete = findViewById(R.id.btn_delete_player);
        btnSelectImage = findViewById(R.id.btn_select_image);
    }

    private void setupEditMode() {
        tvTitle.setText("Chỉnh sửa Cầu thủ");
        btnDelete.setVisibility(android.view.View.VISIBLE);
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
            actTeam.setText(currentPlayer.getTeam(), false);
            actPosition.setText(currentPlayer.getPosition(), false);
            actNationality.setText(currentPlayer.getNationality(), false);
        }
    }

    private void setupCreateMode() {
        tvTitle.setText("Thêm Cầu thủ Mới");
        btnDelete.setVisibility(android.view.View.GONE);
    }

    private void handleSave() {
        String name = Objects.requireNonNull(etName.getText()).toString().trim();
        if (name.isEmpty()) { etName.setError("Trống"); return; }

        String team = actTeam.getText().toString().trim();
        String position = actPosition.getText().toString().trim();
        String nationality = actNationality.getText().toString().trim();
        String height = Objects.requireNonNull(etHeight.getText()).toString();
        String weight = Objects.requireNonNull(etWeight.getText()).toString();
        String desc = Objects.requireNonNull(etDesc.getText()).toString();
        int jerseyNum = 0;
        try { jerseyNum = Integer.parseInt(Objects.requireNonNull(etJerseyNumber.getText()).toString()); } catch (Exception e) {}

        PlayerRequest request = new PlayerRequest(name, team, position, nationality, height, weight, desc, currentImageUrl, jerseyNum);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        user.getIdToken(false).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                if (currentPlayerId == null) createPlayer(token, request);
                else updatePlayer(token, currentPlayerId, request);
            }
        });
    }

    private void createPlayer(String token, PlayerRequest request) {
        apiService.createPlayer("Bearer " + token, request).enqueue(new Callback<Player>() {
            @Override
            public void onResponse(@NonNull Call<Player> call, @NonNull Response<Player> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminPlayerEditActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AdminPlayerEditActivity.this, "Cập nhật xong", Toast.LENGTH_SHORT).show();
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
                .setTitle("Xóa").setMessage("Chắc chắn xóa?")
                .setPositiveButton("Xóa", (dialog, which) -> deletePlayer())
                .setNegativeButton("Hủy", null).show();
    }

    private void deletePlayer() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.getIdToken(false).addOnCompleteListener(task -> {
                apiService.deletePlayerSoft("Bearer " + task.getResult().getToken(), currentPlayerId).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if(response.isSuccessful()) {
                            Toast.makeText(AdminPlayerEditActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                    @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(AdminPlayerEditActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }
}