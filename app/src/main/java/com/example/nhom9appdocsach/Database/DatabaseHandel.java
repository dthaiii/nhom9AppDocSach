package com.example.nhom9appdocsach.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.nhom9appdocsach.Model.Book;
import com.example.nhom9appdocsach.Model.Category;
import com.example.nhom9appdocsach.Model.Comment;
import com.example.nhom9appdocsach.Model.Favorite;
import com.example.nhom9appdocsach.Model.ListPdf;
import com.example.nhom9appdocsach.Model.Noti;
import com.example.nhom9appdocsach.Model.User;
import com.example.nhom9appdocsach.Model.Pdf;

import java.util.ArrayList;

public class DatabaseHandel extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Book.db";
    public static final int DATABASE_VERSION = 3;

    public DatabaseHandel(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //User Table
        db.execSQL("CREATE TABLE user (" + " uid TEXT PRIMARY KEY," +
                "email TEXT," +
                "name TEXT," +
                "profile TEXT," +
                "image TEXT," +
                "usertype TEXT," +
                "timestamp LONG," +
                "password TEXT" +
                ")");
        db.execSQL("INSERT INTO user (uid, email, name, profile, image, usertype, timestamp, password) " +
                "VALUES (" +
                "'admin', 'thai@gmail.com', 'Thái', '', '', 'admin', 0 , 'thai123')");
        //Book Table
        db.execSQL("CREATE TABLE book (" + " bookId TEXT PRIMARY KEY," +
                "title TEXT," +
                "description TEXT," +
                "url TEXT," +
                "categoryId TEXT," +
                "imageThumb TEXT," +
                "lastReadPage INTEGER," +
                "timestamp LONG," +
                "viewsCount LONG," +
                "downloadsCount LONG" +
                ")");
        //Category Table
        db.execSQL("CREATE TABLE category (" + " id TEXT PRIMARY KEY," +
                "category TEXT," +
                "uid TEXT," +
                "timestamp LONG" +
                ")");
        //Comment table
        db.execSQL("CREATE TABLE comment (" + " id TEXT PRIMARY KEY," +
                "bookId TEXT," +
                "comment TEXT," +
                "uid TEXT," +
                "timestamp LONG" +
                ")");
        //Noti table
        db.execSQL("CREATE TABLE noti (" + " id TEXT PRIMARY KEY," +
                "bookId TEXT," +
                "title TEXT," +
                "message TEXT," +
                "uid TEXT," +
                "timestamp LONG" +
                ")");
        //Favor table
        db.execSQL("CREATE TABLE favorite (" + " bookId TEXT," +
                "uid TEXT," +
                "timestamp LONG," +
                "PRIMARY KEY (bookId, uid)" +
                ")");
        //List Pdf table
        db.execSQL("CREATE TABLE listPdf (" + " id TEXT PRIMARY KEY," +
                "url TEXT," +
                "title TEXT," +
                "storageUrl TEXT," +
                "viewsCount LONG," +
                "downloadsCount LONG," +
                "imageThumb TEXT," +
                "timestamp LONG" +
                ")");
        // Pdf table
        db.execSQL("CREATE TABLE Pdf (" + " id TEXT PRIMARY KEY," +
                "uid TEXT," +
                "title TEXT," +
                "description TEXT," +
                "url TEXT," +
                "categoryId TEXT," +
                "imageThumb TEXT," +
                "timestamp LONG," +
                "viewsCount LONG," +
                "downloadsCount LONG," +
                "lastReadPage LONG," +
                "favorite BOOLEAN" +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS book");
        db.execSQL("DROP TABLE IF EXISTS category");
        db.execSQL("DROP TABLE IF EXISTS comment");
        db.execSQL("DROP TABLE IF EXISTS noti");
        db.execSQL("DROP TABLE IF EXISTS favorite");
        db.execSQL("DROP TABLE IF EXISTS listPdf");
        db.execSQL("DROP TABLE IF EXISTS Pdf");
        onCreate(db);
    }

    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uid", user.getUid());
        values.put("email", user.getEmail());
        values.put("name", user.getName());
        values.put("profile", user.getProfile());
        values.put("image", user.getImage());
        values.put("timestamp", user.getTimestamp() > 0 ? user.getTimestamp() : System.currentTimeMillis());
        values.put("usertype", user.getUsertype());
        values.put("password", user.getPassword());
        long res = db.insertWithOnConflict("user", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return res;
    }

    public User getUserById(String uid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE uid = ?", new String[]{uid});
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow("uid")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("profile")),
                    cursor.getString(cursor.getColumnIndexOrThrow("image")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                    cursor.getString(cursor.getColumnIndexOrThrow("usertype")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password"))
            );
        }
        cursor.close();
        db.close();
        return user;
    }

    public ArrayList<User> getAllUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<User> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM user", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new User(
                        cursor.getString(cursor.getColumnIndexOrThrow("uid")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("profile")),
                        cursor.getString(cursor.getColumnIndexOrThrow("image")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                        cursor.getString(cursor.getColumnIndexOrThrow("usertype")),
                        cursor.getString(cursor.getColumnIndexOrThrow("password"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void deleteUser(String uid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("user", "uid=?", new String[]{uid});
        db.close();
    }

    // Book crud
    public long insertBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("bookId", book.getBookId());
        values.put("title", book.getTitle());
        values.put("description", book.getDescription());
        values.put("url", book.getUrl());
        values.put("categoryId", book.getCategoryId());
        values.put("imageThumb", book.getImageThump());
        values.put("lastReadPage", book.getLastReadPage());
        values.put("downloadsCount", book.getDownloadsCount());
        long res = db.insertWithOnConflict("book", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return res;
    }

    // lấy dữ liệu theo id
    public Book getBookById(String bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM book WHERE bookId = ?", new String[]{bookId});
        Book book = null;
        if (cursor.moveToFirst()) {
            book = new Book(
                    cursor.getString(cursor.getColumnIndexOrThrow("bookId")),
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    cursor.getString(cursor.getColumnIndexOrThrow("url")),
                    cursor.getString(cursor.getColumnIndexOrThrow("categoryId")),
                    cursor.getString(cursor.getColumnIndexOrThrow("imageThumb")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("lastReadPage")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("downloadsCount")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("viewsCount"))
            );
        }
        cursor.close();
        db.close();
        return book;
    }

    /// lấy toàn bộ dữ liệu trong bảng user và gán vào list
    public ArrayList<Book> getAllBook() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Book> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM book", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Book(
                        cursor.getString(cursor.getColumnIndexOrThrow("bookId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("url")),
                        cursor.getString(cursor.getColumnIndexOrThrow("categoryId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imageThumb")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("lastReadPage")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("downloadsCount")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("viewsCount"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void deleteBook(String bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("book", "bookId=?", new String[]{bookId});
        db.close();
    }

    public void updateLastReadPage(String bookId, long lastReadPage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("lastReadPage", lastReadPage);
        db.update("book", values, "bookId=?", new String[]{bookId});
        db.close();
    }

    // Category crud
    public long insertCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", category.getId());
        values.put("category", category.getCategory());
        values.put("timestamp", category.getTimestamp());
        long res = db.insertWithOnConflict("category", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return res;
    }

    public Category getCategoryById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM category WHERE id = ?", new String[]{id});
        Category cate = null;
        if (cursor.moveToFirst()) {
            cate = new Category(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("category")),
                    cursor.getString(cursor.getColumnIndexOrThrow("uid")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
            );
        }
        cursor.close();
        db.close();
        return cate;
    }

    public ArrayList<Category> getAllCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Category> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM category", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Category(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getString(cursor.getColumnIndexOrThrow("uid")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void deleteCategory(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("category", "id=?", new String[]{id});
        db.close();
    }

    //Comment crud
    public long insertComment(Comment comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("bookId", comment.getBookId());
        values.put("id", comment.getId());
        values.put("comment", comment.getComment());
        values.put("timestamp", comment.getTimestamp());
        values.put("uid", comment.getUid());
        long res = db.insertWithOnConflict("comment", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return res;
    }

    public ArrayList<Comment> getCommentsByBookId(String bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Comment> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM comment WHERE bookId=?", new String[]{bookId});
        if (cursor.moveToFirst()) {
            do {
                list.add(new Comment(
                        cursor.getString(cursor.getColumnIndexOrThrow("bookId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("comment")),
                        cursor.getString(cursor.getColumnIndexOrThrow("uid")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void deleteComment(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("comment", "id=?", new String[]{id});
        db.close();
    }

    // --- NOTIFICATION CRUD ---
    public long insertNotification(Noti n) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("bookId", n.getBookId());
        values.put("id", n.getId());
        values.put("message", n.getMessage());
        values.put("timestamp", n.getTimestamp());
        values.put("title", n.getTitle());
        values.put("uid", n.getUid());
        long res = db.insertWithOnConflict("noti", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return res;
    }

    public ArrayList<Noti> getAllNotifications() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Noti> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM noti", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Noti(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("bookId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("message")),
                        cursor.getString(cursor.getColumnIndexOrThrow("uid")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void deleteNotification(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("noti", "id=?", new String[]{id});
        db.close();
    }

    // --- FAVORITE CRUD ---
    public long insertFavorite(Favorite fav) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("bookId", fav.getBookId());
        values.put("uid", fav.getUid());
        values.put("timestamp", fav.getTimestamp());
        long res = db.insertWithOnConflict("favorite", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return res;
    }

    public ArrayList<Favorite> getFavoritesByUser(String uid) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Favorite> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM favorite WHERE uid=?", new String[]{uid});
        if (cursor.moveToFirst()) {
            do {
                list.add(new Favorite(
                        cursor.getString(cursor.getColumnIndexOrThrow("bookId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("uid")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void deleteFavorite(String bookid, String uid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("favorite", "bookid=? AND uid=?", new String[]{bookid, uid});
        db.close();
    }

    // --- PDF CRUD ---
    public long insertPdf(Pdf pdf) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", pdf.getId());
        values.put("uid", pdf.getUid());
        values.put("title", pdf.getTitle());
        values.put("description", pdf.getDescription());
        values.put("url", pdf.getUrl());
        values.put("categoryId", pdf.getCategoryId());
        values.put("imageThumb", pdf.getImageThumb());
        values.put("timestamp", pdf.getTimestamp());
        values.put("viewsCount", pdf.getViewsCount());
        values.put("downloadsCount", pdf.getDownloadsCount());
        values.put("lastReadPage", pdf.getLastReadPage());
        values.put("favorite", true);
        long res = db.insertWithOnConflict("pdf", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return res;
    }

    public ArrayList<Pdf> getBooksByCategory(String categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Pdf> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM pdf WHERE categoryId=?", new String[]{categoryId});
        if (cursor.moveToFirst()) {
            do {
                Pdf listPdf = new Pdf();
                listPdf.setCategoryId(cursor.getString(cursor.getColumnIndexOrThrow("categoryId")));
                listPdf.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                listPdf.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                listPdf.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                listPdf.setUid(cursor.getString(cursor.getColumnIndexOrThrow("uid")));
                listPdf.setUrl(cursor.getString(cursor.getColumnIndexOrThrow("url")));
                listPdf.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")));
                listPdf.setImageThumb(cursor.getString(cursor.getColumnIndexOrThrow("imageThumb")));
                listPdf.setViewsCount(cursor.getLong(cursor.getColumnIndexOrThrow("viewsCount")));
                listPdf.setDownloadsCount(cursor.getLong(cursor.getColumnIndexOrThrow("downloadsCount")));
                listPdf.setLastReadPage(cursor.getLong(cursor.getColumnIndexOrThrow("lastReadPage")));
                listPdf.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow("favorite")) == 1);
                list.add(listPdf);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // Cập nhật toàn bộ sách có categoryId cũ thành categoryId mới
    public void updateBooksCategory(String oldCategoryId, String newCategoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", newCategoryId);
        db.update("pdf", values, "categoryId=?", new String[]{oldCategoryId});
        db.update("book", values, "categoryId=?", new String[]{oldCategoryId});
        db.close();
    }

    // Lấy id của category bằng tên category
    public String getCategoryByName(String categoryName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String id = null;
        Cursor cursor = db.rawQuery("SELECT id FROM category WHERE category = ?", new String[]{categoryName});
        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
        }
        cursor.close();
        db.close();
        return id;
    }

    // Lấy danh sách ListPdf theo categoryId
    public ArrayList<ListPdf> getBooksByCategoryId(String categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ListPdf> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM listPdf WHERE id = ?", new String[]{categoryId});
        if (cursor.moveToFirst()) {
            do {
                ListPdf listPdf = new ListPdf(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("url")),
                        cursor.getString(cursor.getColumnIndexOrThrow("storageUrl")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imageThumb")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("viewsCount")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("downloadsCount")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
                );
                list.add(listPdf);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }


    // Lấy Pdf theo id
    public Pdf getBookPdfById(String bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Pdf pdf = null;
        Cursor cursor = db.rawQuery("SELECT * FROM pdf WHERE id = ?", new String[]{bookId});
        if (cursor.moveToFirst()) {
            pdf = new Pdf();
            pdf.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
            pdf.setUid(cursor.getString(cursor.getColumnIndexOrThrow("uid")));
            pdf.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            pdf.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            pdf.setUrl(cursor.getString(cursor.getColumnIndexOrThrow("url")));
            pdf.setCategoryId(cursor.getString(cursor.getColumnIndexOrThrow("categoryId")));
            pdf.setImageThumb(cursor.getString(cursor.getColumnIndexOrThrow("imageThumb")));
            pdf.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")));
            pdf.setViewsCount(cursor.getLong(cursor.getColumnIndexOrThrow("viewsCount")));
            pdf.setDownloadsCount(cursor.getLong(cursor.getColumnIndexOrThrow("downloadsCount")));
            pdf.setLastReadPage(cursor.getLong(cursor.getColumnIndexOrThrow("lastReadPage")));
            pdf.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow("favorite")) == 1);
        }
        cursor.close();
        db.close();
        return pdf;
    }

    // Lấy tên và avatar user từ uid
    public String[] getUserNameAndAvatar(String uid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String name = "";
        String avatar = "";
        Cursor cursor = db.rawQuery("SELECT name, image FROM user WHERE uid = ?", new String[]{uid});
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            avatar = cursor.getString(cursor.getColumnIndexOrThrow("image"));
        }
        cursor.close();
        db.close();
        return new String[]{name, avatar};
    }

    // Xóa comment theo id và bookId
    public boolean deleteCommentById(String id, String bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("comment", "id=? AND bookId=?", new String[]{id, bookId});
        db.close();
        return rows > 0;
    }

    public ArrayList<ListPdf> getAllBookWithThumb(int limit) {
        ArrayList<ListPdf> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Nếu có cột imageThumb trong listPdf thì dùng, nếu không có thì bỏ điều kiện này
        Cursor cursor = db.rawQuery(
                "SELECT * FROM listPdf WHERE imageThumb IS NOT NULL AND imageThumb != '' LIMIT ?",
                new String[]{String.valueOf(limit)}
        );
        if (cursor.moveToFirst()) {
            do {
                ListPdf pdf = new ListPdf(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("url")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("storageUrl")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imageThumb")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("viewsCount")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("downloadsCount")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
                );
                if (cursor.getColumnIndex("imageThumb") != -1) {
                    try {
                        String img = cursor.getString(cursor.getColumnIndexOrThrow("imageThumb"));
                         pdf.setImageThumb(img);
                    } catch (Exception ignore) {}
                }
                list.add(pdf);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // Lấy toàn bộ sách listPdf
    public ArrayList<ListPdf> getAllBooksListPdf() {
        ArrayList<ListPdf> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Sắp xếp theo timestamp giảm dần để lấy truyện mới nhất lên đầu
        Cursor cursor = db.rawQuery("SELECT * FROM listPdf ORDER BY timestamp DESC", null);
        if (cursor.moveToFirst()) {
            do {
                ListPdf pdf = new ListPdf(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("url")),
                        cursor.getString(cursor.getColumnIndexOrThrow("storageUrl")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imageThumb")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("viewsCount")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("downloadsCount")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
                );
                if (cursor.getColumnIndex("imageThumb") != -1) {
                    try {
                        String img = cursor.getString(cursor.getColumnIndexOrThrow("imageThumb"));
                        pdf.setImageThumb(img);
                    } catch (Exception ignore) {}
                }
                list.add(pdf);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public ArrayList<Pdf> getFavoriteBooksByUserId(String uid) {
        ArrayList<Pdf> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // JOIN bảng favorite với bảng pdf để lấy đầy đủ thông tin sách yêu thích
        String query = "SELECT p.* FROM pdf p " +
                "INNER JOIN favorite f ON p.id = f.bookId " +
                "WHERE f.uid = ?";
        Cursor cursor = db.rawQuery(query, new String[]{uid});
        if (cursor.moveToFirst()) {
            do {
                Pdf pdf = new Pdf();
                pdf.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                pdf.setUid(cursor.getString(cursor.getColumnIndexOrThrow("uid")));
                pdf.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                pdf.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                pdf.setUrl(cursor.getString(cursor.getColumnIndexOrThrow("url")));
                pdf.setCategoryId(cursor.getString(cursor.getColumnIndexOrThrow("categoryId")));
                pdf.setImageThumb(cursor.getString(cursor.getColumnIndexOrThrow("imageThumb")));
                pdf.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")));
                pdf.setViewsCount(cursor.getLong(cursor.getColumnIndexOrThrow("viewsCount")));
                pdf.setDownloadsCount(cursor.getLong(cursor.getColumnIndexOrThrow("downloadsCount")));
                pdf.setLastReadPage(cursor.getLong(cursor.getColumnIndexOrThrow("lastReadPage")));
                int favoriteIdx = cursor.getColumnIndex("favorite");
                if (favoriteIdx != -1) {
                    pdf.setFavorite(cursor.getInt(favoriteIdx) == 1);
                }
                list.add(pdf);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void updateUserPassword(String uid, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword); // Giả sử bạn lưu mật khẩu trong cột "profile" (nên đặt tên là "password" hơn)
        db.update("user", values, "uid=?", new String[]{uid});
        db.close();
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE email = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow("uid")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("profile")),
                    cursor.getString(cursor.getColumnIndexOrThrow("image")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                    cursor.getString(cursor.getColumnIndexOrThrow("usertype")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password"))
            );
        }
        cursor.close();
        db.close();
        return user;
    }

    public boolean deleteUserById(String uid) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Xóa user
        int rows = db.delete("user", "uid=?", new String[]{uid});
        db.close();
        return rows > 0;
    }

    public ArrayList<Pdf> getAllBooksPdf() {
        ArrayList<Pdf> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pdf", null);
        if (cursor.moveToFirst()) {
            do {
                Pdf pdf = new Pdf();
                pdf.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                pdf.setUid(cursor.getString(cursor.getColumnIndexOrThrow("uid")));
                pdf.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                pdf.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                pdf.setUrl(cursor.getString(cursor.getColumnIndexOrThrow("url")));
                pdf.setCategoryId(cursor.getString(cursor.getColumnIndexOrThrow("categoryId")));
                pdf.setImageThumb(cursor.getString(cursor.getColumnIndexOrThrow("imageThumb")));
                pdf.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")));
                pdf.setViewsCount(cursor.getLong(cursor.getColumnIndexOrThrow("viewsCount")));
                pdf.setDownloadsCount(cursor.getLong(cursor.getColumnIndexOrThrow("downloadsCount")));
                pdf.setLastReadPage(cursor.getLong(cursor.getColumnIndexOrThrow("lastReadPage")));
                int favoriteIdx = cursor.getColumnIndex("favorite");
                if (favoriteIdx != -1) {
                    pdf.setFavorite(cursor.getInt(favoriteIdx) == 1);
                }
                list.add(pdf);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean isEmailExists(String email) {
        boolean exists = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT 1 FROM user WHERE email = ? LIMIT 1";
            cursor = db.rawQuery(query, new String[]{email});
            exists = (cursor != null && cursor.moveToFirst());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return exists;
    }

    public long insertListPdf(ListPdf listPdf) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", listPdf.getId());
        values.put("title", listPdf.getTitle());
        values.put("url", listPdf.getUrl());
        values.put("storageUrl", listPdf.getStorageUrl());
        values.put("imageThumb", listPdf.getImageThumb());
        values.put("viewsCount", listPdf.getViewsCount());
        values.put("downloadsCount", listPdf.getDownloadsCount());
        values.put("timestamp", listPdf.getTimestamp());
        long res = db.insertWithOnConflict("listPdf", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return res;
    }
}

