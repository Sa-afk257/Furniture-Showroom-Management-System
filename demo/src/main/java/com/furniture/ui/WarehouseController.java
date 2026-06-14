package com.furniture.ui;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import com.furniture.dao.PurchaseDAO.PurchaseStats;
import com.furniture.dao.WarehouseDAO;
import com.furniture.model.Warehouse;
import com.furniture.model.Product;
import com.furniture.model.PurchaseDetails;
import com.furniture.model.PurchaseTransaction;
import com.furniture.dao.WarehouseDAO.WarehouseStats;

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

public class WarehouseController {

    private WarehouseDAO warehouseDAO = new WarehouseDAO();

    @FXML
    private Label totalWarehousesLabel;
    @FXML
    private Label totalCapacityLabel;
    @FXML
    private Label usedCapacityLabel;
    @FXML
    private Label availableCapacityLabel;
    @FXML
    private Label fullWarehousesLabel;

    /* Filter */
    @FXML
    private ComboBox<String> warehouseCombo;

    @FXML
    private ComboBox<String> managerCombo;

    @FXML
    private ComboBox<String> cityCombo;

    @FXML
    private ComboBox<String> warehouseStatusCombo;

    @FXML
    private TextField minCapacityField;

    @FXML
    private TextField maxCapacityField;

    @FXML
    private TextField minUsedPercentField;

    @FXML
    private TextField maxUsedPercentField;

    @FXML
    private Label warehousesFoundLabel;

    /* Main Table */

    @FXML
    private TableView<Warehouse> warehouseTable;

    @FXML
    private TableColumn<Warehouse, Integer> colNo;

    @FXML
    private TableColumn<Warehouse, Integer> colWarehouseID;

    @FXML
    private TableColumn<Warehouse, String> colWarehouseName;

    @FXML
    private TableColumn<Warehouse, String> colManagerName;

    @FXML
    private TableColumn<Warehouse, Integer> colCapacity;

    @FXML
    private TableColumn<Warehouse, Double> colUsedPercent;

    @FXML
    private TableColumn<Warehouse, Integer> colProductsCount;

    @FXML
    private TableColumn<Warehouse, String> colWarehouseStatus;

    @FXML
    private TableColumn<Warehouse, Void> colAction;

    /* Panels */
    @FXML
    private VBox filterPanel;

    @FXML
    private VBox addWarehousePanel;

    @FXML
    private VBox warehouseDetailsPanel;

    /* Buttons */
    @FXML
    private Button filterToggleBtn;

    @FXML
    private Button addWarehouseBtn;

    @FXML
    private Button saveWarehouseBtn;

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

    /* Add Warehouse Form */
    @FXML
    private TextField addWarehouseNameField;

    @FXML
    private ComboBox<String> addManagerCombo;

    @FXML
    private TextField addCapacityField;

    @FXML
    private TextField addCityField;

    @FXML
    private TextField addTownField;

    @FXML
    private TextField addAreaField;

    @FXML
    private TextField addStreetField;

    @FXML
    private TextField addBuildingField;

    @FXML
    private Label formTitleLabel;

    /* Warnings */
    @FXML
    private Label warehouseNameWarningLabel;

    @FXML
    private Label managerWarningLabel;

    @FXML
    private Label capacityWarningLabel;

    @FXML
    private Label cityWarningLabel;

    /* Details Panel */
    @FXML
    private Label detailsWarehouseIdLabel;

    @FXML
    private Label detailsWarehouseNameLabel;

    @FXML
    private Label detailsManagerNameLabel;

    @FXML
    private Label detailsCapacityLabel;

    @FXML
    private Label detailsUsedPercentLabel;

    @FXML
    private Label detailsUsedCapacityLabel;

    @FXML
    private Label detailsRemainingCapacityLabel;

    @FXML
    private Label detailsProductsCountLabel;

    @FXML
    private Label detailsWarehouseStatusLabel;

    @FXML
    private Label detailsCityLabel;

    @FXML
    private Label detailsTownLabel;

