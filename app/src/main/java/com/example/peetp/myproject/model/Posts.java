package com.example.peetp.myproject.model;

public class Posts {
    public String uid, time, date, postimage, profileimage, description, username ;

    public Posts(){

    }

    public Posts(String uid, String time, String date, String postimage, String profileimage, String description, String username) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postimage = postimage;
        this.profileimage = profileimage;
        this.description = description;
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
