package com.axel.roomseva.ui.Model;

import com.axel.roomseva.ui.Model.Visit;

import java.util.Map;

public class Customer {
    private String aadhar;
    private String name;
    private String phone;
    private Map<String, Visit> visits;

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setVisits(Map<String, Visit> visits) {
        this.visits = visits;
    }



    public Map<String, Visit> getVisits() {
        return visits;
    }
}
