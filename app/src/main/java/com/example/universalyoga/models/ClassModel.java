package com.example.universalyoga.models;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class ClassModel {
    private String id; // id of the class
    private String instructorUid; // uid of the instructor
    private String dayOfWeek; // monday, tuesday, etc.
    private Time timeStart; // 11h, 12h, etc.
    private int capacity; // 15, 20, 25, etc.
    private int duration; // 1h, 2h, etc.
    private int price; // 10, 20, 30, etc.
    private String type; // yoga, pilates, etc.
    private String status; // active, inactive, etc.
    private String description; // description of the class
    private long createdAt; // epoch time
    private long startAt; // epoch time
    private long endAt; // epoch time

    public ClassModel() {
        this.createdAt = System.currentTimeMillis();
    }

    public ClassModel(String id, String instructorUid, String dayOfWeek, Time timeStart, int capacity, int duration, int price, String type, String status, String description, long startAt, long endAt) {
        this.id = id;
        this.instructorUid = instructorUid;
        this.dayOfWeek = dayOfWeek;
        this.timeStart = timeStart;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.type = type;
        this.status = status;
        this.description = description; // Đảm bảo trường này không bị bỏ sót
        this.createdAt = System.currentTimeMillis();
        this.startAt = startAt;
        this.endAt = endAt;
    }

    // Getters và Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstructorUid() {
        return this.instructorUid;
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

    // Chuyển đổi đối tượng thành map để lưu trữ trong Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("instructorUid", instructorUid);
        map.put("capacity", capacity);
        map.put("duration", duration);
        map.put("price", price);
        map.put("type", type);
        map.put("status", status);
        map.put("description", description); // Đảm bảo trường description có mặt trong map
        map.put("createdAt", createdAt);
        map.put("startAt", startAt);
        map.put("endAt", endAt);
        return map;
    }

    @Override
    public String toString() {
        return "ClassModel{" +
                "id='" + id + '\'' +
                ", capacity=" + capacity +
                ", duration=" + duration +
                ", price=" + price +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' + // Bảo đảm không bỏ sót description
                ", createdAt=" + createdAt +
                ", startAt=" + startAt +
                ", endAt=" + endAt +
                '}';
    }
}
