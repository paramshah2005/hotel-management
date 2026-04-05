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
        form.setStyle("-fx-background-color: #1c2433; -fx-background-radius: 10;");

        TextField firstName = new TextField();
        TextField lastName = new TextField();
        TextField email = new TextField();
        TextField phone = new TextField();
        TextField address = new TextField();
        TextField idProof = new TextField();

        String inputStyle = "-fx-background-color: #656565; -fx-text-fill: #e5e7eb; -fx-prompt-text-fill: #ffffff;";

        firstName.setStyle(inputStyle);
        lastName.setStyle(inputStyle);
        email.setStyle(inputStyle);
        phone.setStyle(inputStyle);
        address.setStyle(inputStyle);
        idProof.setStyle(inputStyle);

        Label lbFirst = new Label("First Name");
        Label lbLast = new Label("Last Name");
        Label lbEmail = new Label("Email");
        Label lbPhone = new Label("Phone");
        Label lbAddress = new Label("Address");
        Label lbId = new Label("ID Proof");

        lbFirst.setStyle("-fx-text-fill: #ffffff;");
        lbLast.setStyle("-fx-text-fill: #ffffff;");
        lbEmail.setStyle("-fx-text-fill: #ffffff;");
        lbPhone.setStyle("-fx-text-fill: #ffffff;");
        lbAddress.setStyle("-fx-text-fill: #ffffff;");
        lbId.setStyle("-fx-text-fill: #ffffff;");

        form.add(lbFirst, 0, 0);
        form.add(firstName, 1, 0);
        form.add(lbLast, 0, 1);
        form.add(lastName, 1, 1);
        form.add(lbEmail, 0, 2);
        form.add(email, 1, 2);
        form.add(lbPhone, 0, 3);
        form.add(phone, 1, 3);
        form.add(lbAddress, 0, 4);
        form.add(address, 1, 4);
        form.add(lbId, 0, 5);
        form.add(idProof, 1, 5);

        Button btnAdd = new Button("Add Guest");
        Button btnDelete = new Button("Delete");

        btnAdd.setStyle("-fx-background-color: #2a3447; -fx-text-fill: #ffffff;");
        btnDelete.setStyle("-fx-background-color: #2a3447; -fx-text-fill: #ffffff;");

        btnAdd.setOnMouseEntered(e -> btnAdd.setStyle("-fx-background-color: #374151; -fx-text-fill: #d1d5db;"));
        btnAdd.setOnMouseExited(e -> btnAdd.setStyle("-fx-background-color: #2a3447; -fx-text-fill: #ffffff;"));

        btnDelete.setOnMouseEntered(e -> btnDelete.setStyle("-fx-background-color: #374151; -fx-text-fill: #d1d5db;"));
        btnDelete.setOnMouseExited(e -> btnDelete.setStyle("-fx-background-color: #2a3447; -fx-text-fill: #ffffff;"));

        HBox actions = new HBox(10, btnAdd, btnDelete);

        TextField search = new TextField();
        search.setPromptText("Search...");
        search.setStyle(inputStyle);

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
        table.setPlaceholder(new Label("No guests found"));

        table.setStyle(
                "-fx-background-color: #1c2433;" +
                "-fx-control-inner-background: #1c2433;" +
                "-fx-table-cell-border-color: transparent;" +
                "-fx-text-fill: #6b7280;"
        );

        table.setItems(FXCollections.observableArrayList(
                guestService.getAllGuests()
        ));

        btnAdd.setOnAction(e -> {
            Guest g = new Guest();
            g.setFirstName(firstName.getText());
            g.setLastName(lastName.getText());
            g.setEmail(email.getText());
            g.setPhone(phone.getText());
            g.setAddress(address.getText());
            g.setIdProof(idProof.getText());

            guestService.addGuest(g);
            table.setItems(FXCollections.observableArrayList(
                    guestService.getAllGuests()
            ));
        });

        btnDelete.setOnAction(e -> {
            Guest selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                guestService.deleteGuest(selected.getId());
                table.setItems(FXCollections.observableArrayList(
                        guestService.getAllGuests()
                ));
            }
        });

        search.textProperty().addListener((obs, oldVal, newVal) -> {
            table.setItems(FXCollections.observableArrayList(
                    guestService.searchGuests(newVal)
            ));
        });

        VBox root = new VBox(10, form, actions, search, table);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #121826;");

        stage.setScene(new Scene(root, 600, 500));
        stage.show();
    }
}