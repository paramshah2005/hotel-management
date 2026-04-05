package com.hotel.service;

import com.hotel.db.RoomDAO;
import com.hotel.db.RoomTypeDAO;
import com.hotel.model.Room;
import com.hotel.model.RoomType;
import java.util.List;

public class RoomService {

    private final RoomDAO roomDAO = new RoomDAO();
    private final RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

    public List<Room> getAllRooms() {
        return roomDAO.getAll();
    }

    public List<Room> getAvailableRooms() {
        return roomDAO.getAvailable();
    }

    public List<RoomType> getAllRoomTypes() {
        return roomTypeDAO.getAll();
    }

    public boolean addRoom(Room room) {
        if (room.getRoomNumber() == null || room.getRoomNumber().isBlank())
            return false;
        if (room.getPricePerNight() <= 0)
            return false;
        roomDAO.add(room);
        return true;
    }

    public boolean updateRoom(Room room) {
        if (room.getRoomNumber() == null || room.getRoomNumber().isBlank())
            return false;
        roomDAO.update(room);
        return true;
    }

    public void deleteRoom(int id) {
        roomDAO.delete(id);
    }

    public void updateRoomStatus(int id, String status) {
        roomDAO.updateStatus(id, status);
    }

    public boolean addRoomType(RoomType rt) {
        if (rt.getName() == null || rt.getName().isBlank())
            return false;
        roomTypeDAO.add(rt);
        return true;
    }

    public void updateRoomType(RoomType rt) {
        roomTypeDAO.update(rt);
    }

    public void deleteRoomType(int id) {
        roomTypeDAO.delete(id);
    }
}