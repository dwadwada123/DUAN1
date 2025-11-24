package com.example.duan1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.R;
import com.example.duan1.activities.PlayerDetailActivity;
import com.example.duan1.adapters.PlayerAdapter;
import com.example.duan1.models.Player;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText etSearch;
    private Button btnFilterPlayer, btnFilterTeam, btnSearch;
    private RecyclerView rvSearchResults;
    private ProgressBar pbLoading;
    private LinearLayout layoutEmptyState;
    private boolean isSearchByPlayer = true;
    private PlayerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.et_search);
        btnFilterPlayer = view.findViewById(R.id.btn_filter_player);
        btnFilterTeam = view.findViewById(R.id.btn_filter_team);
        btnSearch = view.findViewById(R.id.btn_search);
        rvSearchResults = view.findViewById(R.id.rv_search_results);
        pbLoading = view.findViewById(R.id.pb_loading);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);

        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PlayerAdapter(getContext(), this::onPlayerClick);
        rvSearchResults.setAdapter(adapter);

        updateFilterUI();

        btnFilterPlayer.setOnClickListener(v -> {
            isSearchByPlayer = true;
            updateFilterUI();
        });

        btnFilterTeam.setOnClickListener(v -> {
            isSearchByPlayer = false;
            updateFilterUI();
        });

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập từ khóa", Toast.LENGTH_SHORT).show();
            } else {
                performSearchDummy(query);
            }
        });
    }

    private void updateFilterUI() {
        if (isSearchByPlayer) {
            btnFilterPlayer.setBackgroundResource(R.drawable.button_active_background);
            btnFilterPlayer.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

            btnFilterTeam.setBackgroundResource(R.drawable.button_inactive_background);
            btnFilterTeam.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_text));

            etSearch.setHint("Nhập tên cầu thủ...");
        } else {
            btnFilterTeam.setBackgroundResource(R.drawable.button_active_background);
            btnFilterTeam.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

            btnFilterPlayer.setBackgroundResource(R.drawable.button_inactive_background);
            btnFilterPlayer.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_text));

            etSearch.setHint("Nhập tên đội bóng...");
        }
    }

    private void performSearchDummy(String query) {
        layoutEmptyState.setVisibility(View.GONE);
        rvSearchResults.setVisibility(View.GONE);
        pbLoading.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            List<Player> dummyList = new ArrayList<>();

            // Messi
            Player messi = new Player();
            messi.setIdPlayer("34145903");
            messi.setName("Lionel Messi");
            messi.setTeam("Inter Miami CF");
            messi.setNationality("Argentina");
            messi.setPosition("Forward");
            messi.setThumbUrl("https://www.thesportsdb.com/images/media/player/thumb/rscppl1516206339.jpg");
            messi.setCutoutUrl("https://www.thesportsdb.com/images/media/player/cutout/60515729.png");
            messi.setHeight("1.70 m");
            messi.setWeight("72 kg");
            messi.setDescription("Lionel Andrés Messi is an Argentine professional footballer who plays as a forward...");
            dummyList.add(messi);

            // Ronaldo
            Player ronaldo = new Player();
            ronaldo.setIdPlayer("34145904");
            ronaldo.setName("Cristiano Ronaldo");
            ronaldo.setTeam("Al Nassr");
            ronaldo.setNationality("Portugal");
            ronaldo.setPosition("Forward");
            ronaldo.setThumbUrl("https://www.thesportsdb.com/images/media/player/thumb/h031l41593338320.jpg");
            ronaldo.setCutoutUrl("https://www.thesportsdb.com/images/media/player/cutout/1593338320.png");
            ronaldo.setHeight("1.87 m");
            ronaldo.setWeight("83 kg");
            ronaldo.setDescription("Cristiano Ronaldo dos Santos Aveiro is a Portuguese professional footballer...");
            dummyList.add(ronaldo);

            // Mbappe
            Player mbappe = new Player();
            mbappe.setIdPlayer("34145905");
            mbappe.setName("Kylian Mbappé");
            mbappe.setTeam("Real Madrid");
            mbappe.setNationality("France");
            mbappe.setPosition("Forward");
            mbappe.setThumbUrl("https://www.thesportsdb.com/images/media/player/thumb/20180627160309.jpg"); // Ảnh ví dụ
            mbappe.setHeight("1.78 m");
            mbappe.setWeight("73 kg");
            dummyList.add(mbappe);

            pbLoading.setVisibility(View.GONE);
            rvSearchResults.setVisibility(View.VISIBLE);
            adapter.setData(dummyList);

        }, 1500);
    }

    private void onPlayerClick(Player player) {
        Intent intent = new Intent(getContext(), PlayerDetailActivity.class);
        intent.putExtra("player_data", player);
        startActivity(intent);
    }
}