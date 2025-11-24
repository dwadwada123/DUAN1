package com.example.duan1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.duan1.R;
import com.example.duan1.activities.LoginActivity;

public class AdminStatsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnLogout = view.findViewById(R.id.btn_admin_logout);

        View cardUsers = view.findViewById(R.id.stat_card_users);
        View cardSquads = view.findViewById(R.id.stat_card_squads);

        setupStatCard(cardUsers, "Tổng Người dùng", "1,250", R.drawable.ic_users_active);
        setupStatCard(cardSquads, "Tổng Đội hình", "3,400", R.drawable.ic_formation);

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void setupStatCard(View cardView, String label, String value, int iconResId) {
        if (cardView != null) {
            TextView tvLabel = cardView.findViewById(R.id.tv_stat_label);
            TextView tvValue = cardView.findViewById(R.id.tv_stat_value);
            ImageView ivIcon = cardView.findViewById(R.id.iv_stat_icon);

            if (tvLabel != null) tvLabel.setText(label);
            if (tvValue != null) tvValue.setText(value);
            if (ivIcon != null && iconResId != 0) {
                ivIcon.setImageResource(iconResId);
            }
        }
    }
}