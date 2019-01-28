package com.example.peetp.myproject.model;

public class Users {
    private String  profileimage, status, uid, degree, fullname, major, mobilenumber, sec, username, usertype, office, type_username, adviser, key, onlinestatus;

    public Users(){

    }

    public Users(String profileimage, String status, String uid, String degree, String fullname, String major, String mobilenumber, String sec, String username, String usertype, String office, String type_username, String adviser, String key, String onlinestatus) {
        this.profileimage = profileimage;
        this.status = status;
        this.uid = uid;
        this.degree = degree;
        this.fullname = fullname;
        this.major = major;
        this.mobilenumber = mobilenumber;
        this.sec = sec;
        this.username = username;
        this.usertype = usertype;
        this.office = office;
        this.type_username = type_username;
        this.adviser = adviser;
        this.key = key;
        this.onlinestatus = onlinestatus;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getType_username() {
        return type_username;
    }

    public void setType_username(String type_username) {
        this.type_username = type_username;
    }

    public String getAdviser() {
        return adviser;
    }

    public void setAdviser(String adviser) {
        this.adviser = adviser;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOnlinestatus() {
        return onlinestatus;
    }

    public void setOnlinestatus(String onlinestatus) {
        this.onlinestatus = onlinestatus;
    }



}
