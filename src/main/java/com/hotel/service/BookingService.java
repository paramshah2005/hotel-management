package com.hotel.service;

import com.hotel.db.BookingDAO;
import com.hotel.db.PaymentDAO;
import com.hotel.db.RoomDAO;
import com.hotel.model.Booking;
import com.hotel.model.Guest;
import com.hotel.model.Payment;
import com.hotel.model.Room;
import java.util.List;

public class BookingService {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    public static final double TAX_RATE = 0.18;

    public List<Booking> getAllBookings() {
        return bookingDAO.getAll();
    }

    public List<Booking> getActiveBookings() {
        return bookingDAO.getActive();
    }

    public List<Payment> getAllPayments() {
        return paymentDAO.getAll();
    }

    public List<Payment> getPaymentsByBooking(int bookingId) {
        return paymentDAO.getByBookingId(bookingId);
    }

    public boolean createBooking(Booking b) {
        if (b.getGuestId() <= 0 || b.getRoomId() <= 0)
            return false;
        if (b.getCheckIn() == null || b.getCheckOut() == null)
            return false;
        if (!b.getCheckOut().isAfter(b.getCheckIn()))
            return false;

        List<Booking> existing = bookingDAO.getAll();

        for (Booking ex : existing) {
            if (ex.getRoomId() == b.getRoomId()) {
                boolean overlap = !(b.getCheckOut().isBefore(ex.getCheckIn()) ||
                        b.getCheckIn().isAfter(ex.getCheckOut()));
                if (overlap && !ex.getStatus().equals("CANCELLED"))
                    return false;
            }

            if (ex.getGuestId() == b.getGuestId() &&
                    (ex.getStatus().equals("CONFIRMED") || ex.getStatus().equals("CHECKED_IN"))) {
                return false;
            }
        }

        Room room = roomDAO.getById(b.getRoomId());
        if (room == null)
            return false;

        long nights = b.getNumberOfNights();
        double subtotal = room.getPricePerNight() * nights;
        double total = subtotal + (subtotal * TAX_RATE);

        b.setTotalAmount(total);
        b.setStatus("CONFIRMED");

        int id = bookingDAO.add(b);
        if (id != -1) {
            roomDAO.updateStatus(b.getRoomId(), "OCCUPIED");
            return true;
        }

        return false;
    }

    public void checkIn(int bookingId) {
        bookingDAO.updateStatus(bookingId, "CHECKED_IN");
    }

    public void checkOut(int bookingId, int roomId) {
        bookingDAO.updateStatus(bookingId, "CHECKED_OUT");
        roomDAO.updateStatus(roomId, "AVAILABLE");
    }

    public void cancelBooking(int bookingId, int roomId) {
        bookingDAO.updateStatus(bookingId, "CANCELLED");
        roomDAO.updateStatus(roomId, "AVAILABLE");
    }

    public boolean processPayment(Payment p) {
        if (p.getAmount() <= 0)
            return false;

        List<Payment> existing = paymentDAO.getByBookingId(p.getBookingId());
        if (!existing.isEmpty())
            return false;

        int id = paymentDAO.add(p);
        return id != -1;
    }

    public double calculateSubtotal(double pricePerNight, long nights) {
        return pricePerNight * nights;
    }

    public double calculateTax(double subtotal) {
        return subtotal * TAX_RATE;
    }

    public double calculateTotal(double subtotal) {
        return subtotal + calculateTax(subtotal);
    }

    public long getAvailableRoomsCount() {
        return roomDAO.getAvailable().size();
    }

    public long getActiveBookingsCount() {
        return bookingDAO.getActive().size();
    }

    public double getTotalRevenue() {
        return paymentDAO.getAll().stream().mapToDouble(Payment::getAmount).sum();
    }

    public List<Booking> getBookingsByGuest(int guestId) {
        List<Booking> all = bookingDAO.getAll();
        List<Booking> result = new java.util.ArrayList<>();

        for (Booking b : all) {
            if (b.getGuestId() == guestId) {
                result.add(b);
            }
        }

        return result;
    }

    public double getTotalSpentByGuest(int guestId) {
        List<Booking> list = getBookingsByGuest(guestId);

        double total = 0;
        for (Booking b : list) {
            total += b.getTotalAmount();
        }

        return total;
    }

    public List<Booking> getUnpaidBookings() {
        List<Booking> all = bookingDAO.getAll();
        List<Booking> unpaid = new java.util.ArrayList<>();

        for (Booking b : all) {
            List<Payment> payments = paymentDAO.getByBookingId(b.getId());

            if (payments.isEmpty()) {
                unpaid.add(b);
            }
        }

        return unpaid;
    }

    public List<Guest> getAvailableGuests() {
        List<Guest> allGuests = new GuestService().getAllGuests();
        List<Booking> active = bookingDAO.getActive();

        List<Guest> available = new java.util.ArrayList<>();

        for (Guest g : allGuests) {
            boolean isBooked = false;

            for (Booking b : active) {
                if (b.getGuestId() == g.getId()) {
                    isBooked = true;
                    break;
                }
            }

            if (!isBooked) {
                available.add(g);
            }
        }

        return available;
    }
}