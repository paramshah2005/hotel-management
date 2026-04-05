package com.hotel.service;

import com.hotel.db.BookingDAO;
import com.hotel.db.PaymentDAO;
import com.hotel.db.RoomDAO;
import com.hotel.model.Booking;
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

        Room room = roomDAO.getById(b.getRoomId());
        long nights = java.time.temporal.ChronoUnit.DAYS.between(b.getCheckIn(), b.getCheckOut());
        double subtotal = calculateSubtotal(room.getPricePerNight(), nights);
        double total = calculateTotal(subtotal);

        b.setTotalAmount(total);
        b.setStatus("CONFIRMED");

        int id = bookingDAO.add(b);
        return id != -1;
    }

    public void checkIn(int bookingId, int roomId) {
        bookingDAO.updateStatus(bookingId, "CHECKED_IN");
        roomDAO.updateStatus(roomId, "OCCUPIED");
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
}