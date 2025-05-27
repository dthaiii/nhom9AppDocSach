package com.example.nhom9appdocsach.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Model.Book;
import com.example.nhom9appdocsach.Model.ListPdf;
import com.example.nhom9appdocsach.Model.Noti;
import com.example.nhom9appdocsach.Model.Category;
import com.example.nhom9appdocsach.Model.Pdf;
import com.example.nhom9appdocsach.databinding.ActivityAddBookBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class AddBookActivity extends AppCompatActivity {
    private ActivityAddBookBinding binding;
    private ArrayList<String> categoryTitleArrayList, categoryIdArrayList;
    private static final int PDF_PICK_CODE = 1000;
    private static final String TAG = "ADD_PDF_TAG";
    private Uri pdfUri = null;
    private Uri imageUri;
    private String selectedCategoryID, selectedCategoryTitle;
    private DatabaseHandel dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHandel(this);

        loadPdfCategories();

        binding.btnattach.setOnClickListener(v -> pdfPickIntent());
        binding.edttheloai.setOnClickListener(v -> CategoryPickDialog());
        binding.btnsubmit.setOnClickListener(v -> {
            validateData();
            clear();
        });
        binding.btnback.setOnClickListener(v -> onBackPressed());
        binding.CVImage.setOnClickListener(v -> pickImageGallery());
    }

    private void clear() {
        binding.edttenbook.setText("");
        binding.edtmota.setText("");
        binding.edttheloai.setText("");
        binding.imageThumb.setImageResource(0);
        pdfUri = null;
        imageUri = null;
    }

    private String title="", description="";
    private void validateData() {
        title = binding.edttenbook.getText().toString().trim();
        description = binding.edtmota.getText().toString().trim();

        if (TextUtils.isEmpty(title)){
            Toast.makeText(this, "Nhập tên", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Nhập mô tả", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedCategoryTitle)) {
            Toast.makeText(this, "Chọn thể loại", Toast.LENGTH_SHORT).show();
        } else if (pdfUri == null) {
            Toast.makeText(this, "Chọn tệp PDF", Toast.LENGTH_SHORT).show();
        } else if (imageUri == null) {
            Toast.makeText(this, "Chọn ảnh bìa sách", Toast.LENGTH_SHORT).show();
        } else {
            saveFilesAndData();
        }
    }

    private void saveFilesAndData() {
        Toast.makeText(this, "Đang lưu file...", Toast.LENGTH_SHORT).show();

        try {
            long timestamp = System.currentTimeMillis();
            // Copy PDF vào thư mục app
            String pdfFileName = "book_" + timestamp + ".pdf";
            String pdfPath = copyUriToInternalStorage(pdfUri, pdfFileName);

            // Copy ảnh vào thư mục app
            String imageFileName = "cover_" + timestamp + ".jpg";
            String imagePath = copyUriToInternalStorage(imageUri, imageFileName);

            // Tạo đối tượng Book
            Book book = new Book(
                    String.valueOf(timestamp), // bookId
                    title,
                    description,
                    pdfPath,
                    selectedCategoryID,
                    imagePath,
                    0,
                    timestamp,
                    0,
                    0

            );
            long res = dbHelper.insertBook(book);
            Pdf pdf = new Pdf();
            pdf.setId(book.getBookId());
            pdf.setUid(getCurrentUserId());
            pdf.setTitle(book.getTitle());
            pdf.setDescription(book.getDescription());
            pdf.setUrl(book.getUrl());
            pdf.setCategoryId(book.getCategoryId());
            pdf.setImageThumb(book.getImageThump());
            pdf.setTimestamp(System.currentTimeMillis());
            pdf.setViewsCount(0);
            pdf.setDownloadsCount(0);
            pdf.setLastReadPage(0);
            pdf.setFavorite(false);
            dbHelper.insertPdf(pdf);
            ListPdf listPdf = new ListPdf(
                    book.getBookId(),           // id
                    book.getTitle(),            // title
                    book.getUrl(),              // url (file path tới PDF)
                    "",                         // storageUrl
                    book.getImageThump(),       // imageThumb (ảnh bìa)
                    0,                          // viewsCount
                    0, // downloadsCount
                    timestamp
            );
            dbHelper.insertListPdf(listPdf);

            // Tạo thông báo
            String userName = dbHelper.getUserById(getCurrentUserId()).getName();
            Noti noti = new Noti(
                    book.getBookId(),
                    String.valueOf(timestamp),
                    userName + " đã đăng tải sách mới: " + title,
                    getCurrentUserId(),
                    "Sách mới: " + title,
                    timestamp
            );
            dbHelper.insertNotification(noti);

            if (res > 0) {
                Toast.makeText(this, "Thêm sách thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi khi thêm sách!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    // Copy file từ Url về bộ nhớ app, trả về path
    private String copyUriToInternalStorage(Uri uri, String fileName) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File file = new File(getFilesDir(), fileName);
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] buf = new byte[4096];
        int len;
        while ((len = inputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, len);
        }
        outputStream.close();
        inputStream.close();
        return file.getAbsolutePath();
    }

    // Lấy userId hiện tại
    private String getCurrentUserId() {
        return getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("uid", "");
    }

    //--- Load thể loại từ SQLite
    private void loadPdfCategories() {
        categoryTitleArrayList = new ArrayList<>();
        categoryIdArrayList = new ArrayList<>();
        ArrayList<Category> categoryList = dbHelper.getAllCategories();
        for (Category cat : categoryList) {
            categoryTitleArrayList.add(cat.getCategory());
            categoryIdArrayList.add(cat.getId());
        }
    }

    //Chọn thể loại
    private void CategoryPickDialog() {
        String[] categoriesArray = categoryTitleArrayList.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn thể loại")
                .setItems(categoriesArray, (dialog, which) -> {
                    selectedCategoryTitle = categoryTitleArrayList.get(which);
                    selectedCategoryID = categoryIdArrayList.get(which);
                    binding.edttheloai.setText(selectedCategoryTitle);
                })
                .show();
    }

    //Chọn ảnh bìa truyện
    private void pickImageGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        galleryActivityResultLauncher.launch(intent);
    }

    //Lấy ảnh từ thư viện
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        imageUri = data.getData();
                        if (imageUri != null) {
                            getContentResolver().takePersistableUriPermission(imageUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Glide.with(this).load(imageUri).into(binding.imageThumb);
                        }
                    }
                }
            }
    );

    //Lấy pdf từ thư mục của máy
    private void pdfPickIntent() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if(requestCode == PDF_PICK_CODE){
                pdfUri = data.getData();
            }
        }
    }
}