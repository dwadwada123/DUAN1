package com.example.duan1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.R;
import com.example.duan1.models.Squad;

import java.util.List;

public class SquadAdapter extends RecyclerView.Adapter<SquadAdapter.SquadViewHolder> {

    private Context context;
    private List<Squad> squadList;
    private final OnSquadActionListener listener;

    public SquadAdapter(Context context, List<Squad> squadList, OnSquadActionListener listener) {
        this.context = context;
        this.squadList = squadList;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull SquadViewHolder holder, int position) {
        Squad squad = squadList.get(position);
        if (squad == null) return;

        holder.tvName.setText(squad.getSquadName());
        holder.tvFormation.setText(squad.getFormation());

        int count = squad.getItems() != null ? squad.getItems().size() : 0;
        holder.tvInfo.setText(count + "/11 cầu thủ đã xếp");
        holder.itemView.setOnClickListener(v -> listener.onSquadClick(squad));
        holder.btnDelete.setOnClickListener(v -> listener.onSquadDelete(squad));
    }

    public void setData(List<Squad> newList) {
        this.squadList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SquadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_formation_summary, parent, false);
        return new SquadViewHolder(view);
    }

    public interface OnSquadActionListener {
        void onSquadClick(Squad squad);

        void onSquadDelete(Squad squad);
    }

    @Override
    public int getItemCount() {
        return squadList != null ? squadList.size() : 0;
    }

    public static class SquadViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvFormation, tvInfo;
        ImageButton btnDelete;

        public SquadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_formation_name);
            tvFormation = itemView.findViewById(R.id.tv_formation_schema);
            tvInfo = itemView.findViewById(R.id.tv_summary_info);
            btnDelete = itemView.findViewById(R.id.btn_delete_squad);
        }
    }
}