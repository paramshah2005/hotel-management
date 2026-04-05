package com.hotel.db;

import com.hotel.model.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public List<Payment> getAll() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY payment_date DESC";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Payment p = new Payment(
                        rs.getInt("id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_method"),
                        rs.getString("status"));
                p.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching payments: " + e.getMessage());
        }
        return list;
    }

    public List<Payment> getByBookingId(int bookingId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE booking_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Payment p = new Payment(
                        rs.getInt("id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_method"),
                        rs.getString("status"));
                p.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching payments by booking: " + e.getMessage());
        }
        return list;
    }

    public int add(Payment p) {
        String sql = "INSERT INTO payments (booking_id, amount, payment_method, status) VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, p.getBookingId());
            ps.setDouble(2, p.getAmount());
            ps.setString(3, p.getPaymentMethod());
            ps.setString(4, p.getStatus());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("id");
        } catch (SQLException e) {
            System.out.println("Error adding payment: " + e.getMessage());
        }
        return -1;
    }
}