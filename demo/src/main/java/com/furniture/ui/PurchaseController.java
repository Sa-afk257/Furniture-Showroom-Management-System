package com.furniture.ui;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import com.furniture.dao.PurchaseDAO;
import com.furniture.model.PurchaseTransaction;
import com.furniture.model.PurchaseDetails;
import com.furniture.dao.PurchaseDAO.PurchaseStats;

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

public class PurchaseController {

    private PurchaseDAO purchaseDAO = new PurchaseDAO();

    @FXML
    private Label totalPurchasesLabel;
    @FXML
    private Label totalPurchaseCostLabel;
    @FXML
    private Label productsPurchasedLabel;
    @FXML
    private Label activeSuppliersLabel;
    @FXML
    private Label lowStockItemsLabel;

    /* Filter */
    @FXML
    private ComboBox<String> supplierCombo;
    @FXML
    private ComboBox<String> supplierTypeCombo;
    @FXML
    private ComboBox<String> employeeCombo;
    @FXML
    private ComboBox<String> productCombo;
    @FXML
    private ComboBox<String> categoryCombo;
    @FXML
    private ComboBox<String> warehouseCombo;

    @FXML
    private DatePicker fromPurchaseDatePicker;
    @FXML
    private DatePicker toPurchaseDatePicker;

    @FXML
    private TextField minTotalField;
    @FXML
    private TextField maxTotalField;
    @FXML
    private TextField minQuantityField;
    @FXML
    private TextField maxQuantityField;

    @FXML
    private Label purchasesFoundLabel;

    /* Main Table */
    @FXML
    private TableView<PurchaseTransaction> purchaseTable;

    @FXML
    private TableColumn<PurchaseTransaction, Integer> colNo;
    @FXML
    private TableColumn<PurchaseTransaction, Integer> colPurchaseID;
    @FXML
    private TableColumn<PurchaseTransaction, String> colSupplierName;
    @FXML
    private TableColumn<PurchaseTransaction, String> colSupplierType;
    @FXML
    private TableColumn<PurchaseTransaction, String> colEmployeeName;
    @FXML
    private TableColumn<PurchaseTransaction, LocalDate> colPurchaseDate;
    @FXML
    private TableColumn<PurchaseTransaction, Integer> colItemsCount;
    @FXML
    private TableColumn<PurchaseTransaction, Double> colTotalQuantity;
    @FXML
    private TableColumn<PurchaseTransaction, Double> colTotalAmount;
    @FXML
    private TableColumn<PurchaseTransaction, Void> colAction;

    /* Panels */
    @FXML
    private VBox filterPanel;
    @FXML
    private VBox addPurchasePanel;
    @FXML
    private VBox PurchaseDetailsPanel;

    /* Buttons */
    @FXML
    private Button filterToggleBtn;
    @FXML
    private Button addPurchaseBtn;
    @FXML
    private Button savePurchaseBtn;
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

    /* Add Purchase Form */
    @FXML
    private ComboBox<String> addSupplierCombo;
    @FXML
    private ComboBox<String> addEmployeeCombo;
    @FXML
    private DatePicker addPurchaseDatePicker;

    @FXML
    private ComboBox<String> addProductCombo;
    @FXML
    private ComboBox<String> addWarehouseCombo;
    @FXML
    private TextField addQuantityField;
    @FXML

    private TextField addPriceField;
    @FXML
    private Button addItemToPurchaseBtn;

    @FXML
    private Label selectedProductStockLabel;

    @FXML
    private TableView<PurchaseDetails> addPurchaseDetailsTable;
    @FXML
    private TableColumn<PurchaseDetails, String> colAddProductName;
    @FXML
    private TableColumn<PurchaseDetails, String> colAddCategory;
    @FXML
    private TableColumn<PurchaseDetails, String> colAddWarehouse;
    @FXML
    private TableColumn<PurchaseDetails, Double> colAddQuantity;
    @FXML
    private TableColumn<PurchaseDetails, Double> colAddUnitPrice;
    @FXML
    private TableColumn<PurchaseDetails, Double> colAddSubtotal;
    @FXML
    private TableColumn<PurchaseDetails, Void> colAddRemove;
    @FXML
    private TableColumn<PurchaseDetails, Void> colAddEdit;

    @FXML
    private Label addTotalQuantityLabel;
    @FXML
    private Label addTotalAmountLabel;
    @FXML
    private Label formTitleLabel;

    /* Warnings */
    @FXML
    private Label supplierWarningLabel;
    @FXML
    private Label employeeWarningLabel;
    @FXML
    private Label purchaseDateWarningLabel;
    @FXML
    private Label productWarningLabel;
    @FXML
    private Label warehouseWarningLabel;
    @FXML
    private Label quantityWarningLabel;
    @FXML
    private Label priceWarningLabel;

