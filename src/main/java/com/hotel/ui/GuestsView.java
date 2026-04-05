package com.hotel.ui;

import com.hotel.model.Guest;
import com.hotel.service.GuestService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GuestsView {

    private GuestService guestService = new GuestService();
    private TableView<Guest> table = new TableView<>();

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Manage Guests");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        TextField firstName = new TextField();
        TextField lastName = new TextField();
        TextField email = new TextField();
        TextField phone = new TextField();
        TextField address = new TextField();
        TextField idProof = new TextField();

        form.add(new Label("First Name"), 0, 0);
        form.add(firstName, 1, 0);
        form.add(new Label("Last Name"), 0, 1);
        form.add(lastName, 1, 1);
        form.add(new Label("Email"), 0, 2);
        form.add(email, 1, 2);
        form.add(new Label("Phone"), 0, 3);
        form.add(phone, 1, 3);
        form.add(new Label("Address"), 0, 4);
        form.add(address, 1, 4);
        form.add(new Label("ID Proof"), 0, 5);
        form.add(idProof, 1, 5);

        Button btnAdd = new Button("Add Guest");
        Button btnDelete = new Button("Delete");

        HBox actions = new HBox(10, btnAdd, btnDelete);

        TextField search = new TextField();
        search.setPromptText("Search...");

        // Table
        TableColumn<Guest, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFullName()));

        TableColumn<Guest, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<Guest, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));

        table.getColumns().addAll(nameCol, emailCol, phoneCol);
        refreshTable();

        // Actions
        btnAdd.setOnAction(e -> {
            Guest g = new Guest();
            g.setFirstName(firstName.getText());
            g.setLastName(lastName.getText());
            g.setEmail(email.getText());
            g.setPhone(phone.getText());
            g.setAddress(address.getText());
            g.setIdProof(idProof.getText());

            guestService.addGuest(g);
            refreshTable();
        });

        btnDelete.setOnAction(e -> {
            Guest selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                guestService.deleteGuest(selected.getId());
                refreshTable();
            }
        });

        search.textProperty().addListener((obs, oldVal, newVal) -> {
            table.setItems(FXCollections.observableArrayList(
                    guestService.searchGuests(newVal)
            ));
        });

        VBox root = new VBox(10, form, actions, search, table);
        root.setPadding(new Insets(10));

        stage.setScene(new Scene(root, 600, 500));
        stage.show();
    }

    private void refreshTable() {
        table.setItems(FXCollections.observableArrayList(
                guestService.getAllGuests()
        ));
    }
}