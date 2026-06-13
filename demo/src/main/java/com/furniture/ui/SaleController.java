package com.furniture.ui;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import com.furniture.dao.SaleDAO;
import com.furniture.model.Sale;
import com.furniture.model.SaleDetailes;
import com.furniture.dao.SaleDAO.SaleStats;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.poi.ss.usermodel.*;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import javafx.scene.control.TableCell;

public class SaleController {

    private SaleDAO saleDAO = new SaleDAO();

    @FXML
    private Label totalSalesLabel;
    @FXML
    private Label totalRevenueLabel;
    @FXML
    private Label productsSoldLabel;
    @FXML
    private Label pendingDeliveriesLabel;
    @FXML
    private Label outstandingBalanceLabel;

    /* Filter */
    @FXML
    private ComboBox<String> customerCombo;
    @FXML
    private ComboBox<String> employeeCombo;
    @FXML
    private DatePicker fromSaleDatePicker;
    @FXML
    private DatePicker toSaleDatePicker;
    @FXML
    private TextField minTotalField;
    @FXML
    private TextField maxTotalField;
    @FXML
    private ComboBox<String> paymentStatusCombo;
    @FXML
    private ComboBox<String> deliveryStatusCombo;
    @FXML
    private ComboBox<String> balanceStatusCombo;
    @FXML
    private Label salesFoundLabel;

    /* Main Table */
    @FXML
    private TableView<Sale> saleTable;

    @FXML
    private TableColumn<Sale, Integer> colNo;
    @FXML
    private TableColumn<Sale, Integer> colSaleID;
    @FXML
    private TableColumn<Sale, String> colCustomerName;
    @FXML
    private TableColumn<Sale, String> colEmployeeName;
    @FXML
    private TableColumn<Sale, LocalDate> colSaleDate;
    @FXML
    private TableColumn<Sale, Integer> colItemsCount;
    @FXML
    private TableColumn<Sale, Double> colTotalAmount;
    @FXML
    private TableColumn<Sale, Double> colPaidAmount;
    @FXML
    private TableColumn<Sale, Double> colBalance;
    @FXML
    private TableColumn<Sale, String> colDeliveryStatus;
    @FXML
    private TableColumn<Sale, Void> colAction;

    /* Panels */
    @FXML
    private VBox filterPanel;
    @FXML
    private VBox addSalePanel;
    @FXML
    private VBox saleDetailsPanel;

    /* Buttons */
    @FXML
    private Button filterToggleBtn;
    @FXML
    private Button addSaleBtn;
    @FXML
    private Button saveSaleBtn;
    @FXML
    private Button cancelAddBtn;
    @FXML
    private Button closeDetailsBtn;
    @FXML
    private Button applyFiltersBtn;
    @FXML
    private Button resetFiltersBtn;
    @FXML
    private Button undoBtn;
    @FXML
    private Button redoBtn;
    @FXML
    private Button resetBtn;
    @FXML
    private Button exportBtn;
    @FXML
    private Button saveAllBtn;

    /* Search */
    @FXML
    private TextField searchField;

    /* Add Sale Form */
    @FXML
    private ComboBox<String> addCustomerCombo;
    @FXML
    private ComboBox<String> addEmployeeCombo;
    @FXML
    private DatePicker addSaleDatePicker;
    @FXML
    private ComboBox<String> addProductCombo;
    @FXML
    private TextField addQuantityField;
    @FXML
    private Button addItemToSaleBtn;

    @FXML
    private Label selectedProductPriceLabel;
    @FXML
    private Label selectedProductStockLabel;

    @FXML
    private TableView<SaleDetailes> addSaleDetailesTable;
    @FXML
    private TableColumn<SaleDetailes, String> colAddProductName;
    @FXML
    private TableColumn<SaleDetailes, Integer> colAddQuantity;
    @FXML
    private TableColumn<SaleDetailes, Double> colAddUnitPrice;
    @FXML
    private TableColumn<SaleDetailes, Double> colAddSubtotal;
    @FXML
    private TableColumn<SaleDetailes, Void> colAddRemove;

    @FXML
    private TextField addPaidAmountField;
    @FXML
    private ComboBox<String> addPaymentMethodCombo;
    @FXML
    private ComboBox<String> addDeliveryStatusCombo;
    @FXML
    private DatePicker addDeliveryDatePicker;

    @FXML
    private Label addTotalAmountLabel;
    @FXML
    private Label addBalanceLabel;
    @FXML
    private Label formTitleLabel;

    /* Warnings */
    @FXML
    private Label customerWarningLabel;
    @FXML
    private Label employeeWarningLabel;
    @FXML
    private Label saleDateWarningLabel;
    @FXML
    private Label productWarningLabel;
    @FXML
    private Label quantityWarningLabel;
    @FXML
    private Label paidAmountWarningLabel;
    @FXML
    private Label paymentMethodWarningLabel;
    @FXML
    private Label deliverystatusWarningLabel;
    @FXML
    private Label deliveryDateWarningLabel;

