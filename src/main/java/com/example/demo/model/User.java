package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private String phoneNumber;
    private String googleOAuthId;
    private String otp;

    // Constructors
    public User() {
    }

    public User(String username, String password, String phoneNumber, String googleOAuthId, String otp) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.googleOAuthId = googleOAuthId;
        this.otp = otp;
    }
    public User(String username, String password, String phoneNumber, String googleOAuthId) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.googleOAuthId = googleOAuthId;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGoogleOAuthId() {
        return googleOAuthId;
    }

    public void setGoogleOAuthId(String googleOAuthId) {
        this.googleOAuthId = googleOAuthId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}