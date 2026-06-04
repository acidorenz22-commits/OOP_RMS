package com.OOPRms;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {

    public static class MenuItem {
        private final IntegerProperty id;
        private final StringProperty name;
        private final DoubleProperty price;

        public MenuItem(int id, String name, double price) {
            this.id    = new SimpleIntegerProperty(id);
            this.name  = new SimpleStringProperty(name);
            this.price = new SimpleDoubleProperty(price);
        }
        public int    getId()    { return id.get(); }
        public String getName()  { return name.get(); }
        public double getPrice() { return price.get(); }
        public IntegerProperty idProperty()    { return id; }
        public StringProperty  nameProperty()  { return name; }
        public DoubleProperty  priceProperty() { return price; }
    }

    private TableView<MenuItem> table = new TableView<>();
    private ObservableList<MenuItem> data = FXCollections.observableArrayList();

    private TextField nameField   = new TextField();
    private TextField priceField  = new TextField();
    private TextField idField     = new TextField();
    private Label     statusLabel = new Label();

    @Override
    public void start(Stage stage) {

        insertTestOrders();

        TableColumn<MenuItem, Integer> colId = new TableColumn<>("ORDER ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(60);

        TableColumn<MenuItem, String> colName = new TableColumn<>("Item Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(220);

        TableColumn<MenuItem, Double> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrice.setPrefWidth(100);

        table.getColumns().addAll(colId, colName, colPrice);
        table.setItems(data);
        table.setPrefHeight(300);

        table.getSelectionModel().selectedItemProperty()
            .addListener((obs, old, sel) -> {
                if (sel != null) {
                    idField.setText(String.valueOf(sel.getId()));
                    nameField.setText(sel.getName());
                    priceField.setText(String.valueOf(sel.getPrice()));
                }
            });

        idField.setPromptText("ID (for update/delete)");
        idField.setEditable(false);
        idField.setStyle("-fx-background-color: #f0f0f0;");
        nameField.setPromptText("Item name");
        priceField.setPromptText("Price");

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);
        form.setPadding(new Insets(10));
        form.add(new Label("ID:"),    0, 0); form.add(idField,    1, 0);
        form.add(new Label("Name:"),  0, 1); form.add(nameField,  1, 1);
        form.add(new Label("Price:"), 0, 2); form.add(priceField, 1, 2);

        Button btnAdd    = new Button("Add");
        Button btnUpdate = new Button("Update");
        Button btnDelete = new Button("Delete");
        Button btnClear  = new Button("Clear");

        btnAdd.setStyle(
            "-fx-background-color:#4CAF50;-fx-text-fill:white;-fx-font-weight:bold;");
        btnUpdate.setStyle(
            "-fx-background-color:#2196F3;-fx-text-fill:white;-fx-font-weight:bold;");
        btnDelete.setStyle(
            "-fx-background-color:#f44336;-fx-text-fill:white;-fx-font-weight:bold;");

        btnAdd.setPrefWidth(80);
        btnUpdate.setPrefWidth(80);
        btnDelete.setPrefWidth(80);
        btnClear.setPrefWidth(80);

        btnAdd.setOnAction(e -> handleAdd());
        btnUpdate.setOnAction(e -> handleUpdate());
        btnDelete.setOnAction(e -> handleDelete());
        btnClear.setOnAction(e -> clearFields());

        HBox buttons = new HBox(10, btnAdd, btnUpdate, btnDelete, btnClear);
        buttons.setPadding(new Insets(0, 10, 10, 10));

        statusLabel.setStyle("-fx-text-fill: green; -fx-padding: 0 10 10 10;");

        Button btnMenuReport    = new Button("Menu Report");
        Button btnOrdersReport  = new Button("Orders Report");
        Button btnBillingReport = new Button("Billing Report");

        btnMenuReport.setStyle(
            "-fx-background-color:#9C27B0;-fx-text-fill:white;-fx-font-weight:bold;");
        btnOrdersReport.setStyle(
            "-fx-background-color:#FF9800;-fx-text-fill:white;-fx-font-weight:bold;");
        btnBillingReport.setStyle(
            "-fx-background-color:#607D8B;-fx-text-fill:white;-fx-font-weight:bold;");

        btnMenuReport.setOnAction(e -> {
            ReportGenerator.generateMenuReport();
            setStatus("Menu report saved to reports/output/", true);
        });
        btnOrdersReport.setOnAction(e -> {
            ReportGenerator.generateOrdersReport();
            setStatus("Orders report saved to reports/output/", true);
        });
        btnBillingReport.setOnAction(e -> {
            ReportGenerator.generateBillingReport("Walk-in Customer", calculateTotal());
            setStatus("Billing report saved to reports/output/", true);
        });

        HBox reportButtons = new HBox(10,
            btnMenuReport, btnOrdersReport, btnBillingReport);
        reportButtons.setPadding(new Insets(0, 10, 10, 10));

        Label title = new Label("BIGTASK Restaurant Management System");
        title.setStyle(
            "-fx-font-size:18px;-fx-font-weight:bold;-fx-padding:15 10 5 10;");

        VBox root = new VBox(10);
        root.getChildren().addAll(
            title, table, form, buttons, statusLabel, reportButtons);

        loadData();

        Scene scene = new Scene(root, 480, 600);
        stage.setTitle("BIGTASK Restaurant Management System");
        stage.setScene(scene);
        stage.show();
    }

    private void insertTestOrders() {
        String clear = "DELETE FROM orders";
        String insert = "INSERT INTO orders (item_name, quantity, total) VALUES (?, ?, ?)";

        Object[][] testData = {
            {"Sinigang", 2, 130.0},
            {"Adobo",    1,  90.0},
            {"Bulalo",   3, 360.0},
            {"Embutido", 2, 160.0}
        };

        try (java.sql.Connection conn = DatabaseConnection.connect();
             java.sql.Statement st = conn.createStatement();
             java.sql.PreparedStatement ps = conn.prepareStatement(insert)) {

            st.execute(clear);

            for (Object[] row : testData) {
                ps.setString(1, (String) row[0]);
                ps.setInt(2,    (Integer) row[1]);
                ps.setDouble(3, (Double) row[2]);
                ps.executeUpdate();
            }
            System.out.println("Test orders inserted.");

        } catch (Exception e) {
            System.out.println("insertTestOrders error: " + e.getMessage());
        }
    }

    private void handleAdd() {
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();
        if (name.isEmpty() || priceText.isEmpty()) {
            setStatus("Please enter name and price.", false); return;
        }
        try {
            double price = Double.parseDouble(priceText);
            AddMenu.addItem(name, price);
            setStatus("Item added!", true);
            clearFields(); loadData();
        } catch (NumberFormatException e) {
            setStatus("Price must be a number.", false);
        }
    }

    private void handleUpdate() {
        String idText    = idField.getText().trim();
        String name      = nameField.getText().trim();
        String priceText = priceField.getText().trim();
        if (idText.isEmpty() || name.isEmpty() || priceText.isEmpty()) {
            setStatus("Select a row first, then edit name/price.", false); return;
        }
        try {
            int id = Integer.parseInt(idText);
            double price = Double.parseDouble(priceText);
            UpdateMenu.updateItem(id, name, price);
            setStatus("Item updated!", true);
            clearFields(); loadData();
        } catch (NumberFormatException e) {
            setStatus("Invalid ID or price.", false);
        }
    }

    private void handleDelete() {
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            setStatus("Select a row to delete.", false); return;
        }
        int id = Integer.parseInt(idText);
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete item #" + id + "?",
            ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                DeleteMenu.deleteItem(id);
                setStatus("Item deleted!", true);
                clearFields(); loadData();
            }
        });
    }

    private void loadData() {
        data.clear();
        for (Object[] row : ViewMenu.getItems()) {
            data.add(new MenuItem(
                (int) row[0],
                (String) row[1],
                (double) row[2]));
        }
    }

    private void clearFields() {
        idField.clear(); nameField.clear(); priceField.clear();
        table.getSelectionModel().clearSelection();
    }

    private void setStatus(String msg, boolean ok) {
        statusLabel.setText(msg);
        statusLabel.setStyle(
            "-fx-text-fill:" + (ok ? "green" : "red") +
            ";-fx-padding:0 10 10 10;");
    }

    private double calculateTotal() {
        double total = 0;
        String sql = "SELECT SUM(total) FROM orders";
        try (java.sql.Connection conn = DatabaseConnection.connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) total = rs.getDouble(1);
        } catch (Exception e) {
            System.out.println("Error calculating total: " + e.getMessage());
        }
        return total;
    }

    public static void main(String[] args) { launch(args); }
}