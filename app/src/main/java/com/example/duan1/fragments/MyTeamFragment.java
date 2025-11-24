package com.example.duan1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.R;
import com.example.duan1.activities.TacticsBoardActivity;
import com.example.duan1.adapters.SquadAdapter;
import com.example.duan1.dialogs.CreateSquadDialog;
import com.example.duan1.models.Squad;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MyTeamFragment extends Fragment {

    private RecyclerView rvSquads;
    private LinearLayout layoutEmpty;
    private FloatingActionButton fabCreate;
    private SquadAdapter adapter;
    private final List<Squad> dummySquads = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_team, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvSquads = view.findViewById(R.id.rv_squads);
        layoutEmpty = view.findViewById(R.id.layout_empty_state);
        fabCreate = view.findViewById(R.id.fab_create_squad);

        rvSquads.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SquadAdapter(getContext(), dummySquads, squad -> {
            Intent intent = new Intent(getContext(), TacticsBoardActivity.class);
            intent.putExtra("squad_id", squad.getSquadId());
            startActivity(intent);
        });

        rvSquads.setAdapter(adapter);

        fabCreate.setOnClickListener(v -> {
            CreateSquadDialog.show(getContext(), (name, formation) -> {
                Squad newSquad = new Squad("id_" + System.currentTimeMillis(), "user1", name, formation);
                dummySquads.add(newSquad);
                updateUI();
                Toast.makeText(getContext(), "Đã tạo đội: " + name, Toast.LENGTH_SHORT).show();
            });
        });

        loadDummyData();
    }

    private void loadDummyData() {
        dummySquads.add(new Squad("sq1", "user1", "Dream Team 2025", "4-3-3"));
        dummySquads.add(new Squad("sq2", "user1", "MU All Stars", "4-4-2"));
        updateUI();
    }

    private void updateUI() {
        if (dummySquads.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvSquads.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvSquads.setVisibility(View.VISIBLE);
            adapter.setData(dummySquads);
        }
    }
}