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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main extends Application {

    private static final String C_DARK_GREEN  = "#1B4332";
    private static final String C_MID_GREEN   = "#2D6A4F";
    private static final String C_LIGHT_GREEN = "#52B788";
    private static final String C_CREAM       = "#F8F4E3";
    private static final String C_CREAM_DARK  = "#EDE8D0";
    private static final String C_BROWN       = "#5C4A2A";
    private static final String C_WHITE       = "#FFFFFF";

    public static class MenuItem {
        private final IntegerProperty id;
        private final StringProperty  name;
        private final DoubleProperty  price;
        private final IntegerProperty stock;
        private final StringProperty  status;

        public MenuItem(int id, String name, double price, int stock, String status) {
            this.id     = new SimpleIntegerProperty(id);
            this.name   = new SimpleStringProperty(name);
            this.price  = new SimpleDoubleProperty(price);
            this.stock  = new SimpleIntegerProperty(stock);
            this.status = new SimpleStringProperty(status);
        }
        public int    getId()     { return id.get(); }
        public String getName()   { return name.get(); }
        public double getPrice()  { return price.get(); }
        public int    getStock()  { return stock.get(); }
        public String getStatus() { return status.get(); }
        public IntegerProperty idProperty()     { return id; }
        public StringProperty  nameProperty()   { return name; }
        public DoubleProperty  priceProperty()  { return price; }
        public IntegerProperty stockProperty()  { return stock; }
        public StringProperty  statusProperty() { return status; }
    }

    public static class OrderItem {
        private final StringProperty  name;
        private final DoubleProperty  price;
        private final IntegerProperty quantity;
        private final DoubleProperty  subtotal;

        public OrderItem(String name, double price, int quantity) {
            this.name     = new SimpleStringProperty(name);
            this.price    = new SimpleDoubleProperty(price);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.subtotal = new SimpleDoubleProperty(price * quantity);
        }
        public String getName()     { return name.get(); }
        public double getPrice()    { return price.get(); }
        public int    getQuantity() { return quantity.get(); }
        public double getSubtotal() { return subtotal.get(); }
        public StringProperty  nameProperty()     { return name; }
        public DoubleProperty  priceProperty()    { return price; }
        public IntegerProperty quantityProperty() { return quantity; }
        public DoubleProperty  subtotalProperty() { return subtotal; }

        public void addQuantity(int qty) {
            quantity.set(quantity.get() + qty);
            subtotal.set(price.get() * quantity.get());
        }
    }

    private TableView<MenuItem>  staffTable  = new TableView<>();
    private ObservableList<MenuItem> staffData = FXCollections.observableArrayList();
    private TextField staffNameField  = new TextField();
    private TextField staffPriceField = new TextField();
    private TextField staffStockField = new TextField();
    private TextField staffIdField    = new TextField();
    private Label     staffStatus     = new Label();

    private TableView<OrderItem>  orderTable = new TableView<>();
    private ObservableList<OrderItem> orderData = FXCollections.observableArrayList();
    private Label totalLabel     = new Label("Grand Total: ₱0.00");
    private Label qtyLabel       = new Label("Total Items: 0");
    private Label customerStatus = new Label();
    private VBox  customerPanelRef = null;

    @Override
    public void start(Stage stage) {
        DatabaseConnection.createTables();

        Button btnCustomer = new Button("🛒  Customer Order");
        Button btnStaff    = new Button("⚙  Staff Panel");

        String navActive =
            "-fx-background-color:" + C_MID_GREEN + ";" +
            "-fx-text-fill:" + C_WHITE + ";" +
            "-fx-font-weight:bold;-fx-font-size:13px;" +
            "-fx-padding:10 20 10 20;-fx-cursor:hand;" +
            "-fx-background-radius:0;";

        String navInactive =
            "-fx-background-color:" + C_DARK_GREEN + ";" +
            "-fx-text-fill:#95d5b2;" +
            "-fx-font-weight:bold;-fx-font-size:13px;" +
            "-fx-padding:10 20 10 20;-fx-cursor:hand;" +
            "-fx-background-radius:0;";

        btnCustomer.setStyle(navActive);
        btnStaff.setStyle(navInactive);

        Label appTitle = new Label("🍃 BIGTASK Restaurant");
        appTitle.setStyle(
            "-fx-font-size:15px;-fx-font-weight:bold;" +
            "-fx-text-fill:" + C_WHITE + ";-fx-padding:10 15 10 15;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox navBar = new HBox(appTitle, spacer, btnCustomer, btnStaff);
        navBar.setStyle("-fx-background-color:" + C_DARK_GREEN + ";");
        navBar.setAlignment(Pos.CENTER_LEFT);

        VBox customerPanel = buildCustomerPanel();
        customerPanelRef   = customerPanel;
        VBox staffPanel    = buildStaffPanel();

        StackPane content = new StackPane(customerPanel, staffPanel);
        staffPanel.setVisible(false);

        btnCustomer.setOnAction(e -> {
            customerPanel.setVisible(true);
            staffPanel.setVisible(false);
            btnCustomer.setStyle(navActive);
            btnStaff.setStyle(navInactive);
            refreshCustomerMenu();
        });

        btnStaff.setOnAction(e -> {
            staffPanel.setVisible(true);
            customerPanel.setVisible(false);
            btnStaff.setStyle(navActive);
            btnCustomer.setStyle(navInactive);
            loadStaffData();
        });

        VBox root = new VBox(navBar, content);
        VBox.setVgrow(content, Priority.ALWAYS);

        Scene scene = new Scene(root, 820, 700);
        stage.setTitle("BIGTASK Restaurant Management System");
        stage.setScene(scene);
        stage.show();

        refreshCustomerMenu();
    }

    // ══════════════════════════════════════════════════
    //  CUSTOMER ORDER PANEL
    // ══════════════════════════════════════════════════
    private VBox buildCustomerPanel() {

        Label menuTitle = new Label("Available Menu");
        menuTitle.setStyle(
            "-fx-font-size:14px;-fx-font-weight:bold;" +
            "-fx-text-fill:" + C_BROWN + ";-fx-padding:10 0 6 0;");

        VBox menuItemsBox = new VBox(6);
        menuItemsBox.setPadding(new Insets(0, 10, 10, 0));

        ScrollPane menuScroll = new ScrollPane(menuItemsBox);
        menuScroll.setFitToWidth(true);
        menuScroll.setStyle("-fx-background-color:transparent;");
        menuScroll.setPrefHeight(420);
        VBox.setVgrow(menuScroll, Priority.ALWAYS);

        VBox leftPanel = new VBox(menuTitle, menuScroll);
        leftPanel.setPadding(new Insets(10, 5, 10, 15));
        leftPanel.setPrefWidth(340);

        Label orderTitle = new Label("Current Order");
        orderTitle.setStyle(
            "-fx-font-size:14px;-fx-font-weight:bold;" +
            "-fx-text-fill:" + C_BROWN + ";-fx-padding:10 0 6 0;");

        TableColumn<OrderItem, String> colName = new TableColumn<>("Item");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(140);

        TableColumn<OrderItem, Integer> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colQty.setPrefWidth(50);

        TableColumn<OrderItem, Double> colSub = new TableColumn<>("Subtotal (₱)");
        colSub.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colSub.setPrefWidth(90);

        orderTable.getColumns().addAll(colName, colQty, colSub);
        orderTable.setItems(orderData);
        orderTable.setPrefHeight(300);
        orderTable.setStyle(
            "-fx-font-size:12px;" +
            "-fx-border-color:" + C_LIGHT_GREEN + ";" +
            "-fx-border-width:1.5px;");

        Button btnRemove = new Button("Remove Selected");
        btnRemove.setStyle(
            "-fx-background-color:#7B4B2A;-fx-text-fill:" + C_WHITE + ";" +
            "-fx-font-weight:bold;-fx-font-size:12px;" +
            "-fx-background-radius:5px;-fx-cursor:hand;");
        btnRemove.setOnAction(e -> {
            OrderItem sel = orderTable.getSelectionModel().getSelectedItem();
            if (sel != null) {
                orderData.remove(sel);
                updateOrderSummary();
            }
        });

        Button btnClear = new Button("Clear Order");
        btnClear.setStyle(
            "-fx-background-color:" + C_CREAM_DARK + ";" +
            "-fx-text-fill:" + C_BROWN + ";" +
            "-fx-font-weight:bold;-fx-font-size:12px;" +
            "-fx-border-color:" + C_LIGHT_GREEN + ";" +
            "-fx-border-width:1px;-fx-border-radius:5px;" +
            "-fx-background-radius:5px;-fx-cursor:hand;");
        btnClear.setOnAction(e -> {
            orderData.clear();
            updateOrderSummary();
        });

        HBox orderBtns = new HBox(8, btnRemove, btnClear);

        totalLabel.setStyle(
            "-fx-font-size:15px;-fx-font-weight:bold;" +
            "-fx-text-fill:" + C_DARK_GREEN + ";");
        qtyLabel.setStyle(
            "-fx-font-size:12px;-fx-text-fill:" + C_BROWN + ";");

        Button btnBillOut = new Button("💳  Bill Out & Print Receipt");
        btnBillOut.setStyle(
            "-fx-background-color:" + C_DARK_GREEN + ";" +
            "-fx-text-fill:" + C_WHITE + ";" +
            "-fx-font-weight:bold;-fx-font-size:13px;" +
            "-fx-background-radius:5px;-fx-cursor:hand;" +
            "-fx-padding:10 20 10 20;");
        btnBillOut.setMaxWidth(Double.MAX_VALUE);
        btnBillOut.setOnAction(e -> handleBillOut());

        customerStatus.setStyle(
            "-fx-font-size:12px;-fx-text-fill:" + C_MID_GREEN + ";");

        VBox summaryBox = new VBox(8,
            orderTitle, orderTable, orderBtns,
            new Separator(), qtyLabel, totalLabel,
            btnBillOut, customerStatus);
        summaryBox.setPadding(new Insets(10, 15, 10, 5));
        VBox.setVgrow(orderTable, Priority.ALWAYS);

        HBox body = new HBox(leftPanel, new Separator(), summaryBox);
        HBox.setHgrow(summaryBox, Priority.ALWAYS);
        body.setStyle("-fx-background-color:" + C_CREAM + ";");
        VBox.setVgrow(body, Priority.ALWAYS);

        VBox panel = new VBox(body);
        VBox.setVgrow(panel, Priority.ALWAYS);
        panel.setStyle("-fx-background-color:" + C_CREAM + ";");
        panel.setUserData(menuItemsBox);
        return panel;
    }

    private void refreshCustomerMenu() { rebuildMenuCards(); }

    private void rebuildMenuCards() {
        if (customerPanelRef == null) return;
        VBox menuItemsBox = (VBox) customerPanelRef.getUserData();
        if (menuItemsBox == null) return;
        menuItemsBox.getChildren().clear();

        for (Object[] row : ViewMenu.getActiveItems()) {
            String name  = (String) row[1];
            double price = (double) row[2];

            Label nameLabel = new Label(name);
            nameLabel.setStyle(
                "-fx-font-size:13px;-fx-font-weight:bold;" +
                "-fx-text-fill:" + C_BROWN + ";");

            Label priceLabel = new Label("₱" + String.format("%.2f", price));
            priceLabel.setStyle(
                "-fx-font-size:12px;-fx-text-fill:" + C_MID_GREEN + ";");

            Button btnAdd = new Button("+ Add");
            btnAdd.setStyle(
                "-fx-background-color:" + C_MID_GREEN + ";" +
                "-fx-text-fill:" + C_WHITE + ";" +
                "-fx-font-size:11px;-fx-font-weight:bold;" +
                "-fx-background-radius:4px;-fx-cursor:hand;");
            btnAdd.setOnAction(e -> addToOrder(name, price));

            Region cardSpacer = new Region();
            HBox.setHgrow(cardSpacer, Priority.ALWAYS);

            VBox cardText = new VBox(2, nameLabel, priceLabel);
            HBox card = new HBox(10, cardText, cardSpacer, btnAdd);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(10, 12, 10, 12));
            card.setStyle(
                "-fx-background-color:" + C_WHITE + ";" +
                "-fx-border-color:" + C_LIGHT_GREEN + ";" +
                "-fx-border-width:1px;-fx-border-radius:6px;" +
                "-fx-background-radius:6px;");

            menuItemsBox.getChildren().add(card);
        }
    }

    private void addToOrder(String name, double price) {
        for (OrderItem item : orderData) {
            if (item.getName().equals(name)) {
                item.addQuantity(1);
                orderTable.refresh();
                updateOrderSummary();
                return;
            }
        }
        orderData.add(new OrderItem(name, price, 1));
        updateOrderSummary();
    }

    private void updateOrderSummary() {
        double total = 0;
        int qty = 0;
        for (OrderItem item : orderData) {
            total += item.getSubtotal();
            qty   += item.getQuantity();
        }
        totalLabel.setText("Grand Total: ₱" + String.format("%.2f", total));
        qtyLabel.setText("Total Items: " + qty);
    }

    private void handleBillOut() {
        if (orderData.isEmpty()) {
            customerStatus.setText("No items in order.");
            customerStatus.setStyle("-fx-font-size:12px;-fx-text-fill:#7B4B2A;");
            return;
        }

        // Generate unique order reference and timestamp
        String now = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String orderRef = "ORD-" + LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        // Save orders to DB with order_ref and time
        String insert = "INSERT INTO orders "
                      + "(order_ref, item_name, quantity, total, order_time) "
                      + "VALUES (?, ?, ?, ?, ?)";
        try (java.sql.Connection conn = DatabaseConnection.connect();
             java.sql.PreparedStatement ps = conn.prepareStatement(insert)) {
            for (OrderItem item : orderData) {
                ps.setString(1, orderRef);
                ps.setString(2, item.getName());
                ps.setInt(3,    item.getQuantity());
                ps.setDouble(4, item.getSubtotal());
                ps.setString(5, now);
                ps.executeUpdate();

                // Deduct stock
                UpdateMenu.deductStock(item.getName(), item.getQuantity());
            }
        } catch (Exception e) {
            System.out.println("Save order error: " + e.getMessage());
        }

        double grandTotal = 0;
        int totalQty = 0;
        for (OrderItem item : orderData) {
            grandTotal += item.getSubtotal();
            totalQty   += item.getQuantity();
        }

        // Generate receipt
        ReportGenerator.generateBillingReport(
            "Walk-in Customer", grandTotal, totalQty, orderRef, now);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bill Out");
        alert.setHeaderText("Order Complete!");
        alert.setContentText(
            "Order Ref: " + orderRef + "\n" +
            "Total Items: " + totalQty + "\n" +
            "Grand Total: ₱" + String.format("%.2f", grandTotal) + "\n\n" +
            "Receipt saved to reports/output/");
        alert.showAndWait();

        orderData.clear();
        updateOrderSummary();
        refreshCustomerMenu(); // refresh to show updated stock

        customerStatus.setText("Receipt printed — " + orderRef);
        customerStatus.setStyle(
            "-fx-font-size:12px;-fx-text-fill:" + C_MID_GREEN + ";");
    }

    // ══════════════════════════════════════════════════
    //  STAFF PANEL
    // ══════════════════════════════════════════════════
    private VBox buildStaffPanel() {

        TableColumn<MenuItem, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(45);

        TableColumn<MenuItem, String> colName = new TableColumn<>("Item Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(180);

        TableColumn<MenuItem, Double> colPrice = new TableColumn<>("Price (₱)");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrice.setPrefWidth(90);

        TableColumn<MenuItem, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStock.setPrefWidth(70);

        TableColumn<MenuItem, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(100);

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    switch (item) {
                        case "available" ->
                            setStyle("-fx-text-fill:#2D6A4F;-fx-font-weight:bold;");
                        case "out_of_stock" ->
                            setStyle("-fx-text-fill:#7B4B2A;-fx-font-weight:bold;");
                        default -> setStyle("");
                    }
                }
            }
        });

        staffTable.getColumns().addAll(colId, colName, colPrice, colStock, colStatus);
        staffTable.setItems(staffData);
        staffTable.setPrefHeight(240);
        staffTable.setStyle(
            "-fx-background-color:" + C_WHITE + ";" +
            "-fx-border-color:" + C_LIGHT_GREEN + ";" +
            "-fx-border-width:1.5px;-fx-font-size:13px;");

        staffTable.getSelectionModel().selectedItemProperty()
            .addListener((obs, old, sel) -> {
                if (sel != null) {
                    staffIdField.setText(String.valueOf(sel.getId()));
                    staffNameField.setText(sel.getName());
                    staffPriceField.setText(String.valueOf(sel.getPrice()));
                    staffStockField.setText(String.valueOf(sel.getStock()));
                }
            });

        String fieldStyle =
            "-fx-background-color:" + C_WHITE + ";" +
            "-fx-border-color:" + C_LIGHT_GREEN + ";" +
            "-fx-border-width:1.5px;-fx-border-radius:5px;" +
            "-fx-background-radius:5px;-fx-padding:6px;" +
            "-fx-font-size:13px;-fx-text-fill:" + C_BROWN + ";";

        String labelStyle =
            "-fx-font-size:13px;-fx-text-fill:" + C_BROWN + ";-fx-font-weight:bold;";

        staffIdField.setPromptText("Auto-filled");
        staffIdField.setEditable(false);
        staffIdField.setStyle(fieldStyle + "-fx-background-color:" + C_CREAM_DARK + ";");
        staffNameField.setPromptText("Item name");
        staffNameField.setStyle(fieldStyle);
        staffPriceField.setPromptText("Price");
        staffPriceField.setStyle(fieldStyle);
        staffStockField.setPromptText("Stock quantity");
        staffStockField.setStyle(fieldStyle);

        staffIdField.setPrefWidth(240);
        staffNameField.setPrefWidth(240);
        staffPriceField.setPrefWidth(240);
        staffStockField.setPrefWidth(240);

        Label lblId    = new Label("ID:");    lblId.setStyle(labelStyle);
        Label lblName  = new Label("Name:");  lblName.setStyle(labelStyle);
        Label lblPrice = new Label("Price:"); lblPrice.setStyle(labelStyle);
        Label lblStock = new Label("Stock:"); lblStock.setStyle(labelStyle);

        GridPane form = new GridPane();
        form.setHgap(12); form.setVgap(10);
        form.setPadding(new Insets(15));
        form.setStyle("-fx-background-color:" + C_CREAM_DARK + ";");
        form.add(lblId,    0, 0); form.add(staffIdField,    1, 0);
        form.add(lblName,  0, 1); form.add(staffNameField,  1, 1);
        form.add(lblPrice, 0, 2); form.add(staffPriceField, 1, 2);
        form.add(lblStock, 0, 3); form.add(staffStockField, 1, 3);

        Button btnAdd    = new Button("Add");
        Button btnUpdate = new Button("Update");
        Button btnDelete = new Button("Delete");
        Button btnClear  = new Button("Clear");

        String btnPrimary =
            "-fx-background-color:" + C_MID_GREEN + ";-fx-text-fill:" + C_WHITE + ";" +
            "-fx-font-weight:bold;-fx-font-size:13px;" +
            "-fx-background-radius:5px;-fx-cursor:hand;";
        String btnDanger =
            "-fx-background-color:#7B4B2A;-fx-text-fill:" + C_WHITE + ";" +
            "-fx-font-weight:bold;-fx-font-size:13px;" +
            "-fx-background-radius:5px;-fx-cursor:hand;";
        String btnOutline =
            "-fx-background-color:" + C_CREAM + ";-fx-text-fill:" + C_BROWN + ";" +
            "-fx-font-weight:bold;-fx-font-size:13px;" +
            "-fx-border-color:" + C_LIGHT_GREEN + ";-fx-border-width:1.5px;" +
            "-fx-border-radius:5px;-fx-background-radius:5px;-fx-cursor:hand;";

        btnAdd.setStyle(btnPrimary);    btnAdd.setPrefWidth(85);
        btnUpdate.setStyle(btnPrimary); btnUpdate.setPrefWidth(85);
        btnDelete.setStyle(btnDanger);  btnDelete.setPrefWidth(85);
        btnClear.setStyle(btnOutline);  btnClear.setPrefWidth(85);

        btnAdd.setOnAction(e -> handleStaffAdd());
        btnUpdate.setOnAction(e -> handleStaffUpdate());
        btnDelete.setOnAction(e -> handleStaffDelete());
        btnClear.setOnAction(e -> clearStaffFields());

        HBox crudBtns = new HBox(8, btnAdd, btnUpdate, btnDelete, btnClear);
        crudBtns.setPadding(new Insets(10, 15, 10, 15));
        crudBtns.setStyle("-fx-background-color:" + C_CREAM_DARK + ";");

        Button btnAvailable = new Button("Set Available");
        Button btnOOS       = new Button("Set Out of Stock");

        String btnStatus =
            "-fx-font-weight:bold;-fx-font-size:12px;" +
            "-fx-background-radius:5px;-fx-cursor:hand;";
        btnAvailable.setStyle(btnStatus +
            "-fx-background-color:#2D6A4F;-fx-text-fill:white;");
        btnOOS.setStyle(btnStatus +
            "-fx-background-color:#7B4B2A;-fx-text-fill:white;");
        btnAvailable.setPrefWidth(120);
        btnOOS.setPrefWidth(140);

        btnAvailable.setOnAction(e -> setItemStatus("available"));
        btnOOS.setOnAction(e -> setItemStatus("out_of_stock"));

        Label statusSectionLabel = new Label("SET ITEM STATUS");
        statusSectionLabel.setStyle(
            "-fx-font-size:11px;-fx-font-weight:bold;" +
            "-fx-text-fill:" + C_LIGHT_GREEN + ";");

        HBox statusBtns = new HBox(8, btnAvailable, btnOOS);
        VBox statusSection = new VBox(4, statusSectionLabel, statusBtns);
        statusSection.setPadding(new Insets(10, 15, 5, 15));

        Label reportSectionLabel = new Label("GENERATE REPORTS");
        reportSectionLabel.setStyle(
            "-fx-font-size:11px;-fx-font-weight:bold;" +
            "-fx-text-fill:" + C_LIGHT_GREEN + ";");

        Button btnMenuReport   = new Button("Menu Report");
        Button btnOrdersReport = new Button("Orders Report");

        String btnReport =
            "-fx-background-color:" + C_DARK_GREEN + ";-fx-text-fill:" + C_WHITE + ";" +
            "-fx-font-weight:bold;-fx-font-size:12px;" +
            "-fx-background-radius:5px;-fx-cursor:hand;";
        btnMenuReport.setStyle(btnReport);   btnMenuReport.setPrefWidth(115);
        btnOrdersReport.setStyle(btnReport); btnOrdersReport.setPrefWidth(115);

        btnMenuReport.setOnAction(e -> {
            ReportGenerator.generateMenuReport();
            setStaffStatus("Menu report saved.", true);
        });
        btnOrdersReport.setOnAction(e -> {
            ReportGenerator.generateOrdersReport();
            setStaffStatus("Orders report saved.", true);
        });

        HBox reportBtns = new HBox(8, btnMenuReport, btnOrdersReport);
        VBox reportSection = new VBox(4, reportSectionLabel, reportBtns);
        reportSection.setPadding(new Insets(5, 15, 10, 15));

        staffStatus.setStyle(
            "-fx-font-size:12px;-fx-text-fill:" + C_MID_GREEN + ";" +
            "-fx-padding:0 15 8 15;");

        VBox panel = new VBox(
            staffTable, form, crudBtns,
            new Separator(), statusSection,
            new Separator(), reportSection,
            staffStatus);
        panel.setStyle("-fx-background-color:" + C_CREAM + ";");
        VBox.setVgrow(staffTable, Priority.ALWAYS);

        loadStaffData();
        return panel;
    }

    private void setItemStatus(String status) {
        MenuItem sel = staffTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            setStaffStatus("Select an item first.", false); return;
        }
        UpdateMenu.updateStatus(sel.getId(), status);
        setStaffStatus("Status updated to: " + status, true);
        loadStaffData();
    }

    private void handleStaffAdd() {
        String name      = staffNameField.getText().trim();
        String priceText = staffPriceField.getText().trim();
        String stockText = staffStockField.getText().trim();
        if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            setStaffStatus("Please fill in all fields.", false); return;
        }
        try {
            double price = Double.parseDouble(priceText);
            int stock    = Integer.parseInt(stockText);
            AddMenu.addItem(name, price, stock);
            setStaffStatus("Item added successfully.", true);
            clearStaffFields(); loadStaffData();
        } catch (NumberFormatException e) {
            setStaffStatus("Price and stock must be valid numbers.", false);
        }
    }

    private void handleStaffUpdate() {
        String idText    = staffIdField.getText().trim();
        String name      = staffNameField.getText().trim();
        String priceText = staffPriceField.getText().trim();
        String stockText = staffStockField.getText().trim();
        if (idText.isEmpty() || name.isEmpty() ||
            priceText.isEmpty() || stockText.isEmpty()) {
            setStaffStatus("Select a row first.", false); return;
        }
        try {
            int    id    = Integer.parseInt(idText);
            double price = Double.parseDouble(priceText);
            int    stock = Integer.parseInt(stockText);
            UpdateMenu.updateItem(id, name, price, stock);
            setStaffStatus("Item updated successfully.", true);
            clearStaffFields(); loadStaffData();
        } catch (NumberFormatException e) {
            setStaffStatus("Invalid input.", false);
        }
    }

    private void handleStaffDelete() {
        String idText = staffIdField.getText().trim();
        if (idText.isEmpty()) {
            setStaffStatus("Select a row to delete.", false); return;
        }
        int id = Integer.parseInt(idText);
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete item #" + id + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                DeleteMenu.deleteItem(id);
                setStaffStatus("Item deleted.", true);
                clearStaffFields(); loadStaffData();
            }
        });
    }

    private void loadStaffData() {
        staffData.clear();
        for (Object[] row : ViewMenu.getItems()) {
            staffData.add(new MenuItem(
                (int)    row[0],
                (String) row[1],
                (double) row[2],
                (int)    row[3],
                (String) row[4]));
        }
    }

    private void clearStaffFields() {
        staffIdField.clear(); staffNameField.clear();
        staffPriceField.clear(); staffStockField.clear();
        staffTable.getSelectionModel().clearSelection();
    }

    private void setStaffStatus(String msg, boolean ok) {
        staffStatus.setText(msg);
        staffStatus.setStyle(
            "-fx-text-fill:" + (ok ? C_MID_GREEN : "#7B4B2A") +
            ";-fx-font-size:12px;-fx-padding:0 15 8 15;");
    }

    public static void main(String[] args) { launch(args); }
}