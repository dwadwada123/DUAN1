package com.example.duan1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.duan1.R;
import com.example.duan1.activities.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileFragment extends Fragment {

    private TextInputEditText etEmail, etDisplayName;
    private Button btnSave, btnLogout;
    private TextView tvStatTotalPlayers, tvStatFavTeam, tvStatMostUsed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEmail = view.findViewById(R.id.et_email);
        etDisplayName = view.findViewById(R.id.et_display_name);
        btnSave = view.findViewById(R.id.btn_save_profile);
        btnLogout = view.findViewById(R.id.btn_logout);
        View cardTotal = view.findViewById(R.id.stat_total_players);
        View cardFav = view.findViewById(R.id.stat_favorite_team);
        View cardMost = view.findViewById(R.id.stat_most_used_player);

        tvStatTotalPlayers = cardTotal.findViewById(R.id.tv_stat_value);
        ((TextView) cardTotal.findViewById(R.id.tv_stat_label)).setText("Tổng số cầu thủ");

        tvStatFavTeam = cardFav.findViewById(R.id.tv_stat_value);
        ((TextView) cardFav.findViewById(R.id.tv_stat_label)).setText("Đội bóng yêu thích");

        tvStatMostUsed = cardMost.findViewById(R.id.tv_stat_value);
        ((TextView) cardMost.findViewById(R.id.tv_stat_label)).setText("Cầu thủ dùng nhiều nhất");

        loadDummyProfile();

        btnSave.setOnClickListener(v -> {
            String newName = etDisplayName.getText().toString();
            Toast.makeText(getContext(), "Đã cập nhật tên thành: " + newName, Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadDummyProfile() {
        etEmail.setText("admin@example.com");
        etDisplayName.setText("HLV Park Hang Seo");
        tvStatTotalPlayers.setText("23");
        tvStatFavTeam.setText("Real Madrid");
        tvStatMostUsed.setText("C. Ronaldo");
    }
}