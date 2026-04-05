package com.hotel.db;

import com.hotel.model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public List<Room> getAll() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT r.*, rt.name as type_name FROM rooms r JOIN room_types rt ON r.room_type_id = rt.id";
        try (Statement stmt = DBConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getInt("room_type_id"),
                        rs.getString("type_name"),
                        rs.getInt("floor"),
                        rs.getString("status"),
                        rs.getDouble("price_per_night")));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching rooms: " + e.getMessage());
        }
        return list;
    }

    public Room getById(int id) {
        String sql = "SELECT r.*, rt.name as type_name FROM rooms r JOIN room_types rt ON r.room_type_id = rt.id WHERE r.id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getInt("room_type_id"),
                        rs.getString("type_name"),
                        rs.getInt("floor"),
                        rs.getString("status"),
                        rs.getDouble("price_per_night"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching room by id: " + e.getMessage());
        }
        return null;
    }

    public List<Room> getAvailable() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT r.*, rt.name as type_name FROM rooms r JOIN room_types rt ON r.room_type_id = rt.id WHERE r.status = 'AVAILABLE'";
        try (Statement stmt = DBConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getInt("room_type_id"),
                        rs.getString("type_name"),
                        rs.getInt("floor"),
                        rs.getString("status"),
                        rs.getDouble("price_per_night")));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching available rooms: " + e.getMessage());
        }
        return list;
    }

    public void add(Room room) {
        String sql = "INSERT INTO rooms (room_number, room_type_id, floor, status, price_per_night) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, room.getRoomNumber());
            ps.setInt(2, room.getRoomTypeId());
            ps.setInt(3, room.getFloor());
            ps.setString(4, room.getStatus());
            ps.setDouble(5, room.getPricePerNight());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding room: " + e.getMessage());
        }
    }

    public void update(Room room) {
        String sql = "UPDATE rooms SET room_number=?, room_type_id=?, floor=?, status=?, price_per_night=? WHERE id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, room.getRoomNumber());
            ps.setInt(2, room.getRoomTypeId());
            ps.setInt(3, room.getFloor());
            ps.setString(4, room.getStatus());
            ps.setDouble(5, room.getPricePerNight());
            ps.setInt(6, room.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating room: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM rooms WHERE id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting room: " + e.getMessage());
        }
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE rooms SET status=? WHERE id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating room status: " + e.getMessage());
        }
    }
}