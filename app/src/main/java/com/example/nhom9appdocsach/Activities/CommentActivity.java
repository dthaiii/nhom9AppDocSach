package com.example.nhom9appdocsach.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nhom9appdocsach.Adapter.AdapterComment;
import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Model.Comment;
import com.example.nhom9appdocsach.databinding.ActivityCommentBinding;

import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {
    private ActivityCommentBinding binding;
    private String bookId;
    private ArrayList<Comment> commentArrayList;
    private AdapterComment adapterComment;
    private DatabaseHandel dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dbHelper = new DatabaseHandel(this);
        initializeComponents();
        setupClickListeners();
        loadComments();
    }

    // Tải bình luận và hiển thị từ SQLite
    private void loadComments() {
        commentArrayList = dbHelper.getCommentsByBookId(bookId);
        adapterComment = new AdapterComment(CommentActivity.this, commentArrayList, getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("uid", ""));
        binding.Rcvcomment.setAdapter(adapterComment);
    }

    private void initializeComponents() {
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
    }

    private void setupClickListeners() {
        binding.btnback.setOnClickListener(v -> onBackPressed());

        binding.btncmt.setOnClickListener(v -> {
            String comment = binding.edtcmt.getText().toString().trim();
            if (TextUtils.isEmpty(comment)) {
                binding.edtcmt.setError("Vui lòng nhập bình luận");
                binding.edtcmt.requestFocus();
            } else {
                addComment(comment);
            }
        });
    }

    // Thêm bình luận vào SQLite
    private void addComment(String commentText) {
        Toast.makeText(this, "Đang đăng bình luận...", Toast.LENGTH_SHORT).show();

        long timestamp = System.currentTimeMillis();
        String uid = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("uid", "");
        String commentId = String.valueOf(timestamp);

        Comment comment = new Comment(
                commentId,
                bookId,
                commentText,
                uid,
                timestamp
        );

        long res = dbHelper.insertComment(comment);

        if (res > 0) {
            Toast.makeText(this, "Bình luận đăng thành công", Toast.LENGTH_SHORT).show();
            binding.edtcmt.setText("");
            loadComments(); // Cập nhật lại danh sách sau khi thêm
        } else {
            Toast.makeText(this, "Lỗi khi đăng bình luận!", Toast.LENGTH_SHORT).show();
        }
    }

}