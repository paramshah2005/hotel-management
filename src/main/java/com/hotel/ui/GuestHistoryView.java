package com.hotel.ui;

import com.hotel.model.*;
import com.hotel.service.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GuestHistoryView {

    private BookingService bookingService = new BookingService();
    private GuestService guestService = new GuestService();

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Guest Billing History");

        String inputStyle = "-fx-background-color: #656565; -fx-text-fill: #e5e7eb;";

        ComboBox<Guest> guestBox = new ComboBox<>(
                FXCollections.observableArrayList(guestService.getAllGuests()));
        guestBox.setStyle(inputStyle);

        Label totalLabel = new Label("Total Spent: ₹0");
        totalLabel.setStyle("-fx-text-fill: white;");

        TableView<Booking> table = new TableView<>();

        TableColumn<Booking, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getRoomNumber()));

        TableColumn<Booking, String> inCol = new TableColumn<>("Check-in");
        inCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCheckIn().toString()));

        TableColumn<Booking, String> outCol = new TableColumn<>("Check-out");
        outCol.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCheckOut().toString()));

        TableColumn<Booking, String> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                "₹" + String.format("%.2f", d.getValue().getTotalAmount())));

        table.getColumns().setAll(roomCol, inCol, outCol, amtCol);
        table.setPlaceholder(new Label("Select a guest to view history"));

        table.setStyle(
                "-fx-background-color: #1c2433;" +
                        "-fx-control-inner-background: #1c2433;" +
                        "-fx-text-fill: #6b7280;" +
                        "-fx-table-cell-border-color: transparent;"

        );

        guestBox.setOnAction(e -> {
            Guest g = guestBox.getValue();
            if (g != null) {

                table.setItems(FXCollections.observableArrayList(
                        bookingService.getBookingsByGuest(g.getId())));

                double total = bookingService.getTotalSpentByGuest(g.getId());
                totalLabel.setText("Total Spent: ₹" + String.format("%.2f", total));
            }
        });

        VBox root = new VBox(10, guestBox, totalLabel, table);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #121826;");

        stage.setScene(new Scene(root, 600, 450));
        stage.show();
    }
}