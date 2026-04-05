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

        ComboBox<Booking> bookingBox = new ComboBox<>(
                FXCollections.observableArrayList(bookingService.getAllBookings())
        );

        Label details = new Label("Invoice Details");

        ComboBox<String> methodBox = new ComboBox<>(
                FXCollections.observableArrayList("CASH", "CARD", "UPI", "BANK_TRANSFER")
        );

        Button btnPay = new Button("Process Payment");

        TableColumn<Payment, Number> bookingCol = new TableColumn<>("Booking");
        bookingCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getBookingId()));

        TableColumn<Payment, Number> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getAmount()));

        TableColumn<Payment, String> methodCol = new TableColumn<>("Method");
        methodCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getPaymentMethod()));

        table.getColumns().addAll(bookingCol, amtCol, methodCol);

        bookingBox.setOnAction(e -> {
            Booking b = bookingBox.getValue();
            if (b != null) {
                details.setText("Guest: " + b.getGuestName()
                        + "\nRoom: " + b.getRoomNumber()
                        + "\nNights: " + b.getNumberOfNights()
                        + "\nTotal: " + b.getTotalAmount());

                table.setItems(FXCollections.observableArrayList(
                        bookingService.getPaymentsByBooking(b.getId())
                ));
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
                        bookingService.getPaymentsByBooking(b.getId())
                ));
            }
        });

        VBox root = new VBox(10,
                bookingBox,
                details,
                new Label("Method"), methodBox,
                btnPay,
                table
        );

        root.setPadding(new Insets(10));

        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }
}