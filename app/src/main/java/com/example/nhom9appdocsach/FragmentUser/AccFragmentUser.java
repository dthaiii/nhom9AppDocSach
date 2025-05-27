package com.example.nhom9appdocsach.FragmentUser;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nhom9appdocsach.Activities.FavoriteBookActivity;
import com.example.nhom9appdocsach.Activities.ForgotPassActivity;
import com.example.nhom9appdocsach.Activities.LoginActivity;
import com.example.nhom9appdocsach.Activities.Profile_Activity;
import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Model.User;
import com.example.nhom9appdocsach.MyApplication;
import com.example.nhom9appdocsach.R;
import com.example.nhom9appdocsach.databinding.FragmentAccUserBinding;

public class AccFragmentUser extends Fragment {
    private FragmentAccUserBinding binding;
    private DatabaseHandel dbHelper;
    private static final String TAG = "PROFILE_TAG";
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    boolean nightMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccUserBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() == null) return;

        dbHelper = new DatabaseHandel(getContext());

        try {
            sharedPreferences = getContext().getSharedPreferences("MODE", Context.MODE_PRIVATE);
            nightMode = sharedPreferences.getBoolean("nightMode", false);
//            if (nightMode) {
//                binding.switchmode.setChecked(true);
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing SharedPreferences: " + e.getMessage());
        }

        initializeClickListeners();
        loadUserInfo();
    }

    private void initializeClickListeners() {
//        binding.switchmode.setOnClickListener(v -> {
//            try {
//                if (nightMode) {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                    editor = sharedPreferences.edit();
//                    editor.putBoolean("nightMode", false);
//                } else {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                    editor = sharedPreferences.edit();
//                    editor.putBoolean("nightMode", true);
//                }
//                editor.apply();
//            } catch (Exception e) {
//                Log.e(TAG, "Error handling night mode: " + e.getMessage());
//            }
//        });

        binding.btnout.setOnClickListener(v -> {
            try {
                // Xóa uid khỏi SharedPreferences khi đăng xuất
                SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                prefs.edit().remove("uid").apply();
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            } catch (Exception e) {
                Log.e(TAG, "Error signing out: " + e.getMessage());
            }
        });

        binding.userProfile.setOnClickListener(v -> {
            try {
                if (getActivity() != null) {
                    Intent intent = new Intent(requireActivity(), Profile_Activity.class);
                    startActivity(intent);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error opening profile: " + e.getMessage());
            }
        });

        binding.btnlikebook.setOnClickListener(v -> {
            try {
                if (getActivity() != null) {
                    Intent intent = new Intent(requireActivity(), FavoriteBookActivity.class);
                    startActivity(intent);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error opening favorite books: " + e.getMessage());
            }
        });
        binding.btnchangepass.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ForgotPassActivity.class);
            startActivity(intent);
        });
        binding.btndeltk.setOnClickListener(v -> {
            deleteAccount();
        });
    }

    private void deleteAccount() {
        // Hiển thị dialog yêu cầu nhập lại mật khẩu trước
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_reauthenticate, null);
        EditText passwordEt = view.findViewById(R.id.edtpass);

        builder.setView(view)
                .setTitle("Xác thực")
                .setMessage("Vui lòng nhập mật khẩu để xác nhận xóa tài khoản")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    String password = passwordEt.getText().toString().trim();
                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(requireContext(), "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Lấy uid từ SharedPreferences
                    SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    String uid = prefs.getString("uid", "");
                    if (uid == null || uid.isEmpty()) {
                        Toast.makeText(requireContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    User user = dbHelper.getUserById(uid);
                    if (user == null) {
                        Toast.makeText(requireContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Kiểm tra mật khẩu
                    if (!password.equals(user.getPassword())) {
                        Toast.makeText(requireContext(), "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    showDeleteConfirmationDialog(uid);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDeleteConfirmationDialog(String uid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Xóa tài khoản")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    proceedWithAccountDeletion(uid);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void proceedWithAccountDeletion(String uid) {
        // Hiển thị dialog loading
        ProgressDialog progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setMessage("Đang xóa tài khoản...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        boolean deleted = dbHelper.deleteUserById(uid);

        progressDialog.dismiss();
        if (deleted) {
            Toast.makeText(requireContext(), "Tài khoản đã được xóa", Toast.LENGTH_SHORT).show();
            // Xóa uid khỏi SharedPreferences
            SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            prefs.edit().remove("uid").apply();

            // Quay về màn hình đăng nhập
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        } else {
            Toast.makeText(requireContext(), "Lỗi khi xóa tài khoản", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserInfo() {
        // Lấy uid từ SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String uid = prefs.getString("uid", "");
        if (uid == null || uid.isEmpty()) {
            Log.e(TAG, "User ID is null");
            return;
        }
        User user = dbHelper.getUserById(uid);
        if (user == null) {
            Log.e(TAG, "User data does not exist in database");
            return;
        }
        binding.txtEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        if (user.getName() != null && !user.getName().isEmpty()) {
            binding.txtName.setText(user.getName());
            binding.txtName.setVisibility(View.VISIBLE);
        } else {
            binding.txtName.setVisibility(View.GONE);
        }
        if (user.getTimestamp() > 0) {
            String formattedDate = MyApplication.formatTimestamp(user.getTimestamp());
            binding.txtDate.setText(formattedDate);
        }
//        if (getContext() != null) {
//            if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
//                Glide.with(requireContext())
//                        .load(user.getProfileImage())
//                        .placeholder(R.drawable.avatar)
//                        .error(R.drawable.avatar)
//                        .into(binding.imgAvatar);
//            } else {
//                Glide.with(requireContext())
//                        .load(R.drawable.avatar)
//                        .into(binding.imgAvatar);
//            }
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}