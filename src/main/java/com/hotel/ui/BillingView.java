package com.hotel.ui;

import com.hotel.model.*;
import com.hotel.service.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class BillingView {

    private BookingService bookingService = new BookingService();
    private TableView<Payment> table = new TableView<>();

    private void generateInvoice(Booking b) {
        try {
            File dir = new File("invoices");
            if (!dir.exists())
                dir.mkdir();

            String fileName = "invoices/invoice_" + b.getId() + ".txt";

            FileWriter writer = new FileWriter(fileName);

            double total = b.getTotalAmount();
            double subtotal = total / 1.18;
            double tax = total - subtotal;

            String line = "==================================================\n";

            writer.write(line);
            writer.write(String.format("%30s\n", "HOTEL INVOICE"));
            writer.write(line);

            writer.write(String.format("Invoice No : %d\n", b.getId()));
            writer.write(String.format("Date       : %s\n\n", java.time.LocalDate.now()));

            writer.write("CUSTOMER DETAILS\n");
            writer.write("----------------------------------------------\n");
            writer.write(String.format("Guest Name : %s\n", b.getGuestName()));
            writer.write(String.format("Room No    : %s\n\n", b.getRoomNumber()));

            writer.write("BOOKING DETAILS\n");
            writer.write("----------------------------------------------\n");
            writer.write(String.format("Check-in   : %s\n", b.getCheckIn()));
            writer.write(String.format("Check-out  : %s\n", b.getCheckOut()));
            writer.write(String.format("Nights     : %d\n\n", b.getNumberOfNights()));

            writer.write("BILL SUMMARY\n");
            writer.write("----------------------------------------------\n");

            writer.write(String.format("%-25s %15s\n", "Subtotal:", "₹" + String.format("%.2f", subtotal)));
            writer.write(String.format("%-25s %15s\n", "GST (18%):", "₹" + String.format("%.2f", tax)));
            writer.write(String.format("%-25s %15s\n", "Total:", "₹" + String.format("%.2f", total)));

            writer.write(line);

            writer.write(String.format("%30s\n", "Thank you for your stay!"));
            writer.write(String.format("%28s\n", "Visit Again"));

            writer.write(line);

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Billing");

        String inputStyle = "-fx-background-color: #656565; -fx-text-fill: #e5e7eb;";

        ComboBox<Booking> bookingBox = new ComboBox<>();
        bookingBox.setStyle(inputStyle);

        ComboBox<String> methodBox = new ComboBox<>(
                FXCollections.observableArrayList("CASH", "CARD", "UPI", "BANK_TRANSFER"));
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
        Button btnInvoice = new Button("Generate Invoice");
        btnInvoice.setStyle("-fx-background-color: #2a3447; -fx-text-fill: #ffffff;");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

        TableColumn<Payment, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()));

        TableColumn<Payment, String> guestCol = new TableColumn<>("Guest");

        TableColumn<Payment, Number> bookingCol = new TableColumn<>("Booking");
        bookingCol
                .setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getBookingId()));

        TableColumn<Payment, Number> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getAmount()));

        TableColumn<Payment, String> methodCol = new TableColumn<>("Method");
        methodCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getPaymentMethod()));

        TableColumn<Payment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getPaymentDate() != null
                        ? d.getValue().getPaymentDate().format(formatter)
                        : ""));

        table.getColumns().setAll(idCol, guestCol, bookingCol, amtCol, methodCol, dateCol);
        table.setPlaceholder(new Label("No payment history available"));
        table.setStyle(
                "-fx-background-color: #1c2433;" +
                        "-fx-control-inner-background: #1c2433;" +
                        "-fx-table-cell-border-color: transparent;" +
                        "-fx-text-fill: #d1d5db;");
        VBox root = new VBox(15, bookingBox, detailsBox, methodBox, btnPay, btnInvoice, table);
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
                        bookingGuestMap.getOrDefault(d.getValue().getBookingId(), "Unknown")));

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
                                "\nTotal: ₹" + String.format("%.2f", total));
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

                    // Refresh table
                    List<Payment> updatedPayments = bookingService.getAllPayments();
                    table.setItems(FXCollections.observableArrayList(updatedPayments));

                    // Refresh bookingBox (VERY IMPORTANT)
                    List<Booking> updatedBookings = bookingService.getUnpaidBookings();
                    bookingBox.setItems(FXCollections.observableArrayList(updatedBookings));

                    // Optional: clear selection
                    bookingBox.setValue(null);
                } else {
                    details.setText("Payment failed");
                }
            }
        });
        btnInvoice.setOnAction(e -> {
            Payment selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                details.setText("Select a payment first");
                return;
            }

            Booking b = bookingService.getAllBookings()
                    .stream()
                    .filter(x -> x.getId() == selected.getBookingId())
                    .findFirst()
                    .orElse(null);

            if (b != null) {
                generateInvoice(b);
                details.setText("Invoice saved in /invoices folder");
            }
        });
    }
}