    @FXML
    private Label detailsAreaLabel;

    @FXML
    private Label detailsStreetLabel;

    @FXML
    private Label detailsBuildingLabel;

    @FXML
    private Label detailsWarehouseStatusGridLabel;

    /* Details Table */
    @FXML
    private TableView<Product> warehouseProductsTable;

    @FXML
    private TableColumn<Product, String> colDetailProductName;

    @FXML
    private TableColumn<Product, String> colDetailCategory;

    @FXML
    private TableColumn<Product, Double> colDetailQuantity;

    @FXML
    private TableColumn<Product, Double> colDetailPrice;

    @FXML
    private TableColumn<Product, String> colDetailStatus;

    /* Lists */

    private List<Warehouse> originalWarehouses = new ArrayList<>();

    private Stack<List<Warehouse>> undoStack = new Stack<>();
    private Stack<List<Warehouse>> redoStack = new Stack<>();

    private List<Warehouse> pendingAdds = new ArrayList<>();
    private List<Warehouse> pendingUpdates = new ArrayList<>();
    private List<Warehouse> pendingDeletes = new ArrayList<>();

    private Warehouse warehouseBeingUpdated = null;

    private boolean clearingForm = false;

    private Warehouse selectedWarehouseForDetails = null;

    @FXML
    private void initialize() {

        setupWarehouseTable();
        setupDetailsTable();

        loadWarehousesTable();
        loadWarehouseStats();
        loadWarehouseFilters();
        loadManagers();

        setupSearch();
        setupAddWarehouseButtons();
        setupSaveWarehouse();
        setupLiveValidation();
        setupUndoRedoReset();

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

        addWarehouseBtn.setOnAction(e -> {
            if (addWarehousePanel.isVisible() && warehouseBeingUpdated == null) {
                addWarehousePanel.setVisible(false);
                addWarehousePanel.setManaged(false);
            } else {
                openAddForm();
            }
        });

        closeDetailsBtn.setOnAction(e -> {
            warehouseDetailsPanel.setVisible(false);
            warehouseDetailsPanel.setManaged(false);
        });

        exportBtn.setOnAction(e -> exportWarehousesToExcel());
        saveAllBtn.setOnAction(e -> saveAllChanges());
    }

