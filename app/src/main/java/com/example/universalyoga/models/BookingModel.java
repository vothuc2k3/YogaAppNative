package com.example.universalyoga.models;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingModel {
    private String id;
    private String uid;
    private boolean isConfirmed;
    private long createdAt;

    public BookingModel(){}

    public BookingModel(String id, String uid, boolean isConfirmed, long createdAt) {
        this.id = id;
        this.uid = uid;
        this.isConfirmed = isConfirmed;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("uid", uid);
        map.put("isConfirmed", isConfirmed);
        map.put("createdAt", new Timestamp(new Date(createdAt)));
        return map;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }
}
