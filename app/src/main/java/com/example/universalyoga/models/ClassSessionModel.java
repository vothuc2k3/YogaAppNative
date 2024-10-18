package com.example.universalyoga.models;

import java.util.HashMap;
import java.util.Map;

public class ClassSessionModel {
    private String id;
    private String classId;
    private String instructorId;
    private long date;
    private long startTime;
    private long endTime;
    private int price;
    private String room;
    private String note;
    private boolean isDeleted;

    public ClassSessionModel() {
    }

    public ClassSessionModel(String id, String classId, String instructorId, long date, long startTime, long endTime, int price, String room, String note) {
        this.id = id;
        this.classId = classId;
        this.instructorId = instructorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.room = room;
        this.note = note;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        map.put("classId", classId);
        map.put("instructorId", instructorId);
        map.put("date", date);
        map.put("startTime", startTime);  // Thêm startTime
        map.put("endTime", endTime);      // Thêm endTime
        map.put("price", price);
        map.put("room", room);
        map.put("note", note);
        map.put("isDeleted", isDeleted);
        return map;
    }

    public static ClassSessionModel fromMap(Map<String, Object> map) {
        ClassSessionModel sessionModel = new ClassSessionModel();
        sessionModel.setId((String) map.get("id"));
        sessionModel.setClassId((String) map.get("classId"));
        sessionModel.setInstructorId((String) map.get("instructorId"));
        sessionModel.setDate(map.get("date") != null ? ((Number) map.get("date")).longValue() : 0L);
        sessionModel.setStartTime(map.get("startTime") != null ? ((Number) map.get("startTime")).longValue() : 0L);  // Thêm startTime
        sessionModel.setEndTime(map.get("endTime") != null ? ((Number) map.get("endTime")).longValue() : 0L);        // Thêm endTime
        sessionModel.setPrice(map.get("price") != null ? ((Number) map.get("price")).intValue() : 0);
        sessionModel.setRoom((String) map.get("room"));
        sessionModel.setNote((String) map.get("note"));
        sessionModel.setDeleted(map.get("isDeleted") != null && (Boolean) map.get("isDeleted"));
        return sessionModel;
    }
}