    /* Details Panel */
    @FXML
    private Label detailsSaleIdLabel;
    @FXML
    private Label detailsPaymentBadgeLabel;
    @FXML
    private Label detailsCustomerNameLabel;
    @FXML
    private Label detailsSaleDateLabel;
    @FXML
    private Label detailsTotalAmountLabel;
    @FXML
    private Label detailsPaidAmountLabel;
    @FXML
    private Label detailsBalanceLabel;
    @FXML
    private Label detailsItemsCountLabel;
    @FXML
    private Label detailsPaymentStatusLabel;
    @FXML
    private Label detailsDeliveryStatusLabel;
    @FXML
    private Label detailsSaleNumberLabel;
    @FXML
    private Label detailsCustomerLabel;
    @FXML
    private Label detailsEmployeeLabel;
    @FXML
    private Label detailsFullSaleDateLabel;
    @FXML
    private Label detailsDeliveryDateLabel;
    @FXML
    private Label detailsPaymentMethodLabel;

    @FXML
    private TableView<SaleDetailes> SaleDetailesTable;
    @FXML
    private TableColumn<SaleDetailes, String> colDetailProductName;
    @FXML
    private TableColumn<SaleDetailes, Integer> colDetailQuantity;
    @FXML
    private TableColumn<SaleDetailes, Double> colDetailUnitPrice;
    @FXML
    private TableColumn<SaleDetailes, Double> colDetailSubtotal;

    /* Lists */
    private List<Sale> originalSales = new ArrayList<>();
    private Stack<List<Sale>> undoStack = new Stack<>();
    private Stack<List<Sale>> redoStack = new Stack<>();

    private List<Sale> pendingAdds = new ArrayList<>();
    private List<Sale> pendingUpdates = new ArrayList<>();
    private List<Sale> pendingDeletes = new ArrayList<>();

    private Sale saleBeingUpdated = null;
    private boolean clearingForm = false;
    private Sale selectedSaleForDetails = null;

    @FXML
    private void initialize() {

        setupSaleTable();
        setupDetailsTables();
        loadSalesTable();
        loadSaleStats();
        loadSaleFilters();
        loadCustomers();
        loadEmployees();
        loadProducts();

        setupSearch();
        setupAddSaleButtons();
        setupSaveSale();
        setupLiveValidation();
        setupUndoRedoReset();
        setupAddSaleDetailesTable();
        setupPaymentAndDeliveryCombos();

        addItemToSaleBtn.setOnAction(e -> addItemToSale());

        applyFiltersBtn.setOnAction(e -> applyFilters());
        resetFiltersBtn.setOnAction(e -> resetFilters());

        filterToggleBtn.setOnAction(e -> {
            if (filterPanel.isVisible()) {
                filterPanel.setVisible(false);
                filterPanel.setManaged(false);
            } else {
                showOnlyPanel(filterPanel);
            }
        });

        addSaleBtn.setOnAction(e -> {
            if (addSalePanel.isVisible() && saleBeingUpdated == null) {
                addSalePanel.setVisible(false);
                addSalePanel.setManaged(false);
            } else {
                openAddForm();
            }
        });

        closeDetailsBtn.setOnAction(e -> {
            saleDetailsPanel.setVisible(false);
            saleDetailsPanel.setManaged(false);
        });

        exportBtn.setOnAction(e -> exportSalesToExcel());
        saveAllBtn.setOnAction(e -> saveAllChanges());

        addProductCombo.setOnAction(e -> updateSelectedProductInfo());
    }

