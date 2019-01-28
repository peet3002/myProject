package com.example.peetp.myproject.model;

public class Messages {

    private String date, message, profileimage, time, username, sender, receiver ;

    public Messages(){

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Messages(String date, String message, String profileimage, String time, String username, String sender, String receiver) {
        this.date = date;
        this.message = message;
        this.profileimage = profileimage;
        this.time = time;
        this.username = username;
        this.sender = sender;
        this.receiver = receiver;
    }
}
