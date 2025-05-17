package com.example.nhom9appdocsach.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.nhom9appdocsach.Model.User;

public class DatabaseHandel extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Book.db";
    public static final int DATABASE_VERSION = 1;
    public DatabaseHandel(Context context){
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
                "timestamp LONG" +
                ")");
        //Book Table
        db.execSQL("CREATE TABLE book (" + " bookId TEXT PRIMARY KEY," +
                "title TEXT," +
                "description TEXT," +
                "url TEXT," +
                "categoryId TEXT," +
                "imageThump TEXT," +
                "lastReadPage INTEGER," +
                "downloadsCount INTEGER" +
                ")");
        //Category Table
        db.execSQL("CREATE TABLE category (" + " id TEXT PRIMARY KEY," +
                "category TEXT," +
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
                "bookID TEXT," +
                "title TEXT," +
                "message TEXT," +
                "uid TEXT," +
                "bookId TEXT," +
                "timestamp LONG" +
                ")");
        //Favor table
        db.execSQL("CREATE TABLE favorite (" + " bookId TEXT," +
                "uid TEXT," +
                "timestamp LONG," +
                "PRIMARY KEY (bookId, uid)"+
                ")");
        //List Pdf table
        db.execSQL("CREATE TABLE listPdf (" + " id TEXT PRIMARY KEY," +
                "url TEXT," +
                "title TEXT," +
                "storageUrl TEXT," +
                "viewsCount LONG," +
                "downloadsCount LONG" +
                ")");
        //List Pdf table
        db.execSQL("CREATE TABLE Pdf (" + " id TEXT PRIMARY KEY,"+
                "uid TEXT," +
                "title TEXT," +
                "description TEXT," +
                "url TEXT," +
                "categoryId TEXT," +
                "timestamp LONG," +
                "viewsCount LONG," +
                "downloadsCount LONG," +
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
    public long insertUser(User user){

    }
}
