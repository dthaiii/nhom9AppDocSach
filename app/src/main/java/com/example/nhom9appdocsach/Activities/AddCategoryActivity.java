package com.example.nhom9appdocsach.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Model.Category;
import com.example.nhom9appdocsach.databinding.ActivityAddCatagoryBinding;

import java.util.ArrayList;

public class AddCategoryActivity extends AppCompatActivity {
    private ActivityAddCatagoryBinding binding;
    private DatabaseHandel dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCatagoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHandel(this);

        binding.btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
        binding.btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String category = "";

    private void validateData() {
        category = binding.edttheloai.getText().toString().trim();
        if (TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Nhập thể loại", Toast.LENGTH_SHORT).show();
        } else {
            checkCategoryExists();
        }
    }

    private void checkCategoryExists() {
        Toast.makeText(this, "Đang kiểm tra thể loại...", Toast.LENGTH_SHORT).show();

        ArrayList<Category> categories = dbHelper.getAllCategories();
        boolean categoryExists = false;
        for (Category cat : categories) {
            if (cat.getCategory().equalsIgnoreCase(category)) {
                categoryExists = true;
                break;
            }
        }

        if (categoryExists) {
            Toast.makeText(this, "Thể loại này đã tồn tại!", Toast.LENGTH_SHORT).show();
        } else {
            addCategoryToSQLite();
        }
    }

    private void addCategoryToSQLite() {
        Toast.makeText(this, "Đang thêm thể loại...", Toast.LENGTH_SHORT).show();

        long timestamp = System.currentTimeMillis();
        // Lấy uid hiện tại từ SharedPreferences (nếu có)
        String uid = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("uid", "");

        Category newCategory = new Category(
                String.valueOf(timestamp),
                category,
                uid,
                timestamp
        );

        long res = dbHelper.insertCategory(newCategory);

        if (res > 0) {
            Toast.makeText(this, "Thêm thể loại thành công", Toast.LENGTH_SHORT).show();
            binding.edttheloai.setText("");
        } else {
            Toast.makeText(this, "Lỗi khi thêm thể loại!", Toast.LENGTH_SHORT).show();
        }
    }
}