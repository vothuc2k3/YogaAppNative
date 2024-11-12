package com.example.universalyoga.models;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class ClassModel {
    private String id;
    private String dayOfWeek;
    private Time timeStart;
    private int capacity;
    private int duration;
    private int sessionCount; 
    private String typeId;
    private String status;
    private String description;
    private long createdAt;
    private long startAt;
    private long endAt;
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
