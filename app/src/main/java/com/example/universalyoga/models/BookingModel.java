package com.example.universalyoga.models;

import java.util.HashMap;
import java.util.Map;

public class BookingModel {
    private String id;
    private String uid;
    private int totalPrice;
    private long createdAt;

    public BookingModel(){}

    public BookingModel(String id, String uid, int totalPrice, long createdAt) {
        this.id = id;
        this.uid = uid;
        this.totalPrice = totalPrice;
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

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
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
        map.put("totalPrice", totalPrice);
        map.put("createdAt", createdAt);
        return map;
    }
}