    /* Details Panel */
    @FXML
    private Label detailsPurchaseIdLabel;
    @FXML
    private Label detailsSupplierNameLabel;
    @FXML
    private Label detailsTotalAmountLabel;
    @FXML
    private Label detailsSupplierTypeLabel;
    @FXML
    private Label detailsSupplierEmailLabel;
    @FXML
    private Label detailsEmployeeLabel;
    @FXML
    private Label detailsPurchaseDateLabel;
    @FXML
    private Label detailsItemsCountLabel;
    @FXML
    private Label detailsTotalQuantityLabel;

    @FXML
    private TableView<PurchaseDetails> purchaseDetailsTable;
    @FXML
    private TableColumn<PurchaseDetails, String> colDetailProductName;
    @FXML
    private TableColumn<PurchaseDetails, String> colDetailCategory;
    @FXML
    private TableColumn<PurchaseDetails, String> colDetailWarehouse;
    @FXML
    private TableColumn<PurchaseDetails, Double> colDetailQuantity;
    @FXML
    private TableColumn<PurchaseDetails, Double> colDetailUnitPrice;
    @FXML
    private TableColumn<PurchaseDetails, Double> colDetailSubtotal;
    @FXML
    private TableColumn<PurchaseDetails, Double> colDetailCurrentStock;

    /* Lists */

    private List<PurchaseTransaction> originalPurchases = new ArrayList<>();

    private Stack<List<PurchaseTransaction>> undoStack = new Stack<>();
    private Stack<List<PurchaseTransaction>> redoStack = new Stack<>();

    private List<PurchaseTransaction> pendingAdds = new ArrayList<>();
    private List<PurchaseTransaction> pendingUpdates = new ArrayList<>();
    private List<PurchaseTransaction> pendingDeletes = new ArrayList<>();

    private PurchaseTransaction purchaseBeingUpdated = null;
    private boolean clearingForm = false;
    private PurchaseTransaction selectedPurchaseForDetails = null;
    private PurchaseDetails purchaseItemBeingEdited = null;

    @FXML
    private void initialize() {

        setupPurchaseTable();
        setupDetailsTables();

        loadPurchasesTable();
        loadPurchaseStats();
        loadPurchaseFilters();

        loadSuppliers();
        loadEmployees();
        loadProducts();
        loadWarehouses();

        setupSearch();
        setupAddPurchaseButtons();
        setupSavePurchase();
        setupLiveValidation();
        setupUndoRedoReset();
        setupAddPurchaseDetailsTable();

        addItemToPurchaseBtn.setOnAction(e -> addItemToPurchase());

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

        addPurchaseBtn.setOnAction(e -> {
            if (addPurchasePanel.isVisible() && purchaseBeingUpdated == null) {
                addPurchasePanel.setVisible(false);
                addPurchasePanel.setManaged(false);
            } else {
                openAddForm();
            }
        });

        closeDetailsBtn.setOnAction(e -> {
            PurchaseDetailsPanel.setVisible(false);
            PurchaseDetailsPanel.setManaged(false);
        });

        exportBtn.setOnAction(e -> exportPurchasesToExcel());
        saveAllBtn.setOnAction(e -> saveAllChanges());

        addProductCombo.setOnAction(e -> updateSelectedProductInfo());
        addWarehouseCombo.setOnAction(e -> updateSelectedProductInfo());
    }

    private void setupPurchaseTable() {

        colNo.setCellValueFactory(
                new PropertyValueFactory<>("no"));

        colPurchaseID.setCellValueFactory(
                new PropertyValueFactory<>("purchaseID"));

        colSupplierName.setCellValueFactory(
                new PropertyValueFactory<>("supplierName"));

        colSupplierType.setCellValueFactory(
                new PropertyValueFactory<>("supplierType"));

        colEmployeeName.setCellValueFactory(
                new PropertyValueFactory<>("employeeName"));

        colItemsCount.setCellValueFactory(
                new PropertyValueFactory<>("itemsCount"));

        colTotalQuantity.setCellValueFactory(
                new PropertyValueFactory<>("totalQuantity"));

        colPurchaseDate.setCellValueFactory(
                new PropertyValueFactory<>("purchaseDate"));

        colPurchaseDate.setCellFactory(column -> new TableCell<>() {

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

        colTotalAmount.setCellValueFactory(
                new PropertyValueFactory<>("totalAmount"));

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

        colTotalQuantity.setCellFactory(column -> new TableCell<>() {

            @Override
            protected void updateItem(Double item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText("0");
                } else {
                    setText(String.format("%.0f", item));
                }
            }
        });

        setupActionColumn();
    }

    private void setupDetailsTables() {

        colDetailProductName.setCellValueFactory(
                new PropertyValueFactory<>("productName"));

        colDetailCategory.setCellValueFactory(
                new PropertyValueFactory<>("categoryName"));

        colDetailWarehouse.setCellValueFactory(
                new PropertyValueFactory<>("warehouseName"));

        colDetailQuantity.setCellValueFactory(
                new PropertyValueFactory<>("quantity"));

        colDetailUnitPrice.setCellValueFactory(
                new PropertyValueFactory<>("price"));

        colDetailSubtotal.setCellValueFactory(
                new PropertyValueFactory<>("subtotal"));

        colDetailCurrentStock.setCellValueFactory(
                new PropertyValueFactory<>("currentStock"));
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
                    PurchaseTransaction purchase = getTableView().getItems().get(getIndex());
                    openPurchaseDetails(purchase);
                });

