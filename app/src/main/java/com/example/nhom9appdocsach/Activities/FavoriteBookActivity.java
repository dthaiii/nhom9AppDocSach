package com.example.nhom9appdocsach.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nhom9appdocsach.Adapter.AdapterFavoriteBook;
import com.example.nhom9appdocsach.Model.Pdf;
import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.databinding.ActivityFavoriteBookBinding;

import java.util.ArrayList;

public class FavoriteBookActivity extends AppCompatActivity {
    private ActivityFavoriteBookBinding binding;
    private ArrayList<Pdf> pdfArrayList;
    private AdapterFavoriteBook adapterFavoriteBook;
    private boolean isDataLoading = false;
    private static final String TAG = "FAVORITE_LIST_TAG";
    private DatabaseHandel dbHelper;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHandel(this);

        // Lấy uid từ SharedPreferences (bạn cần lưu uid khi user đăng nhập)
        uid = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("uid", "");

        loadFavoriteBook();
        setupSwipeRefresh();

        binding.btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (adapterFavoriteBook != null)
                        adapterFavoriteBook.getFilter().filter(s);
                } catch (Exception e) {
                    Log.d(TAG, "onTextChanged: " + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void setupSwipeRefresh() {
        if (binding != null && binding.swipeRefreshLayout != null) {
            binding.swipeRefreshLayout.setOnRefreshListener(() -> {
                if (!isDataLoading) {
                    loadFavoriteBook();
                }
                binding.swipeRefreshLayout.setRefreshing(false);
            });
        }
    }

    // Lấy danh sách sách yêu thích từ SQLite
    private void loadFavoriteBook() {
        isDataLoading = true;
        pdfArrayList = dbHelper.getFavoriteBooksByUserId(uid); // <-- Bạn cần viết hàm này trong DatabaseHandel
        binding.txtpage.setText("" + pdfArrayList.size());
        adapterFavoriteBook = new AdapterFavoriteBook(FavoriteBookActivity.this, pdfArrayList);
        binding.Rcvbook.setAdapter(adapterFavoriteBook);
        isDataLoading = false;
    }
}