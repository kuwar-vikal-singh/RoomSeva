package com.axel.roomseva.ui.Model;

public class Hotel {
    private String name;
    private String hotelName;
    private String email;
    private String password;
    private String availableRooms;

    public Hotel() {
        // Default constructor required for calls to DataSnapshot.getValue(Hotel.class)
    }

    public Hotel(String name, String hotelName, String email, String password, String availableRooms) {
        this.name = name;
        this.hotelName = hotelName;
        this.email = email;
        this.password = password;
        this.availableRooms = availableRooms;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(String availableRooms) {
        this.availableRooms = availableRooms;
    }
}