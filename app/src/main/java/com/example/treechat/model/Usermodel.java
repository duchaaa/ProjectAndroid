package com.example.treechat.model;

import com.google.firebase.Timestamp;

public class Usermodel {
    private String phone;
    private String username;
    private Timestamp creTimestamp;
    private String userId;
    private String FCMtoken;
    public Usermodel(String phone, String username, Timestamp creTimestamp,String userId) {
        this.phone = phone;
        this.username = username;
        this.creTimestamp = creTimestamp;
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreTimestamp() {
        return creTimestamp;
    }

    public void setCreTimestamp(Timestamp creTimestamp) {
        this.creTimestamp = creTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFCMtoken() {
        return FCMtoken;
    }

    public void setFCMtoken(String FCMtoken) {
        this.FCMtoken = FCMtoken;
    }

    public Usermodel(){

    }

}
