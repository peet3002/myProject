package com.example.peetp.myproject;

public class Users {
    public String username, profileimage, status;

    public Users(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Users(String username, String profileimage, String status) {
        this.username = username;
        this.profileimage = profileimage;
        this.status = status;
    }
}
