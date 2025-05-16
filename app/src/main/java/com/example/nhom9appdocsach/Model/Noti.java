package com.example.nhom9appdocsach.Model;

public class Noti {
    String id, title, message, uid, bookId;
    long timestamp;

    public Noti(){

    }
    public Noti(String id, String title, String message, String uid, String bookId, long timestamp) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.uid = uid;
        this.bookId = bookId;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
