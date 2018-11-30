package com.example.peetp.myproject;

public class Students {
    public String stdId, stdName, stdMajor;

    public Students(){

    }

    public String getStdId() {
        return stdId;
    }

    public void setStdId(String stdId) {
        this.stdId = stdId;
    }

    public String getStdName() {
        return stdName;
    }

    public void setStdName(String stdName) {
        this.stdName = stdName;
    }

    public String getStdMajor() {
        return stdMajor;
    }

    public void setStdMajor(String stdMajor) {
        this.stdMajor = stdMajor;
    }

    public Students(String stdId, String stdName, String stdMajor) {
        this.stdId = stdId;
        this.stdName = stdName;
        this.stdMajor = stdMajor;
    }
}
