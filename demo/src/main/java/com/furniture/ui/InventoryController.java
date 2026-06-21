package com.furniture.ui;

import com.furniture.model.Inventory;
import com.furniture.model.Product;
import com.furniture.model.StockMovement;
import com.furniture.model.Warehouse;
import com.furniture.model.Category;
import com.furniture.dao.InventoryDAO;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class InventoryController {
    /* ===================== DAO ===================== */
    private final InventoryDAO inventoryDAO = new InventoryDAO();

    /* ===================== DATA ===================== */
    private final List<Inventory> originalInventory = new ArrayList<>();

    private final Stack<List<Inventory>> undoStack = new Stack<>();
    private final Stack<List<Inventory>> redoStack = new Stack<>();

    private final List<Inventory> pendingAdds = new ArrayList<>();
    private final List<Inventory> pendingUpdates = new ArrayList<>();
    private final List<Inventory> pendingDeletes = new ArrayList<>();

    private Inventory inventoryBeingUpdated = null;

    /* ===================== STATS CARDS ===================== */
    @FXML
    private Label totalRecordsLabel;
    @FXML
    private Label totalWarehousesLabel;
    @FXML
    private Label lowStockLabel;
    @FXML
    private Label outOfStockLabel;
    @FXML
    private Label inventoryValueLabel;

    /* ===================== TOOLBAR ===================== */
    @FXML
    private TextField txtSearch;

    @FXML
    private Button filterToggleBtn;
    @FXML
    private Button addInventoryBtn;
    @FXML
    private Button exportBtn;

    /* ===================== MAIN TABLE ===================== */
    @FXML
    private TableView<Inventory> inventoryTable;

    @FXML
    private TableColumn<Inventory, Integer> colNo;
    @FXML
    private TableColumn<Inventory, Integer> colProductID;
    @FXML
    private TableColumn<Inventory, String> colImage;
    @FXML
    private TableColumn<Inventory, String> colProductName;
    @FXML
    private TableColumn<Inventory, String> colCategory;
    @FXML
    private TableColumn<Inventory, String> colWarehouse;
    @FXML
    private TableColumn<Inventory, String> colManager;
    @FXML
    private TableColumn<Inventory, Double> colQuantity;
    @FXML
    private TableColumn<Inventory, Double> colUnitPrice;
    @FXML
    private TableColumn<Inventory, Double> colStockValue;
    @FXML
    private TableColumn<Inventory, String> colStatus;
    @FXML
    private TableColumn<Inventory, Void> colAction;

    /* ===================== FILTER PANEL ===================== */
    @FXML
    private VBox filterPanel;

    @FXML
    private ComboBox<Warehouse> warehouseFilterCombo;
    @FXML
    private ComboBox<Category> categoryFilterCombo;
    @FXML
    private ComboBox<String> productFilterCombo;
    @FXML
    private ComboBox<String> managerFilterCombo;
    @FXML
    private ComboBox<String> statusCombo;

    @FXML
    private TextField minQuantityField;
    @FXML
    private TextField maxQuantityField;
    @FXML
    private TextField minValueField;
    @FXML
    private TextField maxValueField;

    @FXML
    private Button applyFiltersBtn;
    @FXML
    private Button resetFiltersBtn;

    @FXML
    private Label recordsFoundLabel;

    /* ===================== ADD / UPDATE PANEL ===================== */
    @FXML
    private VBox addInventoryPanel;

    @FXML
    private Label formTitleLabel;

    @FXML
    private ComboBox<Product> productCombo;
    @FXML
    private ComboBox<Warehouse> warehouseComboAdd;
    @FXML
    private TextField quantityField;

    @FXML
    private DatePicker movement_datePicker;

    @FXML
    private Button saveInventoryBtn;
    @FXML
    private Button cancelAddBtn;

    /* ===================== DETAILS PANEL ===================== */
    @FXML
    private VBox inventoryDetailsPanel;

    @FXML
    private ImageView detailsProductImage;

    @FXML
    private Label detailsProductNameLabel;
    @FXML
    private Label detailsStatusLabel;
    @FXML
    private Label detailsProductIdLabel;
    @FXML
    private Label detailsCategoryLabel;
    @FXML
    private Label detailsColorLabel;
    @FXML
    private Label detailsMaterialLabel;
    @FXML
    private Label detailsPriceLabel;
    @FXML
    private Label detailsQuantityLabel;
    @FXML
    private Label detailsStockValueLabel;
    @FXML
    private Label detailsWarehouseLabel;
    @FXML
    private Label detailsManagerLabel;
    @FXML
    private Label detailsCapacityLabel;
    @FXML
    private Label detailsUsedCapacityLabel;
    @FXML
    private Label detailsRemainingCapacityLabel;
    @FXML
    private Label detailsDescriptionLabel;
    @FXML
    private Circle detailsColorCircle;
    @FXML
    private Button closeDetailsBtn;

    private Map<String, String> colorMap = new HashMap<>();

    /* ===================== MOVEMENT TABLE ===================== */
    @FXML
    private TableView<StockMovement> movementTable;

    @FXML
    private TableColumn<StockMovement, String> colMovementType;
    @FXML
    private TableColumn<StockMovement, Double> colMovementQty;
    @FXML
    private TableColumn<StockMovement, LocalDate> colmovement_date;

    /* ===================== TABLE ACTIONS ===================== */
    @FXML
    private Button undoBtn;
    @FXML
    private Button redoBtn;
    @FXML
    private Button resetBtn;
    @FXML
    private Button saveAllBtn;
    /* ===================== WARNING ===================== */
    @FXML
    private Label productWarningLabel;
    @FXML
    private Label warehouseWarningLabel;
    @FXML
    private Label quantityWarningLabel;
    @FXML
    private Label movementDateWarningLabel;

    private boolean clearingForm = false;

    @FXML
    private void initialize() {

        setupTable();

        setupMovementTable();

        setupActionColumn();

        inventoryTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        loadInventory();

        loadColorsFromExcel();

        loadStats();

        loadCombos();

        setupComboBoxes();

        setupLiveValidation();

        setupButtons();

        setupSearch();

        setupFilters();

        closeAllPanels();
    }

    private void setupMovementTable() {

        colMovementType.setCellValueFactory(
                new PropertyValueFactory<>("movementType"));

        colMovementQty.setCellValueFactory(
                new PropertyValueFactory<>("quantity"));

        colmovement_date.setCellValueFactory(cellData -> {
            java.sql.Date date = cellData.getValue().getmovement_date();
            return new javafx.beans.property.SimpleObjectProperty<>(
                    date == null ? null : date.toLocalDate());
        });
    }

    private void setupComboBoxes() {

        /* Product Combo */

        productCombo.setCellFactory(lv -> new ListCell<>() {

            @Override
            protected void updateItem(Product item, boolean empty) {

                super.updateItem(item, empty);

                setText(
                        empty || item == null
                                ? null
                                : item.getProductName());
            }
        });

        productCombo.setButtonCell(new ListCell<>() {

            @Override
            protected void updateItem(Product item, boolean empty) {

                super.updateItem(item, empty);

                setText(
                        empty || item == null
                                ? null
                                : item.getProductName());
            }
        });

        /* Warehouse Combo */

        warehouseComboAdd.setCellFactory(lv -> new ListCell<>() {

            @Override
            protected void updateItem(Warehouse item, boolean empty) {

                super.updateItem(item, empty);

                setText(
                        empty || item == null
                                ? null
                                : item.getWarehouseName());
            }
        });

        warehouseComboAdd.setButtonCell(new ListCell<>() {

            @Override
            protected void updateItem(Warehouse item, boolean empty) {

                super.updateItem(item, empty);

                setText(
                        empty || item == null
                                ? null
                                : item.getWarehouseName());
            }
        });

        /* Category Filter */

        categoryFilterCombo.setCellFactory(lv -> new ListCell<>() {

            @Override
            protected void updateItem(Category item, boolean empty) {

                super.updateItem(item, empty);

                setText(
                        empty || item == null
                                ? null
                                : item.getCategoryName());
            }
        });

        categoryFilterCombo.setButtonCell(new ListCell<>() {

            @Override
            protected void updateItem(Category item, boolean empty) {

                super.updateItem(item, empty);

                setText(
                        empty || item == null
                                ? null
                                : item.getCategoryName());
            }
        });

        /* Warehouse Filter */

        warehouseFilterCombo.setCellFactory(lv -> new ListCell<>() {

            @Override
            protected void updateItem(Warehouse item, boolean empty) {

                super.updateItem(item, empty);

                setText(
                        empty || item == null
                                ? null
                                : item.getWarehouseName());
            }
        });

        warehouseFilterCombo.setButtonCell(new ListCell<>() {

            @Override
            protected void updateItem(Warehouse item, boolean empty) {

                super.updateItem(item, empty);

                setText(
                        empty || item == null
                                ? null
                                : item.getWarehouseName());
            }
        });

    }

    private void loadColorsFromExcel() {

        try {
            InputStream is = getClass().getResourceAsStream("/data/colorsManual.xlsx");

            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String hex = row.getCell(0).getStringCellValue().trim();
                String colorName = row.getCell(1).getStringCellValue().trim();

                colorName = colorName.replace("(W3C)", "").trim();

                colorMap.put(colorName.toLowerCase(), hex);
            }

            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setColorCircle(String colorName) {

        detailsColorLabel.setText(emptyToDash(colorName));

        String hex = colorMap.getOrDefault(
                colorName == null ? "" : colorName.toLowerCase().trim(),
                "#999999");

        detailsColorCircle.setFill(Color.web(hex));
    }

    private void setupTable() {

        colNo.setCellValueFactory(
                new PropertyValueFactory<>("no"));

        colProductID.setCellValueFactory(
                new PropertyValueFactory<>("productID"));

        colProductName.setCellValueFactory(
                new PropertyValueFactory<>("productName"));

        colCategory.setCellValueFactory(
                new PropertyValueFactory<>("categoryName"));

        colWarehouse.setCellValueFactory(
                new PropertyValueFactory<>("warehouseName"));

        colManager.setCellValueFactory(
                new PropertyValueFactory<>("managerName"));

        colQuantity.setCellValueFactory(
                new PropertyValueFactory<>("quantity"));
        colQuantity.setCellFactory(column -> new TableCell<>() {

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

        colUnitPrice.setCellValueFactory(
                new PropertyValueFactory<>("unitPrice"));
        colUnitPrice.setCellFactory(column -> new TableCell<>() {

            @Override
            protected void updateItem(Double item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {

                    setText("$0");

                } else {

                    setText(String.format("$%,.2f", item));
                }
            }
        });

        colStockValue.setCellValueFactory(
                new PropertyValueFactory<>("stockValue"));
        colStockValue.setCellFactory(column -> new TableCell<>() {

            @Override
            protected void updateItem(Double item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {

                    setText("$0");

                } else {

                    setText(String.format("$%,.2f", item));
                }
            }
        });
        colImage.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        colImage.setCellFactory(column -> new TableCell<>() {

            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(45);
                imageView.setFitHeight(45);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String item, boolean empty) {

                super.updateItem(item, empty);

                if (empty) {

                    setGraphic(null);

                    return;
                }

                Inventory inventory = getTableView().getItems().get(getIndex());

                try {

                    String imagePath = inventory.getImagePath();

                    if (imagePath != null && !imagePath.isBlank()) {

                        Image image = new Image(
                                getClass()
                                        .getResourceAsStream(imagePath));

                        imageView.setImage(image);

                        setGraphic(imageView);

                    } else {

                        setGraphic(null);
                    }

                } catch (Exception e) {

                    setGraphic(null);
                }
            }
        });

        colStatus.setCellValueFactory(
                new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(column -> new TableCell<>() {

            @Override
            protected void updateItem(String item, boolean empty) {

                super.updateItem(item, empty);

                getStyleClass().removeAll(
                        "status-instock",
                        "status-lowstock",
                        "status-outstock");

                if (empty || item == null) {

                    setText(null);

                } else {

                    setText(item);

                    switch (item) {

                        case "In Stock" ->
                            getStyleClass().add("status-instock");

                        case "Low Stock" ->
                            getStyleClass().add("status-lowstock");

                        case "Out Of Stock" ->
                            getStyleClass().add("status-outstock");
                    }
                }
            }
        });

    }

    private void saveStateForUndo() {

        undoStack.push(new ArrayList<>(inventoryTable.getItems()));
    }

    private void undoAction() {

        if (undoStack.isEmpty()) {
            return;
        }

        redoStack.push(new ArrayList<>(inventoryTable.getItems()));

        List<Inventory> previousState = undoStack.pop();

        inventoryTable.setItems(FXCollections.observableArrayList(previousState));

        recordsFoundLabel.setText(String.valueOf(previousState.size()));

        refreshRowNumbers();
    }

    private void redoAction() {

        if (redoStack.isEmpty()) {
            return;
        }

        undoStack.push(new ArrayList<>(inventoryTable.getItems()));

        List<Inventory> nextState = redoStack.pop();

        inventoryTable.setItems(FXCollections.observableArrayList(nextState));

        recordsFoundLabel.setText(String.valueOf(nextState.size()));

        refreshRowNumbers();
    }

    private void resetTableChanges() {

        pendingAdds.clear();

        pendingUpdates.clear();

        pendingDeletes.clear();

        undoStack.clear();

        redoStack.clear();

        loadInventory();

        loadStats();

        clearForm();

        closeAllPanels();
    }

    private void openUpdateForm(Inventory inventory) {

        inventoryBeingUpdated = inventory;

        productCombo.getItems().stream().filter(p -> p.getProductID() == inventory.getProductID()).findFirst()
                .ifPresent(productCombo::setValue);

        warehouseComboAdd.getItems().stream().filter(w -> w.getWarehouseID() == inventory.getWarehouseID()).findFirst()
                .ifPresent(warehouseComboAdd::setValue);
        inventoryBeingUpdated.setOldWarehouseID(inventory.getWarehouseID());

        inventoryBeingUpdated.setOldProductID(inventory.getProductID());

        formTitleLabel.setText("✏ UPDATE INVENTORY");

        saveInventoryBtn.setText("UPDATE INVENTORY");

        quantityField.setText(String.valueOf(inventory.getQuantity()));

        showOnlyPanel(addInventoryPanel);
    }

    private boolean validateForm() {

        boolean valid = true;

        valid &= validateComboBox(productCombo, productWarningLabel);
        valid &= validateComboBox(warehouseComboAdd, warehouseWarningLabel);
        valid &= validateDatePicker(movement_datePicker, movementDateWarningLabel);
        valid &= validateNumberField(quantityField, quantityWarningLabel);

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

    private void hideAllWarnings() {

        Label[] warnings = {
                productWarningLabel,
                warehouseWarningLabel,
                quantityWarningLabel,
                movementDateWarningLabel
        };

        for (Label warning : warnings) {
            warning.setVisible(false);
            warning.setManaged(false);
        }
    }

    private void removeAllValidationErrors() {

        productCombo.getStyleClass().remove("validation-error");
        warehouseComboAdd.getStyleClass().remove("validation-error");
        movement_datePicker.getStyleClass().remove("validation-error");
        quantityField.getStyleClass().remove("validation-error");
    }

    private void clearForm() {

        clearingForm = true;

        productCombo.setValue(null);
        warehouseComboAdd.setValue(null);
        quantityField.clear();

        movement_datePicker.setValue(LocalDate.now());

        inventoryBeingUpdated = null;

        hideAllWarnings();
        removeAllValidationErrors();

        clearingForm = false;
    }

    private void setupLiveValidation() {

        productCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateComboBox(productCombo, productWarningLabel);
            }
        });

        warehouseComboAdd.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateComboBox(warehouseComboAdd, warehouseWarningLabel);
            }
        });

        movement_datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateDatePicker(movement_datePicker, movementDateWarningLabel);
            }
        });

        quantityField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateNumberField(quantityField, quantityWarningLabel);
            }
        });
    }

    private void loadInventory() {

        List<Inventory> inventoryList = inventoryDAO.getAllInventoryForTable();

        for (int i = 0; i < inventoryList.size(); i++) {

            inventoryList.get(i).setNo(i + 1);

            Inventory inv = inventoryList.get(i);

            inv.setStockValue(
                    inv.getQuantity() * inv.getUnitPrice());

            if (inv.getQuantity() <= 0) {

                inv.setStatus("Out Of Stock");

            } else if (inv.getQuantity() <= 10) {

                inv.setStatus("Low Stock");

            } else {

                inv.setStatus("In Stock");
            }
        }

        originalInventory.clear();

        originalInventory.addAll(inventoryList);

        inventoryTable.setItems(
                FXCollections.observableArrayList(inventoryList));

        recordsFoundLabel.setText(
                String.valueOf(inventoryList.size()));

        refreshRowNumbers();
    }

    private void loadStats() {

        InventoryDAO.InventoryStats stats = inventoryDAO.getInventoryStats();

        totalRecordsLabel.setText(String.valueOf(stats.getTotalRecords()));

        totalWarehousesLabel.setText(String.valueOf(stats.getTotalWarehouses()));

        lowStockLabel.setText(String.valueOf(stats.getLowStock()));

        outOfStockLabel.setText(String.valueOf(stats.getOutOfStock()));

        inventoryValueLabel.setText(String.format("$%,.0f", stats.getInventoryValue()));
    }

    private void loadCombos() {

        /* ADD PANEL */

        productCombo.setItems(
                FXCollections.observableArrayList(
                        inventoryDAO.getAllProducts()));

        warehouseComboAdd.setItems(
                FXCollections.observableArrayList(
                        inventoryDAO.getAllWarehouses()));

        movement_datePicker.setValue(
                LocalDate.now());

        /* FILTER PANEL */

        warehouseFilterCombo.setItems(
                FXCollections.observableArrayList(
                        inventoryDAO.getAllWarehouses()));

        categoryFilterCombo.setItems(
                FXCollections.observableArrayList(
                        inventoryDAO.getAllCategories()));

        productFilterCombo.getItems().clear();

        productFilterCombo.getItems().add("All Products");

        productFilterCombo.getItems().addAll(
                inventoryDAO.getAllProductNames());

        productFilterCombo.getSelectionModel().selectFirst();

        managerFilterCombo.getItems().clear();

        managerFilterCombo.getItems().add("All Managers");

        managerFilterCombo.getItems().addAll(
                inventoryDAO.getAllManagerNames());

        managerFilterCombo.getSelectionModel().selectFirst();

        statusCombo.getItems().setAll(
                "All",
                "In Stock",
                "Low Stock",
                "Out Of Stock");

        statusCombo.getSelectionModel().selectFirst();
    }

    private void addInventory() {

        if (!validateForm()) {
            return;
        }

        saveStateForUndo();

        Product product = productCombo.getValue();

        Warehouse warehouse = warehouseComboAdd.getValue();

        if (inventoryDAO.inventoryExists(warehouse.getWarehouseID(), product.getProductID())) {

            Alert alert = new Alert(Alert.AlertType.WARNING);

            alert.setHeaderText(null);

            alert.setContentText("This inventory record already exists.");

            alert.showAndWait();

            return;
        }

        Inventory inventory = new Inventory();

        inventory.setNo(inventoryTable.getItems().size() + 1);

        inventory.setProductID(product.getProductID());

        inventory.setProductName(product.getProductName());

        inventory.setCategoryName(product.getCategoryName());

        inventory.setWarehouseID(warehouse.getWarehouseID());

        inventory.setWarehouseName(warehouse.getWarehouseName());

        inventory.setManagerName(warehouse.getManagerName());

        inventory.setWarehouseCapacity(warehouse.getCapacity());

        double usedCapacity = originalInventory.stream()
                .filter(i -> i.getWarehouseID() == warehouse.getWarehouseID())
                .mapToDouble(Inventory::getQuantity)
                .sum();

        double newUsedCapacity = usedCapacity + inventory.getQuantity();

        inventory.setUsedCapacity(newUsedCapacity);
        inventory.setRemainingCapacity(
                warehouse.getCapacity() - newUsedCapacity);

        inventory.setQuantity(Double.parseDouble(quantityField.getText()));

        inventory.setMovementType("IN");

        inventory.setmovement_date(movement_datePicker.getValue());

        inventory.setUnitPrice(product.getPrice());

        inventory.setImagePath(product.getImagePath());

        inventory.setColor(product.getColor());

        inventory.setMaterial(product.getMaterial());

        inventory.setDescription(product.getDescription());

        inventory.setStockValue(inventory.getQuantity() * inventory.getUnitPrice());

        if (inventory.getQuantity() == 0) {

            inventory.setStatus("Out Of Stock");

        } else if (inventory.getQuantity() <= 10) {

            inventory.setStatus("Low Stock");

        } else {

            inventory.setStatus("In Stock");
        }

        pendingAdds.add(inventory);
        if (inventory.getmovement_date() == null) {

            inventory.setmovement_date(
                    LocalDate.now());
        }

        inventoryTable.getItems().add(inventory);

        originalInventory.add(inventory);

        recordsFoundLabel.setText(String.valueOf(inventoryTable.getItems().size()));

        clearForm();

        loadStats();

        refreshRowNumbers();

        redoStack.clear();
    }

    private void updateInventory() {

        if (!validateForm()) {
            return;
        }

        saveStateForUndo();

        Product product = productCombo.getValue();

        Warehouse warehouse = warehouseComboAdd.getValue();

        inventoryBeingUpdated.setProductID(product.getProductID());

        inventoryBeingUpdated.setProductName(product.getProductName());

        inventoryBeingUpdated.setCategoryName(product.getCategoryName());

        inventoryBeingUpdated.setWarehouseID(warehouse.getWarehouseID());

        inventoryBeingUpdated.setWarehouseName(warehouse.getWarehouseName());

        inventoryBeingUpdated.setQuantity(Double.parseDouble(quantityField.getText()));

        inventoryBeingUpdated.setMovementType("IN");

        inventoryBeingUpdated.setmovement_date(movement_datePicker.getValue());

        inventoryBeingUpdated.setUnitPrice(product.getPrice());

        inventoryBeingUpdated.setImagePath(product.getImagePath());

        inventoryBeingUpdated.setColor(product.getColor());

        inventoryBeingUpdated.setMaterial(product.getMaterial());

        inventoryBeingUpdated.setDescription(product.getDescription());

        inventoryBeingUpdated.setStockValue(inventoryBeingUpdated.getQuantity() * inventoryBeingUpdated.getUnitPrice());

        double qty = inventoryBeingUpdated.getQuantity();

        if (qty == 0) {

            inventoryBeingUpdated.setStatus("Out Of Stock");

        } else if (qty <= 10) {

            inventoryBeingUpdated.setStatus("Low Stock");

        } else {

            inventoryBeingUpdated.setStatus("In Stock");
        }

        if (!pendingUpdates.contains(inventoryBeingUpdated)) {

            pendingUpdates.add(inventoryBeingUpdated);
        }

        inventoryTable.refresh();

        recordsFoundLabel.setText(String.valueOf(inventoryTable.getItems().size()));

        clearForm();

        addInventoryPanel.setVisible(false);

        addInventoryPanel.setManaged(false);

        loadStats();

        refreshRowNumbers();

        redoStack.clear();
    }

    private void setupButtons() {

        addInventoryBtn.setOnAction(e -> {

            if (addInventoryPanel.isVisible()) {

                addInventoryPanel.setVisible(false);
                addInventoryPanel.setManaged(false);

                return;
            }

            inventoryBeingUpdated = null;

            clearForm();

            formTitleLabel.setText("✚ ADD INVENTORY");

            saveInventoryBtn.setText("ADD INVENTORY");

            showOnlyPanel(addInventoryPanel);
        });

        cancelAddBtn.setOnAction(e -> {

            clearForm();

            addInventoryPanel.setVisible(false);
            addInventoryPanel.setManaged(false);
        });

        filterToggleBtn.setOnAction(e -> {

            if (filterPanel.isVisible()) {

                filterPanel.setVisible(false);
                filterPanel.setManaged(false);

            } else {

                showOnlyPanel(filterPanel);
            }
        });

        saveInventoryBtn.setOnAction(e -> {

            if (inventoryBeingUpdated == null) {

                addInventory();

            } else {

                updateInventory();
            }
        });
        closeDetailsBtn.setOnAction(e -> {
            inventoryDetailsPanel.setVisible(false);
            inventoryDetailsPanel.setManaged(false);
        });

        saveAllBtn.setOnAction(e -> saveAllChanges());

        exportBtn.setOnAction(e -> exportInventoryToExcel());

        undoBtn.setOnAction(e -> undoAction());

        redoBtn.setOnAction(e -> redoAction());

        resetBtn.setOnAction(e -> resetTableChanges());
    }

    private void refreshRowNumbers() {

        for (int i = 0; i < inventoryTable.getItems().size(); i++) {

            inventoryTable.getItems().get(i).setNo(i + 1);
        }

        inventoryTable.refresh();
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

                    Inventory inventory = getTableView().getItems().get(getIndex());

                    openInventoryDetails(inventory);
                });

                editBtn.setOnAction(e -> {

                    Inventory inventory = getTableView().getItems().get(getIndex());

                    openUpdateForm(inventory);
                });

                deleteBtn.setOnAction(e -> {

                    Inventory inventory = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Inventory");
                    alert.setHeaderText(null);
                    alert.setContentText(
                            "Are you sure you want to delete inventory record for "
                                    + inventory.getProductName() + "?");

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isEmpty() || result.get() != ButtonType.OK) {
                        return;
                    }

                    saveStateForUndo();

                    pendingDeletes.add(inventory);

                    inventoryTable.getItems().remove(inventory);
                    originalInventory.remove(inventory);

                    recordsFoundLabel.setText(
                            String.valueOf(inventoryTable.getItems().size()));

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

    private void openInventoryDetails(Inventory inventory) {

        detailsProductNameLabel.setText(
                emptyToDash(inventory.getProductName()));

        detailsStatusLabel.setText(
                emptyToDash(inventory.getStatus()));

        detailsProductIdLabel.setText(
                String.valueOf(inventory.getProductID()));

        detailsCategoryLabel.setText(
                emptyToDash(inventory.getCategoryName()));

        setColorCircle(inventory.getColor());

        detailsMaterialLabel.setText(
                emptyToDash(inventory.getMaterial()));

        detailsPriceLabel.setText(
                String.format("$%,.2f", inventory.getUnitPrice()));

        detailsQuantityLabel.setText(
                String.format("%.0f", inventory.getQuantity()));

        detailsStockValueLabel.setText(
                String.format("$%,.2f", inventory.getStockValue()));

        detailsWarehouseLabel.setText(
                emptyToDash(inventory.getWarehouseName()));

        detailsManagerLabel.setText(
                emptyToDash(inventory.getManagerName()));

        detailsCapacityLabel.setText(
                String.format("%.0f", inventory.getWarehouseCapacity()));

        detailsUsedCapacityLabel.setText(
                String.format("%.0f", inventory.getUsedCapacity()));

        detailsRemainingCapacityLabel.setText(
                String.format("%.0f", inventory.getRemainingCapacity()));

        detailsDescriptionLabel.setText(
                emptyToDash(inventory.getDescription()));

        loadProductImage(inventory);

        loadMovementHistory(inventory);

        showOnlyPanel(inventoryDetailsPanel);
    }

    private void loadProductImage(Inventory inventory) {

        try {

            if (inventory.getImagePath() == null
                    || inventory.getImagePath().isBlank()) {

                detailsProductImage.setImage(null);

                return;
            }

            Image image = new Image(
                    getClass().getResourceAsStream(
                            inventory.getImagePath()));

            detailsProductImage.setImage(image);

        } catch (Exception e) {

            detailsProductImage.setImage(null);
        }
    }

    private void loadMovementHistory(Inventory inventory) {

        movementTable.setItems(
                FXCollections.observableArrayList(
                        inventoryDAO.getProductMovements(
                                inventory.getProductID())));
    }

    private void saveAllChanges() {

        try {

            for (Inventory inventory : pendingAdds) {

                inventoryDAO.insertInventory(
                        inventory.getWarehouseID(),
                        inventory.getProductID(),
                        inventory.getQuantity());

                inventoryDAO.insertStockMovement(
                        inventory.getProductID(),
                        "IN",
                        inventory.getQuantity(),
                        LocalDate.now());
            }

            for (Inventory inventory : pendingUpdates) {

                inventoryDAO.updateInventory(
                        inventory.getOldWarehouseID(),
                        inventory.getOldProductID(),
                        inventory.getWarehouseID(),
                        inventory.getProductID(),
                        inventory.getQuantity());

                inventoryDAO.insertStockMovement(
                        inventory.getProductID(),
                        "IN",
                        inventory.getQuantity(),
                        LocalDate.now());
            }

            for (Inventory inventory : pendingDeletes) {

                inventoryDAO.insertStockMovement(
                        inventory.getProductID(),
                        "OUT",
                        inventory.getQuantity(),
                        LocalDate.now());
                inventoryDAO.deleteInventory(
                        inventory.getWarehouseID(),
                        inventory.getProductID());
            }

            pendingAdds.clear();

            pendingUpdates.clear();

            pendingDeletes.clear();

            undoStack.clear();

            redoStack.clear();

            loadStats();

            closeAllPanels();

            loadInventory();

            loadStats();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Success");

            alert.setHeaderText(null);

            alert.setContentText(
                    "All inventory changes saved successfully.");

            alert.showAndWait();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void setupSearch() {

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {

            String keyword = newVal.toLowerCase().trim();

            if (keyword.isEmpty()) {

                inventoryTable.setItems(
                        FXCollections.observableArrayList(originalInventory));

                recordsFoundLabel.setText(
                        String.valueOf(originalInventory.size()));

                refreshRowNumbers();

                return;
            }

            List<Inventory> filtered = originalInventory.stream()

                    .filter(i -> contains(
                            String.valueOf(i.getProductID()),
                            keyword)
                            || contains(i.getProductName(), keyword)
                            || contains(i.getCategoryName(), keyword)
                            || contains(i.getWarehouseName(), keyword)
                            || contains(i.getManagerName(), keyword)
                            || contains(i.getStatus(), keyword))

                    .toList();

            inventoryTable.setItems(
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

        return date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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

    private void setupFilters() {

        applyFiltersBtn.setOnAction(e -> applyFilters());

        resetFiltersBtn.setOnAction(e -> resetFilters());
    }

    private void applyFilters() {

        Warehouse warehouse = warehouseFilterCombo.getValue();

        Category category = categoryFilterCombo.getValue();

        String product = productFilterCombo.getValue();

        String manager = managerFilterCombo.getValue();

        String status = statusCombo.getValue();

        double minQty = parseDoubleOrDefault(
                minQuantityField.getText(),
                0);

        double maxQty = parseDoubleOrDefault(
                maxQuantityField.getText(),
                Double.MAX_VALUE);

        double minValue = parseDoubleOrDefault(
                minValueField.getText(),
                0);

        double maxValue = parseDoubleOrDefault(
                maxValueField.getText(),
                Double.MAX_VALUE);

        List<Inventory> filtered = originalInventory.stream()

                .filter(i -> warehouse == null
                        || i.getWarehouseName()
                                .equals(
                                        warehouse.getWarehouseName()))

                .filter(i -> category == null
                        || i.getCategoryName()
                                .equals(
                                        category.getCategoryName()))

                .filter(i -> product == null
                        || product.equals("All Products")
                        || i.getProductName()
                                .equals(product))

                .filter(i -> manager == null
                        || manager.equals("All Managers")
                        || i.getManagerName()
                                .equals(manager))

                .filter(i -> status == null
                        || status.equals("All")
                        || i.getStatus()
                                .equals(status))

                .filter(i -> i.getQuantity() >= minQty
                        && i.getQuantity() <= maxQty)

                .filter(i -> i.getStockValue() >= minValue
                        && i.getStockValue() <= maxValue)

                .toList();

        inventoryTable.setItems(
                FXCollections.observableArrayList(filtered));

        recordsFoundLabel.setText(
                String.valueOf(filtered.size()));

        refreshRowNumbers();
    }

    private void resetFilters() {

        warehouseFilterCombo.setValue(null);

        categoryFilterCombo.setValue(null);

        productFilterCombo.getSelectionModel()
                .selectFirst();

        managerFilterCombo.getSelectionModel()
                .selectFirst();

        statusCombo.getSelectionModel()
                .selectFirst();

        minQuantityField.clear();
        maxQuantityField.clear();

        minValueField.clear();
        maxValueField.clear();

        inventoryTable.setItems(
                FXCollections.observableArrayList(
                        originalInventory));

        recordsFoundLabel.setText(
                String.valueOf(
                        originalInventory.size()));

        refreshRowNumbers();
    }

    private void showOnlyPanel(VBox panel) {

        filterPanel.setVisible(false);
        filterPanel.setManaged(false);

        addInventoryPanel.setVisible(false);
        addInventoryPanel.setManaged(false);

        inventoryDetailsPanel.setVisible(false);
        inventoryDetailsPanel.setManaged(false);

        panel.setVisible(true);
        panel.setManaged(true);
    }

    private void closeAllPanels() {

        filterPanel.setVisible(false);
        filterPanel.setManaged(false);

        addInventoryPanel.setVisible(false);
        addInventoryPanel.setManaged(false);

        inventoryDetailsPanel.setVisible(false);
        inventoryDetailsPanel.setManaged(false);
    }

    private void exportInventoryToExcel() {

        try {
            File exportFolder = new File("src/main/resources/exports");

            if (!exportFolder.exists()) {
                exportFolder.mkdirs();
            }

            String fileName = "inventory_" + LocalDate.now() + ".xlsx";
            File file = new File(exportFolder, fileName);

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Inventory");

            String[] headers = {
                    "No.",
                    "Product ID",
                    "Product Name",
                    "Category",
                    "Warehouse",
                    "Manager",
                    "Quantity",
                    "Unit Price",
                    "Stock Value",
                    "Status",
                    "Color",
                    "Material"
            };

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowIndex = 1;

            for (Inventory inventory : inventoryTable.getItems()) {

                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(inventory.getNo());
                row.createCell(1).setCellValue(inventory.getProductID());
                row.createCell(2).setCellValue(emptyToDash(inventory.getProductName()));
                row.createCell(3).setCellValue(emptyToDash(inventory.getCategoryName()));
                row.createCell(4).setCellValue(emptyToDash(inventory.getWarehouseName()));
                row.createCell(5).setCellValue(emptyToDash(inventory.getManagerName()));
                row.createCell(6).setCellValue(inventory.getQuantity());
                row.createCell(7).setCellValue(inventory.getUnitPrice());
                row.createCell(8).setCellValue(inventory.getStockValue());
                row.createCell(9).setCellValue(emptyToDash(inventory.getStatus()));
                row.createCell(10).setCellValue(emptyToDash(inventory.getColor()));
                row.createCell(11).setCellValue(emptyToDash(inventory.getMaterial()));
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
                    "Inventory exported successfully!\n\nSaved to:\n"
                            + file.getAbsolutePath());
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to export inventory file.");
            alert.showAndWait();
        }
    }

}
