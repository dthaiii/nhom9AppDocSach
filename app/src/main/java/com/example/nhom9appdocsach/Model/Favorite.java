package com.example.nhom9appdocsach.Model;

public class Favorite {
    String bookId, uid;
    long timestamp;
    public Favorite(){

    }
    public Favorite(String bookId, String uid, long timestamp){
        this.bookId = bookId;
        this.uid = uid;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
