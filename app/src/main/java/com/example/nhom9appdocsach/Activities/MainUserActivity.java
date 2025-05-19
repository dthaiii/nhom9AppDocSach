package com.example.nhom9appdocsach.Activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.nhom9appdocsach.FragmentUser.AccFragmentUser;
import com.example.nhom9appdocsach.FragmentUser.BookFragmentUser;
import com.example.nhom9appdocsach.FragmentUser.HomeFragmentUser;
import com.example.nhom9appdocsach.FragmentUser.NotiFragmentUser;
import com.example.nhom9appdocsach.R;
import com.example.nhom9appdocsach.databinding.ActivityMainUserBinding;

public class MainUserActivity extends AppCompatActivity {
    private ActivityMainUserBinding binding;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupBottomNavigation();
        setupFabButton();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragmentUser(), true);
        }
    }

    private void setupFabButton() {
        binding.btnfab.setOnClickListener(v -> {
            Intent intent = new Intent(MainUserActivity.this, AddBookActivity.class);
            startActivity(intent);
        });
    }

    private void setupBottomNavigation() {
        binding.BottomNvi.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment fragment = null;

            if (itemId == R.id.btnhome) {
                fragment = new HomeFragmentUser();
            } else if (itemId == R.id.btnbook) {
                fragment = new BookFragmentUser();
            } else if (itemId == R.id.btnnoti) {
                fragment = new NotiFragmentUser();
            } else if (itemId == R.id.btnperson) {
                fragment = new AccFragmentUser();
            }

            if (fragment != null) {
                loadFragment(fragment, false);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        try {
            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                transaction.setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out
                );

                if (isAppInitialized) {
                    transaction.add(R.id.framelayout, fragment);
                } else {
                    transaction.replace(R.id.framelayout, fragment);
                    if (!(fragment instanceof HomeFragmentUser)) {
                        transaction.addToBackStack(null);
                    }
                }

                transaction.commit();
                currentFragment = fragment;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}