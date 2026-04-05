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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        try {
            DBConnection.getConnection();
        } catch (Exception e) {
            System.out.println("DB Connection Failed: " + e.getMessage());
        }

        VBox root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0b1220;");

        Label title = new Label("Hotel Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill: #e2e8f0;");

        Button btnRooms = new Button("Manage Rooms");
        Button btnGuests = new Button("Manage Guests");
        Button btnBookings = new Button("Manage Bookings");
        Button btnBilling = new Button("Billing");
        Button btnPayments = new Button("Payments");
        Button btnHistory = new Button("Guest History");

        Button[] buttons = {btnRooms, btnGuests, btnBookings, btnBilling, btnPayments, btnHistory};

        for (Button btn : buttons) {
            btn.setPrefWidth(260);
            btn.setPrefHeight(45);

            btn.setStyle(
                "-fx-background-color: #1f2a44;" +
                "-fx-text-fill: #e2e8f0;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 14;"
            );

            btn.setOnMouseEntered(e ->
                btn.setStyle(
                    "-fx-background-color: #2e3a59;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 14px;" +
                    "-fx-background-radius: 14;"
                )
            );

            btn.setOnMouseExited(e ->
                btn.setStyle(
                    "-fx-background-color: #1f2a44;" +
                    "-fx-text-fill: #e2e8f0;" +
                    "-fx-font-size: 14px;" +
                    "-fx-background-radius: 14;"
                )
            );
        }

        btnRooms.setOnAction(e -> new RoomsView().show());
        btnGuests.setOnAction(e -> new GuestsView().show());
        btnBookings.setOnAction(e -> new BookingsView().show());
        btnBilling.setOnAction(e -> new BillingView().show());
        btnPayments.setOnAction(e -> new PaymentsView().show());
        btnHistory.setOnAction(e -> new GuestHistoryView().show());

        VBox buttonColumn = new VBox(18, btnRooms, btnGuests, btnBookings, btnBilling, btnPayments, btnHistory);
        buttonColumn.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, buttonColumn);

        Scene scene = new Scene(root, 340, 420);

        primaryStage.setTitle("Hotel Management System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}