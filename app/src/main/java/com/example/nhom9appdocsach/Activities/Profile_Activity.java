package com.example.nhom9appdocsach.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Model.User;
import com.example.nhom9appdocsach.R;
import com.example.nhom9appdocsach.databinding.ActivityProfileBinding;

public class Profile_Activity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private ProgressDialog progressDialog;
    private static final String TAG = "PROFILE_EDIT_TAG";
    private String name = "";
    private DatabaseHandel dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHandel(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Đợi 1 chút nha!!!");
        progressDialog.setCanceledOnTouchOutside(false);

        // Giả định uid được lưu ở SharedPreferences sau khi đăng nhập
        String uid = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("uid", null);

        loadUserInfo(uid);

        binding.btnback.setOnClickListener(v -> onBackPressed());
        binding.btnedit.setOnClickListener(v -> validateData(uid));
    }

    private void validateData(String uid) {
        name = binding.txtName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Nhập tên", Toast.LENGTH_SHORT).show();
        } else {
            updateProfile(uid);
        }
    }

    private void updateProfile(String uid) {
        Log.d(TAG, "updateProfile: đang cập nhật thông tin người dùng ");
        progressDialog.setMessage("Cập nhật thông tin người dùng");
        progressDialog.show();

        // Lấy thông tin User hiện tại
        User user = dbHelper.getUserById(uid);
        if (user == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }
        user.setName(name);

        dbHelper.insertUser(user);

        progressDialog.dismiss();
        Toast.makeText(Profile_Activity.this, "Thông tin đã được cập nhật", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void loadUserInfo(String uid) {
        Log.d(TAG, "LoadUserInfo: đang tải..." + uid);
        User user = dbHelper.getUserById(uid);
        if (user != null) {
            binding.txtEmail.setText(user.getEmail());
            binding.txtName.setText(user.getName());
            // Nếu bạn vẫn muốn hiển thị avatar ở chỗ khác, hãy thêm code ở đây.
            // Glide.with(Profile_Activity.this)
            //         .load(user.getImage())
            //         .placeholder(R.drawable.avatar)
            //         .into(binding.someOtherImageView);
        }
    }
}