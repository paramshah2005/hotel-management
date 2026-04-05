package com.hotel;

import com.hotel.db.DBConnection;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.hotel.ui.*;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        try {
            DBConnection.getConnection();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to connect to database");
            alert.setContentText("Please check your database configuration.\n\n" + e.getMessage());
            alert.showAndWait();
            return; 
        }

        Label title = new Label("Hotel Management System");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label subtitle = new Label("Select a module to continue");
        subtitle.setFont(Font.font("Arial", 12));

        Button btnRooms    = new Button("Manage Rooms");
        Button btnGuests   = new Button("Manage Guests");
        Button btnBookings = new Button("Manage Bookings");
        Button btnBilling  = new Button("Billing");
        Button btnPayments = new Button("View Payments");

        for (Button btn : new Button[]{btnRooms, btnGuests, btnBookings, btnBilling, btnPayments}) {
            btn.setPrefWidth(200);
        }

        // Navigation
        btnRooms.setOnAction(e -> new RoomsView().show());
        btnGuests.setOnAction(e -> new GuestsView().show());
        btnBookings.setOnAction(e -> new BookingsView().show());
        btnBilling.setOnAction(e -> new BillingView().show());
        btnPayments.setOnAction(e -> new PaymentsView().show());

        VBox layout = new VBox(12,
                title,
                subtitle,
                btnRooms,
                btnGuests,
                btnBookings,
                btnBilling,
                btnPayments
        );
        layout.setStyle("-fx-background-color: #d6eaff;");

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        primaryStage.setTitle("Hotel Management System");
        primaryStage.setScene(new Scene(layout, 320, 340));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}