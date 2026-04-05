package com.hotel.ui;

import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.service.RoomService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class RoomsView {

    private final RoomService roomService = new RoomService();

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Manage Rooms");

        // Form fields
        Label lbNumber = new Label("Room Number:");
        TextField tfNumber = new TextField();

        Label lbFloor = new Label("Floor:");
        TextField tfFloor = new TextField();

        Label lbPrice = new Label("Price Per Night:");
        TextField tfPrice = new TextField();

        Label lbType = new Label("Room Type:");
        ComboBox<RoomType> cbType = new ComboBox<>(FXCollections.observableArrayList(roomService.getAllRoomTypes()));

        Label lbStatus = new Label("Status:");
        ComboBox<String> cbStatus = new ComboBox<>(
                FXCollections.observableArrayList("AVAILABLE", "OCCUPIED", "MAINTENANCE"));
        cbStatus.setValue("AVAILABLE");

        Button btnAdd = new Button("Add Room");
        Button btnDelete = new Button("Delete Selected");
        btnAdd.setStyle("-fx-background-color: #66bb6a; -fx-text-fill: white;");
        btnDelete.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white;");
        Label msgLabel = new Label();

        // Table
        TableView<Room> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Room, String> colNumber = new TableColumn<>("Room No.");
        colNumber
                .setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getRoomNumber()));

        TableColumn<Room, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getRoomTypeName()));

        TableColumn<Room, String> colFloor = new TableColumn<>("Floor");
        colFloor.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getFloor())));

        TableColumn<Room, String> colPrice = new TableColumn<>("Price/Night");
        colPrice.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty("Rs." + d.getValue().getPricePerNight()));

        TableColumn<Room, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStatus()));

        table.getColumns().addAll(colNumber, colType, colFloor, colPrice, colStatus);
        table.setItems(FXCollections.observableArrayList(roomService.getAllRooms()));

        btnAdd.setOnAction(e -> {
            try {
                if (cbType.getValue() == null) {
                    msgLabel.setText("Select a room type.");
                    return;
                }
                Room room = new Room();
                room.setRoomNumber(tfNumber.getText().trim());
                room.setFloor(Integer.parseInt(tfFloor.getText().trim()));
                room.setPricePerNight(Double.parseDouble(tfPrice.getText().trim()));
                room.setRoomTypeId(cbType.getValue().getId());
                room.setRoomTypeName(cbType.getValue().getName());
                room.setStatus(cbStatus.getValue());
                if (roomService.addRoom(room)) {
                    table.setItems(FXCollections.observableArrayList(roomService.getAllRooms()));
                    tfNumber.clear();
                    tfFloor.clear();
                    tfPrice.clear();
                    msgLabel.setText("Room added.");
                } else {
                    msgLabel.setText("Failed. Check inputs.");
                }
            } catch (NumberFormatException ex) {
                msgLabel.setText("Invalid floor or price.");
            }
        });

        btnDelete.setOnAction(e -> {
            Room selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                msgLabel.setText("Select a room first.");
                return;
            }
            roomService.deleteRoom(selected.getId());
            table.setItems(FXCollections.observableArrayList(roomService.getAllRooms()));
            msgLabel.setText("Room deleted.");
        });

        // Layout
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.addRow(0, lbNumber, tfNumber, lbFloor, tfFloor);
        form.addRow(1, lbPrice, tfPrice, lbType, cbType);
        form.addRow(2, lbStatus, cbStatus);

        HBox btnRow = new HBox(10, btnAdd, btnDelete, msgLabel);
        btnRow.setPadding(new Insets(5, 10, 5, 10));

        VBox layout = new VBox(10, form, btnRow, table);
        layout.setStyle("-fx-background-color: #d6eaff;");
        layout.setPadding(new Insets(10));

        stage.setScene(new Scene(layout, 700, 500));
        stage.show();
    }
}