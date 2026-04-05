package com.hotel.db;

import com.hotel.model.RoomType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeDAO {

    public List<RoomType> getAll() {
        List<RoomType> list = new ArrayList<>();
        String sql = "SELECT * FROM room_types";

        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new RoomType(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching room types: " + e.getMessage());
        }

        return list;
    }

    public void add(RoomType rt) {
        String sql = "INSERT INTO room_types (name, description) VALUES (?, ?)";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, rt.getName());
            ps.setString(2, rt.getDescription());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding room type: " + e.getMessage());
        }
    }

    public void update(RoomType rt) {
        String sql = "UPDATE room_types SET name=?, description=? WHERE id=?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, rt.getName());
            ps.setString(2, rt.getDescription());
            ps.setInt(3, rt.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating room type: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM room_types WHERE id=?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error deleting room type: " + e.getMessage());
        }
    }
}