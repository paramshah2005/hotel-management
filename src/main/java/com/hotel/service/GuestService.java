package com.hotel.service;

import com.hotel.db.GuestDAO;
import com.hotel.model.Guest;
import java.util.List;

public class GuestService {

    private final GuestDAO guestDAO = new GuestDAO();

    public List<Guest> getAllGuests() {
        return guestDAO.getAll();
    }

    public Guest getGuestById(int id) {
        return guestDAO.getById(id);
    }

    public List<Guest> searchGuests(String keyword) {
        return guestDAO.search(keyword);
    }

    public boolean addGuest(Guest g) {
        if (g.getFirstName() == null || g.getFirstName().isBlank())
            return false;
        if (g.getLastName() == null || g.getLastName().isBlank())
            return false;
        if (g.getPhone() == null || g.getPhone().isBlank())
            return false;
        int id = guestDAO.add(g);
        return id != -1;
    }

    public boolean updateGuest(Guest g) {
        if (g.getFirstName() == null || g.getFirstName().isBlank())
            return false;
        guestDAO.update(g);
        return true;
    }

    public void deleteGuest(int id) {
        guestDAO.delete(id);
    }
}