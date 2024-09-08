package com.axel.roomseva.ui.Model;

public class User {
    private String name;
    private String hotelName;
    private String email;
    private String phoneNumber;
    private String availableRooms;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String hotelName, String email, String phoneNumber, String availableRooms) {
        this.name = name;
        this.hotelName = hotelName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.availableRooms = availableRooms;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getAvailableRooms() { return availableRooms; }
    public void setAvailableRooms(String availableRooms) { this.availableRooms = availableRooms; }
}
