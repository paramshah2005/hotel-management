package com.hotel.db;

import com.hotel.model.Guest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {

    public List<Guest> getAll() {
        List<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM guests ORDER BY created_at DESC";
        try (Statement stmt = DBConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Guest g = new Guest(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("id_proof"));
                list.add(g);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching guests: " + e.getMessage());
        }
        return list;
    }

    public Guest getById(int id) {
        String sql = "SELECT * FROM guests WHERE id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Guest(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("id_proof"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching guest: " + e.getMessage());
        }
        return null;
    }

    public List<Guest> search(String keyword) {
        List<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM guests WHERE LOWER(first_name) LIKE ? OR LOWER(last_name) LIKE ? OR LOWER(email) LIKE ? OR phone LIKE ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            String kw = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ps.setString(4, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Guest(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("id_proof")));
            }
        } catch (SQLException e) {
            System.out.println("Error searching guests: " + e.getMessage());
        }
        return list;
    }

    public int add(Guest g) {
        String sql = "INSERT INTO guests (first_name, last_name, email, phone, address, id_proof) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, g.getFirstName());
            ps.setString(2, g.getLastName());
            ps.setString(3, g.getEmail());
            ps.setString(4, g.getPhone());
            ps.setString(5, g.getAddress());
            ps.setString(6, g.getIdProof());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("id");
        } catch (SQLException e) {
            System.out.println("Error adding guest: " + e.getMessage());
        }
        return -1;
    }

    public void update(Guest g) {
        String sql = "UPDATE guests SET first_name=?, last_name=?, email=?, phone=?, address=?, id_proof=? WHERE id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, g.getFirstName());
            ps.setString(2, g.getLastName());
            ps.setString(3, g.getEmail());
            ps.setString(4, g.getPhone());
            ps.setString(5, g.getAddress());
            ps.setString(6, g.getIdProof());
            ps.setInt(7, g.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating guest: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM guests WHERE id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting guest: " + e.getMessage());
        }
    }
}