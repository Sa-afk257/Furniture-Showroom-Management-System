package com.furniture.ui;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

import com.furniture.dao.ProductDAO;
import com.furniture.dao.ProductDAO.ProductStats;
import com.furniture.model.Product;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.scene.Node;
import java.io.InputStream;
import org.apache.poi.ss.usermodel.*;

public class ProductController {

    private ProductDAO productDAO = new ProductDAO();
    @FXML
    private Label totalProductsLabel;
    @FXML
    private Label inStockLabel;
    @FXML
    private Label lowStockLabel;
    @FXML
    private Label outOfStockLabel;
    @FXML
    private Label totalValueLabel;
    @FXML
    private ComboBox<String> categoryCombo;
    @FXML
    private ComboBox<String> warehouseCombo;
    @FXML
    private ComboBox<String> supplierCombo;
    @FXML
    private TextField minPriceField;
    @FXML
    private TextField maxPriceField;
    @FXML
    private ComboBox<String> stockLevelCombo;

    @FXML
    private FlowPane colorsPane;
    @FXML
    private FlowPane materialsPane;

    @FXML
    private ComboBox<String> salesPerformanceCombo;
    @FXML
    private ComboBox<String> discountStatusCombo;
    @FXML
    private ComboBox<String> returnRateCombo;

    @FXML
    private Button applyFiltersBtn;
    @FXML
    private Button resetFiltersBtn;
    @FXML
    private Label productsFoundLabel;

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> colNo;
    @FXML
    private TableColumn<Product, Integer> colID;
    @FXML
    private TableColumn<Product, String> colImage;
    @FXML
    private TableColumn<Product, String> colName;
    @FXML
    private TableColumn<Product, Double> colPrice;
    @FXML
    private TableColumn<Product, String> colCategory;
    @FXML
    private TableColumn<Product, String> colWarehouse;
    @FXML
    private TableColumn<Product, String> colSupplier;
    @FXML
    private TableColumn<Product, String> colColor;
    @FXML
    private TableColumn<Product, String> colMaterial;
    @FXML
    private TableColumn<Product, Double> colQuantity;
    @FXML
    private TableColumn<Product, String> colStatus;
    @FXML
    private TableColumn<Product, Void> colAction;

    @FXML
    private VBox filterPanel;
    @FXML
    private VBox addProductPanel;

    @FXML
    private Button filterToggleBtn;
    @FXML
    private Button addProductBtn;

    @FXML
    private ComboBox<String> addCategoryCombo;
    @FXML
    private ComboBox<String> addStatusCombo;
    @FXML
    private ComboBox<String> addWarehouseCombo;
    @FXML
    private ComboBox<String> addSupplierCombo;

    @FXML
    private Button cancelAddBtn;
    @FXML
    private TextField addProductNameField;
    @FXML
    private TextField addPriceField;
    @FXML
    private TextField addColorField;
    @FXML
    private TextField addMaterialField;
    @FXML
    private TextField addStockField;
    @FXML
    private Button chooseImageBtn;
    @FXML
    private Label selectedImageLabel;
    @FXML
    private Button saveProductBtn;
    @FXML
    private Label nameWarningLabel;
    @FXML
    private Label priceWarningLabel;
    @FXML
    private Label categoryWarningLabel;
    @FXML
    private Label warehouseWarningLabel;
    @FXML
    private Label supplierWarningLabel;
    @FXML
    private Label colorWarningLabel;
    @FXML
    private Label materialWarningLabel;
    @FXML
    private Label statusWarningLabel;
    @FXML
    private Label imageWarningLabel;
    @FXML
    private Label stockWarningLabel;
    @FXML
    private Label formTitleLabel;
    @FXML
    private Button undoBtn;
    @FXML
    private Button redoBtn;
    @FXML
    private Button resetBtn;
    @FXML
    private Button saveAllBtn;

    private File selectedImageFile;

    private List<Product> originalProducts = new ArrayList<>();
    private List<Product> pendingProducts = new ArrayList<>();
    private Stack<List<Product>> undoStack = new Stack<>();
    private Stack<List<Product>> redoStack = new Stack<>();
    private List<Product> pendingDeletes = new ArrayList<>();
    private List<Product> pendingUpdates = new ArrayList<>();
    private List<PendingProductAdd> pendingAdds = new ArrayList<>();

