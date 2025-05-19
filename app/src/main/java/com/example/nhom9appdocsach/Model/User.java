package com.example.nhom9appdocsach.Model;

public class User {
    String uid, email, name, profile, image, usertype, password;
    long timestamp;
    public User(){

    }
    public User(String uid, String email, String name, String profile, String image, long timestamp, String usertype, String password) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.profile = profile;
        this.image = image;
        this.timestamp = timestamp;
        this.usertype = usertype;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
