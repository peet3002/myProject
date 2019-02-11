package com.example.peetp.myproject.model;


public class MessageList {
    private String id, message, receiver ,date, time, profileimage, username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public MessageList() {

    }

    public MessageList(String id, String message, String receiver, String date, String time, String profileimage, String username) {
        this.id = id;
        this.message = message;
        this.receiver = receiver;
        this.date = date;
        this.time = time;
        this.profileimage = profileimage;
        this.username = username;
    }

    //    public static Comparator<MessageList> ByDate = new Comparator<MessageList>() {
//        @Override
//        public int compare(MessageList o1, MessageList o2) {
//            return Integer.valueOf(o1.getDate()).compareTo(Integer.valueOf(o2.getDate()));
//        }
//    };



}
