package com.example.duan1.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.R;
import com.example.duan1.adapters.UserAdapter;
import com.example.duan1.models.User;
import java.util.ArrayList;
import java.util.List;

public class AdminUsersFragment extends Fragment {

    private RecyclerView rvUsers;
    private UserAdapter adapter;
    private List<User> dummyUsers = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvUsers = view.findViewById(R.id.rv_user_list);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdapter(getContext(), dummyUsers, user -> {
            showDeleteConfirmDialog(user);
        });

        rvUsers.setAdapter(adapter);

        loadDummyData();
    }

    private void showDeleteConfirmDialog(User user) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa người dùng")
                .setMessage("Bạn có chắc muốn xóa tài khoản " + user.getEmail() + " không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dummyUsers.remove(user);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void loadDummyData() {
        dummyUsers.add(new User("u1", "tranvuphong05@gmail.com", "Trần Vũ Phong", "user"));
        dummyUsers.add(new User("u2", "tranlequocdung0@gmail.com", "Trần Lê Quốc Dũng", "user"));
        dummyUsers.add(new User("u3", "denngungoc123@gmail.com", "Trần Tâm", "user"));
        adapter.setData(dummyUsers);
    }
}