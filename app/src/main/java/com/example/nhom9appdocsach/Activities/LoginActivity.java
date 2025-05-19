package com.example.nhom9appdocsach.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Model.User;
import com.example.nhom9appdocsach.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private DatabaseHandel dbHelper;
    private String email = "", password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dbHelper = new DatabaseHandel(this);
        binding.btnlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        binding.btnsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void validateData() {
        email = binding.edtuser.getText().toString().trim();
        password = binding.edtpass.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Nhập Email hợp lệ!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Nhập mật khẩu!", Toast.LENGTH_SHORT).show();
        } else {
            loginUser();
        }
    }

    private void loginUser() {
        // Lấy user theo email
        User user = dbHelper.getUserByEmail(email);
        if (user == null) {
            Toast.makeText(this, "Tài khoản không tồn tại!", Toast.LENGTH_SHORT).show();
            binding.edtpass.setText("");
            return;
        }

        // Kiểm tra mật khẩu lưu mật khẩu trong trường password
        String passwordInDb = user.getPassword();
        if (passwordInDb != null && passwordInDb.equals(password)) {
            // Lưu lại uid vào SharedPreferences để giữ đăng nhập sau này
            getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().putString("uid", user.getUid()).apply();

            if ("admin".equalsIgnoreCase(user.getUsertype())) {
                startActivity(new Intent(LoginActivity.this, MainAdminActivity.class));
            } else {
                startActivity(new Intent(LoginActivity.this, MainUserActivity.class));
            }
            finish();
        } else {
            Toast.makeText(this, "Email hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
            binding.edtpass.setText("");
        }
    }

}