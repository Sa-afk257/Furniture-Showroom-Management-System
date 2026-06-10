package com.furniture.ui;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.furniture.dao.ProductDAO;
import com.furniture.dao.ProductDAO.ProductStats;
import com.furniture.model.Product;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.scene.Node;

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
    private TableColumn<Product, String> colColor;
    @FXML
    private TableColumn<Product, String> colMaterial;
    @FXML
    private TableColumn<Product, Double> colQuantity;
    @FXML
    private TableColumn<Product, String> colStatus;
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

    private File selectedImageFile;

    @FXML
    private void initialize() {

        setupProductTable();
        loadProductsTable();
        loadProductStats();

        loadAddProductFormData();
        loadProductFilters();

        setupAddProductButtons();
        setupImageChooser();
        setupSaveProduct();

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
            if (addProductPanel.isVisible()) {
                addProductPanel.setVisible(false);
                addProductPanel.setManaged(false);
            } else {
                showOnlyPanel(addProductPanel);
            }
        });
    }

    private void setupProductTable() {

        colNo.setCellValueFactory(new PropertyValueFactory<>("no"));
        colID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colMaterial.setCellValueFactory(new PropertyValueFactory<>("material"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadProductsTable() {
        List<Product> products = productDAO.getAllProductsForTable();
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
            }
        });
    }

    private void setupSaveProduct() {
        saveProductBtn.setOnAction(e -> addProduct());
    }

    private void addProduct() {
        try {
            String name = addProductNameField.getText().trim();
            double price = Double.parseDouble(addPriceField.getText().trim());
            String categoryName = addCategoryCombo.getValue();
            String color = addColorField.getText().trim();
            String material = addMaterialField.getText().trim();
            String status = addStatusCombo.getValue();
            double stock = Double.parseDouble(addStockField.getText().trim());

            if (name.isEmpty() || categoryName == null || color.isEmpty()
                    || material.isEmpty() || selectedImageFile == null) {
                System.out.println("Please fill all fields and choose image.");
                return;
            }

            int categoryId = productDAO.getCategoryIdByName(categoryName);

            String imagePath = saveImageToResources(selectedImageFile);

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

            productDAO.insertProduct(product, stock);

            clearAddForm();
            addProductPanel.setVisible(false);
            addProductPanel.setManaged(false);

            loadProductsTable();
            loadProductStats();
            loadProductFilters();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearAddForm() {
        addProductNameField.clear();
        addPriceField.clear();
        addColorField.clear();
        addMaterialField.clear();
        addStockField.clear();
        addCategoryCombo.getSelectionModel().clearSelection();
        addStatusCombo.getSelectionModel().select("available");
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

            switch (color.toLowerCase()) {

                case "black" ->
                    circle.setStyle("-fx-background-color: #000000;");

                case "brown" ->
                    circle.setStyle("-fx-background-color: #6d4c41;");

                case "gray" ->
                    circle.setStyle("-fx-background-color: #808080;");

                case "white" ->
                    circle.setStyle("-fx-background-color: #f5f5f5;");

                case "beige" ->
                    circle.setStyle("-fx-background-color: #d6c6a5;");

                default ->
                    circle.setStyle("-fx-background-color: #999999;");
            }

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

            File destination = new File(
                    "src/main/resources/com/furniture/images/products/",
                    fileName);

            destination.getParentFile().mkdirs();

            java.nio.file.Files.copy(
                    imageFile.toPath(),
                    destination.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            return destination.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
