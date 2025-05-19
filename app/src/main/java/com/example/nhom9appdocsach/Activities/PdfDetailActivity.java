package com.example.nhom9appdocsach.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.nhom9appdocsach.Adapter.AdapterCommentDetails;
import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Model.Book;
import com.example.nhom9appdocsach.Model.Comment;
import com.example.nhom9appdocsach.MyApplication;
import com.example.nhom9appdocsach.R;
import com.example.nhom9appdocsach.databinding.ActivityPdfDetailBinding;

import java.util.ArrayList;

public class PdfDetailActivity extends AppCompatActivity {

    private static final String TAG_DOWNLOAD = "DOWNLOAD_TAG";
    private ActivityPdfDetailBinding binding;
    String bookId, bookTitle, bookUrl;
    long        timestamp;
    boolean isInMyFavorite = false;
    private boolean isDataLoading = false;
    private boolean isAdded = false;
    private ArrayList<Comment> commentArrayList;
    private AdapterCommentDetails adapterCommentDetails;
    private DatabaseHandel dbHelper;
    private String uid; // Lưu uid đăng nhập

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHandel(this);

        // Lấy uid từ SharedPreferences
        uid = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("uid", null);

        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        binding.btnsave.setVisibility(View.GONE);

        checkIsFavorite();
        loadBookDetails();
        loadComments();
        setupSwipeRefresh();
        MyApplication.incrementBookViewCount(this, bookId);

        binding.btnback.setOnClickListener(v -> onBackPressed());
        binding.btnread.setOnClickListener(v -> {
            Intent intent1 = new Intent(PdfDetailActivity.this, PdfViewActivity.class);
            intent1.putExtra("bookId", bookId);
            startActivity(intent1);
        });
        binding.btnsave.setOnClickListener(v -> {
            Log.d(TAG_DOWNLOAD, "onClick:Checking permission");
            if (ContextCompat.checkSelfPermission(PdfDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG_DOWNLOAD, "onClick: Permission already granted, can download book");
                MyApplication.downloadBook(PdfDetailActivity.this, "" + bookId, "" + bookTitle, "" + bookUrl);
            } else {
                Log.d(TAG_DOWNLOAD, "onClick: Permission was not granted, request permission");
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });
        binding.btnfavor.setOnClickListener(v -> {
            if (isInMyFavorite) {
                MyApplication.removeFromFavorite(this, bookId, uid);
            } else {
                MyApplication.addToFavorite(this, bookId, uid);
            }
            // Cập nhật lại trạng thái yêu thích sau khi thao tác
            checkIsFavorite();
        });
        binding.btncomment.setOnClickListener(v -> {
            Intent intentComment = new Intent(PdfDetailActivity.this, CommentActivity.class);
            intentComment.putExtra("bookId", bookId);
            startActivity(intentComment);
        });
    }

    private void loadComments() {
        commentArrayList = dbHelper.getCommentsByBookId(bookId);
        adapterCommentDetails = new AdapterCommentDetails(PdfDetailActivity.this, commentArrayList);
        binding.Rcvcomment.setAdapter(adapterCommentDetails);
    }

    private void setupSwipeRefresh() {
        if (binding != null && binding.swipeRefreshLayout != null) {
            binding.swipeRefreshLayout.setOnRefreshListener(() -> {
                if (!isDataLoading) {
                    loadAllData();
                }
                binding.swipeRefreshLayout.setRefreshing(false);
            });
        }
    }

    private void loadAllData() {
        isDataLoading = true;
        loadBookDetails();
        checkIsFavorite();
        loadComments();
        isDataLoading = false;
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(TAG_DOWNLOAD, "Permission Granted");
                    MyApplication.downloadBook(this, "" + bookId, "" + bookTitle, "" + bookUrl);
                } else {
                    Log.d(TAG_DOWNLOAD, "Permission tu choi");
                    Toast.makeText(this, "Permission từ chối", Toast.LENGTH_SHORT).show();
                }
            });

    private void loadBookDetails() {
        Book book = dbHelper.getBookById(bookId);
        if (book == null) return;
        timestamp = book.getTimestamp();
        bookTitle = book.getTitle();
        bookUrl = book.getUrl();

        binding.btnsave.setVisibility(View.VISIBLE);

        String date = MyApplication.formatTimestamp(timestamp);
        MyApplication.loadCategory(this, book.getCategoryId(), binding.txtcategory);
        MyApplication.loadImageFromUrl(this, bookId, binding.ImageThumb, binding.progressBar);
        MyApplication.LoadPdfSize(this, bookUrl, bookId, binding.txtsize);
        binding.txttitle.setText(bookTitle);
        binding.txtmota.setText(book.getDescription());
        binding.txtviews.setText(String.valueOf(book.getViewsCount()));
        binding.txtdownload.setText(String.valueOf(book.getDownloadsCount()));
        binding.txtdate.setText(date);
    }

    // Kiểm tra xem người dùng có bấm yêu thích không
    private void checkIsFavorite() {
        isInMyFavorite = dbHelper.getFavoriteBooksByUserId(uid).stream().anyMatch(pdf -> bookId.equals(pdf.getId()));
        if (isInMyFavorite) {
            binding.btnfavor.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.baseline_favorite_white, 0, 0);
        } else {
            binding.btnfavor.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.baseline_favorite_border, 0, 0);
        }
    }
}