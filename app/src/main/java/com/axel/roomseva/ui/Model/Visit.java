package com.axel.roomseva.ui.Model;

import java.io.Serializable;

public class Visit {
    private String additionalInfo;
    private String dateTime;
    private String phoneNumber;
    private String roomNo;
    private String checkIn; // Assuming you need this field as well
    private String checkOut; // Added for checkout time

    // Default constructor required for Firebase
    public Visit() { }

    public Visit(String additionalInfo, String dateTime, String phoneNumber, String roomNo, String checkIn, String checkOut) {
        this.additionalInfo = additionalInfo;
        this.dateTime = dateTime;
        this.phoneNumber = phoneNumber;
        this.roomNo = roomNo;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    // Getters and setters
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }
}
