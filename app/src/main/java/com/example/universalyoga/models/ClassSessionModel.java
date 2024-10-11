package com.example.universalyoga.models;

import com.google.firebase.Timestamp;

import java.util.UUID;

public class ClassSessionModel {
    private String id;
    private String classId;
    private Timestamp startAt;
    private Timestamp endAt;
    private String note;

    public ClassSessionModel(String id, String classId, Timestamp startAt, Timestamp endAt, String note) {
        this.id = id;
        this.classId = classId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public String getClassId() {
        return classId;
    }


    public Timestamp getStartAt() {
        return startAt;
    }

    public Timestamp getEndAt() {
        return endAt;
    }

    public String getNote() {
        return note;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClassId(String classId) {
        this.classId = id;
    }

    public void setStartAt(Timestamp startAt) {
        this.startAt = startAt;
    }

    public void setEndAt(Timestamp endAt) {
        this.endAt = endAt;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
