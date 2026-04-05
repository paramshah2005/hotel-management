package com.hotel.ui;

import com.hotel.service.BookingService;
import com.hotel.service.RoomService;
import com.hotel.model.Booking;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;

public class DashboardView {

    private BookingService bookingService = new BookingService();
    private RoomService roomService = new RoomService();

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Dashboard");

        Label title = new Label("Dashboard");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        long totalRooms = roomService.getAllRooms().size();
        long availableRooms = roomService.getAvailableRooms().size();
        long activeBookings = bookingService.getActiveBookingsCount();
        double revenue = bookingService.getTotalRevenue();

        VBox card1 = createCard("Total Rooms", String.valueOf(totalRooms));
        VBox card2 = createCard("Available Rooms", String.valueOf(availableRooms));
        VBox card3 = createCard("Active Bookings", String.valueOf(activeBookings));
        VBox card4 = createCard("Revenue", "₹" + String.format("%.2f", revenue));

        HBox row1 = new HBox(15, card1, card2);
        HBox row2 = new HBox(15, card3, card4);

        row1.setAlignment(Pos.CENTER);
        row2.setAlignment(Pos.CENTER);

        String topCustomer = "N/A";

        List<Booking> bookings = bookingService.getAllBookings();
        if (!bookings.isEmpty()) {
            topCustomer = bookings.stream()
                    .max(Comparator.comparingDouble(Booking::getTotalAmount))
                    .map(b -> b.getGuestName() + " (₹" + String.format("%.2f", b.getTotalAmount()) + ")")
                    .orElse("N/A");
        }

        Label topCustomerLabel = new Label("Top Customer: " + topCustomer);
        topCustomerLabel.setStyle("-fx-text-fill: #d1d5db; -fx-font-size: 14px;");

        VBox insightsBox = new VBox(10, topCustomerLabel);
        insightsBox.setPadding(new Insets(15));
        insightsBox.setPrefWidth(375);
        insightsBox.setMaxWidth(375);
        insightsBox.setAlignment(Pos.CENTER_LEFT);
        insightsBox.setStyle("-fx-background-color: #1c2433; -fx-background-radius: 10;");

        VBox root = new VBox(20, title, row1, row2, insightsBox);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #121826;");

        stage.setScene(new Scene(root, 500, 400));
        stage.show();
    }

    private VBox createCard(String labelText, String valueText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 13px;");

        Label value = new Label(valueText);
        value.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        VBox box = new VBox(5, label, value);
        box.setPadding(new Insets(15));
        box.setPrefWidth(180);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-background-color: #1c2433; -fx-background-radius: 10;");

        return box;
    }
}