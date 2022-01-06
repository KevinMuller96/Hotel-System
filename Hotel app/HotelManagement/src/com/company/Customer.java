package com.company;

import java.io.Serializable;

public class Customer implements Serializable {
    private String firstName;
    private String lastName;
    private int bill;
    private String phoneNumber;
    private String email;
    private int company;
    private boolean allInclusive;
    private boolean halfBoard;

    public Customer(String firstName, String lastName, int bill, String phoneNumber, String email, int company, boolean allInclusive, boolean halfBoard) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.bill = bill;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.company = company;
        this.allInclusive = allInclusive;
        this.halfBoard = halfBoard;
    }

    @Override
    public String toString() {
        return "\nFirstname: " + firstName +
                "\nLastname: " + lastName +
                "\nBill: " + bill +
                "\nPhonenumber: " + phoneNumber +
                "\nEmail: " + email +
                "\nCompany: " + company +
                "\nAll Inculsive: " + allInclusive +
                "\nHalf Board: " + halfBoard;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getBill() {
        return bill;
    }

    public void setBill(int bill) {
        this.bill = bill;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCompany() {
        return company;
    }

    public void setCompany(int company) {
        this.company = company;
    }

    public boolean isAllInclusive() {
        return allInclusive;
    }

    public void setAllInclusive(boolean allInclusive) {
        this.allInclusive = allInclusive;
    }

    public boolean isHalfBoard() {
        return halfBoard;
    }

    public void setHalfBoard(boolean halfBoard) {
        this.halfBoard = halfBoard;
    }
}