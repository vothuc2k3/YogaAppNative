package com.example.universalyoga.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserModel implements Serializable {
    private String uid;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileImage;
    private String role;

    public UserModel(){}

    public UserModel(String uid, String name, String email, String phoneNumber) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImage = "https://firebasestorage.googleapis.com/v0/b/yoga-application-63a57.appspot.com/o/defaultAvatar.png?alt=media&token=8199f3df-0c74-418d-b65d-c5c2e306a1fa";
        this.role = "instructor";
    }

    // Getters và Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", uid);
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("phoneNumber", phoneNumber);
        userMap.put("profileImage", profileImage);
        userMap.put("role", role);
        return userMap;
    }
}
