package com.example.nhom9appdocsach.FragmentAdmin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nhom9appdocsach.Activities.FavoriteBookActivity;
import com.example.nhom9appdocsach.Activities.ForgotPassActivity;
import com.example.nhom9appdocsach.Activities.LoginActivity;
import com.example.nhom9appdocsach.Activities.Profile_Activity;
import com.example.nhom9appdocsach.MyApplication;
import com.example.nhom9appdocsach.R;
import com.example.nhom9appdocsach.databinding.FragmentAccAdminBinding;
import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Model.User;

public class AccFragmentAdmin extends Fragment {
    private FragmentAccAdminBinding binding;
    private static final String TAG = "PROFILE_TAG";
    public static final String PREFS_NAME = "AppSettings";
    public static final String KEY_NIGHT_MODE = "NightMode";
    public static final String PREFS_USER = "UserPrefs";
    public static final String KEY_UID = "uid";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean nightMode;

    private DatabaseHandel dbHelper;
    private String currentUid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccAdminBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() == null) return;

        dbHelper = new DatabaseHandel(getContext());

        try {
            sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();

            // Đọc trạng thái night mode đã lưu
            nightMode = sharedPreferences.getBoolean(KEY_NIGHT_MODE, false);

            initializeClickListeners();
            loadUserInfo();
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated: " + e.getMessage());
        }
    }

    private void updateTheme(boolean isNight) {
        if (getActivity() != null) {
            try {
                AppCompatDelegate.setDefaultNightMode(
                        isNight ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
                );
            } catch (Exception e) {
                Log.e(TAG, "Error updating theme: " + e.getMessage());
            }
        }
    }

    private void initializeClickListeners() {
//        binding.switchmode.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            try {
//                // Lưu trạng thái mới
//                editor.putBoolean(KEY_NIGHT_MODE, isChecked);
//                editor.apply();
//                nightMode = isChecked;
//
//                // Cập nhật theme
//                updateTheme(isChecked);
//
//                // Reload fragment
//                if (getActivity() != null && getFragmentManager() != null) {
//                    Fragment currentFragment = getFragmentManager().findFragmentById(R.id.framelayout);
//                    if (currentFragment != null) {
//                        getFragmentManager()
//                                .beginTransaction()
//                                .detach(currentFragment)
//                                .attach(currentFragment)
//                                .commit();
//                    }
//                }
//            } catch (Exception e) {
//                Log.e(TAG, "Error handling night mode: " + e.getMessage());
//            }
//        });

        binding.btnout.setOnClickListener(v -> {
            try {
                // Xóa uid khỏi SharedPreferences
                SharedPreferences prefsUser = requireContext().getSharedPreferences(PREFS_USER, Context.MODE_PRIVATE);
                prefsUser.edit().remove(KEY_UID).apply();

                // Quay về màn hình đăng nhập
                if (getActivity() != null) {
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                }
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

        binding.btndeltk.setOnClickListener(v -> deleteAccount());
    }

    private void deleteAccount() {
        Toast.makeText(requireContext(), "Chưa hỗ trợ xóa tài khoản Admin!", Toast.LENGTH_SHORT).show();
    }

    private void loadUserInfo() {
        // Lấy uid hiện tại từ SharedPreferences
        SharedPreferences prefsUser = requireContext().getSharedPreferences(PREFS_USER, Context.MODE_PRIVATE);
        currentUid = prefsUser.getString(KEY_UID, null);

        if (currentUid == null) {
            Log.e(TAG, "User ID is null");
            return;
        }

        try {
            User user = dbHelper.getUserById(currentUid);
            if (user != null) {
                // Set email
                if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                    binding.txtEmail.setText(user.getEmail());
                }
                // Set name
                if (user.getName() != null && !user.getName().isEmpty()) {
                    binding.txtName.setText(user.getName());
                    binding.txtName.setVisibility(View.VISIBLE);
                } else {
                    binding.txtName.setVisibility(View.GONE);
                }
                // Set timestamp
                if (user.getTimestamp() > 0) {
                    String formattedDate = MyApplication.formatTimestamp(user.getTimestamp());
                    binding.txtDate.setText(formattedDate);
                }
                // Set avatar
//                if (getContext() != null) {
//                    if (user.getImage() != null && !user.getImage().isEmpty()) {
//                        Glide.with(requireContext())
//                                .load(user.getImage())
//                                .placeholder(R.drawable.avatar)
//                                .error(R.drawable.avatar)
//                                .into(binding.imgAvatar);
//                    } else {
//                        Glide.with(requireContext())
//                                .load(R.drawable.avatar)
//                                .into(binding.imgAvatar);
//                    }
//                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading user info: " + e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}