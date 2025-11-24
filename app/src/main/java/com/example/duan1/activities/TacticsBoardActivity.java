package com.example.duan1.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.duan1.R;
import com.example.duan1.adapters.PlayerAdapter;
import com.example.duan1.models.Player;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class TacticsBoardActivity extends AppCompatActivity {
    private final int[] SLOT_IDS = {
            R.id.slot_0, R.id.slot_1, R.id.slot_2, R.id.slot_3, R.id.slot_4,
            R.id.slot_5, R.id.slot_6, R.id.slot_7, R.id.slot_8, R.id.slot_9, R.id.slot_10
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tactics_board);

        for (int i = 0; i < SLOT_IDS.length; i++) {
            int slotIndex = i;
            View slotView = findViewById(SLOT_IDS[i]);
            TextView tvPos = slotView.findViewById(R.id.tv_position_label);
            if (i == 0) tvPos.setText("GK");
            else if (i <= 4) tvPos.setText("DEF");
            else if (i <= 7) tvPos.setText("MID");
            else tvPos.setText("ATT");

            slotView.setOnClickListener(v -> showSelectPlayerSheet(slotIndex));
        }

        findViewById(R.id.btn_save_squad).setOnClickListener(v -> {
            Toast.makeText(this, "Đã lưu đội hình thành công!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showSelectPlayerSheet(int slotIndex) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_select_player, null);
        dialog.setContentView(view);

        RecyclerView rvPlayers = view.findViewById(R.id.rv_available_players);
        rvPlayers.setLayoutManager(new LinearLayoutManager(this));

        List<Player> dummyPickList = new ArrayList<>();
        Player p1 = new Player();
        p1.setName("Messi");
        p1.setThumbUrl("https://www.thesportsdb.com/images/media/player/thumb/rscppl1516206339.jpg");
        p1.setTeam("Inter Miami");
        p1.setPosition("Forward");
        Player p2 = new Player();
        p2.setName("Ronaldo");
        p2.setThumbUrl("https://www.thesportsdb.com/images/media/player/thumb/h031l41593338320.jpg");
        p2.setTeam("Al Nassr");
        p2.setPosition("Forward");
        dummyPickList.add(p1);
        dummyPickList.add(p2);
        PlayerAdapter adapter = new PlayerAdapter(this, selectedPlayer -> {
            updateSlotUI(slotIndex, selectedPlayer);
            dialog.dismiss();
        });
        adapter.setData(dummyPickList);
        rvPlayers.setAdapter(adapter);

        dialog.show();
    }

    private void updateSlotUI(int slotIndex, Player player) {
        View slotView = findViewById(SLOT_IDS[slotIndex]);

        ImageView ivAvatar = slotView.findViewById(R.id.iv_player_avatar);
        TextView tvName = slotView.findViewById(R.id.tv_player_name);

        tvName.setText(player.getName());
        Glide.with(this).load(player.getImage()).into(ivAvatar);
    }
}