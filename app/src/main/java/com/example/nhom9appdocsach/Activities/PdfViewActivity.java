package com.example.nhom9appdocsach.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Model.Pdf;
import com.example.nhom9appdocsach.databinding.ActivityPdfViewBinding;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class PdfViewActivity extends AppCompatActivity {

    private ActivityPdfViewBinding binding;
    private String bookId;
    private static final String TAG = "PDF_VIEW_TAG";
    private int lastReadPage = 0;
    private PDFView pdfView;
    private String pdfUrl, pdfUriString;
    private File pdfFile;

    private static final long CACHE_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 7 days

    private DatabaseHandel dbHelper;
    private Pdf currentPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHandel(this);

        pdfView = binding.pdfView;
        binding.txtnamebook.setSelected(true);

        // Restore state if available
        if (savedInstanceState != null) {
            lastReadPage = savedInstanceState.getInt("lastReadPage", 0);
            bookId = savedInstanceState.getString("bookId");
            pdfUrl = savedInstanceState.getString("pdfUrl");
            pdfUriString = savedInstanceState.getString("pdfUriString");
        } else {
            Intent intent = getIntent();
            bookId = intent.getStringExtra("bookId");
        }

        configureViewer();

        if (isNetworkAvailable()) {
            loadBookDetails();
        } else {
            loadFromCache();
        }

        binding.btnback.setOnClickListener(v -> {
            saveLastReadPage();
            onBackPressed();
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("lastReadPage", pdfView.getCurrentPage());
        outState.putString("bookId", bookId);
        outState.putString("pdfUrl", pdfUrl);
        outState.putString("pdfUriString", pdfUriString);
    }

    private void configureViewer() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        }
        return false;
    }

    private void loadFromCache() {
        File cacheFile = new File(getCacheDir(), "pdf_" + bookId + ".pdf");
        if (cacheFile.exists() && !isCacheExpired(cacheFile)) {
            loadPdfFromFile(cacheFile);
        } else {
            showNoConnectionError();
        }
    }

    private boolean isCacheExpired(File cacheFile) {
        long lastModified = cacheFile.lastModified();
        return System.currentTimeMillis() - lastModified > CACHE_EXPIRATION_TIME;
    }

    private void showNoConnectionError() {
        binding.progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
    }

    private void loadBookDetails() {
        binding.progressBar.setVisibility(View.VISIBLE);

        currentPdf = dbHelper.getBookPdfById(bookId);
        if (currentPdf == null) {
            handleError("Không tìm thấy sách");
            return;
        }
        pdfUrl = currentPdf.getUrl();
        pdfUriString = currentPdf.getUrl(); // Nếu lưu local, đây là Uri string
        lastReadPage = (int) currentPdf.getLastReadPage();

        String title = currentPdf.getTitle();
        if (title != null) {
            binding.txtnamebook.setText(title);
        }

        if (pdfUriString != null && pdfUriString.startsWith("content://")) {
            // Đọc file PDF qua URI
            try {
                Uri pdfUri = Uri.parse(pdfUriString);
                loadPdfFromUri(pdfUri);
            } catch (Exception e) {
                handleError("Lỗi đọc file: " + e.getMessage());
            }
        } else if (pdfUrl != null && (pdfUrl.startsWith("file://") || new File(pdfUrl).exists())) {
            // Đọc từ local file path hoặc file absolute path
            try {
                File file;
                if (pdfUrl.startsWith("file://")) {
                    file = new File(Uri.parse(pdfUrl).getPath());
                } else {
                    file = new File(pdfUrl);
                }
                if (file.exists()) {
                    loadPdfFromFile(file);
                } else {
                    handleError("File PDF không tồn tại: " + pdfUrl);
                }
            } catch (Exception e) {
                handleError("Lỗi đọc file: " + e.getMessage());
            }
        } else {
            handleError("Đường dẫn tệp PDF không hợp lệ hoặc chưa hỗ trợ: " + pdfUrl);
        }
    }

    private void loadPdfFromUri(Uri uri) {
        try {
            pdfView.fromStream(getContentResolver().openInputStream(uri))
                    .defaultPage(lastReadPage)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .enableAnnotationRendering(false)
                    .scrollHandle(new DefaultScrollHandle(this))
                    .spacing(0)
                    .autoSpacing(false)
                    .pageFitPolicy(FitPolicy.WIDTH)
                    .fitEachPage(true)
                    .pageSnap(false)
                    .nightMode(false)
                    .enableAntialiasing(true)
                    .pageFling(true)
                    .onLoad(nbPages -> {
                        float width = pdfView.getWidth();
                        float height = pdfView.getHeight();
                        float ratio = width / height;
                        pdfView.setMinZoom(ratio * 0.5f);
                        pdfView.setMidZoom(ratio);
                        pdfView.setMaxZoom(ratio * 3.0f);
                    })
                    .onPageChange((page, pageCount) -> {
                        int currentPage = page + 1;
                        binding.txtpage.setText(currentPage + "/" + pageCount);
                    })
                    .onError(t -> handleError("Lỗi tải tệp PDF: " + t.getMessage()))
                    .onPageError((page, t) -> handleError("Lỗi ở trang " + page + ": " + t.getMessage()))
                    .load();
            binding.progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            handleError("Lỗi đọc file: " + e.getMessage());
        }
    }

    private void loadPdfFromFile(File pdfFile) {
        try {
            pdfView.fromFile(pdfFile)
                    .defaultPage(lastReadPage)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .enableAnnotationRendering(false)
                    .scrollHandle(new DefaultScrollHandle(this))
                    .spacing(0)
                    .autoSpacing(false)
                    .pageFitPolicy(FitPolicy.WIDTH)
                    .fitEachPage(true)
                    .pageSnap(false)
                    .nightMode(false)
                    .enableAntialiasing(true)
                    .pageFling(true)
                    .onLoad(nbPages -> {
                        float width = pdfView.getWidth();
                        float height = pdfView.getHeight();
                        float ratio = width / height;
                        pdfView.setMinZoom(ratio * 0.5f);
                        pdfView.setMidZoom(ratio);
                        pdfView.setMaxZoom(ratio * 3.0f);
                    })
                    .onPageChange((page, pageCount) -> {
                        int currentPage = page + 1;
                        binding.txtpage.setText(currentPage + "/" + pageCount);
                    })
                    .onError(t -> handleError("Lỗi tải tệp PDF: " + t.getMessage()))
                    .onPageError((page, t) -> handleError("Lỗi ở trang " + page + ": " + t.getMessage()))
                    .load();
            binding.progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            handleError("Lỗi tải tệp: " + e.getMessage());
        }
    }

    private void handleError(String errorMessage) {
        binding.progressBar.setVisibility(View.GONE);
        Log.e(TAG, errorMessage);
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void cleanupOldCache() {
        File[] cacheFiles = getCacheDir().listFiles((dir, name) -> name.startsWith("pdf_"));
        if (cacheFiles != null) {
            for (File file : cacheFiles) {
                if (isCacheExpired(file)) {
                    file.delete();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveLastReadPage();
    }

    private void saveLastReadPage() {
        if (pdfView != null && pdfView.getCurrentPage() >= 0 && currentPdf != null) {
            dbHelper.updateLastReadPage(bookId, pdfView.getCurrentPage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanupOldCache();
    }
}