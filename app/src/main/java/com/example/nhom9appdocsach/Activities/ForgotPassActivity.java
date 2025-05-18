package com.example.nhom9appdocsach.Activities;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Model.User;
import com.example.nhom9appdocsach.databinding.ActivityForgotPassBinding;

public class ForgotPassActivity extends AppCompatActivity {
    private ActivityForgotPassBinding binding;
    private DatabaseHandel dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dbHelper = new DatabaseHandel(this);

        binding.btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private String email = "";

    private void validateData() {
        email = binding.edtemail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Vui lòng nhập lại email đúng định dạng!", Toast.LENGTH_SHORT).show();
        } else {
            recoverPasswordOffline();
        }
    }

    private void recoverPasswordOffline() {
        // Tìm user theo email trong SQLite
        User user = dbHelper.getUserByEmail(email);
        if (user == null) {
            Toast.makeText(this, "Email không tồn tại trong hệ thống!", Toast.LENGTH_SHORT).show();
        } else {
            // Có thể chọn 1 trong 2 cách sau:

            // Cách 1: Thông báo liên hệ admin
//            Toast.makeText(this, "Vui lòng liên hệ quản trị viên để cấp lại mật khẩu.", Toast.LENGTH_LONG).show();

            // Cách 2: Tự động đặt lại mật khẩu về mặc định ("123456") cho user này
             dbHelper.updateUserPassword(user.getUid(), "123456");
             Toast.makeText(this, "Mật khẩu đã được đặt lại về '123456'. Vui lòng đăng nhập lại và đổi mật khẩu!", Toast.LENGTH_LONG).show();
        }
    }
}