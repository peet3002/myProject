package com.example.peetp.myproject.model;

public class Counsels {
    private String date, detail, receiver, time, type, uid, username, key, fullname, degree, sec, receiver_status;
    private Boolean status;

    public Counsels(){

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public String getReceiver_status() {
        return receiver_status;
    }

    public void setReceiver_status(String receiver_status) {
        this.receiver_status = receiver_status;
    }

    public Counsels(String date, String detail, String receiver, String time, String type, String uid, String username, String key, String fullname, String degree, String sec, String receiver_status, Boolean status) {
        this.date = date;
        this.detail = detail;
        this.receiver = receiver;
        this.time = time;
        this.type = type;
        this.uid = uid;
        this.username = username;
        this.key = key;
        this.fullname = fullname;
        this.degree = degree;
        this.sec = sec;
        this.receiver_status = receiver_status;
        this.status = status;
    }
}
