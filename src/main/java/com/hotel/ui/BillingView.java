package com.hotel.ui;

import com.hotel.model.*;
import com.hotel.service.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class BillingView {

        private BookingService bookingService = new BookingService();
        private TableView<Payment> table = new TableView<>();

        public void show() {
                Stage stage = new Stage();
                stage.setTitle("Billing");

                String inputStyle = "-fx-background-color: #656565; -fx-text-fill: #e5e7eb;";

                ComboBox<Booking> bookingBox = new ComboBox<>();
                bookingBox.setItems(FXCollections.observableArrayList(bookingService.getAllBookings()));
                bookingBox.setStyle(inputStyle);

                ComboBox<String> methodBox = new ComboBox<>(
                                FXCollections.observableArrayList("CASH", "CARD", "UPI", "BANK_TRANSFER"));
                methodBox.setStyle(inputStyle);

                Label details = new Label("Invoice Details");
                details.setStyle("-fx-text-fill: #ffffff;");

                Button btnPay = new Button("Process Payment");
                btnPay.setStyle("-fx-background-color: #2a3447; -fx-text-fill: #ffffff;");
                btnPay.setOnMouseEntered(
                                e -> btnPay.setStyle("-fx-background-color: #374151; -fx-text-fill: #d1d5db;"));
                btnPay.setOnMouseExited(e -> btnPay.setStyle("-fx-background-color: #2a3447; -fx-text-fill: #ffffff;"));

                TableColumn<Payment, Number> amtCol = new TableColumn<>("Amount");
                amtCol.setCellValueFactory(
                                d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getAmount()));

                TableColumn<Payment, String> methodCol = new TableColumn<>("Method");
                methodCol.setCellValueFactory(
                                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getPaymentMethod()));

                table.getColumns().setAll(amtCol, methodCol);

                table.setStyle(
                                "-fx-background-color: #1c2433;" +
                                                "-fx-control-inner-background: #1c2433;" +
                                                "-fx-table-cell-border-color: transparent;" +
                                                "-fx-text-fill: #6b7280;");

                // 🔥 THIS WAS MISSING
                bookingBox.setOnAction(e -> {
                        Booking b = bookingBox.getValue();
                        if (b != null) {
                                details.setText(
                                                "Guest: " + b.getGuestName() +
                                                                "\nRoom: " + b.getRoomNumber() +
                                                                "\nNights: " + b.getNumberOfNights() +
                                                                "\nTotal: ₹" + b.getTotalAmount());

                                table.setItems(FXCollections.observableArrayList(
                                                bookingService.getAllPayments()));
                        }
                });

                btnPay.setOnAction(e -> {
                        Booking b = bookingBox.getValue();
                        if (b != null) {
                                Payment p = new Payment();
                                p.setBookingId(b.getId());
                                p.setAmount(b.getTotalAmount());
                                p.setPaymentMethod(methodBox.getValue());
                                p.setStatus("COMPLETED");

                                bookingService.processPayment(p);

                                table.setItems(FXCollections.observableArrayList(
                                                bookingService.getAllPayments()));
                        }
                });

                VBox root = new VBox(10, bookingBox, details, methodBox, btnPay, table);
                root.setPadding(new Insets(10));
                root.setStyle("-fx-background-color: #121826;");

                stage.setScene(new Scene(root, 500, 500));
                stage.show();
        }
}