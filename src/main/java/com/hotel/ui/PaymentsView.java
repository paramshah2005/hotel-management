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
        revenueLabel.setStyle("-fx-text-fill: #ffffff;");

        TableColumn<Payment, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()));

        TableColumn<Payment, Number> bookingCol = new TableColumn<>("Booking");
        bookingCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getBookingId()));

        TableColumn<Payment, Number> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getAmount()));

        TableColumn<Payment, String> methodCol = new TableColumn<>("Method");
        methodCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getPaymentMethod()));

        table.getColumns().addAll(idCol, bookingCol, amtCol, methodCol);

        table.setStyle(
                "-fx-background-color: #1c2433;" +
                "-fx-control-inner-background: #1c2433;" +
                "-fx-table-cell-border-color: transparent;" +
                "-fx-text-fill: #6b7280;"
        );

        table.setItems(FXCollections.observableArrayList(
                bookingService.getAllPayments()
        ));

        revenueLabel.setText("Total Revenue: " + bookingService.getTotalRevenue());

        VBox root = new VBox(10, revenueLabel, table);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #121826;");

        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
}