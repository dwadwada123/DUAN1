package com.example.duan1.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.example.duan1.services.RecruitRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerDetailActivity extends AppCompatActivity {

    private ImageView ivPlayerImage, ivTeamLogo, ivFlag;
    private TextView tvName, tvTeam, tvPosition, tvNationality, tvNumber, tvHeight, tvWeight, tvDesc;
    private Button btnRecruit;
    private ProgressBar pbLoading;

    private Player currentPlayer;
    private ApiService apiService;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);

        mAuth = FirebaseAuth.getInstance();
        apiService = ApiClient.getApiService();

        setupToolbar();
        initViews();

        if (getIntent().hasExtra("player_data")) {
            currentPlayer = (Player) getIntent().getSerializableExtra("player_data");
            displayData();
        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu cầu thủ", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnRecruit.setOnClickListener(v -> handleRecruitPlayer());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void initViews() {
        ivPlayerImage = findViewById(R.id.iv_player_image);
        ivTeamLogo = findViewById(R.id.iv_team_logo);
        ivFlag = findViewById(R.id.iv_nationality_flag);
        tvName = findViewById(R.id.tv_player_name);
        tvTeam = findViewById(R.id.tv_player_team);
        tvPosition = findViewById(R.id.tv_detail_position);
        tvNationality = findViewById(R.id.tv_detail_nationality);
        tvNumber = findViewById(R.id.tv_detail_jersey);
        tvHeight = findViewById(R.id.tv_detail_height);
        tvWeight = findViewById(R.id.tv_detail_weight);
        tvDesc = findViewById(R.id.tv_player_description);
        btnRecruit = findViewById(R.id.btn_recruit_player);
        pbLoading = findViewById(R.id.pb_loading);
    }

    private void displayData() {
        if (currentPlayer == null) return;

        tvName.setText(currentPlayer.getName());
        tvTeam.setText(currentPlayer.getTeam());
        tvPosition.setText(currentPlayer.getPosition());
        tvNationality.setText(currentPlayer.getNationality());
        if (currentPlayer.getJerseyNumber() > 0) {
            tvNumber.setText(String.valueOf(currentPlayer.getJerseyNumber()));
        } else {
            tvNumber.setText("-");
        }

        tvHeight.setText(currentPlayer.getHeight() != null ? currentPlayer.getHeight() : "N/A");
        tvWeight.setText(currentPlayer.getWeight() != null ? currentPlayer.getWeight() : "N/A");
        tvDesc.setText(currentPlayer.getDescription() != null ? currentPlayer.getDescription() : "Chưa có tiểu sử.");
        
        Glide.with(this)
                .load(currentPlayer.getImage())
                .placeholder(R.drawable.ic_football_with_trophy)
                .error(R.drawable.ic_football_with_trophy)
                .into(ivPlayerImage);

        ivTeamLogo.setImageResource(R.drawable.ic_team_logo_example);
        ivFlag.setImageResource(R.drawable.ic_flag_example);
    }

    private void handleRecruitPlayer() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        user.getIdToken(false).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String token = task.getResult().getToken();
                callRecruitApi(token);
            } else {
                setLoading(false);
                Toast.makeText(this, "Lỗi xác thực", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callRecruitApi(String token) {
        RecruitRequest request = new RecruitRequest(currentPlayer.getId());

        apiService.recruitPlayer("Bearer " + token, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(PlayerDetailActivity.this, "Chiêu mộ thành công!", Toast.LENGTH_LONG).show();
                    btnRecruit.setEnabled(false);
                    btnRecruit.setText("Đã sở hữu");
                    btnRecruit.setBackgroundColor(getColor(R.color.gray_icon));
                } else {
                    if (response.code() == 400) {
                        Toast.makeText(PlayerDetailActivity.this, "Bạn đã sở hữu cầu thủ này rồi!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlayerDetailActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(PlayerDetailActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            pbLoading.setVisibility(View.VISIBLE);
            btnRecruit.setVisibility(View.INVISIBLE);
        } else {
            pbLoading.setVisibility(View.GONE);
            btnRecruit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}