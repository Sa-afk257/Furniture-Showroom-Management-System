package com.furniture.ui;


import com.furniture.model.Product;
import com.furniture.model.PurchaseDetails;
import com.furniture.model.PurchaseTransaction;
import com.furniture.model.Supplier;
import com.furniture.dao.SupplierDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SupplierController {

    /* ===================== DAO ===================== */
    private final SupplierDAO supplierDAO = new SupplierDAO();

    /* ===================== DATA ===================== */

    private final Stack<List<Supplier>> undoStack = new Stack<>();
    private final Stack<List<Supplier>> redoStack = new Stack<>();

    private final List<Supplier> pendingAdds = new ArrayList<>();
    private final List<Supplier> pendingUpdates = new ArrayList<>();
    private final List<Supplier> pendingDeletes = new ArrayList<>();

    private Supplier supplierBeingUpdated = null;

    /* ===================== STATS CARDS ===================== */
    @FXML
    private Label totalSuppliersLabel;
    @FXML
    private Label localSuppliersLabel;

    @FXML
    private Label topSupplierNameLabel;
    @FXML
    private Label topSupplierAmountLabel;

    @FXML
    private Label topSupplierProductsNameLabel;
    @FXML
    private Label topSupplierProductsValueLabel;

    @FXML
    private Label suppliedProductsLabel;

    /* ===================== TOOLBAR ===================== */
    @FXML
    private TextField txtSearch;

    @FXML
    private Button filterToggleBtn;
    @FXML
    private Button addSupplierBtn;
    @FXML
    private Button exportBtn;

    /* ===================== MAIN TABLE ===================== */
    @FXML
    private TableView<Supplier> SupplierTable;

    @FXML
    private TableColumn<Supplier, Integer> colNo;
    @FXML
    private TableColumn<Supplier, Integer> colSupplierID;
    @FXML
    private TableColumn<Supplier, String> colFullName;
    @FXML
    private TableColumn<Supplier, String> colSupplierType;
    @FXML
    private TableColumn<Supplier, String> colEmail;
    @FXML
    private TableColumn<Supplier, String> colPhone;
    @FXML
    private TableColumn<Supplier, String> colCity;
    @FXML
    private TableColumn<Supplier, Integer> colProductsCount;
    @FXML
    private TableColumn<Supplier, Integer> colPurchasesCount;
    @FXML
    private TableColumn<Supplier, Double> colTotalPurchasedAmount;
    @FXML
    private TableColumn<Supplier, String> colLastPurchaseDate;
    @FXML
    private TableColumn<Supplier, String> colStatus;
    @FXML
    private TableColumn<Supplier, Void> colAction;

    /* ===================== FILTER PANEL ===================== */
    @FXML
    private VBox filterPanel;

    @FXML
    private ComboBox<String> supplierTypeFilterCombo;
    @FXML
    private ComboBox<String> cityFilterCombo;
    @FXML
    private ComboBox<String> productRelationFilterCombo;
    @FXML
    private ComboBox<String> purchaseActivityFilterCombo;
    @FXML
    private TextField minTotalAmountField;
    @FXML
    private TextField maxTotalAmountField;
    @FXML
    private DatePicker fromLastPurchaseDatePicker;
    @FXML
    private DatePicker toLastPurchaseDatePicker;
    @FXML
    private ComboBox<String> supplierStatusFilterCombo;

    @FXML
    private Button applyFiltersBtn;
    @FXML
    private Button resetFiltersBtn;
    @FXML
    private Label recordsFoundLabel;

    /* ===================== ADD / UPDATE PANEL ===================== */
    @FXML
    private VBox addSupplierPanel;

    @FXML
    private Label supplierFormTitleLabel;

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField middleInitialField;
    @FXML
    private TextField lastNameField;

    @FXML
    private ComboBox<String> supplierTypeCombo;

    @FXML
    private TextField cityField;
    @FXML
    private TextField townField;
    @FXML
    private TextField areaField;
    @FXML
    private TextField streetField;
    @FXML
    private TextField buildingField;

    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;

    @FXML
    private Button saveSupplierBtn;
    @FXML
    private Button cancelAddBtn;

    /* ===================== DETAILS PANEL ===================== */
    @FXML
    private VBox supplierDetailsPanel;

    @FXML
    private Label detailsSupplierNameLabel;
    @FXML
    private Label detailsSupplierTypeBadgeLabel;
    @FXML
    private Label detailsSupplierIdLabel;
    @FXML
    private Label detailsSupplierCityLabel;
    @FXML
    private Label detailsSupplierEmailLabel;
    @FXML
    private Label detailsSupplierPhoneLabel;
    @FXML
    private Label detailsSupplierTownAreaLabel;
    @FXML
    private Label detailsSupplierStreetBuildingLabel;

    /* ===== SUPPLIER SUMMARY ===== */
    @FXML
    private Label detailsProductsCountLabel;
    @FXML
    private Label detailsSupplierPurchasesCountLabel;
    @FXML
    private Label detailsSupplierTotalAmountLabel;
    @FXML
    private Label detailsSupplierTotalQuantityLabel;
    @FXML
    private Label detailsSupplierLastPurchaseDateLabel;
    @FXML
    private Label detailsSupplierAvgPurchaseLabel;

    @FXML
    private Button closeDetailsBtn;

    /* ===================== RELATED TABLES ===================== */

    @FXML
    private TableView<Product> supplierProductsTable;

    @FXML
    private TableColumn<Product, Integer> colSupProductId;

    @FXML
    private TableColumn<Product, String> colSupProductName;

    @FXML
    private TableColumn<Product, String> colSupProductCategory;

    @FXML
    private TableColumn<Product, String> colSupProductStatus;

    @FXML
    private TableView<PurchaseTransaction> supplierPurchasesTable;

    @FXML
    private TableColumn<PurchaseTransaction, Integer> colSupPurchaseId;

    @FXML
    private TableColumn<PurchaseTransaction, LocalDate> colSupPurchaseDate;

    @FXML
    private TableColumn<PurchaseTransaction, String> colSupPurchaseEmployee;

    @FXML
    private TableColumn<PurchaseTransaction, Double> colSupPurchaseAmount;

    @FXML
    private TableView<PurchaseDetails> supplierPurchasedProductsTable;

    @FXML
    private TableColumn<PurchaseDetails, String> colSupSummaryProduct;

    @FXML
    private TableColumn<PurchaseDetails, Double> colSupSummaryQuantity;

    @FXML
    private TableColumn<PurchaseDetails, Double> colSupSummaryCost;

    /* ===================== TABLE ACTIONS ===================== */

    @FXML
    private Button undoBtn;

    @FXML
    private Button redoBtn;

    @FXML
    private Button resetBtn;

    @FXML
    private Button saveAllBtn;

    /* ===================== WARNING LABELS ===================== */

    @FXML
    private Label supplierTypeWarningLabel;
    @FXML
    private Label firstNameWarningLabel;
    @FXML
    private Label lastNameWarningLabel;
    @FXML
    private Label cityWarningLabel;
    @FXML
    private Label townWarningLabel;
    @FXML
    private Label areaWarningLabel;
    @FXML
    private Label streetWarningLabel;
    @FXML
    private Label buildingWarningLabel;

    @FXML
    private Label emailWarningLabel;
    @FXML
    private Label phoneWarningLabel;

    private ObservableList<Supplier> originalSuppliers = FXCollections.observableArrayList();

    private boolean clearingForm = false;


    @FXML
    private void initialize() {

        setupTable();

        setupActionColumn();

        setupDetailsTables();

        SupplierTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        loadSuppliers();

        loadStats();

        loadCombos();

        setupLiveValidation();

        setupButtons();

        setupSearch();

        setupFilters();

        closeAllPanels();
    }

    private void setupTable() {

        colNo.setCellValueFactory(
                new PropertyValueFactory<>("no"));

        colSupplierID.setCellValueFactory(
                new PropertyValueFactory<>("supplierID"));

        colFullName.setCellValueFactory(
                new PropertyValueFactory<>("fullName"));

        colSupplierType.setCellValueFactory(
                new PropertyValueFactory<>("supplier_type"));

        colEmail.setCellValueFactory(
                new PropertyValueFactory<>("email"));

        colPhone.setCellValueFactory(
                new PropertyValueFactory<>("phone"));

        colCity.setCellValueFactory(
                new PropertyValueFactory<>("city"));

        colProductsCount.setCellValueFactory(
                new PropertyValueFactory<>("productsCount"));

        colPurchasesCount.setCellValueFactory(
                new PropertyValueFactory<>("purchasesCount"));

        colTotalPurchasedAmount.setCellValueFactory(
                new PropertyValueFactory<>("totalPurchasedAmount"));

        colTotalPurchasedAmount.setCellFactory(column -> new TableCell<>() {

            @Override
            protected void updateItem(Double item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%,.2f", item));
                }
            }
        });

        colLastPurchaseDate.setCellValueFactory(
                new PropertyValueFactory<>("lastPurchaseDate"));

        colStatus.setCellValueFactory(
                new PropertyValueFactory<>("status"));

        colStatus.setCellFactory(column -> new TableCell<>() {

            @Override
            protected void updateItem(String item, boolean empty) {

                super.updateItem(item, empty);

                getStyleClass().removeAll(
                        "status-active",
                        "status-inactive",
                        "status-top");

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);

                    switch (item) {
                        case "Active" ->
                            getStyleClass().add("status-active");

                        case "New" ->
                            getStyleClass().add("status-inactive");

                        case "Top" ->
                            getStyleClass().add("status-top");
                    }
                }
            }
        });
    }

    private void saveStateForUndo() {

        undoStack.push(new ArrayList<>(SupplierTable.getItems()));
    }

    private void undoAction() {

        if (undoStack.isEmpty()) {
            return;
        }

        redoStack.push(new ArrayList<>(SupplierTable.getItems()));

        List<Supplier> previousState = undoStack.pop();

        SupplierTable.setItems(FXCollections.observableArrayList(previousState));

        recordsFoundLabel.setText(String.valueOf(previousState.size()));

        refreshRowNumbers();
    }

    private void redoAction() {

        if (redoStack.isEmpty()) {
            return;
        }

        undoStack.push(new ArrayList<>(SupplierTable.getItems()));

        List<Supplier> nextState = redoStack.pop();

        SupplierTable.setItems(FXCollections.observableArrayList(nextState));

        recordsFoundLabel.setText(String.valueOf(nextState.size()));

        refreshRowNumbers();
    }

    private void resetTableChanges() {

        pendingAdds.clear();

        pendingUpdates.clear();

        pendingDeletes.clear();

        undoStack.clear();

        redoStack.clear();

        loadSuppliers();

        loadStats();

        clearForm();

        closeAllPanels();
    }

    private void openUpdateForm(Supplier supplier) {

        supplierBeingUpdated = supplier;

        supplierFormTitleLabel.setText("✏ UPDATE SUPPLIER");
        saveSupplierBtn.setText("UPDATE SUPPLIER");

        firstNameField.setText(supplier.getFirstName());
        middleInitialField.setText(supplier.getMiddelInitial());
        lastNameField.setText(supplier.getLastName());

        supplierTypeCombo.setValue(supplier.getSupplier_type());

        cityField.setText(supplier.getCity());
        townField.setText(supplier.getTown());
        areaField.setText(supplier.getArea());
        streetField.setText(supplier.getStreet());
        buildingField.setText(supplier.getBuilding());

        emailField.setText(supplier.getEmail());

        if (supplier.getSupplier_Phone() != null
                && !supplier.getSupplier_Phone().isEmpty()) {

            phoneField.setText(supplier.getSupplier_Phone().get(0));
        } else {
            phoneField.clear();
        }

        showOnlyPanel(addSupplierPanel);
    }

    private boolean validateForm() {

        boolean valid = true;

        valid &= validateTextField(firstNameField, firstNameWarningLabel);
        valid &= validateTextField(lastNameField, lastNameWarningLabel);

        valid &= validateComboBox(supplierTypeCombo, supplierTypeWarningLabel);

        valid &= validateTextField(cityField, cityWarningLabel);
        valid &= validateTextField(townField, townWarningLabel);
        valid &= validateTextField(areaField, areaWarningLabel);
        valid &= validateTextField(streetField, streetWarningLabel);
        valid &= validateTextField(buildingField, buildingWarningLabel);

        valid &= validateEmailField(emailField, emailWarningLabel);
        valid &= validateTextField(phoneField, phoneWarningLabel);

        return valid;
    }

    private boolean validateTextField(TextField field, Label warningLabel) {

        boolean valid = field.getText() != null && !field.getText().trim().isEmpty();

        field.getStyleClass().remove("validation-error");

        if (!valid) {
            field.getStyleClass().add("validation-error");
        }

        warningLabel.setVisible(!valid);
        warningLabel.setManaged(!valid);

        return valid;
    }

    private boolean validateComboBox(ComboBox<?> comboBox, Label warningLabel) {

        boolean valid = comboBox.getValue() != null;

        comboBox.getStyleClass().remove("validation-error");

        if (!valid) {
            comboBox.getStyleClass().add("validation-error");
        }

        warningLabel.setVisible(!valid);
        warningLabel.setManaged(!valid);

        return valid;
    }

    private boolean validateEmailField(TextField field, Label warningLabel) {

        String email = field.getText() == null ? "" : field.getText().trim();

        boolean valid = email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

        field.getStyleClass().remove("validation-error");

        if (!valid) {
            field.getStyleClass().add("validation-error");
        }

        warningLabel.setVisible(!valid);
        warningLabel.setManaged(!valid);

        return valid;
    }

    private void hideAllWarnings() {

        Label[] warnings = {
                firstNameWarningLabel,
                lastNameWarningLabel,
                supplierTypeWarningLabel,
                cityWarningLabel,
                townWarningLabel,
                areaWarningLabel,
                streetWarningLabel,
                buildingWarningLabel,
                emailWarningLabel,
                phoneWarningLabel
        };

        for (Label warning : warnings) {
            warning.setVisible(false);
            warning.setManaged(false);
        }
    }

    private void removeAllValidationErrors() {

        firstNameField.getStyleClass().remove("validation-error");
        lastNameField.getStyleClass().remove("validation-error");

        supplierTypeCombo.getStyleClass().remove("validation-error");

        cityField.getStyleClass().remove("validation-error");
        townField.getStyleClass().remove("validation-error");
        areaField.getStyleClass().remove("validation-error");
        streetField.getStyleClass().remove("validation-error");
        buildingField.getStyleClass().remove("validation-error");

        emailField.getStyleClass().remove("validation-error");
        phoneField.getStyleClass().remove("validation-error");
    }

    private void clearForm() {

        clearingForm = true;

        firstNameField.clear();
        middleInitialField.clear();
        lastNameField.clear();

        supplierTypeCombo.setValue(null);

        cityField.clear();
        townField.clear();
        areaField.clear();
        streetField.clear();
        buildingField.clear();

        emailField.clear();
        phoneField.clear();

        supplierBeingUpdated = null;

        supplierFormTitleLabel.setText("✚ ADD SUPPLIER");
        saveSupplierBtn.setText("✚ SAVE SUPPLIER");

        hideAllWarnings();
        removeAllValidationErrors();

        clearingForm = false;
    }

    private void setupLiveValidation() {

        firstNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateTextField(firstNameField, firstNameWarningLabel);
        });

        lastNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateTextField(lastNameField, lastNameWarningLabel);
        });

        supplierTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateComboBox(supplierTypeCombo, supplierTypeWarningLabel);
        });

        cityField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateTextField(cityField, cityWarningLabel);
        });

        townField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateTextField(townField, townWarningLabel);
        });

        areaField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateTextField(areaField, areaWarningLabel);
        });

        streetField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateTextField(streetField, streetWarningLabel);
        });

        buildingField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateTextField(buildingField, buildingWarningLabel);
        });

        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateEmailField(emailField, emailWarningLabel);
        });

        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateTextField(phoneField, phoneWarningLabel);
        });
    }

    private void loadSuppliers() {

        List<Supplier> supplierList = supplierDAO.getAllSuppliersForTable();

        for (int i = 0; i < supplierList.size(); i++) {

            Supplier supplier = supplierList.get(i);
            supplier.setNo(i + 1);

            if (supplier.getStatus() == null || supplier.getStatus().isBlank()) {

                if (supplier.getPurchasesCount() > 0 || supplier.getProductsCount() > 0) {
                    supplier.setStatus("Active");
                } else {
                    supplier.setStatus("New");
                }
            }
        }

        originalSuppliers.clear();
        originalSuppliers.addAll(supplierList);

        SupplierTable.setItems(
                FXCollections.observableArrayList(supplierList));

        recordsFoundLabel.setText(
                String.valueOf(supplierList.size()));

        refreshRowNumbers();
    }

    private void loadStats() {

        SupplierDAO.SupplierStats stats = supplierDAO.getSupplierStats();

        totalSuppliersLabel.setText(
                String.valueOf(stats.getTotalSuppliers()));

        localSuppliersLabel.setText(
                String.valueOf(stats.getLocalSuppliers()));

        topSupplierNameLabel.setText(
                stats.getTopSupplierName());

        topSupplierAmountLabel.setText(
                String.format("$%,.2f", stats.getTopSupplierAmount()));

        topSupplierProductsNameLabel.setText(
                stats.getTopSupplierProductsName());

        topSupplierProductsValueLabel.setText(
                stats.getTopSupplierProductsCount() + " Products");

        suppliedProductsLabel.setText(
                String.valueOf(stats.getSuppliedProducts()));
    }

    private void loadCombos() {

        supplierTypeCombo.getItems().setAll(
                "Local",
                "International");

        supplierTypeFilterCombo.getItems().setAll(
                "Local",
                "International");

        cityFilterCombo.getItems().setAll(
                supplierDAO.getCities());

        productRelationFilterCombo.getItems().setAll(
                "Has Products",
                "No Products");

        purchaseActivityFilterCombo.getItems().setAll(
                "Has Purchases",
                "No Purchases");

        supplierStatusFilterCombo.getItems().setAll(
                "Active",
                "New",
                "Top");

    }

    private void setupButtons() {

        addSupplierBtn.setOnAction(e -> {

            if (addSupplierPanel.isVisible()) {

                addSupplierPanel.setVisible(false);
                addSupplierPanel.setManaged(false);

                return;
            }

            supplierBeingUpdated = null;

            clearForm();

            supplierFormTitleLabel.setText("✚ ADD SUPPLIER");
            saveSupplierBtn.setText("✚ SAVE SUPPLIER");

            showOnlyPanel(addSupplierPanel);
        });

        cancelAddBtn.setOnAction(e -> {

            clearForm();

            addSupplierPanel.setVisible(false);
            addSupplierPanel.setManaged(false);
        });

        filterToggleBtn.setOnAction(e -> {

            if (filterPanel.isVisible()) {

                filterPanel.setVisible(false);
                filterPanel.setManaged(false);

            } else {

                showOnlyPanel(filterPanel);
            }
        });

        saveSupplierBtn.setOnAction(e -> {

            if (supplierBeingUpdated == null) {

                addSupplier();

            } else {

                updateSupplier();
            }
        });

        closeDetailsBtn.setOnAction(e -> {
            supplierDetailsPanel.setVisible(false);
            supplierDetailsPanel.setManaged(false);
        });

        saveAllBtn.setOnAction(e -> saveAllChanges());

        exportBtn.setOnAction(e -> exportSuppliersToExcel());

        undoBtn.setOnAction(e -> undoAction());

        redoBtn.setOnAction(e -> redoAction());

        resetBtn.setOnAction(e -> resetTableChanges());
    }

    private void refreshRowNumbers() {

        for (int i = 0; i < SupplierTable.getItems().size(); i++) {

            SupplierTable.getItems().get(i).setNo(i + 1);
        }

        SupplierTable.refresh();
    }

    private void setupActionColumn() {

        colAction.setCellFactory(column -> new TableCell<>() {

            private final Button viewBtn = new Button("👁");
            private final Button editBtn = new Button("✎");
            private final Button deleteBtn = new Button("🗑");

            private final HBox box = new HBox(6, viewBtn, editBtn, deleteBtn);

            {
                box.setAlignment(Pos.CENTER);

                viewBtn.getStyleClass().add("table-show-btn");
                editBtn.getStyleClass().add("table-edit-btn");
                deleteBtn.getStyleClass().add("table-delete-btn");

                viewBtn.setOnAction(e -> {

                    Supplier supplier = getTableView().getItems().get(getIndex());

                    openSupplierDetails(supplier);
                });

                editBtn.setOnAction(e -> {

                    Supplier supplier = getTableView().getItems().get(getIndex());

                    openUpdateForm(supplier);
                });

                deleteBtn.setOnAction(e -> {

                    Supplier supplier = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Supplier");
                    alert.setHeaderText(null);
                    alert.setContentText(
                            "Are you sure you want to delete supplier record for "
                                    + supplier.getFullName() + "?");

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isEmpty() || result.get() != ButtonType.OK) {
                        return;
                    }

                    saveStateForUndo();

                    pendingDeletes.add(supplier);

                    SupplierTable.getItems().remove(supplier);
                    originalSuppliers.remove(supplier);

                    recordsFoundLabel.setText(
                            String.valueOf(SupplierTable.getItems().size()));

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

    private void setupSearch() {

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {

            String keyword = newVal.toLowerCase().trim();

            if (keyword.isEmpty()) {

                SupplierTable.setItems(
                        FXCollections.observableArrayList(originalSuppliers));

                recordsFoundLabel.setText(
                        String.valueOf(originalSuppliers.size()));

                refreshRowNumbers();

                return;
            }

            List<Supplier> filtered = originalSuppliers.stream()
                    .filter(s -> contains(s.getFullName(), keyword)
                            || contains(s.getEmail(), keyword)
                            || contains(s.getSupplier_type(), keyword)
                            || contains(s.getCity(), keyword)
                            || contains(s.getPhone(), keyword)
                            || contains(String.valueOf(s.getSupplierID()), keyword))
                    .toList();

            SupplierTable.setItems(
                    FXCollections.observableArrayList(filtered));

            recordsFoundLabel.setText(
                    String.valueOf(filtered.size()));

            refreshRowNumbers();
        });
    }

    private String formatDate(LocalDate date) {

        if (date == null) {
            return "-";
        }

        return date.format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String emptyToDash(String value) {

        if (value == null || value.trim().isEmpty()) {
            return "-";
        }

        return value;
    }

    private boolean contains(String value, String keyword) {

        return value != null && value.toLowerCase().contains(keyword);
    }

    private double parseDoubleOrDefault(String text, double defaultValue) {

        try {
            return Double.parseDouble(text.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private void setupDetailsTables() {

        colSupProductId.setCellValueFactory(
                new PropertyValueFactory<>("productID"));

        colSupProductName.setCellValueFactory(
                new PropertyValueFactory<>("productName"));

        colSupProductCategory.setCellValueFactory(
                new PropertyValueFactory<>("categoryName"));

        colSupProductStatus.setCellValueFactory(
                new PropertyValueFactory<>("status"));

        colSupPurchaseId.setCellValueFactory(
                new PropertyValueFactory<>("purchaseID"));

        colSupPurchaseDate.setCellValueFactory(
                new PropertyValueFactory<>("purchaseDate"));

        colSupPurchaseEmployee.setCellValueFactory(
                new PropertyValueFactory<>("employeeName"));

        colSupPurchaseAmount.setCellValueFactory(
                new PropertyValueFactory<>("totalAmount"));

        colSupSummaryProduct.setCellValueFactory(
                new PropertyValueFactory<>("productName"));

        colSupSummaryQuantity.setCellValueFactory(
                new PropertyValueFactory<>("quantity"));

        colSupSummaryCost.setCellValueFactory(
                new PropertyValueFactory<>("subtotal"));
    }

    private void setupFilters() {

        applyFiltersBtn.setOnAction(e -> applyFilters());

        resetFiltersBtn.setOnAction(e -> resetFilters());
    }

    private void showOnlyPanel(VBox panel) {

        closeAllPanels();

        panel.setVisible(true);
        panel.setManaged(true);
    }

    private void closeAllPanels() {

        filterPanel.setVisible(false);
        filterPanel.setManaged(false);

        addSupplierPanel.setVisible(false);
        addSupplierPanel.setManaged(false);

        supplierDetailsPanel.setVisible(false);
        supplierDetailsPanel.setManaged(false);
    }

    private void addSupplier() {

        if (!validateForm()) {
            return;
        }

        saveStateForUndo();

        Supplier supplier = new Supplier();

        supplier.setNo(SupplierTable.getItems().size() + 1);

        supplier.setFirstName(firstNameField.getText().trim());
        supplier.setMiddelInitial(middleInitialField.getText().trim());
        supplier.setLastName(lastNameField.getText().trim());

        supplier.setSupplier_type(supplierTypeCombo.getValue());

        supplier.setCity(cityField.getText().trim());
        supplier.setTown(townField.getText().trim());
        supplier.setArea(areaField.getText().trim());
        supplier.setStreet(streetField.getText().trim());
        supplier.setBuilding(buildingField.getText().trim());

        supplier.setEmail(emailField.getText().trim());

        List<String> phones = new ArrayList<>();
        phones.add(phoneField.getText().trim());
        supplier.setSupplier_Phone(phones);

        supplier.setProductsCount(0);
        supplier.setPurchasesCount(0);
        supplier.setTotalPurchasedAmount(0.0);
        supplier.setLastPurchaseDate("-");
        supplier.setStatus("New");

        pendingAdds.add(supplier);

        SupplierTable.getItems().add(supplier);
        originalSuppliers.add(supplier);

        recordsFoundLabel.setText(
                String.valueOf(SupplierTable.getItems().size()));

        clearForm();

        loadStats();

        refreshRowNumbers();

        redoStack.clear();
    }

    private void updateSupplier() {

        if (!validateForm()) {
            return;
        }

        saveStateForUndo();

        supplierBeingUpdated.setFirstName(firstNameField.getText().trim());
        supplierBeingUpdated.setMiddelInitial(middleInitialField.getText().trim());
        supplierBeingUpdated.setLastName(lastNameField.getText().trim());

        supplierBeingUpdated.setSupplier_type(supplierTypeCombo.getValue());

        supplierBeingUpdated.setCity(cityField.getText().trim());
        supplierBeingUpdated.setTown(townField.getText().trim());
        supplierBeingUpdated.setArea(areaField.getText().trim());
        supplierBeingUpdated.setStreet(streetField.getText().trim());
        supplierBeingUpdated.setBuilding(buildingField.getText().trim());

        supplierBeingUpdated.setEmail(emailField.getText().trim());

        List<String> phones = new ArrayList<>();
        phones.add(phoneField.getText().trim());
        supplierBeingUpdated.setSupplier_Phone(phones);

        if (!pendingUpdates.contains(supplierBeingUpdated)) {
            pendingUpdates.add(supplierBeingUpdated);
        }

        SupplierTable.refresh();

        clearForm();

        addSupplierPanel.setVisible(false);
        addSupplierPanel.setManaged(false);

        loadStats();

        refreshRowNumbers();

        redoStack.clear();
    }

    private void openSupplierDetails(Supplier supplier) {

        detailsSupplierNameLabel.setText(
                emptyToDash(supplier.getFullName()));

        detailsSupplierTypeBadgeLabel.setText(
                emptyToDash(supplier.getSupplier_type()));

        detailsSupplierIdLabel.setText(
                String.valueOf(supplier.getSupplierID()));

        detailsSupplierCityLabel.setText(
                emptyToDash(supplier.getCity()));

        detailsSupplierEmailLabel.setText(
                emptyToDash(supplier.getEmail()));

        detailsSupplierPhoneLabel.setText(
                supplier.getSupplier_Phone() != null
                        && !supplier.getSupplier_Phone().isEmpty()
                                ? supplier.getSupplier_Phone().get(0)
                                : "-");

        detailsSupplierTownAreaLabel.setText(
                emptyToDash(supplier.getTown()) + " / "
                        + emptyToDash(supplier.getArea()));

        detailsSupplierStreetBuildingLabel.setText(
                emptyToDash(supplier.getStreet()) + " / "
                        + emptyToDash(supplier.getBuilding()));

        detailsProductsCountLabel.setText(
                String.valueOf(supplier.getProductsCount()));

        detailsSupplierPurchasesCountLabel.setText(
                String.valueOf(supplier.getPurchasesCount()));

        detailsSupplierTotalAmountLabel.setText(
                String.format("$%,.2f",
                        supplier.getTotalPurchasedAmount()));

        detailsSupplierTotalQuantityLabel.setText(
                String.valueOf(supplier.getTotalPurchasedQuantity()));

        detailsSupplierLastPurchaseDateLabel.setText(
                emptyToDash(supplier.getLastPurchaseDate()));

        detailsSupplierAvgPurchaseLabel.setText(
                String.format("$%,.2f",
                        supplier.getAveragePurchaseValue()));

        supplierProductsTable.setItems(
                FXCollections.observableArrayList(
                        supplierDAO.getProductsBySupplier(
                                supplier.getSupplierID())));

        supplierPurchasesTable.setItems(
                FXCollections.observableArrayList(
                        supplierDAO.getPurchasesBySupplier(
                                supplier.getSupplierID())));

        supplierPurchasedProductsTable.setItems(
                FXCollections.observableArrayList(
                        supplierDAO.getPurchasedProductsSummary(
                                supplier.getSupplierID())));

        showOnlyPanel(supplierDetailsPanel);
    }

    private void saveAllChanges() {

        try {

            for (Supplier supplier : pendingAdds) {
                supplierDAO.insertSupplier(supplier);
            }

            for (Supplier supplier : pendingUpdates) {
                supplierDAO.updateSupplier(supplier);
            }

            for (Supplier supplier : pendingDeletes) {
                supplierDAO.deleteSupplier(
                        supplier.getSupplierID());
            }

            pendingAdds.clear();
            pendingUpdates.clear();
            pendingDeletes.clear();

            undoStack.clear();
            redoStack.clear();

            closeAllPanels();

            loadSuppliers();
            loadStats();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(
                    "All supplier changes saved successfully.");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyFilters() {

        String type = supplierTypeFilterCombo.getValue();
        String city = cityFilterCombo.getValue();
        String relation = productRelationFilterCombo.getValue();
        String activity = purchaseActivityFilterCombo.getValue();
        String status = supplierStatusFilterCombo.getValue();

        double minAmount = parseDoubleOrDefault(
                minTotalAmountField.getText(), 0);

        double maxAmount = parseDoubleOrDefault(
                maxTotalAmountField.getText(),
                Double.MAX_VALUE);

        LocalDate fromDate = fromLastPurchaseDatePicker.getValue();

        LocalDate toDate = toLastPurchaseDatePicker.getValue();

        List<Supplier> filtered = originalSuppliers.stream()

                .filter(s -> type == null
                        || s.getSupplier_type().equals(type))

                .filter(s -> city == null
                        || s.getCity().equals(city))

                .filter(s -> status == null
                        || s.getStatus().equals(status))

                .filter(s -> s.getTotalPurchasedAmount() >= minAmount)

                .filter(s -> s.getTotalPurchasedAmount() <= maxAmount)

                .filter(s -> relation == null
                        || matchesProductFilter(
                                s, relation))

                .filter(s -> activity == null
                        || matchesPurchaseFilter(
                                s, activity))

                .toList();

        SupplierTable.setItems(
                FXCollections.observableArrayList(filtered));

        recordsFoundLabel.setText(
                String.valueOf(filtered.size()));

        refreshRowNumbers();
    }

    private boolean matchesProductFilter(
            Supplier supplier,
            String filter) {

        return switch (filter) {

            case "Has Products" ->
                supplier.getProductsCount() > 0;

            case "No Products" ->
                supplier.getProductsCount() == 0;

            default ->
                true;
        };
    }

    private boolean matchesPurchaseFilter(
            Supplier supplier,
            String filter) {

        return switch (filter) {

            case "Has Purchases" ->
                supplier.getPurchasesCount() > 0;

            case "No Purchases" ->
                supplier.getPurchasesCount() == 0;

            default ->
                true;
        };
    }

    private void resetFilters() {

        supplierTypeFilterCombo.setValue(null);
        cityFilterCombo.setValue(null);
        productRelationFilterCombo.setValue(null);
        purchaseActivityFilterCombo.setValue(null);
        supplierStatusFilterCombo.setValue(null);

        minTotalAmountField.clear();
        maxTotalAmountField.clear();

        fromLastPurchaseDatePicker.setValue(null);
        toLastPurchaseDatePicker.setValue(null);

        SupplierTable.setItems(
                FXCollections.observableArrayList(
                        originalSuppliers));

        recordsFoundLabel.setText(
                String.valueOf(originalSuppliers.size()));

        refreshRowNumbers();
    }

    private void exportSuppliersToExcel() {

        try {

            File exportFolder = new File("src/main/resources/exports");

            if (!exportFolder.exists()) {
                exportFolder.mkdirs();
            }

            String fileName = "suppliers_" + LocalDate.now() + ".xlsx";

            File file = new File(exportFolder, fileName);

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Suppliers");

            String[] headers = {
                    "No.",
                    "Supplier ID",
                    "Full Name",
                    "Type",
                    "Email",
                    "Phone",
                    "City",
                    "Products Count",
                    "Purchases Count",
                    "Total Purchased Amount",
                    "Last Purchase Date",
                    "Status"
            };

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {

                Cell cell = headerRow.createCell(i);

                cell.setCellValue(headers[i]);
            }

            int rowIndex = 1;

            for (Supplier supplier : SupplierTable.getItems()) {

                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(supplier.getNo());

                row.createCell(1).setCellValue(
                        supplier.getSupplierID());

                row.createCell(2).setCellValue(
                        emptyToDash(supplier.getFullName()));

                row.createCell(3).setCellValue(
                        emptyToDash(supplier.getSupplier_type()));

                row.createCell(4).setCellValue(
                        emptyToDash(supplier.getEmail()));

                row.createCell(5).setCellValue(
                        emptyToDash(supplier.getPhone()));

                row.createCell(6).setCellValue(
                        emptyToDash(supplier.getCity()));

                row.createCell(7).setCellValue(
                        supplier.getProductsCount());

                row.createCell(8).setCellValue(
                        supplier.getPurchasesCount());

                row.createCell(9).setCellValue(
                        supplier.getTotalPurchasedAmount());

                row.createCell(10).setCellValue(
                        emptyToDash(supplier.getLastPurchaseDate()));

                row.createCell(11).setCellValue(
                        emptyToDash(supplier.getStatus()));
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream out = new FileOutputStream(file)) {
                workbook.write(out);
            }

            workbook.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Export Success");

            alert.setHeaderText(null);

            alert.setContentText(
                    "Employees exported successfully!\n\nSaved to:\n"
                            + file.getAbsolutePath());

            alert.showAndWait();

        } catch (Exception e) {

            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Export Error");

            alert.setHeaderText(null);

            alert.setContentText(
                    "Failed to export suppliers file.");

            alert.showAndWait();
        }
    }

}
