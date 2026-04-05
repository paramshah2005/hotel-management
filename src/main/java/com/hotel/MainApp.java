package com.hotel;

import com.hotel.db.DBConnection;
import com.hotel.ui.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        DBConnection.getConnection();

        Label title = new Label("Hotel Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill: white;");

        Button btnRooms    = new Button("Manage Rooms");
        Button btnGuests   = new Button("Manage Guests");
        Button btnBookings = new Button("Manage Bookings");
        Button btnBilling  = new Button("Billing");
        Button btnHistory  = new Button("Guest History");
        Button btnPayments = new Button("Payments");

        Button[] buttons = {btnRooms, btnGuests, btnBookings, btnBilling, btnHistory, btnPayments};

        for (Button btn : buttons) {
            btn.setPrefWidth(260);
            btn.setPrefHeight(55);

            btn.setStyle(
                "-fx-background-color: #24324a;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 12;" +   // 👈 less pill (was ~25)
                "-fx-font-size: 14px;"
            );

            btn.setOnMouseEntered(e ->
                btn.setStyle(
                    "-fx-background-color: #2f3f5c;" +
                    "-fx-text-fill: #e5e7eb;" +
                    "-fx-background-radius: 12;" +
                    "-fx-font-size: 14px;"
                )
            );

            btn.setOnMouseExited(e ->
                btn.setStyle(
                    "-fx-background-color: #24324a;" +
                    "-fx-text-fill: white;" +
                    "-fx-background-radius: 12;" +
                    "-fx-font-size: 14px;"
                )
            );
        }

        btnRooms.setOnAction(e -> new RoomsView().show());
        btnGuests.setOnAction(e -> new GuestsView().show());
        btnBookings.setOnAction(e -> new BookingsView().show());
        btnBilling.setOnAction(e -> new BillingView().show());
        btnHistory.setOnAction(e -> new GuestHistoryView().show());
        btnPayments.setOnAction(e -> new PaymentsView().show());

        VBox layout = new VBox(18, title,
                btnRooms, btnGuests, btnBookings, btnBilling, btnHistory, btnPayments);

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: #0f172a;");

        primaryStage.setTitle("Hotel Management System");
        primaryStage.setScene(new Scene(layout, 360, 480));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}