    private void setupSaleTable() {

        colNo.setCellValueFactory(new PropertyValueFactory<>("no"));
        colSaleID.setCellValueFactory(new PropertyValueFactory<>("saleID"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colEmployeeName.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colItemsCount.setCellValueFactory(new PropertyValueFactory<>("itemsCount"));
        colSaleDate.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        colSaleDate.setCellFactory(column -> new TableCell<>() {

            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText("-");
                } else {
                    setText(item.format(formatter));
                }
            }
        });

        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        colBalance.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText("$0");
                } else {
                    setText(String.format("$%,.0f", item));
                }
            }
        });
        colTotalAmount.setCellValueFactory(
                new PropertyValueFactory<>("total_Amount"));

        colTotalAmount.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText("$0");
                } else {
                    setText(String.format("$%,.0f", item));
                }
            }
        });
        colPaidAmount.setCellValueFactory(
                new PropertyValueFactory<>("paidAmount"));

        colPaidAmount.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText("$0");
                } else {
                    setText(String.format("$%,.0f", item));
                }
            }
        });

        colBalance.setCellValueFactory(
                new PropertyValueFactory<>("balance"));

        colBalance.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText("$0");
                } else {
                    setText(String.format("$%,.0f", item));
                }
            }
        });

        colDeliveryStatus.setCellValueFactory(
                new PropertyValueFactory<>("deliveryStatus"));

        setupActionColumn();
    }

    private void setupDetailsTables() {

        colDetailProductName.setCellValueFactory(
                new PropertyValueFactory<>("productName"));

        colDetailQuantity.setCellValueFactory(
                new PropertyValueFactory<>("quantity"));

        colDetailUnitPrice.setCellValueFactory(
                new PropertyValueFactory<>("price"));

        colDetailSubtotal.setCellValueFactory(
                new PropertyValueFactory<>("subtotal"));
    }

    private void setupActionColumn() {

        colAction.setCellFactory(column -> new javafx.scene.control.TableCell<>() {

            private final Button viewBtn = new Button("👁");
            private final Button editBtn = new Button("✎");
            private final Button deleteBtn = new Button("🗑");

            private final HBox box = new HBox(8, viewBtn, editBtn, deleteBtn);

            {
                box.setAlignment(javafx.geometry.Pos.CENTER);

                viewBtn.getStyleClass().add("table-show-btn");
                editBtn.getStyleClass().add("table-edit-btn");
                deleteBtn.getStyleClass().add("table-delete-btn");

                viewBtn.setOnAction(e -> {
                    Sale sale = getTableView().getItems().get(getIndex());

                    openSaleDetails(sale);
                });

                deleteBtn.setOnAction(e -> {

                    Sale sale = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Customer");
                    alert.setHeaderText(null);
                    alert.setContentText(
                            "Are you sure you want to delete "
                                    + sale.getCustomerName() + "?");

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isEmpty() || result.get() != ButtonType.OK) {
                        return;
                    }

                    saveStateForUndo();

                    pendingDeletes.add(sale);
                    saleTable.getItems().remove(sale);

                    refreshRowNumbers();

                    redoStack.clear();
                });

                editBtn.setOnAction(e -> {
                    Sale sale = getTableView().getItems().get(getIndex());
                    openUpdateForm(sale);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });
    }

    private void setupAddSaleDetailesTable() {

        colAddProductName.setCellValueFactory(
                new PropertyValueFactory<>("productName"));

        colAddQuantity.setCellValueFactory(
                new PropertyValueFactory<>("quantity"));

        colAddUnitPrice.setCellValueFactory(
                new PropertyValueFactory<>("price"));

        colAddSubtotal.setCellValueFactory(
                new PropertyValueFactory<>("subtotal"));

        colAddRemove.setCellFactory(column -> new TableCell<>() {

            private final Button deleteBtn = new Button("🗑");

            {
                deleteBtn.getStyleClass().add("table-delete-btn");

                deleteBtn.setOnAction(e -> {
                    SaleDetailes item = getTableView().getItems().get(getIndex());

                    addSaleDetailesTable.getItems().remove(item);
                    updateSaleSummary();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }

    private void setupPaymentAndDeliveryCombos() {

        addPaymentMethodCombo.getItems().setAll(
                "Cash",
                "Card",
                "Bank Transfer");

        addPaymentMethodCombo.getSelectionModel().selectFirst();

        addDeliveryStatusCombo.getItems().setAll(
                "pending",
                "delivered",
                "cancelled");

        addDeliveryStatusCombo.getSelectionModel().selectFirst();
    }

    private void loadCustomers() {
        addCustomerCombo.getItems().setAll(
                saleDAO.getCustomerNames());
    }

    private void loadEmployees() {
        addEmployeeCombo.getItems().setAll(
                saleDAO.getEmployeeNames());
    }

    private void loadProducts() {
        addProductCombo.getItems().setAll(
                saleDAO.getProductNames());
    }

    private void updateSelectedProductInfo() {

        String product = addProductCombo.getValue();

        if (product == null)
            return;

        double price = saleDAO.getProductPrice(product);

        double stock = saleDAO.getProductStock(product);

        selectedProductPriceLabel.setText(
                String.format("$%.2f", price));

        selectedProductStockLabel.setText(
                String.valueOf(stock));
    }

    private void openSaleDetails(Sale sale) {

        selectedSaleForDetails = sale;

        detailsSaleIdLabel.setText("Sale #" + sale.getSaleID());
        detailsPaymentBadgeLabel.setText(sale.getPaymentStatus());

        detailsCustomerNameLabel.setText(sale.getCustomerName());
        detailsSaleDateLabel.setText(formatDate(sale.getSaleDate()));

        detailsTotalAmountLabel.setText(String.format("$%,.0f", sale.getTotal_Amount()));
        detailsPaidAmountLabel.setText(String.format("$%,.0f", sale.getPaidAmount()));
        detailsBalanceLabel.setText(String.format("$%,.0f", sale.getBalance()));
        detailsItemsCountLabel.setText(String.valueOf(sale.getItemsCount()));

        detailsPaymentStatusLabel.setText(sale.getPaymentStatus());
        detailsDeliveryStatusLabel.setText(sale.getDeliveryStatus());

        detailsSaleNumberLabel.setText(String.valueOf(sale.getSaleID()));
        detailsCustomerLabel.setText(sale.getCustomerName());
        detailsEmployeeLabel.setText(sale.getEmployeeName());
        detailsFullSaleDateLabel.setText(formatDate(sale.getSaleDate()));
        detailsDeliveryDateLabel.setText(formatDate(sale.getDeliveryDate()));
        detailsPaymentMethodLabel.setText(emptyToDash(sale.getPaymentMethod()));

        SaleDetailesTable.setItems(
                FXCollections.observableArrayList(
                        saleDAO.getSaleDetailes(sale.getSaleID())));

        showOnlyPanel(saleDetailsPanel);
    }

    private void addItemToSale() {

        String productName = addProductCombo.getValue();

        if (productName == null || productName.isBlank()) {
            showWarning("Add Item", "Please select a product.");
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(addQuantityField.getText().trim());
        } catch (Exception e) {
            showWarning("Add Item", "Quantity must be a number.");
            return;
        }

        if (quantity <= 0) {
            showWarning("Add Item", "Quantity must be greater than 0.");
            return;
        }

        double stock = saleDAO.getProductStock(productName);

        if (quantity > stock) {
            showWarning("Add Item", "Quantity is greater than available stock.");
            return;
        }

        int productId = saleDAO.getProductIdByName(productName);
        double price = saleDAO.getProductPrice(productName);

        SaleDetailes item = new SaleDetailes(productId, productName, quantity, price);

        addSaleDetailesTable.getItems().add(item);
        productWarningLabel.setVisible(false);
        productWarningLabel.setManaged(false);

        addProductCombo.getSelectionModel().clearSelection();
        addQuantityField.clear();
        selectedProductPriceLabel.setText("$0.00");
        selectedProductStockLabel.setText("0");

        updateSaleSummary();
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "-";
        }

        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String emptyToDash(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        return value;
    }

    private void refreshRowNumbers() {
        for (int i = 0; i < saleTable.getItems().size(); i++) {
            saleTable.getItems().get(i).setNo(i + 1);
        }

        saleTable.refresh();
    }

    private void loadSalesTable() {
        List<Sale> sales = saleDAO.getAllSalesForTable();
        for (int i = 0; i < sales.size(); i++) {
            sales.get(i).setNo(i + 1);
        }
        originalSales.clear();
        originalSales.addAll(sales);
        saleTable.setItems(FXCollections.observableArrayList(sales));
    }

    private void loadSaleStats() {

        SaleStats stats = saleDAO.getSaleStats();

        totalSalesLabel.setText(
                String.valueOf(stats.getTotalSales()));

        totalRevenueLabel.setText(
                String.format("$%,.0f",
                        stats.getTotalRevenue()));

        productsSoldLabel.setText(
                String.valueOf(stats.getProductsSold()));

        pendingDeliveriesLabel.setText(
                String.valueOf(stats.getPendingDeliveries()));

        outstandingBalanceLabel.setText(
                String.format("$%,.0f",
                        stats.getOutstandingBalance()));
    }

    private void setupAddSaleButtons() {
        cancelAddBtn.setOnAction(e -> {
            clearAddForm();
            addSalePanel.setVisible(false);
            addSalePanel.setManaged(false);
        });
    }

    private void setupSaveSale() {
        saveSaleBtn.setOnAction(e -> {
            if (saleBeingUpdated == null) {
                addSale();
            } else {
                updateSale();
            }
        });
    }

    private void setupSearch() {

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {

            String keyword = newValue.toLowerCase().trim();

            if (keyword.isEmpty()) {
                saleTable.setItems(FXCollections.observableArrayList(originalSales));
                refreshRowNumbers();
                return;
            }

            List<Sale> filtered = originalSales.stream()
                    .filter(s -> contains(String.valueOf(s.getSaleID()), keyword)
                            || contains(s.getCustomerName(), keyword)
                            || contains(s.getEmployeeName(), keyword)
                            || contains(formatDate(s.getSaleDate()), keyword)
                            || contains(String.valueOf(s.getItemsCount()), keyword)
                            || contains(String.valueOf(s.getTotal_Amount()), keyword)
                            || contains(String.valueOf(s.getPaidAmount()), keyword)
                            || contains(String.valueOf(s.getBalance()), keyword)
                            || contains(s.getPaymentStatus(), keyword)
                            || contains(s.getDeliveryStatus(), keyword))
                    .toList();

            saleTable.setItems(FXCollections.observableArrayList(filtered));
            refreshRowNumbers();
        });
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private void openAddForm() {
        saleBeingUpdated = null;

        clearAddForm();
        addSaleDatePicker.setValue(LocalDate.now());

        formTitleLabel.setText("✚ ADD SALE");
        saveSaleBtn.setText("ADD");

        showOnlyPanel(addSalePanel);
    }

    private void addSale() {

        if (!validateAddSaleForm()) {
            return;
        }

        if (addSaleDetailesTable.getItems().isEmpty()) {
            showWarning("Add Sale", "Please add at least one product to the sale.");
            return;
        }

        double totalAmount = calculateTotalAmount();
        double paidAmount = parseDoubleOrDefault(addPaidAmountField.getText(), 0);
        double balance = totalAmount - paidAmount;

        Sale sale = new Sale();

        sale.setNo(saleTable.getItems().size() + 1);
        sale.setCustomerName(addCustomerCombo.getValue());
        sale.setEmployeeName(addEmployeeCombo.getValue());
        sale.setSaleDate(addSaleDatePicker.getValue());

        sale.setItemsCount(addSaleDetailesTable.getItems().size());
        sale.setTotal_Amount(totalAmount);
        sale.setPaidAmount(paidAmount);
        sale.setBalance(balance);

        sale.setPaymentMethod(addPaymentMethodCombo.getValue());
        sale.setPaymentStatus(getPaymentStatus(totalAmount, paidAmount));
        if (addPaymentMethodCombo.getValue() == null) {
            showWarning("Add Sale", "Please select payment method.");
            return;
        }

        sale.setDeliveryStatus(addDeliveryStatusCombo.getValue());
        sale.setDeliveryDate(
                addDeliveryDatePicker.getValue() == null
                        ? null
                        : addDeliveryDatePicker.getValue());
        if (addDeliveryStatusCombo.getValue() == null) {
            showWarning("Add Sale", "Please select delivery status.");
            return;
        }

        sale.setItems(new ArrayList<>(addSaleDetailesTable.getItems()));

        saveStateForUndo();

        sale.setCustomerID(
                saleDAO.getCustomerIdByName(
                        sale.getCustomerName()));

        sale.setEmployeeID(
                saleDAO.getEmployeeIdByName(
                        sale.getEmployeeName()));

        pendingAdds.add(sale);
        saleTable.getItems().add(sale);
        originalSales.add(sale);

        salesFoundLabel.setText(String.valueOf(saleTable.getItems().size()));

        loadSaleFilters();
        redoStack.clear();
        clearAddForm();
    }

    private double calculateTotalAmount() {
        return addSaleDetailesTable.getItems()
                .stream()
                .mapToDouble(SaleDetailes::getSubtotal)
                .sum();
    }

    private String getPaymentStatus(double total, double paid) {
        if (paid <= 0) {
            return "Unpaid";
        }

        if (paid < total) {
            return "Partial";
        }

        return "Paid";
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validateAddSaleForm() {

        boolean valid = true;

        valid &= validateComboBox(addCustomerCombo, customerWarningLabel);
        valid &= validateComboBox(addEmployeeCombo, employeeWarningLabel);
        valid &= validateDatePicker(addSaleDatePicker, saleDateWarningLabel);

        valid &= validateComboBox(addPaymentMethodCombo, paymentMethodWarningLabel);
        valid &= validateComboBox(addDeliveryStatusCombo, deliverystatusWarningLabel);
        valid &= validateDatePicker(addDeliveryDatePicker, deliveryDateWarningLabel);

        if (addPaidAmountField.getText().trim().isEmpty()) {
            paidAmountWarningLabel.setVisible(true);
            paidAmountWarningLabel.setManaged(true);
            valid = false;
        }

        if (addSaleDetailesTable.getItems().isEmpty()) {
            productWarningLabel.setVisible(true);
            productWarningLabel.setManaged(true);
            valid = false;
        }
        return valid;
    }

    private boolean validateDatePicker(DatePicker datePicker, Label warningLabel) {
        boolean valid = datePicker.getValue() != null;

        datePicker.getStyleClass().remove("validation-error");

        if (!valid) {
            datePicker.getStyleClass().add("validation-error");
        }

        warningLabel.setVisible(!valid);
        warningLabel.setManaged(!valid);

        return valid;
    }

    private boolean validateComboBox(ComboBox<String> comboBox, Label warningLabel) {
        boolean valid = comboBox.getValue() != null && !comboBox.getValue().trim().isEmpty();

        comboBox.getStyleClass().remove("validation-error");

        if (!valid) {
            comboBox.getStyleClass().add("validation-error");
        }

        warningLabel.setVisible(!valid);
        warningLabel.setManaged(!valid);

        return valid;
    }

    private void setupLiveValidation() {

        addCustomerCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateComboBox(addCustomerCombo, customerWarningLabel);
            }
        });

        addEmployeeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateComboBox(addEmployeeCombo, employeeWarningLabel);
            }
        });

        addSaleDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateDatePicker(addSaleDatePicker, saleDateWarningLabel);
            }
        });

        addPaidAmountField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateSaleSummary();
            boolean valid = !newVal.trim().isEmpty();

            paidAmountWarningLabel.setVisible(!valid);
            paidAmountWarningLabel.setManaged(!valid);
        });

        addPaymentMethodCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            validateComboBox(addPaymentMethodCombo, paymentMethodWarningLabel);
        });

        addDeliveryStatusCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            validateComboBox(addDeliveryStatusCombo, deliverystatusWarningLabel);
        });

        addDeliveryDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            validateDatePicker(addDeliveryDatePicker, deliveryDateWarningLabel);
        });
    }

    private void updateSaleSummary() {

        double total = calculateTotalAmount();
        double paid = parseDoubleOrDefault(addPaidAmountField.getText(), 0);
        double balance = total - paid;

        if (balance < 0) {
            balance = 0;
        }

        addTotalAmountLabel.setText(String.format("$%,.0f", total));
        addBalanceLabel.setText(String.format("$%,.0f", balance));
    }

    private void clearAddForm() {

        clearingForm = true;

        addCustomerCombo.getSelectionModel().clearSelection();
        addEmployeeCombo.getSelectionModel().clearSelection();
        addSaleDatePicker.setValue(LocalDate.now());

        addProductCombo.getSelectionModel().clearSelection();
        addQuantityField.clear();

        selectedProductPriceLabel.setText("$0.00");
        selectedProductStockLabel.setText("0");

        addSaleDetailesTable.getItems().clear();

        addPaidAmountField.clear();

        addPaymentMethodCombo.getSelectionModel().selectFirst();
        addDeliveryStatusCombo.getSelectionModel().selectFirst();
        addDeliveryDatePicker.setValue(null);

        addTotalAmountLabel.setText("$0.00");
        addBalanceLabel.setText("$0.00");

        clearingForm = false;

        hideAllWarnings();
        removeAllValidationErrors();

        saleBeingUpdated = null;
        formTitleLabel.setText("✚ ADD SALE");
        saveSaleBtn.setText("✚  ADD SALE");
    }

    private void hideAllWarnings() {

        Label[] warnings = {
                customerWarningLabel,
                employeeWarningLabel,
                saleDateWarningLabel,
                productWarningLabel,
                quantityWarningLabel,
                paidAmountWarningLabel,
                paymentMethodWarningLabel,
                deliverystatusWarningLabel,
                deliveryDateWarningLabel
        };

        for (Label warning : warnings) {
            warning.setVisible(false);
            warning.setManaged(false);
        }
    }

    private void removeAllValidationErrors() {

        addCustomerCombo.getStyleClass().remove("validation-error");
        addEmployeeCombo.getStyleClass().remove("validation-error");
        addSaleDatePicker.getStyleClass().remove("validation-error");
        addPaymentMethodCombo.getStyleClass().remove("validation-error");
        addDeliveryStatusCombo.getStyleClass().remove("validation-error");
        addDeliveryDatePicker.getStyleClass().remove("validation-error");
    }

    private void openUpdateForm(Sale sale) {

        saleBeingUpdated = sale;

        showOnlyPanel(addSalePanel);

        addCustomerCombo.setValue(sale.getCustomerName());
        addEmployeeCombo.setValue(sale.getEmployeeName());

        if (sale.getSaleDate() != null) {
            addSaleDatePicker.setValue(sale.getSaleDate());
        }

        addSaleDetailesTable.setItems(
                FXCollections.observableArrayList(
                        saleDAO.getSaleDetailes(sale.getSaleID())));

        addPaidAmountField.setText(
                String.valueOf(sale.getPaidAmount()));

        addPaymentMethodCombo.setValue(sale.getPaymentMethod());
        addDeliveryStatusCombo.setValue(sale.getDeliveryStatus());

        if (sale.getDeliveryDate() != null) {
            addDeliveryDatePicker.setValue(
                    sale.getDeliveryDate());
        } else {
            addDeliveryDatePicker.setValue(null);
        }

        updateSaleSummary();

        formTitleLabel.setText("✏ UPDATE SALE");
        saveSaleBtn.setText("UPDATE");
    }

    private void updateSale() {

        if (!validateAddSaleForm()) {
            return;
        }

        if (addSaleDetailesTable.getItems().isEmpty()) {
            showWarning("Update Sale", "Please add at least one product to the sale.");
            return;
        }

        saveStateForUndo();

        double totalAmount = calculateTotalAmount();
        double paidAmount = parseDoubleOrDefault(addPaidAmountField.getText(), 0);
        double balance = totalAmount - paidAmount;

        saleBeingUpdated.setCustomerName(addCustomerCombo.getValue());
        saleBeingUpdated.setEmployeeName(addEmployeeCombo.getValue());
        saleBeingUpdated.setSaleDate(addSaleDatePicker.getValue());

        saleBeingUpdated.setItemsCount(addSaleDetailesTable.getItems().size());
        saleBeingUpdated.setTotal_Amount(totalAmount);
        saleBeingUpdated.setPaidAmount(paidAmount);
        saleBeingUpdated.setBalance(balance);

        saleBeingUpdated.setPaymentMethod(addPaymentMethodCombo.getValue());
        saleBeingUpdated.setPaymentStatus(getPaymentStatus(totalAmount, paidAmount));

        saleBeingUpdated.setDeliveryStatus(addDeliveryStatusCombo.getValue());
        saleBeingUpdated.setDeliveryDate(
                addDeliveryDatePicker.getValue() == null
                        ? null
                        : addDeliveryDatePicker.getValue());

        saleBeingUpdated.setItems(
                new ArrayList<>(addSaleDetailesTable.getItems()));

        if (!pendingUpdates.contains(saleBeingUpdated)) {
            pendingUpdates.add(saleBeingUpdated);
        }

        saleTable.refresh();

        saleBeingUpdated = null;

        clearAddForm();

        addSalePanel.setVisible(false);
        addSalePanel.setManaged(false);

        redoStack.clear();
    }

    private void saveStateForUndo() {
        undoStack.push(new ArrayList<>(saleTable.getItems()));
    }

    private void applyFilters() {

        String customer = customerCombo.getValue();
        String employee = employeeCombo.getValue();

        String paymentStatus = paymentStatusCombo.getValue();
        String deliveryStatus = deliveryStatusCombo.getValue();
        String balanceStatus = balanceStatusCombo.getValue();

        double minTotal = parseDoubleOrDefault(minTotalField.getText(), 0);

        double maxTotal = parseDoubleOrDefault(
                maxTotalField.getText(),
                Double.MAX_VALUE);

        LocalDate fromDate = fromSaleDatePicker.getValue();
        LocalDate toDate = toSaleDatePicker.getValue();

        List<Sale> filtered = originalSales.stream()

                .filter(s -> customer == null
                        || customer.equals("All Customers")
                        || customer.equals(s.getCustomerName()))

                .filter(s -> employee == null
                        || employee.equals("All Employees")
                        || employee.equals(s.getEmployeeName()))

                .filter(s -> paymentStatus == null
                        || paymentStatus.equals("All")
                        || paymentStatus.equals(s.getPaymentStatus()))

                .filter(s -> deliveryStatus == null
                        || deliveryStatus.equals("All")
                        || deliveryStatus.equals(s.getDeliveryStatus()))

                .filter(s -> s.getTotal_Amount() >= minTotal
                        && s.getTotal_Amount() <= maxTotal)

                .filter(s -> balanceStatusMatches(s, balanceStatus))

                .filter(s -> {

                    if (fromDate == null && toDate == null) {
                        return true;
                    }

                    if (s.getSaleDate() == null) {
                        return false;
                    }

                    LocalDate saleDate = s.getSaleDate();

                    boolean afterFrom = fromDate == null
                            || !saleDate.isBefore(fromDate);

                    boolean beforeTo = toDate == null
                            || !saleDate.isAfter(toDate);

                    return afterFrom && beforeTo;
                })

                .toList();

        saleTable.setItems(
                FXCollections.observableArrayList(filtered));

        salesFoundLabel.setText(
                String.valueOf(filtered.size()));

        refreshRowNumbers();
    }

    private double parseDoubleOrDefault(String text, double defaultValue) {
        try {
            return Double.parseDouble(text.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private boolean balanceStatusMatches(
            Sale sale,
            String balanceStatus) {

        if (balanceStatus == null
                || balanceStatus.equals("All")) {
            return true;
        }

        double balance = sale.getBalance();

        return switch (balanceStatus) {
            case "No Balance" -> balance <= 0;
            case "Has Balance" -> balance > 0;
            default -> true;
        };
    }

    private void resetFilters() {

        customerCombo.getSelectionModel().selectFirst();
        employeeCombo.getSelectionModel().selectFirst();
        paymentStatusCombo.getSelectionModel().selectFirst();
        deliveryStatusCombo.getSelectionModel().selectFirst();
        balanceStatusCombo.getSelectionModel().selectFirst();

        minTotalField.clear();
        maxTotalField.clear();

        fromSaleDatePicker.setValue(null);
        toSaleDatePicker.setValue(null);

        saleTable.setItems(FXCollections.observableArrayList(originalSales));

        salesFoundLabel.setText(String.valueOf(originalSales.size()));

        refreshRowNumbers();
    }

    private void loadSaleFilters() {

        customerCombo.getItems().clear();
        customerCombo.getItems().add("All Customers");
        customerCombo.getItems().addAll(
                saleDAO.getCustomerNames());

        customerCombo.getSelectionModel().selectFirst();

        employeeCombo.getItems().clear();
        employeeCombo.getItems().add("All Employees");
        employeeCombo.getItems().addAll(
                saleDAO.getEmployeeNames());

        employeeCombo.getSelectionModel().selectFirst();

        paymentStatusCombo.getItems().setAll(
                "All",
                "Paid",
                "Partial",
                "Unpaid");

        paymentStatusCombo.getSelectionModel().selectFirst();

        deliveryStatusCombo.getItems().setAll(
                "All",
                "Pending",
                "Delivered",
                "Cancelled");

        deliveryStatusCombo.getSelectionModel().selectFirst();

        balanceStatusCombo.getItems().setAll(
                "All",
                "No Balance",
                "Has Balance");

        balanceStatusCombo.getSelectionModel().selectFirst();

        salesFoundLabel.setText(
                String.valueOf((saleTable.getItems().size())));
    }

    private void showOnlyPanel(VBox panel) {

        filterPanel.setVisible(false);
        filterPanel.setManaged(false);

        addSalePanel.setVisible(false);
        addSalePanel.setManaged(false);

        saleDetailsPanel.setVisible(false);
        saleDetailsPanel.setManaged(false);

        panel.setVisible(true);
        panel.setManaged(true);
    }

    private void setupUndoRedoReset() {

        undoBtn.setOnAction(e -> undoAction());

        redoBtn.setOnAction(e -> redoAction());

        resetBtn.setOnAction(e -> resetTableChanges());
    }

    private void undoAction() {

        if (undoStack.isEmpty()) {
            return;
        }

        redoStack.push(new ArrayList<>(saleTable.getItems()));

        List<Sale> previousState = undoStack.pop();

        saleTable.setItems(
                FXCollections.observableArrayList(previousState));

        refreshRowNumbers();
    }

    private void redoAction() {

        if (redoStack.isEmpty()) {
            return;
        }

        undoStack.push(new ArrayList<>(saleTable.getItems()));

        List<Sale> nextState = redoStack.pop();

        saleTable.setItems(
                FXCollections.observableArrayList(nextState));

        refreshRowNumbers();
    }

    private void resetTableChanges() {

        pendingAdds.clear();
        pendingUpdates.clear();
        pendingDeletes.clear();

        undoStack.clear();
        redoStack.clear();

        loadSalesTable();
        loadSaleStats();
        loadSaleFilters();

        clearAddForm();

        addSalePanel.setVisible(false);
        addSalePanel.setManaged(false);

        saleDetailsPanel.setVisible(false);
        saleDetailsPanel.setManaged(false);
    }

    private void saveAllChanges() {

        try {

            for (Sale sale : pendingAdds) {
                saleDAO.insertSale(sale);
            }

            for (Sale sale : pendingUpdates) {
                saleDAO.updateSale(sale);
            }

            for (Sale sale : pendingDeletes) {
                saleDAO.deleteSale(sale.getSaleID());
            }

            pendingAdds.clear();
            pendingUpdates.clear();
            pendingDeletes.clear();

            undoStack.clear();
            redoStack.clear();

            loadSalesTable();
            loadSaleStats();
            loadSaleFilters();

            clearAddForm();

            addSalePanel.setVisible(false);
            addSalePanel.setManaged(false);

            saleDetailsPanel.setVisible(false);
            saleDetailsPanel.setManaged(false);

            System.out.println("All sale changes saved successfully.");

        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save sales changes.");
            alert.showAndWait();
        }
    }

    private void exportSalesToExcel() {

        try {
            File exportFolder = new File("src/main/resources/exports");

            if (!exportFolder.exists()) {
                exportFolder.mkdirs();
            }

            String fileName = "sales_" + java.time.LocalDate.now() + ".xlsx";
            File file = new File(exportFolder, fileName);

            Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Sales");

            String[] headers = {
                    "Sale ID",
                    "Customer",
                    "Employee",
                    "Sale Date",
                    "Items Count",
                    "Total Amount",
                    "Paid Amount",
                    "Balance",
                    "Payment Status",
                    "Payment Method",
                    "Delivery Status",
                    "Delivery Date"
            };

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowIndex = 1;

            for (Sale s : saleTable.getItems()) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(s.getSaleID());
                row.createCell(1).setCellValue(s.getCustomerName());
                row.createCell(2).setCellValue(s.getEmployeeName());

                row.createCell(3).setCellValue(
                        s.getSaleDate() == null
                                ? ""
                                : s.getSaleDate().toString());

                row.createCell(4).setCellValue(s.getItemsCount());
                row.createCell(5).setCellValue(s.getTotal_Amount());
                row.createCell(6).setCellValue(s.getPaidAmount());
                row.createCell(7).setCellValue(s.getBalance());
                row.createCell(8).setCellValue(s.getPaymentStatus());
                row.createCell(9).setCellValue(s.getPaymentMethod());
                row.createCell(10).setCellValue(s.getDeliveryStatus());

                row.createCell(11).setCellValue(
                        s.getDeliveryDate() == null
                                ? ""
                                : s.getDeliveryDate().toString());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (java.io.FileOutputStream outputStream = new java.io.FileOutputStream(file)) {
                workbook.write(outputStream);
            }

            workbook.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Success");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Sales exported successfully!\n\nSaved to:\n"
                            + file.getAbsolutePath());
            alert.showAndWait();

        } catch (Exception ex) {
            ex.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to export sales file.");
            alert.showAndWait();
        }
    }
}