                editBtn.setOnAction(e -> {
                    PurchaseTransaction purchase = getTableView().getItems().get(getIndex());
                    openUpdateForm(purchase);
                });

                deleteBtn.setOnAction(e -> {
                    PurchaseTransaction purchase = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Purchase");
                    alert.setHeaderText(null);
                    alert.setContentText(
                            "Are you sure you want to delete Purchase #"
                                    + purchase.getPurchaseID() + "?");

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isEmpty() || result.get() != ButtonType.OK) {
                        return;
                    }

                    saveStateForUndo();

                    pendingDeletes.add(purchase);
                    purchaseTable.getItems().remove(purchase);

                    refreshRowNumbers();

                    redoStack.clear();
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

    private void setupAddPurchaseDetailsTable() {

        colAddProductName.setCellValueFactory(
                new PropertyValueFactory<>("productName"));

        colAddCategory.setCellValueFactory(
                new PropertyValueFactory<>("categoryName"));

        colAddWarehouse.setCellValueFactory(
                new PropertyValueFactory<>("warehouseName"));

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
                    PurchaseDetails item = getTableView().getItems().get(getIndex());

                    addPurchaseDetailsTable.getItems().remove(item);
                    updatePurchaseSummary();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
        colAddEdit.setCellFactory(column -> new TableCell<>() {

            private final Button editBtn = new Button("✏");

            {
                editBtn.getStyleClass().add("table-edit-btn");

                editBtn.setOnAction(e -> {

                    PurchaseDetails item = getTableView().getItems().get(getIndex());

                    purchaseItemBeingEdited = item;

                    addProductCombo.setValue(item.getProductName());

                    addWarehouseCombo.setValue(
                            item.getWarehouseName());

                    addQuantityField.setText(
                            String.valueOf(item.getQuantity()));

                    addPriceField.setText(
                            String.valueOf(item.getPrice()));

                    updateSelectedProductInfo();

                    addItemToPurchaseBtn.setText("UPDATE ITEM");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editBtn);
            }
        });
    }

    private void loadSuppliers() {
        addSupplierCombo.getItems().setAll(
                purchaseDAO.getSupplierNames());
    }

    private void loadEmployees() {
        addEmployeeCombo.getItems().setAll(
                purchaseDAO.getEmployeeNames());
    }

    private void loadProducts() {
        addProductCombo.getItems().setAll(
                purchaseDAO.getProductNames());
    }

    private void loadWarehouses() {
        addWarehouseCombo.getItems().setAll(
                purchaseDAO.getWarehouseNames());
    }

    private void updateSelectedProductInfo() {

        String product = addProductCombo.getValue();
        String warehouse = addWarehouseCombo.getValue();

        if (product == null || warehouse == null) {
            selectedProductStockLabel.setText("0");
            return;
        }

        double stock = purchaseDAO.getProductStockInWarehouse(product, warehouse);

        selectedProductStockLabel.setText(
                String.format("%.0f", stock));
    }

    private void openPurchaseDetails(PurchaseTransaction purchase) {

        selectedPurchaseForDetails = purchase;

        detailsPurchaseIdLabel.setText("Purchase #" + purchase.getPurchaseID());
        detailsSupplierNameLabel.setText(purchase.getSupplierName());
        detailsTotalAmountLabel.setText(
                String.format("$%,.0f", purchase.getTotalAmount()));

        detailsSupplierTypeLabel.setText(emptyToDash(purchase.getSupplierType()));
        detailsSupplierEmailLabel.setText(emptyToDash(purchase.getSupplierEmail()));
        detailsEmployeeLabel.setText(emptyToDash(purchase.getEmployeeName()));
        detailsPurchaseDateLabel.setText(formatDate(purchase.getPurchaseDate()));

        detailsItemsCountLabel.setText(String.valueOf(purchase.getItemsCount()));
        detailsTotalQuantityLabel.setText(
                String.format("%.0f", purchase.getTotalQuantity()));

        List<PurchaseDetails> details;

        if (purchase.getItems() != null && !purchase.getItems().isEmpty()) {
            details = purchase.getItems();
            System.out.println("DETAILS FROM OBJECT = " + details.size());
        } else {
            details = purchaseDAO.getPurchaseDetails(purchase.getPurchaseID());
            System.out.println("DETAILS FROM DATABASE = " + details.size());
        }

        purchaseDetailsTable.getItems().clear();
        purchaseDetailsTable.setItems(
                FXCollections.observableArrayList(details));

        purchaseDetailsTable.refresh();

        showOnlyPanel(PurchaseDetailsPanel);
    }

    private void addItemToPurchase() {

        String productName = addProductCombo.getValue();
        String warehouseName = addWarehouseCombo.getValue();

        if (productName == null || productName.isBlank()) {
            showWarning("Add Item", "Please select a product.");
            return;
        }

        if (warehouseName == null || warehouseName.isBlank()) {
            showWarning("Add Item", "Please select a warehouse.");
            return;
        }

        double quantity;
        double price;

        try {
            quantity = Double.parseDouble(addQuantityField.getText().trim());
        } catch (Exception e) {
            showWarning("Add Item", "Quantity must be a number.");
            return;
        }

        try {
            price = Double.parseDouble(addPriceField.getText().trim());
        } catch (Exception e) {
            showWarning("Add Item", "Price must be a number.");
            return;
        }

        if (quantity <= 0) {
            showWarning("Add Item", "Quantity must be greater than 0.");
            return;
        }

        if (price <= 0) {
            showWarning("Add Item", "Price must be greater than 0.");
            return;
        }

        int productId = purchaseDAO.getProductIdByName(productName);
        int warehouseId = purchaseDAO.getWarehouseIdByName(warehouseName);
        String categoryName = purchaseDAO.getProductCategoryName(productName);

        PurchaseDetails item = new PurchaseDetails(
                productId,
                productName,
                categoryName,
                warehouseId,
                warehouseName,
                quantity,
                price);

        if (purchaseItemBeingEdited != null) {

            int index = addPurchaseDetailsTable
                    .getItems()
                    .indexOf(purchaseItemBeingEdited);

            addPurchaseDetailsTable
                    .getItems()
                    .set(index, item);

            purchaseItemBeingEdited = null;

            addItemToPurchaseBtn.setText("✚ ADD ITEM");

        } else {

            addPurchaseDetailsTable
                    .getItems()
                    .add(item);
        }

        productWarningLabel.setVisible(false);
        productWarningLabel.setManaged(false);
        warehouseWarningLabel.setVisible(false);
        warehouseWarningLabel.setManaged(false);

        clearingForm = true;

        addProductCombo.getSelectionModel().clearSelection();
        addWarehouseCombo.getSelectionModel().clearSelection();
        addQuantityField.clear();
        addPriceField.clear();

        selectedProductStockLabel.setText("0");

        removeAllValidationErrors();
        hideAllWarnings();

        clearingForm = false;

        updatePurchaseSummary();
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
        for (int i = 0; i < purchaseTable.getItems().size(); i++) {
            purchaseTable.getItems().get(i).setNo(i + 1);
        }

        purchaseTable.refresh();
    }

    private void loadPurchasesTable() {
        List<PurchaseTransaction> purchases = purchaseDAO.getAllPurchasesForTable();

        for (int i = 0; i < purchases.size(); i++) {
            purchases.get(i).setNo(i + 1);
        }

        originalPurchases.clear();
        originalPurchases.addAll(purchases);

        purchaseTable.setItems(
                FXCollections.observableArrayList(purchases));
    }

    private void loadPurchaseStats() {

        PurchaseStats stats = purchaseDAO.getPurchaseStats();

        totalPurchasesLabel.setText(
                String.valueOf(stats.getTotalPurchases()));

        totalPurchaseCostLabel.setText(
                String.format("$%,.0f", stats.getTotalPurchaseCost()));

        productsPurchasedLabel.setText(
                String.format("%.0f", stats.getProductsPurchased()));

        activeSuppliersLabel.setText(
                String.valueOf(stats.getActiveSuppliers()));

        lowStockItemsLabel.setText(
                String.valueOf(stats.getLowStockItems()));
    }

    private void setupAddPurchaseButtons() {
        cancelAddBtn.setOnAction(e -> {
            clearAddForm();
            addPurchasePanel.setVisible(false);
            addPurchasePanel.setManaged(false);
        });
    }

    private void setupSavePurchase() {
        savePurchaseBtn.setOnAction(e -> {
            if (purchaseBeingUpdated == null) {
                addPurchase();
            } else {
                updatePurchase();
            }
        });
    }

    private void setupSearch() {

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {

            String keyword = newValue.toLowerCase().trim();

            if (keyword.isEmpty()) {
                purchaseTable.setItems(
                        FXCollections.observableArrayList(originalPurchases));
                refreshRowNumbers();
                return;
            }

            List<PurchaseTransaction> filtered = originalPurchases.stream()
                    .filter(p -> contains(String.valueOf(p.getPurchaseID()), keyword)
                            || contains(p.getSupplierName(), keyword)
                            || contains(p.getSupplierType(), keyword)
                            || contains(p.getEmployeeName(), keyword)
                            || contains(formatDate(p.getPurchaseDate()), keyword)
                            || contains(String.valueOf(p.getItemsCount()), keyword)
                            || contains(String.valueOf(p.getTotalQuantity()), keyword)
                            || contains(String.valueOf(p.getTotalAmount()), keyword))
                    .toList();

            purchaseTable.setItems(
                    FXCollections.observableArrayList(filtered));

            refreshRowNumbers();
        });
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private void openAddForm() {
        purchaseBeingUpdated = null;

        clearAddForm();
        addPurchaseDatePicker.setValue(LocalDate.now());

        formTitleLabel.setText("✚ ADD PURCHASE");
        savePurchaseBtn.setText("✚  ADD PURCHASE");

        showOnlyPanel(addPurchasePanel);
    }

    private void addPurchase() {

        if (!validateAddPurchaseForm()) {
            return;
        }

        if (addPurchaseDetailsTable.getItems().isEmpty()) {
            showWarning("Add Purchase", "Please add at least one product to the purchase.");
            return;
        }

        double totalAmount = calculateTotalAmount();
        double totalQuantity = calculateTotalQuantity();

        PurchaseTransaction purchase = new PurchaseTransaction();

        purchase.setNo(purchaseTable.getItems().size() + 1);

        purchase.setSupplierName(addSupplierCombo.getValue());
        purchase.setEmployeeName(addEmployeeCombo.getValue());
        purchase.setPurchaseDate(addPurchaseDatePicker.getValue());

        purchase.setSupplierID(
                purchaseDAO.getSupplierIdByName(purchase.getSupplierName()));

        purchase.setEmployeeID(
                purchaseDAO.getEmployeeIdByName(purchase.getEmployeeName()));

        purchase.setSupplierType(
                purchaseDAO.getSupplierTypeByName(purchase.getSupplierName()));

        purchase.setSupplierEmail(
                purchaseDAO.getSupplierEmailByName(purchase.getSupplierName()));

        purchase.setItemsCount(addPurchaseDetailsTable.getItems().size());
        purchase.setTotalQuantity(totalQuantity);
        purchase.setTotalAmount(totalAmount);

        purchase.setItems(
                new ArrayList<>(addPurchaseDetailsTable.getItems()));

        saveStateForUndo();

        pendingAdds.add(purchase);
        purchaseTable.getItems().add(purchase);
        originalPurchases.add(purchase);

        purchasesFoundLabel.setText(
                String.valueOf(purchaseTable.getItems().size()));

        loadPurchaseFilters();

        redoStack.clear();
        clearAddForm();
    }

    private double calculateTotalAmount() {
        return addPurchaseDetailsTable.getItems()
                .stream()
                .mapToDouble(PurchaseDetails::getSubtotal)
                .sum();
    }

    private double calculateTotalQuantity() {
        return addPurchaseDetailsTable.getItems()
                .stream()
                .mapToDouble(PurchaseDetails::getQuantity)
                .sum();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validateAddPurchaseForm() {

        boolean valid = true;

        valid &= validateComboBox(addSupplierCombo, supplierWarningLabel);
        valid &= validateComboBox(addEmployeeCombo, employeeWarningLabel);
        valid &= validateDatePicker(addPurchaseDatePicker, purchaseDateWarningLabel);

        if (addPurchaseDetailsTable.getItems().isEmpty()) {
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

    private boolean validateNumberField(TextField field, Label warningLabel) {
        boolean valid = true;

        try {
            double value = Double.parseDouble(field.getText().trim());
            valid = value > 0;
        } catch (Exception e) {
            valid = false;
        }

        field.getStyleClass().remove("validation-error");

        if (!valid) {
            field.getStyleClass().add("validation-error");
        }

        warningLabel.setVisible(!valid);
        warningLabel.setManaged(!valid);

        return valid;
    }

    private void setupLiveValidation() {

        addSupplierCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateComboBox(addSupplierCombo, supplierWarningLabel);
            }
        });

        addEmployeeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateComboBox(addEmployeeCombo, employeeWarningLabel);
            }
        });

        addPurchaseDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateDatePicker(addPurchaseDatePicker, purchaseDateWarningLabel);
            }
        });

        addProductCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateComboBox(addProductCombo, productWarningLabel);
            }
        });

        addWarehouseCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateComboBox(addWarehouseCombo, warehouseWarningLabel);
            }
        });

        addQuantityField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateNumberField(addQuantityField, quantityWarningLabel);
            }
        });

        addPriceField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateNumberField(addPriceField, priceWarningLabel);
            }
        });
    }

    private void updatePurchaseSummary() {

        double totalAmount = calculateTotalAmount();
        double totalQuantity = calculateTotalQuantity();

        addTotalAmountLabel.setText(
                String.format("$%,.0f", totalAmount));

        addTotalQuantityLabel.setText(
                String.format("%.0f", totalQuantity));
    }

    private void clearAddForm() {

        clearingForm = true;

        addSupplierCombo.getSelectionModel().clearSelection();
        addEmployeeCombo.getSelectionModel().clearSelection();
        addPurchaseDatePicker.setValue(LocalDate.now());

        addProductCombo.getSelectionModel().clearSelection();
        addWarehouseCombo.getSelectionModel().clearSelection();

        addQuantityField.clear();
        addPriceField.clear();

        selectedProductStockLabel.setText("0");

        addPurchaseDetailsTable.getItems().clear();

        addTotalQuantityLabel.setText("0");
        addTotalAmountLabel.setText("$0.00");

        clearingForm = false;

        hideAllWarnings();
        removeAllValidationErrors();

        purchaseBeingUpdated = null;
        purchaseItemBeingEdited = null;
        addItemToPurchaseBtn.setText("✚ ADD ITEM");
        formTitleLabel.setText("✚ ADD PURCHASE");
        savePurchaseBtn.setText("✚  ADD PURCHASE");
    }

    private void hideAllWarnings() {

        Label[] warnings = {
                supplierWarningLabel,
                employeeWarningLabel,
                purchaseDateWarningLabel,
                productWarningLabel,
                warehouseWarningLabel,
                quantityWarningLabel,
                priceWarningLabel
        };

        for (Label warning : warnings) {
            warning.setVisible(false);
            warning.setManaged(false);
        }
    }

    private void removeAllValidationErrors() {

        addSupplierCombo.getStyleClass().remove("validation-error");
        addEmployeeCombo.getStyleClass().remove("validation-error");
        addPurchaseDatePicker.getStyleClass().remove("validation-error");

        addProductCombo.getStyleClass().remove("validation-error");
        addWarehouseCombo.getStyleClass().remove("validation-error");

        addQuantityField.getStyleClass().remove("validation-error");
        addPriceField.getStyleClass().remove("validation-error");
    }

    private void openUpdateForm(PurchaseTransaction purchase) {

        purchaseBeingUpdated = purchase;

        clearAddForm();

        showOnlyPanel(addPurchasePanel);

        formTitleLabel.setText("✏ UPDATE PURCHASE");
        savePurchaseBtn.setText("UPDATE");

        addSupplierCombo.setValue(purchase.getSupplierName());
        addEmployeeCombo.setValue(purchase.getEmployeeName());

        if (purchase.getPurchaseDate() != null) {
            addPurchaseDatePicker.setValue(purchase.getPurchaseDate());
        }

        List<PurchaseDetails> details;

        if (purchase.getItems() != null && !purchase.getItems().isEmpty()) {
            details = purchase.getItems();
        } else {
            details = purchaseDAO.getPurchaseDetails(purchase.getPurchaseID());
        }

        addPurchaseDetailsTable.setItems(
                FXCollections.observableArrayList(details));

        updatePurchaseSummary();

        hideAllWarnings();
        removeAllValidationErrors();
    }

    private void updatePurchase() {

        if (!validateAddPurchaseForm()) {
            return;
        }

        if (addPurchaseDetailsTable.getItems().isEmpty()) {
            showWarning("Update Purchase", "Please add at least one product to the purchase.");
            return;
        }

        saveStateForUndo();

        double totalAmount = calculateTotalAmount();
        double totalQuantity = calculateTotalQuantity();

        purchaseBeingUpdated.setSupplierName(addSupplierCombo.getValue());
        purchaseBeingUpdated.setEmployeeName(addEmployeeCombo.getValue());
        purchaseBeingUpdated.setPurchaseDate(addPurchaseDatePicker.getValue());

        purchaseBeingUpdated.setSupplierID(
                purchaseDAO.getSupplierIdByName(purchaseBeingUpdated.getSupplierName()));

        purchaseBeingUpdated.setEmployeeID(
                purchaseDAO.getEmployeeIdByName(purchaseBeingUpdated.getEmployeeName()));

        purchaseBeingUpdated.setSupplierType(
                purchaseDAO.getSupplierTypeByName(purchaseBeingUpdated.getSupplierName()));

        purchaseBeingUpdated.setSupplierEmail(
                purchaseDAO.getSupplierEmailByName(purchaseBeingUpdated.getSupplierName()));

        purchaseBeingUpdated.setItemsCount(addPurchaseDetailsTable.getItems().size());
        purchaseBeingUpdated.setTotalQuantity(totalQuantity);
        purchaseBeingUpdated.setTotalAmount(totalAmount);

        purchaseBeingUpdated.setItems(
                new ArrayList<>(addPurchaseDetailsTable.getItems()));

        if (!pendingUpdates.contains(purchaseBeingUpdated)) {
            pendingUpdates.add(purchaseBeingUpdated);
        }

        purchaseTable.refresh();

        purchaseBeingUpdated = null;

        clearAddForm();

        addPurchasePanel.setVisible(false);
        addPurchasePanel.setManaged(false);

        redoStack.clear();
    }

    private void saveStateForUndo() {
        undoStack.push(new ArrayList<>(purchaseTable.getItems()));
    }

    private void applyFilters() {

        String supplier = supplierCombo.getValue();
        String supplierType = supplierTypeCombo.getValue();
        String employee = employeeCombo.getValue();
        String product = productCombo.getValue();
        String category = categoryCombo.getValue();
        String warehouse = warehouseCombo.getValue();

        double minTotal = parseDoubleOrDefault(minTotalField.getText(), 0);
        double maxTotal = parseDoubleOrDefault(maxTotalField.getText(), Double.MAX_VALUE);

        double minQuantity = parseDoubleOrDefault(minQuantityField.getText(), 0);
        double maxQuantity = parseDoubleOrDefault(maxQuantityField.getText(), Double.MAX_VALUE);

        LocalDate fromDate = fromPurchaseDatePicker.getValue();
        LocalDate toDate = toPurchaseDatePicker.getValue();

        List<PurchaseTransaction> filtered = originalPurchases.stream()

                .filter(p -> supplier == null
                        || supplier.equals("All Suppliers")
                        || supplier.equals(p.getSupplierName()))

                .filter(p -> supplierType == null
                        || supplierType.equals("All Types")
                        || supplierType.equals(p.getSupplierType()))

                .filter(p -> employee == null
                        || employee.equals("All Employees")
                        || employee.equals(p.getEmployeeName()))

                .filter(p -> p.getTotalAmount() >= minTotal
                        && p.getTotalAmount() <= maxTotal)

                .filter(p -> p.getTotalQuantity() >= minQuantity
                        && p.getTotalQuantity() <= maxQuantity)

                .filter(p -> matchesPurchaseDetailsFilter(
                        p.getPurchaseID(), product, category, warehouse))

                .filter(p -> {

                    if (fromDate == null && toDate == null) {
                        return true;
                    }

                    if (p.getPurchaseDate() == null) {
                        return false;
                    }

                    LocalDate purchaseDate = p.getPurchaseDate();

                    boolean afterFrom = fromDate == null
                            || !purchaseDate.isBefore(fromDate);

                    boolean beforeTo = toDate == null
                            || !purchaseDate.isAfter(toDate);

                    return afterFrom && beforeTo;
                })

                .toList();

        purchaseTable.setItems(
                FXCollections.observableArrayList(filtered));

        purchasesFoundLabel.setText(
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

    private boolean matchesPurchaseDetailsFilter(
            int purchaseID,
            String product,
            String category,
            String warehouse) {

        if ((product == null || product.equals("All Products"))
                && (category == null || category.equals("All Categories"))
                && (warehouse == null || warehouse.equals("All Warehouses"))) {
            return true;
        }

        List<PurchaseDetails> details = purchaseDAO.getPurchaseDetails(purchaseID);

        return details.stream().anyMatch(d -> (product == null || product.equals("All Products")
                || product.equals(d.getProductName()))

                && (category == null || category.equals("All Categories")
                        || category.equals(d.getCategoryName()))

                && (warehouse == null || warehouse.equals("All Warehouses")
                        || warehouse.equals(d.getWarehouseName())));
    }

    private void resetFilters() {

        supplierCombo.getSelectionModel().selectFirst();
        supplierTypeCombo.getSelectionModel().selectFirst();
        employeeCombo.getSelectionModel().selectFirst();
        productCombo.getSelectionModel().selectFirst();
        categoryCombo.getSelectionModel().selectFirst();
        warehouseCombo.getSelectionModel().selectFirst();

        minTotalField.clear();
        maxTotalField.clear();
        minQuantityField.clear();
        maxQuantityField.clear();

        fromPurchaseDatePicker.setValue(null);
        toPurchaseDatePicker.setValue(null);

        purchaseTable.setItems(
                FXCollections.observableArrayList(originalPurchases));

        purchasesFoundLabel.setText(
                String.valueOf(originalPurchases.size()));

        refreshRowNumbers();
    }

    private void loadPurchaseFilters() {

        supplierCombo.getItems().clear();
        supplierCombo.getItems().add("All Suppliers");
        supplierCombo.getItems().addAll(
                purchaseDAO.getSupplierNames());
        supplierCombo.getSelectionModel().selectFirst();

        supplierTypeCombo.getItems().setAll(
                "All Types",
                "Local",
                "International");
        supplierTypeCombo.getSelectionModel().selectFirst();

        employeeCombo.getItems().clear();
        employeeCombo.getItems().add("All Employees");
        employeeCombo.getItems().addAll(
                purchaseDAO.getEmployeeNames());
        employeeCombo.getSelectionModel().selectFirst();

        productCombo.getItems().clear();
        productCombo.getItems().add("All Products");
        productCombo.getItems().addAll(
                purchaseDAO.getProductNames());
        productCombo.getSelectionModel().selectFirst();

        categoryCombo.getItems().clear();
        categoryCombo.getItems().add("All Categories");
        categoryCombo.getItems().addAll(
                purchaseDAO.getCategoryNames());
        categoryCombo.getSelectionModel().selectFirst();

        warehouseCombo.getItems().clear();
        warehouseCombo.getItems().add("All Warehouses");
        warehouseCombo.getItems().addAll(
                purchaseDAO.getWarehouseNames());
        warehouseCombo.getSelectionModel().selectFirst();

        purchasesFoundLabel.setText(
                String.valueOf(purchaseTable.getItems().size()));
    }

    private void showOnlyPanel(VBox panel) {

        filterPanel.setVisible(false);
        filterPanel.setManaged(false);

        addPurchasePanel.setVisible(false);
        addPurchasePanel.setManaged(false);

        PurchaseDetailsPanel.setVisible(false);
        PurchaseDetailsPanel.setManaged(false);

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

        redoStack.push(new ArrayList<>(purchaseTable.getItems()));

        List<PurchaseTransaction> previousState = undoStack.pop();

        purchaseTable.setItems(
                FXCollections.observableArrayList(previousState));

        refreshRowNumbers();
    }

    private void redoAction() {

        if (redoStack.isEmpty()) {
            return;
        }

        undoStack.push(new ArrayList<>(purchaseTable.getItems()));

        List<PurchaseTransaction> nextState = redoStack.pop();

        purchaseTable.setItems(
                FXCollections.observableArrayList(nextState));

        refreshRowNumbers();
    }

    private void resetTableChanges() {

        pendingAdds.clear();
        pendingUpdates.clear();
        pendingDeletes.clear();

        undoStack.clear();
        redoStack.clear();

        loadPurchasesTable();
        loadPurchaseStats();
        loadPurchaseFilters();

        clearAddForm();

        addPurchasePanel.setVisible(false);
        addPurchasePanel.setManaged(false);

        PurchaseDetailsPanel.setVisible(false);
        PurchaseDetailsPanel.setManaged(false);
    }

    private void saveAllChanges() {

        try {

            for (PurchaseTransaction purchase : pendingAdds) {
                purchaseDAO.insertPurchase(purchase);
            }

            for (PurchaseTransaction purchase : pendingUpdates) {
                purchaseDAO.updatePurchase(purchase);
            }

            for (PurchaseTransaction purchase : pendingDeletes) {
                purchaseDAO.deletePurchase(purchase.getPurchaseID());
            }

            pendingAdds.clear();
            pendingUpdates.clear();
            pendingDeletes.clear();

            undoStack.clear();
            redoStack.clear();

            loadPurchasesTable();
            loadPurchaseStats();
            loadPurchaseFilters();

            clearAddForm();

            addPurchasePanel.setVisible(false);
            addPurchasePanel.setManaged(false);

            PurchaseDetailsPanel.setVisible(false);
            PurchaseDetailsPanel.setManaged(false);

            System.out.println("All purchase changes saved successfully.");

        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save purchase changes.");
            alert.showAndWait();
        }
    }

    private void exportPurchasesToExcel() {

        try {
            File exportFolder = new File("src/main/resources/exports");

            if (!exportFolder.exists()) {
                exportFolder.mkdirs();
            }

            String fileName = "purchases_" + java.time.LocalDate.now() + ".xlsx";
            File file = new File(exportFolder, fileName);

            Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Purchases");

            String[] headers = {
                    "Purchase ID",
                    "Supplier",
                    "Supplier Type",
                    "Employee",
                    "Purchase Date",
                    "Items Count",
                    "Total Quantity",
                    "Total Amount"
            };

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowIndex = 1;

            for (PurchaseTransaction p : purchaseTable.getItems()) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(p.getPurchaseID());
                row.createCell(1).setCellValue(p.getSupplierName());
                row.createCell(2).setCellValue(p.getSupplierType());
                row.createCell(3).setCellValue(p.getEmployeeName());

                row.createCell(4).setCellValue(
                        p.getPurchaseDate() == null
                                ? ""
                                : p.getPurchaseDate().toString());

                row.createCell(5).setCellValue(p.getItemsCount());
                row.createCell(6).setCellValue(p.getTotalQuantity());
                row.createCell(7).setCellValue(p.getTotalAmount());
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
                    "Purchases exported successfully!\n\nSaved to:\n"
                            + file.getAbsolutePath());
            alert.showAndWait();

        } catch (Exception ex) {
            ex.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to export purchases file.");
            alert.showAndWait();
        }
    }

}
