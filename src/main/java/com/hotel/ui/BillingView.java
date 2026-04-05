package com.hotel.ui;

import com.hotel.model.*;
import com.hotel.service.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class BillingView {

    private BookingService bookingService = new BookingService();
    private TableView<Payment> table = new TableView<>();

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Billing");

        String inputStyle = "-fx-background-color: #656565; -fx-text-fill: #e5e7eb;";

        ComboBox<Booking> bookingBox = new ComboBox<>();
        bookingBox.setStyle(inputStyle);

        ComboBox<String> methodBox = new ComboBox<>(FXCollections.observableArrayList("CASH", "CARD", "UPI", "BANK_TRANSFER"));
        methodBox.setStyle(inputStyle);

        Label details = new Label("Loading...");
        details.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 13px;");
        details.setWrapText(true);
        details.setMaxWidth(Double.MAX_VALUE);
        details.setMinHeight(120);

        VBox detailsBox = new VBox(details);
        detailsBox.setPadding(new Insets(10));
        detailsBox.setStyle("-fx-background-color: #1c2433; -fx-background-radius: 10;");

        Button btnPay = new Button("Process Payment");
        btnPay.setStyle("-fx-background-color: #2a3447; -fx-text-fill: #ffffff;");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

        TableColumn<Payment, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()));

        TableColumn<Payment, String> guestCol = new TableColumn<>("Guest");

        TableColumn<Payment, Number> bookingCol = new TableColumn<>("Booking");
        bookingCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getBookingId()));

        TableColumn<Payment, Number> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getAmount()));

        TableColumn<Payment, String> methodCol = new TableColumn<>("Method");
        methodCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getPaymentMethod()));

        TableColumn<Payment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getPaymentDate() != null
                        ? d.getValue().getPaymentDate().format(formatter)
                        : ""
        ));

        table.getColumns().setAll(idCol, guestCol, bookingCol, amtCol, methodCol, dateCol);

        VBox root = new VBox(15, bookingBox, detailsBox, methodBox, btnPay, table);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #121826;");

        stage.setScene(new Scene(root, 600, 520));
        stage.show();

        Task<Void> loadTask = new Task<>() {
            List<Booking> bookings;
            List<Payment> payments;
            Map<Integer, String> bookingGuestMap = new HashMap<>();

            @Override
            protected Void call() {
                bookings = bookingService.getUnpaidBookings();
                List<Booking> allBookings = bookingService.getAllBookings();
                payments = bookingService.getAllPayments();

                for (Booking b : allBookings) {
                    bookingGuestMap.put(b.getId(), b.getGuestName());
                }

                return null;
            }

            @Override
            protected void succeeded() {
                bookingBox.setItems(FXCollections.observableArrayList(bookings));
                table.setItems(FXCollections.observableArrayList(payments));

                guestCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                        bookingGuestMap.getOrDefault(d.getValue().getBookingId(), "Unknown")
                ));

                details.setText("Select a booking");
            }
        };

        new Thread(loadTask).start();

        bookingBox.setOnAction(e -> {
            Booking b = bookingBox.getValue();
            if (b != null) {
                double total = b.getTotalAmount();
                double subtotal = total / 1.18;
                double tax = total - subtotal;

                details.setText(
                        "Guest: " + b.getGuestName() +
                        "\nRoom: " + b.getRoomNumber() +
                        "\nNights: " + b.getNumberOfNights() +
                        "\nSubtotal: ₹" + String.format("%.2f", subtotal) +
                        "\nGST (18%): ₹" + String.format("%.2f", tax) +
                        "\nTotal: ₹" + String.format("%.2f", total)
                );
            }
        });

        btnPay.setOnAction(e -> {
            Booking b = bookingBox.getValue();
            if (b != null && methodBox.getValue() != null) {

                Payment p = new Payment();
                p.setBookingId(b.getId());
                p.setAmount(b.getTotalAmount());
                p.setPaymentMethod(methodBox.getValue());
                p.setStatus("COMPLETED");

                boolean success = bookingService.processPayment(p);

                if (success) {
                    details.setText("Payment successful");
                } else {
                    details.setText("Payment failed");
                }
            }
        });
    }
}