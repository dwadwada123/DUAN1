package com.example.duan1.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.duan1.R;
import com.example.duan1.models.Player;

public class PlayerInfoDialog {

    public static void show(Context context, Player player, OnDismissListener listener) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_player_details, null);
        dialog.setContentView(view);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvName = view.findViewById(R.id.tv_player_name_dialog);
        TextView tvTeam = view.findViewById(R.id.tv_detail_team);
        TextView tvPosition = view.findViewById(R.id.tv_detail_position);
        TextView tvNationality = view.findViewById(R.id.tv_detail_nationality);
        ImageView ivAvatar = view.findViewById(R.id.iv_player_image_dialog);
        ImageView ivFlag = view.findViewById(R.id.iv_nationality_flag_dialog);
        ImageButton btnClose = view.findViewById(R.id.btn_close);
        Button btnDismiss = view.findViewById(R.id.btn_dismiss_player);

        tvName.setText(player.getName());
        tvTeam.setText(player.getTeam() != null ? player.getTeam() : "N/A");
        tvPosition.setText(player.getPosition());
        tvNationality.setText(player.getNationality() != null ? player.getNationality() : "N/A");

        Glide.with(context)
                .load(player.getImage())
                .placeholder(R.drawable.ic_football_with_trophy)
                .into(ivAvatar);

        ivFlag.setImageResource(R.drawable.ic_flag_example);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnDismiss.setOnClickListener(v -> {
            listener.onDismissPlayer(player);
            dialog.dismiss();
        });

        dialog.show();
    }

    public interface OnDismissListener {
        void onDismissPlayer(Player player);
    }
}