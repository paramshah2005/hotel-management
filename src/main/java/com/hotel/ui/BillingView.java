package com.hotel.ui;

import com.hotel.model.*;
import com.hotel.service.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

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

        Label details = new Label("Invoice Details");
        details.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 13px;");
        details.setWrapText(true);
        details.setMaxWidth(Double.MAX_VALUE);
        details.setMinHeight(120);

        VBox detailsBox = new VBox(details);
        detailsBox.setPadding(new Insets(10));
        detailsBox.setStyle("-fx-background-color: #1c2433; -fx-background-radius: 10;");

        Button btnPay = new Button("Process Payment");
        btnPay.setStyle("-fx-background-color: #2a3447; -fx-text-fill: #ffffff;");
        btnPay.setOnMouseEntered(e -> btnPay.setStyle("-fx-background-color: #374151; -fx-text-fill: #d1d5db;"));
        btnPay.setOnMouseExited(e -> btnPay.setStyle("-fx-background-color: #2a3447; -fx-text-fill: #ffffff;"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

        TableColumn<Payment, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()));

        TableColumn<Payment, String> guestCol = new TableColumn<>("Guest");
        guestCol.setCellValueFactory(d -> {
            int bookingId = d.getValue().getBookingId();
            for (Booking b : bookingService.getAllBookings()) {
                if (b.getId() == bookingId) {
                    return new javafx.beans.property.SimpleStringProperty(b.getGuestName());
                }
            }
            return new javafx.beans.property.SimpleStringProperty("Unknown");
        });

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

        table.setStyle(
                "-fx-background-color: #1c2433;" +
                "-fx-control-inner-background: #1c2433;" +
                "-fx-table-cell-border-color: transparent;" +
                "-fx-text-fill: #6b7280;"
        );

        refreshAll(bookingBox);

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
                    refreshAll(bookingBox);
                    details.setText("Payment successful");
                } else {
                    details.setText("Payment already exists or failed");
                }
            }
        });

        VBox root = new VBox(15, bookingBox, detailsBox, methodBox, btnPay, table);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #121826;");

        stage.setScene(new Scene(root, 600, 520));
        stage.show();
    }

    private void refreshAll(ComboBox<Booking> bookingBox) {
        List<Booking> bookings = bookingService.getUnpaidBookings();

        bookingBox.setItems(FXCollections.observableArrayList(bookings));

        table.setItems(FXCollections.observableArrayList(
                bookingService.getAllPayments()
        ));
    }
}