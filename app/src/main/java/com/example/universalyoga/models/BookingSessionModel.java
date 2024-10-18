package com.example.universalyoga.models;

public class BookingSessionModel {
    private String bookingId;
    private String sessionId;

    public BookingSessionModel(){}

    public BookingSessionModel(String bookingId, String sessionId){
        this.bookingId = bookingId;
        this.sessionId = sessionId;
    }


    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
