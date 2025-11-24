package com.example.duan1.fragments;

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
import com.example.duan1.adapters.PlayerAdapter;
import com.example.duan1.dialogs.PlayerInfoDialog;
import com.example.duan1.models.Player;
import java.util.ArrayList;
import java.util.List;

public class PlayerInventoryFragment extends Fragment {

    private RecyclerView rvMyPlayers;
    private LinearLayout layoutEmpty;
    private PlayerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMyPlayers = view.findViewById(R.id.rv_my_players);
        layoutEmpty = view.findViewById(R.id.layout_empty_state);

        rvMyPlayers.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PlayerAdapter(getContext(), player -> {
            PlayerInfoDialog.show(getContext(), player, p -> {
                Toast.makeText(getContext(), "Đã sa thải " + p.getName(), Toast.LENGTH_SHORT).show();
            });
        });

        rvMyPlayers.setAdapter(adapter);

        loadDummyData();
    }

    private void loadDummyData() {
        List<Player> dummyList = new ArrayList<>();

        Player p1 = new Player();
        p1.setName("Kevin De Bruyne");
        p1.setTeam("Manchester City");
        p1.setNationality("Belgium");
        p1.setPosition("Midfielder");
        p1.setThumbUrl("https://www.thesportsdb.com/images/media/player/thumb/b2b2321516206884.jpg");
        dummyList.add(p1);

        Player p2 = new Player();
        p2.setName("Virgil van Dijk");
        p2.setTeam("Liverpool");
        p2.setNationality("Netherlands");
        p2.setPosition("Defender");
        p2.setThumbUrl("https://www.thesportsdb.com/images/media/player/thumb/s0g5501516205982.jpg");
        dummyList.add(p2);

        if (dummyList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvMyPlayers.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvMyPlayers.setVisibility(View.VISIBLE);
            adapter.setData(dummyList);
        }
    }
}