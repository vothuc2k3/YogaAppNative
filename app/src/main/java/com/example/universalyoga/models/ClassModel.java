package com.example.universalyoga.models;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class ClassModel {
    private String id; // id of the class
    private String dayOfWeek; // monday, tuesday, etc.
    private Time timeStart; // 11h, 12h, etc.
    private int capacity; // 15, 20, 25, etc.
    private int duration; // 1h, 2h, etc.
    private int sessionCount; // number of sessions of the class    
    private String typeId; // id of the type of the class
    private String status; // active, inactive, etc.
    private String description; // description of the class
    private long createdAt; // epoch time
    private long startAt; // epoch time
    private long endAt; // epoch time

    private boolean isDeleted;

    public ClassModel() {
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getStartAt() {
        return startAt;
    }

    public void setStartAt(long startAt) {
        this.startAt = startAt;
    }

    public long getEndAt() {
        return endAt;
    }

    public void setEndAt(long endAt) {
        this.endAt = endAt;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Time getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Time timeStart) {
        this.timeStart = timeStart;
    }

    public int getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(int sessionCount) {
        this.sessionCount = sessionCount;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("capacity", capacity);
        map.put("duration", duration);
        map.put("sessionCount", sessionCount);
        map.put("typeId", typeId);
        map.put("status", status);
        map.put("description", description);
        map.put("createdAt", createdAt);
        map.put("startAt", startAt);
        map.put("endAt", endAt);
        map.put("dayOfWeek", dayOfWeek);
        map.put("timeStart", timeStart);
        map.put("isDeleted", isDeleted);
        return map;
    }
}
