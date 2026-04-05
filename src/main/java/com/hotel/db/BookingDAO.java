package com.hotel.db;

import com.hotel.model.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private Booking mapBooking(ResultSet rs) throws SQLException {
        Booking b = new Booking(
                rs.getInt("id"),
                rs.getInt("guest_id"),
                rs.getString("guest_name"),
                rs.getInt("room_id"),
                rs.getString("room_number"),
                rs.getDate("check_in").toLocalDate(),
                rs.getDate("check_out").toLocalDate(),
                rs.getString("status"),
                rs.getDouble("total_amount"));
        return b;
    }

    public boolean hasActiveBooking(int guestId) {
        String sql = """
                    SELECT COUNT(*) FROM bookings
                    WHERE guest_id = ?
                    AND status IN ('CONFIRMED', 'CHECKED_IN')
                """;

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, guestId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println("Error checking guest booking: " + e.getMessage());
        }

        return false;
    }

    public boolean isRoomBooked(int roomId, java.time.LocalDate checkIn, java.time.LocalDate checkOut) {
        String sql = """
                    SELECT COUNT(*) FROM bookings
                    WHERE room_id = ?
                    AND status IN ('CONFIRMED', 'CHECKED_IN')
                    AND (
                        check_in < ?
                        AND check_out > ?
                    )
                """;

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setDate(2, java.sql.Date.valueOf(checkOut));
            ps.setDate(3, java.sql.Date.valueOf(checkIn));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println("Error checking booking overlap: " + e.getMessage());
        }

        return false;
    }

    public List<Booking> getAll() {
        List<Booking> list = new ArrayList<>();
        String sql = """
                    SELECT b.*,
                           g.first_name || ' ' || g.last_name as guest_name,
                           r.room_number
                    FROM bookings b
                    JOIN guests g ON b.guest_id = g.id
                    JOIN rooms r ON b.room_id = r.id
                    ORDER BY b.created_at DESC
                """;
        try (Statement stmt = DBConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                list.add(mapBooking(rs));
        } catch (SQLException e) {
            System.out.println("Error fetching bookings: " + e.getMessage());
        }
        return list;
    }

    public List<Booking> getActive() {
        List<Booking> list = new ArrayList<>();
        String sql = """
                    SELECT b.*,
                           g.first_name || ' ' || g.last_name as guest_name,
                           r.room_number
                    FROM bookings b
                    JOIN guests g ON b.guest_id = g.id
                    JOIN rooms r ON b.room_id = r.id
                    WHERE b.status IN ('CONFIRMED', 'CHECKED_IN')
                    ORDER BY b.check_in
                """;
        try (Statement stmt = DBConnection.getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                list.add(mapBooking(rs));
        } catch (SQLException e) {
            System.out.println("Error fetching active bookings: " + e.getMessage());
        }
        return list;
    }

    public int add(Booking b) {
        String sql = "INSERT INTO bookings (guest_id, room_id, check_in, check_out, status, total_amount) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, b.getGuestId());
            ps.setInt(2, b.getRoomId());
            ps.setDate(3, Date.valueOf(b.getCheckIn()));
            ps.setDate(4, Date.valueOf(b.getCheckOut()));
            ps.setString(5, b.getStatus());
            ps.setDouble(6, b.getTotalAmount());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("id");
        } catch (SQLException e) {
            System.out.println("Error adding booking: " + e.getMessage());
        }
        return -1;
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE bookings SET status=? WHERE id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating booking status: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM bookings WHERE id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting booking: " + e.getMessage());
        }
    }
}