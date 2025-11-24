package com.example.duan1.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.example.duan1.R;
import com.example.duan1.models.Player;

public class PlayerDetailActivity extends AppCompatActivity {

    private ImageView ivPlayerImage, ivTeamLogo, ivFlag;
    private TextView tvName, tvTeam, tvPosition, tvNationality, tvNumber, tvHeight, tvWeight, tvDesc;
    private Button btnRecruit;
    private Player currentPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Ánh xạ Views
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

        // Nhận dữ liệu từ SearchFragment
        currentPlayer = (Player) getIntent().getSerializableExtra("player_data");

        if (currentPlayer != null) {
            displayData();
        }

        btnRecruit.setOnClickListener(v -> {
            Toast.makeText(this, "Đã chiêu mộ " + currentPlayer.getName() + " (Demo)", Toast.LENGTH_SHORT).show();
            // TODO: Code lưu vào Firebase sau này
        });
    }

    private void displayData() {
        tvName.setText(currentPlayer.getName());
        tvTeam.setText(currentPlayer.getTeam());
        tvPosition.setText(currentPlayer.getPosition());
        tvNationality.setText(currentPlayer.getNationality());

        // Dữ liệu giả không có số áo nên hardcode hoặc check null
        tvNumber.setText("10");

        tvHeight.setText(currentPlayer.getHeight() != null ? currentPlayer.getHeight() : "N/A");
        tvWeight.setText(currentPlayer.getWeight() != null ? currentPlayer.getWeight() : "N/A");
        tvDesc.setText(currentPlayer.getDescription() != null ? currentPlayer.getDescription() : "Không có thông tin tiểu sử.");

        // Load ảnh cầu thủ
        Glide.with(this)
                .load(currentPlayer.getImage())
                .placeholder(R.drawable.ic_football_with_trophy)
                .into(ivPlayerImage);

        // Load cờ và logo (Tạm thời dùng ảnh mẫu vì dữ liệu giả chưa có link này)
        // Sau này dùng API cờ riêng
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}