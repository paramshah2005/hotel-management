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

        String inputStyle = "-fx-background-color: #656565; -fx-text-fill: #e5e7eb;";

        Label lbNumber = new Label("Room Number:");
        TextField tfNumber = new TextField();
        tfNumber.setStyle(inputStyle);

        Label lbFloor = new Label("Floor:");
        TextField tfFloor = new TextField();
        tfFloor.setStyle(inputStyle);

        Label lbPrice = new Label("Price Per Night:");
        TextField tfPrice = new TextField();
        tfPrice.setStyle(inputStyle);

        Label lbType = new Label("Room Type:");
        ComboBox<RoomType> cbType = new ComboBox<>(FXCollections.observableArrayList(roomService.getAllRoomTypes()));
        cbType.setStyle(inputStyle);

        Label lbStatus = new Label("Status:");
        ComboBox<String> cbStatus = new ComboBox<>(FXCollections.observableArrayList("AVAILABLE", "OCCUPIED"));
        cbStatus.setValue("AVAILABLE");
        cbStatus.setStyle(inputStyle);

        Label msgLabel = new Label();
        msgLabel.setStyle("-fx-text-fill: #ffffff;");

        lbNumber.setStyle("-fx-text-fill: #ffffff;");
        lbFloor.setStyle("-fx-text-fill: #ffffff;");
        lbPrice.setStyle("-fx-text-fill: #ffffff;");
        lbType.setStyle("-fx-text-fill: #ffffff;");
        lbStatus.setStyle("-fx-text-fill: #ffffff;");

        Button btnAdd = new Button("Add Room");
        Button btnDelete = new Button("Delete Selected");

        btnAdd.setStyle("-fx-background-color: #2a3447; -fx-text-fill: #ffffff;");
        btnDelete.setStyle("-fx-background-color: #2a3447; -fx-text-fill: #ffffff;");

        btnAdd.setOnMouseEntered(e -> btnAdd.setStyle("-fx-background-color: #374151; -fx-text-fill: #d1d5db;"));
        btnAdd.setOnMouseExited(e -> btnAdd.setStyle("-fx-background-color: #2a3447; -fx-text-fill: #ffffff;"));

        btnDelete.setOnMouseEntered(e -> btnDelete.setStyle("-fx-background-color: #374151; -fx-text-fill: #d1d5db;"));
        btnDelete.setOnMouseExited(e -> btnDelete.setStyle("-fx-background-color: #2a3447; -fx-text-fill: #ffffff;"));

        TableView<Room> table = new TableView<>();

        TableColumn<Room, String> colNumber = new TableColumn<>("Room No.");
        colNumber.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getRoomNumber()));

        TableColumn<Room, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getRoomTypeName()));

        TableColumn<Room, String> colFloor = new TableColumn<>("Floor");
        colFloor.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getFloor())));

        TableColumn<Room, String> colPrice = new TableColumn<>("Price/Night");
        colPrice.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty("Rs." + d.getValue().getPricePerNight()));

        TableColumn<Room, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStatus()));

        table.getColumns().addAll(colNumber, colType, colFloor, colPrice, colStatus);

        table.setStyle(
                "-fx-background-color: #1c2433;" +
                "-fx-control-inner-background: #1c2433;" +
                "-fx-table-cell-border-color: transparent;" +
                "-fx-text-fill: #6b7280;"
        );

        table.setItems(FXCollections.observableArrayList(roomService.getAllRooms()));

        btnAdd.setOnAction(e -> {
            try {
                if (cbType.getValue() == null) { msgLabel.setText("Select a room type."); return; }
                Room room = new Room();
                room.setRoomNumber(tfNumber.getText().trim());
                room.setFloor(Integer.parseInt(tfFloor.getText().trim()));
                room.setPricePerNight(Double.parseDouble(tfPrice.getText().trim()));
                room.setRoomTypeId(cbType.getValue().getId());
                room.setRoomTypeName(cbType.getValue().getName());
                room.setStatus(cbStatus.getValue());
                if (roomService.addRoom(room)) {
                    table.setItems(FXCollections.observableArrayList(roomService.getAllRooms()));
                    tfNumber.clear(); tfFloor.clear(); tfPrice.clear();
                    msgLabel.setText("Room added.");
                } else {
                    msgLabel.setText("Failed. Check inputs.");
                }
            } catch (Exception ex) {
                msgLabel.setText("Invalid input.");
            }
        });

        btnDelete.setOnAction(e -> {
            Room selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { msgLabel.setText("Select a room first."); return; }
            roomService.deleteRoom(selected.getId());
            table.setItems(FXCollections.observableArrayList(roomService.getAllRooms()));
            msgLabel.setText("Room deleted.");
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.setStyle("-fx-background-color: #1c2433; -fx-background-radius: 10;");

        form.addRow(0, lbNumber, tfNumber, lbFloor, tfFloor);
        form.addRow(1, lbPrice, tfPrice, lbType, cbType);
        form.addRow(2, lbStatus, cbStatus);

        HBox btnRow = new HBox(10, btnAdd, btnDelete, msgLabel);
        btnRow.setPadding(new Insets(5, 10, 5, 10));

        VBox layout = new VBox(10, form, btnRow, table);
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: #121826;");

        stage.setScene(new Scene(layout, 700, 500));
        stage.show();
    }
}