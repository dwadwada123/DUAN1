package com.example.duan1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.duan1.R;
import com.example.duan1.models.Player;
import java.util.ArrayList;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private Context context;
    private List<Player> playerList;
    private OnPlayerClickListener listener;

    public interface OnPlayerClickListener {
        void onPlayerClick(Player player);
    }

    public PlayerAdapter(Context context, OnPlayerClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.playerList = new ArrayList<>();
    }

    public void setData(List<Player> newList) {
        this.playerList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_player_result, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);
        if (player == null) return;

        holder.tvName.setText(player.getName());

        String detailText = (player.getTeam() != null ? player.getTeam() : "Unknown Team")
                + " | "
                + (player.getNationality() != null ? player.getNationality() : "Unknown");
        holder.tvDetails.setText(detailText);

        holder.tvPosition.setText(player.getPosition());

        Glide.with(context)
                .load(player.getImage())
                .placeholder(R.drawable.ic_placeholder_player)
                .error(R.drawable.ic_placeholder_player)
                .into(holder.ivAvatar);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlayerClick(player);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playerList != null ? playerList.size() : 0;
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName, tvDetails, tvPosition;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.player_avatar);
            tvName = itemView.findViewById(R.id.player_name);
            tvDetails = itemView.findViewById(R.id.player_details);
            tvPosition = itemView.findViewById(R.id.player_position);
        }
    }
}