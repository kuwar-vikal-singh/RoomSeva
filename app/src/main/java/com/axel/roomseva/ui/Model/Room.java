package com.axel.roomseva.ui.Model;

public class Room implements Comparable<Room> {
    private String roomNumber;
    private String status;

    public Room() {
        // Default constructor required for calls to DataSnapshot.getValue(Room.class)
    }

    public Room(String roomNumber, String status) {
        this.roomNumber = roomNumber;
        this.status = status;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int compareTo(Room other) {
        // Extract numeric part from roomNumber and compare
        int thisNumber = Integer.parseInt(this.roomNumber.replaceAll("[^0-9]", ""));
        int otherNumber = Integer.parseInt(other.roomNumber.replaceAll("[^0-9]", ""));
        return Integer.compare(thisNumber, otherNumber);
    }
}
