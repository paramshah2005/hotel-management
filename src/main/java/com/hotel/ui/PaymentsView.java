package com.hotel.ui;

import com.hotel.model.Payment;
import com.hotel.service.BookingService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PaymentsView {

    private BookingService bookingService = new BookingService();
    private TableView<Payment> table = new TableView<>();

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("All Payments");

        Label revenueLabel = new Label();

        TableColumn<Payment, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()));

        TableColumn<Payment, Number> bookingCol = new TableColumn<>("Booking");
        bookingCol
                .setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getBookingId()));

        TableColumn<Payment, Number> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getAmount()));

        TableColumn<Payment, String> methodCol = new TableColumn<>("Method");
        methodCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getPaymentMethod()));

        TableColumn<Payment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStatus()));

        TableColumn<Payment, String> guestCol = new TableColumn<>("Guest");
        guestCol.setCellValueFactory(d -> {
            var booking = bookingService.getAllBookings().stream()
                    .filter(b -> b.getId() == d.getValue().getBookingId())
                    .findFirst()
                    .orElse(null);
            return new javafx.beans.property.SimpleStringProperty(
                    booking != null ? booking.getGuestName() : "");
        });

        table.getColumns().addAll(idCol, guestCol, bookingCol, amtCol, methodCol, statusCol);

        table.setItems(FXCollections.observableArrayList(
                bookingService.getAllPayments()));

        revenueLabel.setText("Total Revenue: " + bookingService.getTotalRevenue());

        VBox root = new VBox(10, revenueLabel, table);
        root.setPadding(new Insets(10));

        stage.setScene(new Scene(root, 700, 400));
        stage.show();
    }
}