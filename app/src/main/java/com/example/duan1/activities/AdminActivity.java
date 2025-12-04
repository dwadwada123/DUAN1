package com.example.duan1.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.duan1.R;
import com.example.duan1.fragments.AdminPlayersFragment;
import com.example.duan1.fragments.AdminStatsFragment;
import com.example.duan1.fragments.AdminUsersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        bottomNavigationView = findViewById(R.id.admin_bottom_navigation);

        loadFragment(new AdminStatsFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_admin_stats) {
                selectedFragment = new AdminStatsFragment();
            } else if (itemId == R.id.nav_admin_users) {
                selectedFragment = new AdminUsersFragment();
            } else if (itemId == R.id.nav_admin_players) {
                selectedFragment = new AdminPlayersFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.admin_fragment_container, fragment)
                .commit();
    }
}