package com.hotel.ui;

import com.hotel.model.Booking;
import com.hotel.model.Payment;
import com.hotel.service.BookingService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentsView {

    private BookingService bookingService = new BookingService();
    private TableView<Payment> table = new TableView<>();

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("All Payments");

        Label revenueLabel = new Label("Loading...");
        revenueLabel.setStyle("-fx-text-fill: #ffffff;");

        TableColumn<Payment, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()));

        TableColumn<Payment, String> guestCol = new TableColumn<>("Guest");

        TableColumn<Payment, Number> bookingCol = new TableColumn<>("Booking");
        bookingCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getBookingId()));

        TableColumn<Payment, Number> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getAmount()));

        TableColumn<Payment, String> methodCol = new TableColumn<>("Method");
        methodCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPaymentMethod()));

        table.getColumns().setAll(idCol, guestCol, bookingCol, amtCol, methodCol);
        table.setPlaceholder(new Label("No payments found"));
        table.setStyle(
                "-fx-background-color: #1c2433;" +
                "-fx-control-inner-background: #1c2433;" +
                "-fx-table-cell-border-color: transparent;" +
                "-fx-text-fill: #6b7280;");

        VBox root = new VBox(10, revenueLabel, table);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #121826;");

        stage.setScene(new Scene(root, 600, 400));
        stage.show();

        Task<Void> loadTask = new Task<>() {
            List<Payment> payments;
            Map<Integer, String> bookingGuestMap = new HashMap<>();
            double totalRevenue;

            @Override
            protected Void call() {
                List<Booking> allBookings = bookingService.getAllBookings();
                for (Booking b : allBookings) {
                    bookingGuestMap.put(b.getId(), b.getGuestName());
                }

                payments = bookingService.getAllPayments();
                totalRevenue = bookingService.getTotalRevenue();
                return null;
            }

            @Override
            protected void succeeded() {
                guestCol.setCellValueFactory(d ->
                        new SimpleStringProperty(
                                bookingGuestMap.getOrDefault(d.getValue().getBookingId(), "Unknown")
                        )
                );

                table.setItems(FXCollections.observableArrayList(payments));
                revenueLabel.setText("Total Revenue: ₹" + String.format("%.2f", totalRevenue));
            }

            @Override
            protected void failed() {
                revenueLabel.setText("Failed to load payments.");
            }
        };

        new Thread(loadTask).start();
    }
}