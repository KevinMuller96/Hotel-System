package com.company;

import java.io.Serializable;
import java.time.*;

public class Room implements Serializable {

    private int Numbeds;
    private int price;
    private boolean AC;
    private int capacity;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer customerID;
    private String type;

    public Room(int numbeds, int price, boolean AC, int capacity, LocalDate checkIn, LocalDate checkOut, Integer customerID, String type) {
        Numbeds = numbeds;
        this.price = price;
        this.AC = AC;
        this.capacity = capacity;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.customerID = customerID;
        this.type = type;
    }

    public int getNumbeds() {
        return Numbeds;
    }

    public void setNumbeds(int numbeds) {
        Numbeds = numbeds;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isAC() {
        return AC;
    }

    public void setAC(boolean AC) {
        this.AC = AC;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "\nNumber of beds: " + Numbeds +
                "\nPrice per night: " + price +
                "\nAC: " + AC +
                "\nRoom capacity: " + capacity +
                "\nCheckIn date: " + checkIn +
                "\nCheckOut date: " + checkOut +
                "\nBookingID: " + customerID +
                "\nType: " + type;
    }
}
