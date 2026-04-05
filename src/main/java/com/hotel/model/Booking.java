package com.hotel.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Booking {
    private int id;
    private int guestId;
    private String guestName;
    private int roomId;
    private String roomNumber;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String status;
    private double totalAmount;
    private LocalDateTime createdAt;

    public Booking() {
    }

    public Booking(int id, int guestId, String guestName, int roomId, String roomNumber, LocalDate checkIn,
            LocalDate checkOut, String status, double totalAmount) {
        this.id = id;
        this.guestId = guestId;
        this.guestName = guestName;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getNumberOfNights() {
        if (checkIn != null && checkOut != null)
            return ChronoUnit.DAYS.between(checkIn, checkOut);
        return 0;
    }

    @Override
    public String toString() {
        return guestName + " - ₹" + String.format("%.2f", totalAmount);
    }
}