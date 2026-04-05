package com.hotel.ui;

import com.hotel.model.*;
import com.hotel.service.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

        ComboBox<Guest> guestBox = new ComboBox<>(
                FXCollections.observableArrayList(guestService.getAllGuests()));

        ComboBox<Room> roomBox = new ComboBox<>(
                FXCollections.observableArrayList(roomService.getAvailableRooms()));

        DatePicker checkIn = new DatePicker();
        DatePicker checkOut = new DatePicker();

        Button btnCreate = new Button("Create Booking");
        Button btnCheckIn = new Button("Check-In");
        Button btnCheckOut = new Button("Check-Out");
        Button btnCancel = new Button("Cancel");

        btnCreate.setPrefWidth(120);
        btnCheckIn.setPrefWidth(120);
        btnCheckOut.setPrefWidth(120);
        btnCancel.setPrefWidth(120);

        HBox buttonRow = new HBox(10, btnCreate, btnCheckIn, btnCheckOut, btnCancel);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("Guest"), 0, 0);
        form.add(guestBox, 1, 0);
        form.add(new Label("Room"), 0, 1);
        form.add(roomBox, 1, 1);
        form.add(new Label("Check-in"), 0, 2);
        form.add(checkIn, 1, 2);
        form.add(new Label("Check-out"), 0, 3);
        form.add(checkOut, 1, 3);
        form.add(totalLabel, 1, 4);

        Runnable calc = () -> {
            if (roomBox.getValue() != null && checkIn.getValue() != null && checkOut.getValue() != null) {
                long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn.getValue(), checkOut.getValue());
                double subtotal = bookingService.calculateSubtotal(
                        roomBox.getValue().getPricePerNight(), nights);
                double total = bookingService.calculateTotal(subtotal);
                totalLabel.setText("Total: " + total);
            }
        };

        roomBox.setOnAction(e -> calc.run());
        checkIn.setOnAction(e -> calc.run());
        checkOut.setOnAction(e -> calc.run());

        TableColumn<Booking, String> guestCol = new TableColumn<>("Guest");
        guestCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getGuestName()));

        TableColumn<Booking, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getRoomNumber()));

        TableColumn<Booking, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStatus()));

        table.getColumns().addAll(guestCol, roomCol, statusCol);
        refreshTable();

        btnCreate.setOnAction(e -> {
            Booking b = new Booking();
            b.setGuestId(guestBox.getValue().getId());
            b.setRoomId(roomBox.getValue().getId());
            b.setCheckIn(checkIn.getValue());
            b.setCheckOut(checkOut.getValue());

            bookingService.createBooking(b);
            refreshTable();
        });

        btnCheckIn.setOnAction(e -> {
            Booking b = table.getSelectionModel().getSelectedItem();
            if (b != null) {
                bookingService.checkIn(b.getId(), b.getRoomId());
                refreshTable();
            }
        });

        btnCheckOut.setOnAction(e -> {
            Booking b = table.getSelectionModel().getSelectedItem();
            if (b != null) {
                bookingService.checkOut(b.getId(), b.getRoomId());
                refreshTable();
            }
        });

        btnCancel.setOnAction(e -> {
            Booking b = table.getSelectionModel().getSelectedItem();
            if (b != null) {
                bookingService.cancelBooking(b.getId(), b.getRoomId());
                refreshTable();
            }
        });

        VBox root = new VBox(10, form, buttonRow, table);
        root.setPadding(new Insets(10));

        stage.setScene(new Scene(root, 650, 500));
        stage.show();
    }

    private void refreshTable() {
        table.setItems(FXCollections.observableArrayList(
                bookingService.getAllBookings()));
    }
}