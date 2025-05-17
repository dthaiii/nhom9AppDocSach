package com.example.nhom9appdocsach.Model;

public class Book {
    String bookId, title, description, url, categoryId, imageThump;
    long  downloadsCount;
    long lastReadPage;
     public Book(){

     }
     public Book(String bookId, String title, String description, String url, String categoryId, String imageThump, long downloadsCount, long lastReadPage) {
         this.bookId = bookId;
         this.title = title;
         this.description = description;
         this.url = url;
         this.categoryId = categoryId;
         this.imageThump = imageThump;
         this.downloadsCount = downloadsCount;
         this.lastReadPage = lastReadPage;
     }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getImageThump() {
        return imageThump;
    }

    public void setImageThump(String imageThump) {
        this.imageThump = imageThump;
    }

    public long getDownloadsCount() {
        return downloadsCount;
    }

    public void setDownloadsCount(long downloadsCount) {
        this.downloadsCount = downloadsCount;
    }

    public long getLastReadPage() {
        return lastReadPage;
    }

    public void setLastReadPage(long lastReadPage) {
        this.lastReadPage = lastReadPage;
    }
}
