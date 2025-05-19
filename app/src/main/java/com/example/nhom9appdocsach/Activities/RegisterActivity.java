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
import com.example.nhom9appdocsach.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private DatabaseHandel dbHelper;
    private String name = "", email = "", password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHandel(this);

        binding.btnsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void validateData() {
        name = binding.edtname.getText().toString().trim();
        email = binding.edtuser.getText().toString().trim();
        password = binding.edtpass.getText().toString().trim();
        String cPassword = binding.edtpass2.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Nhập tên", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Nhập lại email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Nhập mật khẩu", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cPassword)) {
            Toast.makeText(this, "Nhập lại mật khẩu", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(cPassword)) {
            Toast.makeText(this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
        } else if (dbHelper.isEmailExists(email)) {
            Toast.makeText(this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
        } else {
            createUserAccount();
        }
    }

    private void createUserAccount() {
        Toast.makeText(RegisterActivity.this, "Đang tạo tài khoản!!!", Toast.LENGTH_SHORT).show();

        long timestamp = System.currentTimeMillis();
        String uid = "user_" + timestamp;

        User user = new User();
        user.setUid(uid);
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password);
        user.setUsertype("user");
        user.setTimestamp(timestamp);

        long result = dbHelper.insertUser(user);
        if (result != -1) {
            Toast.makeText(RegisterActivity.this, "Thành Công", Toast.LENGTH_SHORT).show();

            // Lưu uid vào SharedPreferences để sử dụng cho các màn sau
            getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    .edit().putString("uid", uid).apply();

            startActivity(new Intent(RegisterActivity.this, MainUserActivity.class));
            finish();
        } else {
            Toast.makeText(RegisterActivity.this, "Lỗi khi tạo tài khoản!", Toast.LENGTH_SHORT).show();
        }
    }
}