    private void setupWarehouseTable() {

        colNo.setCellValueFactory(
                new PropertyValueFactory<>("no"));

        colWarehouseID.setCellValueFactory(
                new PropertyValueFactory<>("warehouseID"));

        colWarehouseName.setCellValueFactory(
                new PropertyValueFactory<>("warehouseName"));

        colManagerName.setCellValueFactory(
                new PropertyValueFactory<>("managerName"));

        colCapacity.setCellValueFactory(
                new PropertyValueFactory<>("capacity"));

        colUsedPercent.setCellValueFactory(
                new PropertyValueFactory<>("usedPercent"));

        colProductsCount.setCellValueFactory(
                new PropertyValueFactory<>("productsCount"));

        colWarehouseStatus.setCellValueFactory(
                new PropertyValueFactory<>("warehouseStatus"));

        colUsedPercent.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText("0%");
                } else {
                    setText(String.format("%.1f%%", item));
                }
            }
        });

        setupActionColumn();
    }

    private void setupDetailsTable() {

        colDetailProductName.setCellValueFactory(
                new PropertyValueFactory<>("productName"));

        colDetailCategory.setCellValueFactory(
                new PropertyValueFactory<>("categoryName"));

        colDetailQuantity.setCellValueFactory(
                new PropertyValueFactory<>("stock"));

        colDetailPrice.setCellValueFactory(
                new PropertyValueFactory<>("price"));

        colDetailStatus.setCellValueFactory(
                new PropertyValueFactory<>("status"));
    }

    private void setupActionColumn() {

        colAction.setCellFactory(column -> new TableCell<>() {

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
                    Warehouse warehouse = getTableView().getItems().get(getIndex());
                    openWarehouseDetails(warehouse);
                });

                editBtn.setOnAction(e -> {
                    Warehouse warehouse = getTableView().getItems().get(getIndex());
                    openUpdateForm(warehouse);
                });

                deleteBtn.setOnAction(e -> {
                    Warehouse warehouse = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Warehouse");
                    alert.setHeaderText(null);
                    alert.setContentText(
                            "Are you sure you want to delete Warehouse #"
                                    + warehouse.getWarehouseID() + "?");

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isEmpty() || result.get() != ButtonType.OK) {
                        return;
                    }

                    saveStateForUndo();

                    pendingDeletes.add(warehouse);
                    warehouseTable.getItems().remove(warehouse);

                    refreshRowNumbers();

                    redoStack.clear();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void loadManagers() {
        addManagerCombo.getItems().setAll(
                warehouseDAO.getWarehouseManagerNames());
    }

    private void openWarehouseDetails(Warehouse warehouse) {

        selectedWarehouseForDetails = warehouse;

        detailsWarehouseIdLabel.setText("Warehouse #" + warehouse.getWarehouseID());
        detailsWarehouseNameLabel.setText(emptyToDash(warehouse.getWarehouseName()));
        detailsManagerNameLabel.setText(emptyToDash(warehouse.getManagerName()));

        detailsCapacityLabel.setText(String.valueOf(warehouse.getCapacity()));

        detailsUsedPercentLabel.setText(
                String.format("%.1f%%", warehouse.getUsedPercent()));

        detailsUsedCapacityLabel.setText(
                String.format("%.0f", warehouse.getUsedCapacity()));

        detailsRemainingCapacityLabel.setText(
                String.format("%.0f", warehouse.getRemainingCapacity()));

        detailsProductsCountLabel.setText(
                String.valueOf(warehouse.getProductsCount()));

        String status = emptyToDash(
                warehouse.getWarehouseStatus());

        detailsWarehouseStatusLabel.setText(status);

        detailsWarehouseStatusGridLabel.setText(status);

        detailsCityLabel.setText(emptyToDash(warehouse.getCity()));
        detailsTownLabel.setText(emptyToDash(warehouse.getTown()));
        detailsAreaLabel.setText(emptyToDash(warehouse.getArea()));
        detailsStreetLabel.setText(emptyToDash(warehouse.getStreet()));
        detailsBuildingLabel.setText(emptyToDash(warehouse.getBuilding()));

        warehouseProductsTable.getItems().clear();

        warehouseProductsTable.setItems(
                FXCollections.observableArrayList(
                        warehouseDAO.getProductsInsideWarehouse(
                                warehouse.getWarehouseID())));

        warehouseProductsTable.refresh();

        showOnlyPanel(warehouseDetailsPanel);
    }

    private String emptyToDash(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        return value;
    }

    private void refreshRowNumbers() {
        for (int i = 0; i < warehouseTable.getItems().size(); i++) {
            warehouseTable.getItems().get(i).setNo(i + 1);
        }

        warehouseTable.refresh();
    }

    private void loadWarehousesTable() {

        List<Warehouse> warehouses = warehouseDAO.getAllWarehousesForTable();

        for (int i = 0; i < warehouses.size(); i++) {
            warehouses.get(i).setNo(i + 1);
        }

        originalWarehouses.clear();
        originalWarehouses.addAll(warehouses);

        warehouseTable.setItems(
                FXCollections.observableArrayList(warehouses));

        warehousesFoundLabel.setText(
                String.valueOf(warehouses.size()));
    }

    private void loadWarehouseStats() {

        WarehouseStats stats = warehouseDAO.getWarehouseStats();

        totalWarehousesLabel.setText(
                String.valueOf(stats.getTotalWarehouses()));

        totalCapacityLabel.setText(
                String.format("%,.0f", stats.getTotalCapacity()));

        usedCapacityLabel.setText(
                String.format("%,.0f", stats.getUsedCapacity()));

        availableCapacityLabel.setText(
                String.format("%,.0f", stats.getAvailableCapacity()));

        fullWarehousesLabel.setText(
                String.valueOf(stats.getFullWarehouses()));
    }

    private void setupAddWarehouseButtons() {
        cancelAddBtn.setOnAction(e -> {
            clearAddForm();
            addWarehousePanel.setVisible(false);
            addWarehousePanel.setManaged(false);
        });
    }

    private void setupSaveWarehouse() {
        saveWarehouseBtn.setOnAction(e -> {
            if (warehouseBeingUpdated == null) {
                addWarehouse();
            } else {
                updateWarehouse();
            }
        });
    }

    private void setupSearch() {

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {

            String keyword = newValue.toLowerCase().trim();

            if (keyword.isEmpty()) {
                warehouseTable.setItems(
                        FXCollections.observableArrayList(originalWarehouses));

                warehousesFoundLabel.setText(
                        String.valueOf(originalWarehouses.size()));

                refreshRowNumbers();
                return;
            }

            List<Warehouse> filtered = originalWarehouses.stream()
                    .filter(w -> contains(String.valueOf(w.getWarehouseID()), keyword)
                            || contains(w.getWarehouseName(), keyword)
                            || contains(w.getManagerName(), keyword)
                            || contains(w.getCity(), keyword)
                            || contains(String.valueOf(w.getCapacity()), keyword)
                            || contains(String.format("%.1f", w.getUsedPercent()), keyword)
                            || contains(String.valueOf(w.getProductsCount()), keyword)
                            || contains(w.getWarehouseStatus(), keyword))
                    .toList();

            warehouseTable.setItems(
                    FXCollections.observableArrayList(filtered));

            warehousesFoundLabel.setText(
                    String.valueOf(filtered.size()));

            refreshRowNumbers();
        });
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private void openAddForm() {
        warehouseBeingUpdated = null;

        clearAddForm();

        formTitleLabel.setText("✚ ADD WAREHOUSE");
        saveWarehouseBtn.setText("✚  ADD WAREHOUSE");

        showOnlyPanel(addWarehousePanel);
    }

    private void addWarehouse() {

        if (!validateAddWarehouseForm()) {
            return;
        }

        Warehouse warehouse = new Warehouse();

        warehouse.setNo(warehouseTable.getItems().size() + 1);

        warehouse.setWarehouseName(addWarehouseNameField.getText().trim());
        warehouse.setManagerName(addManagerCombo.getValue());
        warehouse.setEmployeeID(
                warehouseDAO.getEmployeeIdByName(addManagerCombo.getValue()));

        warehouse.setCapacity(
                Integer.parseInt(addCapacityField.getText().trim()));

        warehouse.setCity(addCityField.getText().trim());
        warehouse.setTown(addTownField.getText().trim());
        warehouse.setArea(addAreaField.getText().trim());
        warehouse.setStreet(addStreetField.getText().trim());
        warehouse.setBuilding(addBuildingField.getText().trim());

        warehouse.setUsedCapacity(0);
        warehouse.setRemainingCapacity(warehouse.getCapacity());
        warehouse.setUsedPercent(0);
        warehouse.setProductsCount(0);
        warehouse.setWarehouseStatus("Empty");

        saveStateForUndo();

        pendingAdds.add(warehouse);
        warehouseTable.getItems().add(warehouse);
        originalWarehouses.add(warehouse);

        warehousesFoundLabel.setText(
                String.valueOf(warehouseTable.getItems().size()));

        loadWarehouseFilters();

        redoStack.clear();
        clearAddForm();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validateAddWarehouseForm() {

        boolean valid = true;

        valid &= validateTextField(addWarehouseNameField, warehouseNameWarningLabel);
        valid &= validateComboBox(addManagerCombo, managerWarningLabel);
        valid &= validateNumberField(addCapacityField, capacityWarningLabel);
        valid &= validateTextField(addCityField, cityWarningLabel);

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
            int value = Integer.parseInt(field.getText().trim());
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

        addWarehouseNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateTextField(addWarehouseNameField, warehouseNameWarningLabel);
            }
        });

        addManagerCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateComboBox(addManagerCombo, managerWarningLabel);
            }
        });

        addCapacityField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateNumberField(addCapacityField, capacityWarningLabel);
            }
        });

        addCityField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateTextField(addCityField, cityWarningLabel);
            }
        });
    }

    private void clearAddForm() {

        clearingForm = true;

        addWarehouseNameField.clear();
        addManagerCombo.getSelectionModel().clearSelection();
        addCapacityField.clear();

        addCityField.clear();
        addTownField.clear();
        addAreaField.clear();
        addStreetField.clear();
        addBuildingField.clear();

        clearingForm = false;

        hideAllWarnings();
        removeAllValidationErrors();

        warehouseBeingUpdated = null;

        formTitleLabel.setText("✚ ADD WAREHOUSE");
        saveWarehouseBtn.setText("✚  ADD WAREHOUSE");
    }

    private void hideAllWarnings() {

        Label[] warnings = {
                warehouseNameWarningLabel,
                managerWarningLabel,
                capacityWarningLabel,
                cityWarningLabel
        };

        for (Label warning : warnings) {
            warning.setVisible(false);
            warning.setManaged(false);
        }
    }

    private void removeAllValidationErrors() {

        addWarehouseNameField.getStyleClass().remove("validation-error");
        addManagerCombo.getStyleClass().remove("validation-error");
        addCapacityField.getStyleClass().remove("validation-error");
        addCityField.getStyleClass().remove("validation-error");
    }

    private void openUpdateForm(Warehouse warehouse) {

        warehouseBeingUpdated = warehouse;

        clearAddForm();

        showOnlyPanel(addWarehousePanel);

        formTitleLabel.setText("✏ UPDATE WAREHOUSE");
        saveWarehouseBtn.setText("UPDATE");

        addWarehouseNameField.setText(warehouse.getWarehouseName());
        addManagerCombo.setValue(warehouse.getManagerName());
        addCapacityField.setText(String.valueOf(warehouse.getCapacity()));

        addCityField.setText(warehouse.getCity());
        addTownField.setText(warehouse.getTown());
        addAreaField.setText(warehouse.getArea());
        addStreetField.setText(warehouse.getStreet());
        addBuildingField.setText(warehouse.getBuilding());

        hideAllWarnings();
        removeAllValidationErrors();
    }

    private void updateWarehouse() {

        if (!validateAddWarehouseForm()) {
            return;
        }

        saveStateForUndo();

        warehouseBeingUpdated.setWarehouseName(
                addWarehouseNameField.getText().trim());

        warehouseBeingUpdated.setManagerName(
                addManagerCombo.getValue());

        warehouseBeingUpdated.setEmployeeID(
                warehouseDAO.getEmployeeIdByName(addManagerCombo.getValue()));

        warehouseBeingUpdated.setCapacity(
                Integer.parseInt(addCapacityField.getText().trim()));

        warehouseBeingUpdated.setCity(addCityField.getText().trim());
        warehouseBeingUpdated.setTown(addTownField.getText().trim());
        warehouseBeingUpdated.setArea(addAreaField.getText().trim());
        warehouseBeingUpdated.setStreet(addStreetField.getText().trim());
        warehouseBeingUpdated.setBuilding(addBuildingField.getText().trim());

        double used = warehouseBeingUpdated.getUsedCapacity();
        double remaining = warehouseBeingUpdated.getCapacity() - used;

        warehouseBeingUpdated.setRemainingCapacity(remaining);

        if (warehouseBeingUpdated.getCapacity() > 0) {
            warehouseBeingUpdated.setUsedPercent(
                    (used / warehouseBeingUpdated.getCapacity()) * 100);
        } else {
            warehouseBeingUpdated.setUsedPercent(0);
        }

        warehouseBeingUpdated.setWarehouseStatus(
                calculateWarehouseStatus(
                        warehouseBeingUpdated.getUsedCapacity(),
                        warehouseBeingUpdated.getCapacity()));

        if (!pendingUpdates.contains(warehouseBeingUpdated)) {
            pendingUpdates.add(warehouseBeingUpdated);
        }

        warehouseTable.refresh();

        warehouseBeingUpdated = null;

        clearAddForm();

        addWarehousePanel.setVisible(false);
        addWarehousePanel.setManaged(false);

        redoStack.clear();
    }

    private String calculateWarehouseStatus(double used, int capacity) {

        if (used <= 0) {
            return "Empty";
        }

        if (used >= capacity) {
            return "Full";
        }

        if ((used / capacity) >= 0.80) {
            return "Almost Full";
        }

        return "Available";
    }

    private void saveStateForUndo() {
        undoStack.push(new ArrayList<>(warehouseTable.getItems()));
    }

    private void applyFilters() {

        String warehouseName = warehouseCombo.getValue();
        String manager = managerCombo.getValue();
        String city = cityCombo.getValue();
        String status = warehouseStatusCombo.getValue();

        double minCapacity = parseDoubleOrDefault(minCapacityField.getText(), 0);
        double maxCapacity = parseDoubleOrDefault(maxCapacityField.getText(), Double.MAX_VALUE);

        double minUsedPercent = parseDoubleOrDefault(minUsedPercentField.getText(), 0);
        double maxUsedPercent = parseDoubleOrDefault(minUsedPercentField.getText(), Double.MAX_VALUE);

        List<Warehouse> filtered = originalWarehouses.stream()

                .filter(w -> warehouseName == null
                        || warehouseName.equals("All Warehouses")
                        || warehouseName.equals(w.getWarehouseName()))

                .filter(w -> manager == null
                        || manager.equals("All Managers")
                        || manager.equals(w.getManagerName()))

                .filter(w -> city == null
                        || city.equals("All Cities")
                        || city.equals(w.getCity()))

                .filter(w -> status == null
                        || status.equals("All Status")
                        || status.equals(w.getWarehouseStatus()))

                .filter(w -> w.getCapacity() >= minCapacity
                        && w.getCapacity() <= maxCapacity)

                .filter(w -> w.getUsedPercent() >= minUsedPercent
                        && w.getUsedPercent() <= maxUsedPercent)

                .toList();

        warehouseTable.setItems(
                FXCollections.observableArrayList(filtered));

        warehousesFoundLabel.setText(
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

    private void resetFilters() {

        warehouseCombo.getSelectionModel().selectFirst();
        managerCombo.getSelectionModel().selectFirst();
        cityCombo.getSelectionModel().selectFirst();
        warehouseStatusCombo.getSelectionModel().selectFirst();

        minCapacityField.clear();
        maxCapacityField.clear();

        minUsedPercentField.clear();
        maxUsedPercentField.clear();

        warehouseTable.setItems(
                FXCollections.observableArrayList(originalWarehouses));

        warehousesFoundLabel.setText(
                String.valueOf(originalWarehouses.size()));

        refreshRowNumbers();
    }

    private void loadWarehouseFilters() {

        warehouseCombo.getItems().clear();
        warehouseCombo.getItems().add("All Warehouses");
        warehouseCombo.getItems().addAll(
                warehouseDAO.getWarehouseNames());
        warehouseCombo.getSelectionModel().selectFirst();

        managerCombo.getItems().clear();
        managerCombo.getItems().add("All Managers");
        managerCombo.getItems().addAll(
                warehouseDAO.getWarehouseManagerNames());
        managerCombo.getSelectionModel().selectFirst();

        cityCombo.getItems().clear();
        cityCombo.getItems().add("All Cities");
        cityCombo.getItems().addAll(
                warehouseDAO.getWarehouseCities());
        cityCombo.getSelectionModel().selectFirst();

        warehouseStatusCombo.getItems().setAll(
                "All Status",
                "Empty",
                "Available",
                "Almost Full",
                "Full");
        warehouseStatusCombo.getSelectionModel().selectFirst();

        warehousesFoundLabel.setText(
                String.valueOf(warehouseTable.getItems().size()));
    }

    private void showOnlyPanel(VBox panel) {

        filterPanel.setVisible(false);
        filterPanel.setManaged(false);

        addWarehousePanel.setVisible(false);
        addWarehousePanel.setManaged(false);

        warehouseDetailsPanel.setVisible(false);
        warehouseDetailsPanel.setManaged(false);

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

        redoStack.push(new ArrayList<>(warehouseTable.getItems()));

        List<Warehouse> previousState = undoStack.pop();

        warehouseTable.setItems(
                FXCollections.observableArrayList(previousState));

        refreshRowNumbers();
    }

    private void redoAction() {

        if (redoStack.isEmpty()) {
            return;
        }

        undoStack.push(new ArrayList<>(warehouseTable.getItems()));

        List<Warehouse> nextState = redoStack.pop();

        warehouseTable.setItems(
                FXCollections.observableArrayList(nextState));

        refreshRowNumbers();
    }

    private void resetTableChanges() {

        pendingAdds.clear();
        pendingUpdates.clear();
        pendingDeletes.clear();

        undoStack.clear();
        redoStack.clear();

        loadWarehousesTable();
        loadWarehouseStats();
        loadWarehouseFilters();

        clearAddForm();

        addWarehousePanel.setVisible(false);
        addWarehousePanel.setManaged(false);

        warehouseDetailsPanel.setVisible(false);
        warehouseDetailsPanel.setManaged(false);
    }

    private void saveAllChanges() {

        try {

            for (Warehouse warehouse : pendingAdds) {
                warehouseDAO.insertWarehouse(warehouse);
            }

            for (Warehouse warehouse : pendingUpdates) {
                warehouseDAO.updateWarehouse(warehouse);
            }

            for (Warehouse warehouse : pendingDeletes) {
                warehouseDAO.deleteWarehouse(warehouse.getWarehouseID());
            }

            pendingAdds.clear();
            pendingUpdates.clear();
            pendingDeletes.clear();

            undoStack.clear();
            redoStack.clear();

            loadWarehousesTable();
            loadWarehouseStats();
            loadWarehouseFilters();

            clearAddForm();

            addWarehousePanel.setVisible(false);
            addWarehousePanel.setManaged(false);

            warehouseDetailsPanel.setVisible(false);
            warehouseDetailsPanel.setManaged(false);

            System.out.println("All warehouse changes saved successfully.");

        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to save warehouse changes.");
            alert.showAndWait();
        }
    }

    private void exportWarehousesToExcel() {

        try {
            File exportFolder = new File("src/main/resources/exports");

            if (!exportFolder.exists()) {
                exportFolder.mkdirs();
            }

            String fileName = "warehouses_" + java.time.LocalDate.now() + ".xlsx";
            File file = new File(exportFolder, fileName);

            Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Warehouses");

            String[] headers = {
                    "Warehouse ID",
                    "Warehouse Name",
                    "Manager",
                    "City",
                    "Capacity",
                    "Used Capacity",
                    "Remaining Capacity",
                    "Used %",
                    "Products",
                    "Status"
            };

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowIndex = 1;

            for (Warehouse w : warehouseTable.getItems()) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(w.getWarehouseID());
                row.createCell(1).setCellValue(w.getWarehouseName());
                row.createCell(2).setCellValue(w.getManagerName());
                row.createCell(3).setCellValue(w.getCity());
                row.createCell(4).setCellValue(w.getCapacity());
                row.createCell(5).setCellValue(w.getUsedCapacity());
                row.createCell(6).setCellValue(w.getRemainingCapacity());
                row.createCell(7).setCellValue(w.getUsedPercent());
                row.createCell(8).setCellValue(w.getProductsCount());
                row.createCell(9).setCellValue(w.getWarehouseStatus());
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
                    "Warehouses exported successfully!\n\nSaved to:\n"
                            + file.getAbsolutePath());
            alert.showAndWait();

        } catch (Exception ex) {
            ex.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to export warehouses file.");
            alert.showAndWait();
        }
    }
}
