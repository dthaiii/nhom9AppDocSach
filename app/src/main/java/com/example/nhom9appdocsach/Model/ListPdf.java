package com.example.nhom9appdocsach.Model;

public class ListPdf {
    String id, title, url, storageUrl, imageThumb;
    long viewsCount, downloadsCount, timestamp;
    public ListPdf() {

    }
    public ListPdf(String id, String title, String url, String storageUrl, String imageThumb, long viewsCount, long downloadsCount, long timestamp) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.storageUrl = storageUrl;
        this.viewsCount = viewsCount;
        this.downloadsCount = downloadsCount;
        this.timestamp = timestamp;
        this.imageThumb = imageThumb;

    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageThumb() {
        return imageThumb;
    }

    public void setImageThumb(String imageThumb) {
        this.imageThumb = imageThumb;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStorageUrl() {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
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

