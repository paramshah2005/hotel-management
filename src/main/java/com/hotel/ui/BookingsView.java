package com.hotel.ui;

import com.hotel.model.*;
import com.hotel.service.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class BookingsView {

    private BookingService bookingService = new BookingService();
    private GuestService guestService = new GuestService();
    private RoomService roomService = new RoomService();

    private TableView<Booking> table = new TableView<>();
    private Label totalLabel = new Label("Total: 0");

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Manage Bookings");

        String inputStyle = "-fx-background-color: #656565; -fx-text-fill: #e5e7eb;";

        ComboBox<Guest> guestBox = new ComboBox<>();
        guestBox.setItems(FXCollections.observableArrayList(guestService.getAllGuests()));
        guestBox.setStyle(inputStyle);

        ComboBox<Room> roomBox = new ComboBox<>();
        roomBox.setItems(FXCollections.observableArrayList(roomService.getAvailableRooms()));
        roomBox.setStyle(inputStyle);

        DatePicker checkIn = new DatePicker();
        DatePicker checkOut = new DatePicker();
        checkIn.setStyle(inputStyle);
        checkOut.setStyle(inputStyle);

        totalLabel.setStyle("-fx-text-fill: #ffffff;");

        Button btnCreate = new Button("Create Booking");
        Button btnCheckIn = new Button("Check-In");
        Button btnCheckOut = new Button("Check-Out");
        Button btnCancel = new Button("Cancel");

        Button[] buttons = {btnCreate, btnCheckIn, btnCheckOut, btnCancel};
        for (Button btn : buttons) {
            btn.setStyle("-fx-background-color: #2a3447; -fx-text-fill: #ffffff;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #374151; -fx-text-fill: #d1d5db;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #2a3447; -fx-text-fill: #ffffff;"));
        }

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));
        form.setStyle("-fx-background-color: #1c2433; -fx-background-radius: 10;");

        Label lbGuest = new Label("Guest");
        Label lbRoom = new Label("Room");
        Label lbIn = new Label("Check-in");
        Label lbOut = new Label("Check-out");

        lbGuest.setStyle("-fx-text-fill: #ffffff;");
        lbRoom.setStyle("-fx-text-fill: #ffffff;");
        lbIn.setStyle("-fx-text-fill: #ffffff;");
        lbOut.setStyle("-fx-text-fill: #ffffff;");

        form.add(lbGuest, 0, 0);
        form.add(guestBox, 1, 0);
        form.add(lbRoom, 0, 1);
        form.add(roomBox, 1, 1);
        form.add(lbIn, 0, 2);
        form.add(checkIn, 1, 2);
        form.add(lbOut, 0, 3);
        form.add(checkOut, 1, 3);
        form.add(totalLabel, 1, 4);

        TableColumn<Booking, String> guestCol = new TableColumn<>("Guest");
        guestCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getGuestName()));

        TableColumn<Booking, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getRoomNumber()));

        TableColumn<Booking, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStatus()));

        table.getColumns().addAll(guestCol, roomCol, statusCol);

        table.setStyle(
                "-fx-background-color: #1c2433;" +
                "-fx-control-inner-background: #1c2433;" +
                "-fx-table-cell-border-color: transparent;" +
                "-fx-text-fill: #6b7280;"
        );

        refreshTable();

        HBox actions = new HBox(10, btnCheckIn, btnCheckOut, btnCancel);

        VBox root = new VBox(10, form, btnCreate, actions, table);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #121826;");

        stage.setScene(new Scene(root, 650, 500));
        stage.show();
    }

    private void refreshTable() {
        table.setItems(FXCollections.observableArrayList(
                bookingService.getAllBookings()
        ));
    }
}