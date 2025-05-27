package com.example.nhom9appdocsach;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Locale;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static String formatTimestamp(long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        return DateFormat.format("dd/MM/yyyy", cal).toString();
    }

    // Xóa sách và khỏi yêu thích
    public static void deleteBook(Context context, String bookId, String bookUrl, String bookTitle) {
        DatabaseHandel dbHelper = new DatabaseHandel(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("book", "bookId=?", new String[]{bookId});
        db.delete("favorite", "bookId=?", new String[]{bookId});
        db.close();
        Toast.makeText(context, "Xóa sách thành công", Toast.LENGTH_SHORT).show();
    }

    // Hiển thị kích thước file PDF
    public static void LoadPdfSize(Context context, String url, String bookId, TextView txtsize) {
        DatabaseHandel dbHelper = new DatabaseHandel(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT url FROM book WHERE bookId=?", new String[]{bookId});
        if (cursor.moveToFirst()) {
            String pdfPath = cursor.getString(0);
            File file = new File(pdfPath);
            if (file.exists()) {
                long bytes = file.length();
                double kb = bytes / 1024.0;
                double mb = kb / 1024.0;
                if (mb >= 1) txtsize.setText(String.format("%.2f MB", mb));
                else if (kb >= 1) txtsize.setText(String.format("%.2f KB", kb));
                else txtsize.setText(String.format("%d bytes", bytes));
            } else {
                txtsize.setText("Không tìm thấy file");
            }
        }
        cursor.close();
        db.close();
    }

    // Đọc và hiển thị trang đầu tiên của file PDF
    public static void loadPdfFromUrlSinglePage(Context context, String bookId, PDFView pdfView, ProgressBar progressBar, TextView txtpage) {
        DatabaseHandel dbHelper = new DatabaseHandel(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT url FROM book WHERE bookId=?", new String[]{bookId});
        if (cursor.moveToFirst()) {
            String pdfPath = cursor.getString(0);
            File file = new File(pdfPath);
            if (file.exists()) {
                pdfView.fromFile(file)
                        .pages(0)
                        .spacing(0)
                        .swipeHorizontal(false)
                        .enableSwipe(false)
                        .onLoad(nbPages -> {
                            progressBar.setVisibility(View.INVISIBLE);
                            if (txtpage != null) txtpage.setText(String.valueOf(nbPages));
                        })
                        .onError(t -> progressBar.setVisibility(View.INVISIBLE))
                        .onPageError((page, t) -> progressBar.setVisibility(View.INVISIBLE))
                        .load();
            } else {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
        cursor.close();
        db.close();
    }

    // Hiển thị ảnh thumbnail từ file local
    public static void loadImageFromUrl(Context context, String bookId, ImageView imageView, ProgressBar progressBar) {
        DatabaseHandel dbHelper = new DatabaseHandel(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT imageThumb FROM book WHERE bookId=?", new String[]{bookId});
        if (cursor.moveToFirst()) {
            String imageThumbPath = cursor.getString(0);
            File file = new File(imageThumbPath);
            Log.d("ThumbnailDebug", "Thumb path: " + imageThumbPath + ", Exists: " + new File(imageThumbPath).exists());
            if (file.exists()) {
                Glide.with(context)
                        .load(file)
                        .placeholder(R.drawable.skeleton)
                        .error(R.drawable.warning)
                        .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.INVISIBLE);
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.INVISIBLE);
                                return false;
                            }
                        })
                        .into(imageView);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                imageView.setImageResource(R.drawable.skeleton);
            }
        }
        cursor.close();
        db.close();
    }

    // Hiển thị tên thể loại
    public static void loadCategory(Context context, String categoryId, TextView txttheloai) {
        DatabaseHandel dbHelper = new DatabaseHandel(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //  bảng catagory có cột id và category(tên thể loại)
        Cursor cursor = db.rawQuery("SELECT category FROM category WHERE id=?", new String[]{categoryId});
        if (cursor.moveToFirst()) {
            txttheloai.setText(cursor.getString(0));
        }
        cursor.close();
        db.close();
    }

    // Tăng số lượt xem sách
    public static void incrementBookViewCount(Context context, String bookId) {
        DatabaseHandel dbHelper = new DatabaseHandel(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT viewsCount FROM book WHERE bookId=?", new String[]{bookId});
        long views = 0;
        if (cursor.moveToFirst()) {
            views = cursor.getLong(0);
        }
        cursor.close();
        views++;
        ContentValues values = new ContentValues();
        values.put("viewsCount", views);
        db.update("book", values, "bookId=?", new String[]{bookId});
        db.close();
    }

    // Tải file PDF về máy (copy file local sang thư mục Download)
    public static void downloadBook(Context context, String bookId, String bookTitle, String sourcePath) {
        File src = new File(sourcePath);
        if (!src.exists()){
            Toast.makeText(context, "File không tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }
        File dst = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), bookTitle + ".pdf");
        try{
            FileInputStream in = new FileInputStream(src);
            FileOutputStream out = new FileOutputStream(dst);
            byte[] buffer = new byte[1024];
            int len;
            while((len = in.read(buffer)) > 0){
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
            Toast.makeText(context, "Tải sách thành công: " + bookTitle, Toast.LENGTH_SHORT).show();
            incrementBookDownloadCount(context, bookId);
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "Lỗi khi tải sách: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    // Tăng số lượt tải sách
    private static void incrementBookDownloadCount(Context context, String bookId) {
        DatabaseHandel dbHelper = new DatabaseHandel(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT downloadsCount FROM book WHERE bookId=?", new String[]{bookId});
        long downloads = 0;
        if (cursor.moveToFirst()) {
            downloads = cursor.getLong(0);
        }
        cursor.close();
        downloads++;
        ContentValues values = new ContentValues();
        values.put("downloadsCount", downloads);
        db.update("book", values, "bookId=?", new String[]{bookId});
        db.close();
    }

    // Thêm sách vào yêu thích
    public static void addToFavorite(Context context, String bookId, String uid) {
        DatabaseHandel dbHelper = new DatabaseHandel(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("bookId", bookId);
        values.put("uid", uid);
        values.put("timestamp", System.currentTimeMillis());
        db.insertWithOnConflict("favorite", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        Toast.makeText(context, "Yêu thích", Toast.LENGTH_SHORT).show();
    }

    // Xóa sách khỏi yêu thích
    public static void removeFromFavorite(Context context, String bookId, String uid) {
        DatabaseHandel dbHelper = new DatabaseHandel(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("favorite", "bookId=? AND uid=?", new String[]{bookId, uid});
        db.close();
        Toast.makeText(context, "Xóa yêu thích", Toast.LENGTH_SHORT).show();
    }
}