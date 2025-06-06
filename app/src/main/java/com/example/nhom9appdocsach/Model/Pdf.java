package com.example.nhom9appdocsach.Model;

public class Pdf {
    String uid, id, title, description, url, categoryId, imageThumb;
    long timestamp, viewsCount, downloadsCount, lastReadPage;
    boolean favorite;
    public Pdf(){

    }
    public Pdf(String id, String uid, String title, String description, String url, String categoryId, String imageThumb, long timestamp, long viewsCount, long downloadsCount, long lastReadPage, boolean favorite){
        this.id = id;
        this.uid = uid;
        this.title = title;
        this.description = description;
        this.url = url;
        this.categoryId = categoryId;
        this.imageThumb = imageThumb;
        this.timestamp = timestamp;
        this.viewsCount = viewsCount;
        this.downloadsCount = downloadsCount;
        this.lastReadPage = lastReadPage;
        this.favorite = favorite;
    }

    public long getLastReadPage() {
        return lastReadPage;
    }

    public void setLastReadPage(long lastReadPage) {
        this.lastReadPage = lastReadPage;
    }

    public String getImageThumb() {
        return imageThumb;
    }

    public void setImageThumb(String imageThumb) {
        this.imageThumb = imageThumb;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public long getDownloadsCount() {
        return downloadsCount;
    }

    public void setDownloadsCount(long downloadsCount) {
        this.downloadsCount = downloadsCount;
    }
}
