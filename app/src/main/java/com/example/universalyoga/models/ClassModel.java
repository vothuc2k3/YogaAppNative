package com.example.universalyoga.models;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class ClassModel {
    private String id;
    private String creatorUid;
    private String instructorUid;
    private int capacity;
    private int duration;
    private int price;
    private String type;
    private String status;
    private String description;
    private Timestamp createdAt;
    private Timestamp startAt;
    private Timestamp endAt;

    public ClassModel() {
        this.createdAt = Timestamp.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorUid() {
        return creatorUid;
    }

    public void setCreatorUid(String creatorUid) {
        this.creatorUid = creatorUid;
    }

    public String getInstructorUid() {
        return instructorUid;
    }

    public void setInstructorUid(String instructorUid) {
        this.instructorUid = instructorUid;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp timestamp){this.createdAt = timestamp;}

    public Timestamp getStartAt() {
        return startAt;
    }

    public void setStartAt(Timestamp startAt) {
        this.startAt = startAt;
    }

    public Timestamp getEndAt() {
        return endAt;
    }

    public void setEndAt(Timestamp endAt) {
        this.endAt = endAt;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("creatorUid", creatorUid);
        map.put("instructorUid", instructorUid);
        map.put("capacity", capacity);
        map.put("duration", duration);
        map.put("price", price);
        map.put("type", type);
        map.put("status", status);
        map.put("description", description);
        map.put("createdAt", createdAt);
        map.put("startAt", startAt);
        map.put("endAt", endAt);
        return map;
    }

    @Override
    public String toString() {
        return "ClassModel{" +
                "id='" + id + '\'' +
                ", creatorUid='" + creatorUid + '\'' +
                ", instructorUid='" + instructorUid + '\'' +
                ", capacity=" + capacity +
                ", duration=" + duration +
                ", price=" + price +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", startAt=" + startAt +
                ", endAt=" + endAt +
                '}';
    }
}