    private Map<String, String> colorMap = new HashMap<>();

    private Product productBeingUpdated = null;
    private boolean clearingForm = false;

    @FXML
    private void initialize() {

        loadColorsFromExcel();
        setupProductTable();
        loadProductsTable();
        loadProductStats();

        loadAddProductFormData();
        loadProductFilters();

        setupAddProductButtons();
        setupImageChooser();
        setupSaveProduct();
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

        addProductBtn.setOnAction(e -> {
            if (addProductPanel.isVisible() && productBeingUpdated == null) {
                addProductPanel.setVisible(false);
                addProductPanel.setManaged(false);
            } else {
                openAddForm();
            }
        });
        saveAllBtn.setOnAction(e -> saveAllChanges());
    }

    private void setupProductTable() {

        colNo.setCellValueFactory(new PropertyValueFactory<>("no"));
        colID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("imagePath"));

        colImage.setCellFactory(column -> new javafx.scene.control.TableCell<Product, String>() {

            private final javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();

            {
                imageView.setFitWidth(55);
                imageView.setFitHeight(45);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);

                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                    return;
                }

                try {
                    javafx.scene.image.Image image;

                    java.net.URL url = getClass().getResource(imagePath);

                    if (url == null) {
                        setGraphic(null);
                        setText("No image");
                        return;
                    }

                    image = new javafx.scene.image.Image(url.toExternalForm());

                    imageView.setImage(image);
                    setGraphic(imageView);
                    setText(null);

                    imageView.setImage(image);
                    setGraphic(imageView);
                    setText(null);

                } catch (Exception e) {
                    setGraphic(null);
                    setText("No image");
                }
            }
        });

        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colWarehouse.setCellValueFactory(new PropertyValueFactory<>("warehouseName"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colMaterial.setCellValueFactory(new PropertyValueFactory<>("material"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        setupActionColumn();
    }

    private void setupActionColumn() {

        colAction.setCellFactory(column -> new javafx.scene.control.TableCell<>() {

            private final Button editBtn = new Button("✎");
            private final Button deleteBtn = new Button("🗑");
            private final HBox box = new HBox(8, editBtn, deleteBtn);

            {
                box.setAlignment(javafx.geometry.Pos.CENTER);

                editBtn.getStyleClass().add("table-edit-btn");
                deleteBtn.getStyleClass().add("table-delete-btn");

                deleteBtn.setOnAction(e -> {

                    Product product = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Product");
                    alert.setHeaderText(null);
                    alert.setContentText(
                            "Are you sure you want to delete "
                                    + product.getProductName() + "?");

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isEmpty() || result.get() != ButtonType.OK) {
                        return;
                    }

                    saveStateForUndo();

                    pendingDeletes.add(product);
                    productTable.getItems().remove(product);

                    refreshRowNumbers();

                    redoStack.clear();
                });

                editBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    openUpdateForm(product);
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

    private void refreshRowNumbers() {
        for (int i = 0; i < productTable.getItems().size(); i++) {
            productTable.getItems().get(i).setNo(i + 1);
        }

        productTable.refresh();
    }

    private void loadColorsFromExcel() {

        try {

            InputStream is = getClass().getResourceAsStream("/data/colorsManual.xlsx");

            Workbook workbook = WorkbookFactory.create(is);

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null)
                    continue;

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

    private void loadProductsTable() {
        List<Product> products = productDAO.getAllProductsForTable();
        originalProducts.clear();
        originalProducts.addAll(products);
        productTable.setItems(FXCollections.observableArrayList(products));
    }

    private void loadProductStats() {

        ProductStats stats = productDAO.getProductStats();

        totalProductsLabel.setText(
                String.valueOf(
                        stats.getTotalProducts()));

        inStockLabel.setText(
                String.valueOf(
                        stats.getInStock()));

        lowStockLabel.setText(
                String.valueOf(
                        stats.getLowStock()));

        outOfStockLabel.setText(
                String.valueOf(
                        stats.getOutOfStock()));

        totalValueLabel.setText(
                String.format("$%,.0f",
                        stats.getTotalValue()));
    }

    private void loadAddProductFormData() {

        addCategoryCombo.getItems().setAll(productDAO.getCategories());
        addCategoryCombo.getSelectionModel().selectFirst();

        addWarehouseCombo.getItems().setAll(productDAO.getWarehouses());
        addWarehouseCombo.getSelectionModel().selectFirst();

        addSupplierCombo.getItems().setAll(productDAO.getSuppliers());
        addSupplierCombo.getSelectionModel().selectFirst();

        addStatusCombo.getItems().setAll("available", "outOfStock");
        addStatusCombo.getSelectionModel().select("available");
    }

    private void setupAddProductButtons() {
        cancelAddBtn.setOnAction(e -> {
            clearAddForm();
            addProductPanel.setVisible(false);
            addProductPanel.setManaged(false);
        });
    }

    private void setupImageChooser() {
        chooseImageBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Product Image");

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Image Files", "*.png", "*.jpg", "*.jpeg"));

            File file = fileChooser.showOpenDialog(chooseImageBtn.getScene().getWindow());

            if (file != null) {
                selectedImageFile = file;
                selectedImageLabel.setText(file.getName());

                imageWarningLabel.setVisible(false);
                imageWarningLabel.setManaged(false);
            }
        });
    }

    private void setupSaveProduct() {
        saveProductBtn.setOnAction(e -> {
            if (productBeingUpdated == null) {
                addProduct();
            } else {
                updateProduct();
            }
        });
    }

    private void openAddForm() {
        productBeingUpdated = null;

        clearAddForm();

        formTitleLabel.setText("✚ ADD PRODUCT");
        saveProductBtn.setText("ADD");

        showOnlyPanel(addProductPanel);
    }

    private void addProduct() {

        boolean valid = validateAddProductForm();

        if (!valid) {
            return;
        }

        String name = addProductNameField.getText().trim();
        double price = Double.parseDouble(addPriceField.getText().trim());
        String categoryName = addCategoryCombo.getValue();
        String warehouseName = addWarehouseCombo.getValue();
        String supplierName = addSupplierCombo.getValue();
        String color = addColorField.getText().trim();
        String material = addMaterialField.getText().trim();
        String status = addStatusCombo.getValue();
        double stock = Double.parseDouble(addStockField.getText().trim());

        int categoryId = productDAO.getCategoryIdByName(categoryName);
        String imagePath = saveImageToResources(selectedImageFile);
        int warehouseId = productDAO.getWarehouseIdByName(warehouseName);
        int supplierId = productDAO.getSupplierIdByName(supplierName);

        Product product = new Product(
                name,
                price,
                categoryId,
                color,
                material,
                "No description",
                status,
                LocalDateTime.now(),
                imagePath);

        saveStateForUndo();

        product.setNo(productTable.getItems().size() + 1);
        product.setCategoryName(categoryName);
        product.setStock(stock);
        product.setWarehouseName(warehouseName);
        product.setSupplierName(supplierName);

        pendingAdds.add(
                new PendingProductAdd(
                        product,
                        stock,
                        warehouseId,
                        supplierId));

        productTable.getItems().add(product);
        loadProductFilters();
        redoStack.clear();
        clearAddForm();
    }

    private static class PendingProductAdd {
        Product product;
        double stock;
        int warehouseId;
        int supplierId;

        PendingProductAdd(Product product, double stock, int warehouseId, int supplierId) {
            this.product = product;
            this.stock = stock;
            this.warehouseId = warehouseId;
            this.supplierId = supplierId;
        }
    }

    private boolean validateAddProductForm() {

        boolean valid = true;

        valid &= validateTextField(addProductNameField, nameWarningLabel);
        valid &= validateNumberField(addPriceField, priceWarningLabel);
        valid &= validateComboBox(addCategoryCombo, categoryWarningLabel);
        valid &= validateComboBox(addWarehouseCombo, warehouseWarningLabel);
        valid &= validateComboBox(addSupplierCombo, supplierWarningLabel);
        valid &= validateTextField(addColorField, colorWarningLabel);
        valid &= validateTextField(addMaterialField, materialWarningLabel);
        valid &= validateComboBox(addStatusCombo, statusWarningLabel);
        valid &= validateNumberField(addStockField, stockWarningLabel);

        boolean imageValid = selectedImageFile != null;
        imageWarningLabel.setVisible(!imageValid);
        imageWarningLabel.setManaged(!imageValid);

        return valid && imageValid;
    }

    private boolean validateTextField(TextField field, Label warningLabel) {
        boolean valid = !field.getText().trim().isEmpty();

        field.getStyleClass().remove("validation-error");

        if (!valid) {
            field.getStyleClass().add("validation-error");
        }

        warningLabel.setVisible(!valid);
        warningLabel.setManaged(!valid);

        return valid;
    }

    private boolean validateNumberField(TextField field, Label warningLabel) {
        boolean valid = true;

        try {
            double value = Double.parseDouble(field.getText().trim());
            if (value < 0) {
                valid = false;
            }
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

        addProductNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateTextField(addProductNameField, nameWarningLabel);
            }
        });

        addPriceField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateNumberField(addPriceField, priceWarningLabel);
            }
        });

        addColorField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateTextField(addColorField, colorWarningLabel);
            }
        });

        addMaterialField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateTextField(addMaterialField, materialWarningLabel);
            }
        });

        addStockField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateNumberField(addStockField, stockWarningLabel);
            }
        });

        addCategoryCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateComboBox(addCategoryCombo, categoryWarningLabel);
            }
        });

        addStatusCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateComboBox(addStatusCombo, statusWarningLabel);
            }
        });
    }

    private void clearAddForm() {

        clearingForm = true;

        addProductNameField.clear();
        addPriceField.clear();
        addColorField.clear();
        addMaterialField.clear();
        addStockField.clear();

        addCategoryCombo.getSelectionModel().clearSelection();
        addWarehouseCombo.getSelectionModel().selectFirst();
        addSupplierCombo.getSelectionModel().selectFirst();
        addStatusCombo.getSelectionModel().selectFirst();

        selectedImageFile = null;
        selectedImageLabel.setText("No image selected");

        clearingForm = false;

        hideAllWarnings();
        removeAllValidationErrors();

        productBeingUpdated = null;
        formTitleLabel.setText("✚ ADD PRODUCT");
        saveProductBtn.setText("ADD");
    }

    private void hideAllWarnings() {
        Label[] warnings = {
                nameWarningLabel,
                priceWarningLabel,
                categoryWarningLabel,
                colorWarningLabel,
                materialWarningLabel,
                statusWarningLabel,
                stockWarningLabel,
                imageWarningLabel
        };

        for (Label warning : warnings) {
            warning.setVisible(false);
            warning.setManaged(false);
        }
    }

    private void removeAllValidationErrors() {
        addProductNameField.getStyleClass().remove("validation-error");
        addPriceField.getStyleClass().remove("validation-error");
        addColorField.getStyleClass().remove("validation-error");
        addMaterialField.getStyleClass().remove("validation-error");
        addStockField.getStyleClass().remove("validation-error");

        addCategoryCombo.getStyleClass().remove("validation-error");
        addStatusCombo.getStyleClass().remove("validation-error");
    }

    private void openUpdateForm(Product product) {

        productBeingUpdated = product;

        showOnlyPanel(addProductPanel);

        addProductNameField.setText(product.getProductName());
        addPriceField.setText(String.valueOf(product.getPrice()));
        addCategoryCombo.setValue(product.getCategoryName());
        addColorField.setText(product.getColor());
        addMaterialField.setText(product.getMaterial());
        addStockField.setText(String.valueOf(product.getStock()));
        addStatusCombo.setValue(product.getStatus());

        selectedImageLabel.setText("Current image");
        selectedImageFile = null;

        imageWarningLabel.setVisible(false);
        imageWarningLabel.setManaged(false);

        formTitleLabel.setText("✏ UPDATE PRODUCT");
        saveProductBtn.setText("Update");
    }

    private void updateProduct() {

        boolean valid = validateAddProductFormForUpdate();

        if (!valid) {
            return;
        }

        saveStateForUndo();

        String name = addProductNameField.getText().trim();
        double price = Double.parseDouble(addPriceField.getText().trim());
        String categoryName = addCategoryCombo.getValue();
        String color = addColorField.getText().trim();
        String material = addMaterialField.getText().trim();
        String status = addStatusCombo.getValue();
        double stock = Double.parseDouble(addStockField.getText().trim());

        int categoryId = productDAO.getCategoryIdByName(categoryName);

        productBeingUpdated.setProductName(name);
        productBeingUpdated.setPrice(price);
        productBeingUpdated.setCategory_id(categoryId);
        productBeingUpdated.setCategoryName(categoryName);
        productBeingUpdated.setColor(color);
        productBeingUpdated.setMaterial(material);
        productBeingUpdated.setStatus(status);
        productBeingUpdated.setStock(stock);

        if (selectedImageFile != null) {
            String imagePath = saveImageToResources(selectedImageFile);
            productBeingUpdated.setImagePath(imagePath);
        }

        if (!pendingUpdates.contains(productBeingUpdated)) {
            pendingUpdates.add(productBeingUpdated);
        }
        productTable.refresh();

        productBeingUpdated = null;
        saveProductBtn.setText("Save Product");

        clearAddForm();

        addProductPanel.setVisible(false);
        addProductPanel.setManaged(false);

        redoStack.clear();
    }

    private boolean validateAddProductFormForUpdate() {

        boolean valid = true;

        valid &= validateTextField(addProductNameField, nameWarningLabel);
        valid &= validateNumberField(addPriceField, priceWarningLabel);
        valid &= validateComboBox(addCategoryCombo, categoryWarningLabel);
        valid &= validateTextField(addColorField, colorWarningLabel);
        valid &= validateTextField(addMaterialField, materialWarningLabel);
        valid &= validateComboBox(addStatusCombo, statusWarningLabel);
        valid &= validateNumberField(addStockField, stockWarningLabel);

        // imageWarningLabel.setVisible(false);
        // imageWarningLabel.setManaged(false);

        return valid;
    }

    private void saveStateForUndo() {
        undoStack.push(new ArrayList<>(productTable.getItems()));
    }

    private void createFilterChips(FlowPane pane, List<String> values) {
        pane.getChildren().clear();

        for (String value : values) {
            ToggleButton chip = new ToggleButton(value);
            chip.getStyleClass().add("filter-chip");
            pane.getChildren().add(chip);
        }
    }

    private List<String> getSelectedColors() {

        List<String> selected = new ArrayList<>();

        for (Node node : colorsPane.getChildren()) {

            if (node instanceof ToggleButton btn
                    && btn.isSelected()) {

                selected.add(
                        btn.getUserData().toString());
            }
        }

        return selected;
    }

    private void createColorCircles() {

        colorsPane.getChildren().clear();

        for (String color : productDAO.getColors()) {

            ToggleButton circle = new ToggleButton();

            circle.setUserData(color);

            circle.getStyleClass().add("color-circle");

            String key = color.toLowerCase().trim();

            String hex = colorMap.getOrDefault(
                    key,
                    "#999999");

            circle.setStyle(
                    "-fx-background-color: " + hex + ";");

            colorsPane.getChildren().add(circle);
        }
    }

    private void applyFilters() {
        String category = categoryCombo.getValue();
        String warehouse = warehouseCombo.getValue();
        String supplier = supplierCombo.getValue();
        String stockLevel = stockLevelCombo.getValue();

        String minPrice = minPriceField.getText();
        String maxPrice = maxPriceField.getText();

        List<String> selectedColors = getSelectedColors();
        List<String> selectedMaterials = getSelectedChips(materialsPane);

    }

    private List<String> getSelectedChips(FlowPane pane) {
        List<String> selected = new ArrayList<>();

        for (Node node : pane.getChildren()) {
            if (node instanceof ToggleButton chip && chip.isSelected()) {
                selected.add(chip.getText());
            }
        }

        return selected;
    }

    private void resetFilters() {

        categoryCombo.getSelectionModel().selectFirst();
        warehouseCombo.getSelectionModel().selectFirst();
        supplierCombo.getSelectionModel().selectFirst();

        stockLevelCombo.getSelectionModel().selectFirst();
        salesPerformanceCombo.getSelectionModel().selectFirst();
        discountStatusCombo.getSelectionModel().selectFirst();
        returnRateCombo.getSelectionModel().selectFirst();

        clearSelectedChips(colorsPane);
        clearSelectedChips(materialsPane);

        double[] range = productDAO.getPriceRange();
        minPriceField.setText(String.valueOf((int) range[0]));
        maxPriceField.setText(String.valueOf((int) range[1]));

        productsFoundLabel.setText(String.valueOf(productDAO.countProducts()));
    }

    private void clearSelectedChips(FlowPane pane) {
        for (Node node : pane.getChildren()) {
            if (node instanceof ToggleButton chip) {
                chip.setSelected(false);
            }
        }
    }

    private void loadProductFilters() {

        categoryCombo.getItems().setAll(productDAO.getCategories());
        categoryCombo.getItems().add(0, "All Categories");
        categoryCombo.getSelectionModel().selectFirst();

        warehouseCombo.getItems().setAll(productDAO.getWarehouses());
        warehouseCombo.getItems().add(0, "All Warehouses");
        warehouseCombo.getSelectionModel().selectFirst();

        supplierCombo.getItems().setAll(productDAO.getSuppliers());
        supplierCombo.getItems().add(0, "All Suppliers");
        supplierCombo.getSelectionModel().selectFirst();

        stockLevelCombo.getItems().setAll(
                "All",
                "Available In Stock",
                "Low Stock",
                "Out of Stock",
                "Overstocked");
        stockLevelCombo.getSelectionModel().selectFirst();

        salesPerformanceCombo.getItems().setAll(
                "All",
                "Top Selling",
                "Slow Moving",
                "Never Sold");
        salesPerformanceCombo.getSelectionModel().selectFirst();

        discountStatusCombo.getItems().setAll(
                "All",
                "Has Active Discount",
                "No Discount",
                "Expired Discount");
        discountStatusCombo.getSelectionModel().selectFirst();

        returnRateCombo.getItems().setAll(
                "All",
                "Returned Products",
                "High Return Rate",
                "No Returns");
        returnRateCombo.getSelectionModel().selectFirst();

        createColorCircles();
        createFilterChips(materialsPane, productDAO.getMaterials());

        double[] range = productDAO.getPriceRange();

        minPriceField.setText(String.valueOf((int) range[0]));
        maxPriceField.setText(String.valueOf((int) range[1]));

        productsFoundLabel.setText(String.valueOf(productDAO.countProducts()));
    }

    private void showOnlyPanel(VBox panel) {
        filterPanel.setVisible(false);
        filterPanel.setManaged(false);

        addProductPanel.setVisible(false);
        addProductPanel.setManaged(false);

        panel.setVisible(true);
        panel.setManaged(true);
    }

    private String saveImageToResources(File imageFile) {

        try {

            String fileName = System.currentTimeMillis()
                    + "_" + imageFile.getName();

            File sourceDestination = new File(
                    "src/main/resources/images/products/",
                    fileName);

            File runtimeDestination = new File(
                    "target/classes/images/products/",
                    fileName);

            sourceDestination.getParentFile().mkdirs();
            runtimeDestination.getParentFile().mkdirs();

            java.nio.file.Files.copy(
                    imageFile.toPath(),
                    sourceDestination.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            java.nio.file.Files.copy(
                    imageFile.toPath(),
                    runtimeDestination.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            return "/images/products/" + fileName;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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

        redoStack.push(new ArrayList<>(productTable.getItems()));

        List<Product> previousState = undoStack.pop();

        productTable.setItems(
                FXCollections.observableArrayList(previousState));

        refreshRowNumbers();
    }

    private void redoAction() {

        if (redoStack.isEmpty()) {
            return;
        }

        undoStack.push(new ArrayList<>(productTable.getItems()));

        List<Product> nextState = redoStack.pop();

        productTable.setItems(
                FXCollections.observableArrayList(nextState));

        refreshRowNumbers();
    }

    private void resetTableChanges() {

        pendingAdds.clear();
        pendingProducts.clear();
        pendingUpdates.clear();
        pendingDeletes.clear();

        undoStack.clear();
        redoStack.clear();

        loadProductsTable();
        loadProductStats();
        loadProductFilters();

        clearAddForm();

        addProductPanel.setVisible(false);
        addProductPanel.setManaged(false);
    }

    private void saveAllChanges() {

        try {
            for (PendingProductAdd item : pendingAdds) {
                productDAO.insertProduct(
                        item.product,
                        item.stock,
                        item.warehouseId,
                        item.supplierId);
            }

            for (Product product : pendingUpdates) {
                productDAO.updateProduct(product);
            }

            for (Product product : pendingDeletes) {
                productDAO.deleteProduct(product.getProductID());
            }

            pendingAdds.clear();
            pendingProducts.clear();
            pendingUpdates.clear();
            pendingDeletes.clear();

            undoStack.clear();
            redoStack.clear();

            loadProductsTable();
            loadProductStats();
            loadProductFilters();

            clearAddForm();

            addProductPanel.setVisible(false);
            addProductPanel.setManaged(false);

            System.out.println("All changes saved successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
