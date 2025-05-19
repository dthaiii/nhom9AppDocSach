package com.example.nhom9appdocsach.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Model.Category;
import com.example.nhom9appdocsach.Model.Pdf;
import com.example.nhom9appdocsach.R;
import com.example.nhom9appdocsach.databinding.ActivityPdfEditBinding;

import java.util.ArrayList;

public class PdfEditActivity extends AppCompatActivity {
    private ActivityPdfEditBinding binding;
    private String bookId;
    private ProgressDialog progressDialog;
    private ArrayList<String> categoryTitleArraylist, categoryIdArraylist;
    private static final String TAG = "BOOK_EDIT_TAG";
    private Uri imageUri; // Uri cho ảnh được chọn
    private String selectedCategoryId = "", selectedCategoryTitle = "";
    private DatabaseHandel dbHelper;
    private Pdf currentPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHandel(this);

        bookId = getIntent().getStringExtra("bookId");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Đợi 1 chút nha!!!");
        progressDialog.setCanceledOnTouchOutside(false);

        loadCategories();
        loadBookInfo();

        binding.btnback.setOnClickListener(v -> finish());
        binding.btnupdate.setOnClickListener(v -> validateData());
        binding.edttheloai.setOnClickListener(v -> categoryDialog());
        binding.CVImage.setOnClickListener(v -> chooseImage());
    }

    private void categoryDialog() {
        categoryIdArraylist = new ArrayList<>();
        categoryTitleArraylist = new ArrayList<>();
        ArrayList<Category> categories = dbHelper.getAllCategories();
        for (Category cate : categories) {
            categoryIdArraylist.add(cate.getId());
            categoryTitleArraylist.add(cate.getCategory());
        }
    }

    private void loadCategories() {
        categoryIdArraylist = new ArrayList<>();
        categoryTitleArraylist = new ArrayList<>();
        ArrayList<Category> categories = dbHelper.getAllCategories();
        for (Category cate : categories) {
            categoryIdArraylist.add(cate.getId());
            categoryTitleArraylist.add(cate.getCategory());
        }
    }

    private void validateData() {
        String title = binding.edttenbook.getText().toString().trim();
        String description = binding.edtmota.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Vui lòng nhập tên sách", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedCategoryId)) {
            Toast.makeText(this, "Vui lòng chọn thể loại", Toast.LENGTH_SHORT).show();
        } else {
            updateBook(title, description, imageUri != null ? imageUri.toString() : null);
        }
    }

    private void updateBook(String title, String description, String imageUrl) {
        progressDialog.setMessage("Đang cập nhật thông tin sách...");
        progressDialog.show();

        // Lấy dữ liệu Pdf hiện tại
        Pdf pdf = dbHelper.getBookPdfById(bookId);
        if (pdf == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Không tìm thấy sách", Toast.LENGTH_SHORT).show();
            return;
        }
        pdf.setTitle(title);
        pdf.setDescription(description);
        pdf.setCategoryId(selectedCategoryId);
        if (imageUrl != null) {
            pdf.setImageThumb(imageUrl);
        }
        dbHelper.insertPdf(pdf); // Sử dụng insertPdf với CONFLICT_REPLACE để update

        progressDialog.dismiss();
        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void loadBookInfo() {
        Pdf pdf = dbHelper.getBookPdfById(bookId);
        if (pdf == null) return;
        currentPdf = pdf;
        selectedCategoryId = pdf.getCategoryId();

        binding.edttenbook.setText(pdf.getTitle());
        binding.edtmota.setText(pdf.getDescription());

        if (!TextUtils.isEmpty(pdf.getImageThumb()) && !pdf.getImageThumb().equals("null")) {
            try {
                Glide.with(PdfEditActivity.this)
                        .load(pdf.getImageThumb())
                        .placeholder(R.drawable.avatar)
                        .error(R.drawable.avatar)
                        .into(binding.imageThumb);
            } catch (Exception e) {
                Log.e(TAG, "Lỗi load ảnh: ", e);
            }
        }

        loadCategoryInfo(selectedCategoryId);
    }

    private void loadCategoryInfo(String categoryId) {
        Category cate = dbHelper.getCategoryById(categoryId);
        if (cate != null) {
            binding.edttheloai.setText(cate.getCategory());
        }
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        galleryActivityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            imageUri = data.getData();
                            if (imageUri != null) {
                                getContentResolver().takePersistableUriPermission(imageUri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                Log.d(TAG, "onActivityResult: lấy ảnh từ thư viện " + imageUri);
                                Glide.with(PdfEditActivity.this).load(imageUri).into(binding.imageThumb);
                            }
                        }
                    }
                }
            }
    );
}