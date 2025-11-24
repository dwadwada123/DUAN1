package com.example.duan1.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.duan1.R;
import com.google.android.material.textfield.TextInputEditText;

public class CreateSquadDialog {

    public static void show(Context context, OnSquadCreatedListener listener) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_create_squad, null);
        dialog.setContentView(view);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextInputEditText etName = view.findViewById(R.id.et_squad_name);
        Spinner spFormation = view.findViewById(R.id.spinner_formation_dialog);
        Button btnCreate = view.findViewById(R.id.btn_confirm_create);

        btnCreate.setOnClickListener(v -> {
            String name = "";
            if (etName.getText() != null) {
                name = etName.getText().toString().trim();
            }

            String formation = spFormation.getSelectedItem().toString();

            if (name.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập tên đội hình", Toast.LENGTH_SHORT).show();
            } else {
                listener.onCreated(name, formation);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public interface OnSquadCreatedListener {
        void onCreated(String name, String formation);
    }
}