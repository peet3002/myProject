package com.example.peetp.myproject.model;

public class Comments {
    public String comment, date, profileimage, time, uid, username;

    public Comments(){

    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Comments(String comment, String date, String profileimage, String time, String uid, String username) {
        this.comment = comment;
        this.date = date;
        this.profileimage = profileimage;
        this.time = time;
        this.uid = uid;
        this.username = username;
    }
}
