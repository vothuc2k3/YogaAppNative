package com.example.universalyoga.models;

import java.util.HashMap;
import java.util.Map;

public class ClassSessionModel {
    private String id;
    private String classId;
    private int sessionNumber;
    private String instructorId;
    private long date;
    private int price;
    private String room;
    private String note;

    private long lastSyncTime;
    private boolean isDeleted;

    public ClassSessionModel() {}

    public ClassSessionModel(String id, String classId, int sessionNumber, String instructorId, long date, int price, String room, String note) {
        this.id = id;
        this.classId = classId;
        this.sessionNumber = sessionNumber;
        this.instructorId = instructorId;
        this.date = date;
        this.price = price;
        this.room = room;
        this.note = note;
    }

    public int getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(int sessionNumber) {
        this.sessionNumber = sessionNumber;
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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("classId", classId);
        map.put("sessionNumber", sessionNumber);
        map.put("instructorId", instructorId);
        map.put("date", date);
        map.put("price", price);
        map.put("room", room);
        map.put("note", note);
        map.put("lastSyncTime", lastSyncTime);
        map.put("isDeleted", isDeleted);
        return map;
    }

    public static ClassSessionModel fromMap(Map<String, Object> map) {
        ClassSessionModel sessionModel = new ClassSessionModel();

        sessionModel.setId((String) map.get("id"));
        sessionModel.setClassId((String) map.get("classId"));
        sessionModel.setSessionNumber(map.get("sessionNumber") != null ? ((Number) map.get("sessionNumber")).intValue() : 0);
        sessionModel.setInstructorId((String) map.get("instructorId"));
        sessionModel.setDate(map.get("date") != null ? ((Number) map.get("date")).longValue() : 0L);
        sessionModel.setPrice(map.get("price") != null ? ((Number) map.get("price")).intValue() : 0);
        sessionModel.setRoom((String) map.get("room"));
        sessionModel.setNote((String) map.get("note"));
        sessionModel.setLastSyncTime(map.get("lastSyncTime") != null ? ((Number) map.get("lastSyncTime")).longValue() : 0L);
        sessionModel.setDeleted(map.get("isDeleted") != null && (Boolean) map.get("isDeleted"));

        return sessionModel;
    }


    public long getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(long lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
