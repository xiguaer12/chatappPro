package com.example.entity;

import java.util.Date;

public class User {
    private String username;
    private String password;
    private Date loginTime;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.loginTime = new Date();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
}