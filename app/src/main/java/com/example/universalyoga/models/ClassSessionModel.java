package com.example.universalyoga.models;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClassSessionModel {
    private String id;
    private String classId;
    private int sessionNumber;
    private String instructorId;
    private long date;
    private int price;
    private String room;
    private String note;

    public ClassSessionModel(){}

    public ClassSessionModel(String id, String classId, int sessionNumber, String instructorId, long date, int price, String room, String note){
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

    public void setDate(long date){
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

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("classId", classId);
        map.put("sessionNumber", sessionNumber);
        map.put("instructorId", instructorId);
        map.put("date", date);
        map.put("price", price);
        map.put("room", room);
        map.put("note", note);
        return map;
    }
}
