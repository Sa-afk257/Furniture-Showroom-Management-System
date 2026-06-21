package com.furniture.ui;

import com.furniture.dao.CartDAO;
import com.furniture.dao.FavoriteDAO;
import com.furniture.model.Product;
import com.furniture.ui.Session;

import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ProductDetailsController extends BaseController {

    private FavoriteDAO favoriteDAO = new FavoriteDAO();
    private CartDAO cartDAO = new CartDAO();

    @FXML
    private Label productNameLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label materialLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label warehouseLabel;
    @FXML
    private Label supplierLabel;
    @FXML
    private Label stockLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label productIdLabel;
    @FXML
    private Label createdDateLabel;
    @FXML
    private Button favoriteBtn;
    @FXML
    private Button addToCartBtn;
    @FXML
    private Button cartTopBtn;

    private Product currentProduct;

    @FXML
    private ImageView productImage;

    @FXML
    private HBox colorsContainer;
    private final Map<String, String> colorMap = new HashMap<>();

    @FXML
    private void initialize() {
        loadColorsFromExcel();
        updateCartButton(cartTopBtn);
    }

    private void loadColorsFromExcel() {

        try {
            InputStream is = getClass().getResourceAsStream("/data/colorsManual.xlsx");

            if (is == null) {
                System.out.println("colorsManual.xlsx not found");
                return;
            }

            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null || row.getCell(0) == null || row.getCell(1) == null) {
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

    private void showColors(String colorsText) {

        colorsContainer.getChildren().clear();

        if (colorsText == null || colorsText.isBlank()) {
            colorsContainer.getChildren().add(createColorItem("Unknown", "#999999"));
            return;
        }

        String[] colors = colorsText.split(",");

        for (String color : colors) {

            String colorName = color.trim();
            String key = colorName.toLowerCase();

            String hex = colorMap.getOrDefault(key, "#999999");

            colorsContainer.getChildren().add(createColorItem(colorName, hex));
        }
    }

    private HBox createColorItem(String colorName, String hex) {

        Pane dot = new Pane();
        dot.getStyleClass().add("color-dot");
        dot.setStyle(
                "-fx-background-color: " + hex + ";" +
                        "-fx-border-color: #f8d77a;");

        Label name = new Label(colorName);
        name.getStyleClass().add("color-name");

        HBox box = new HBox(7, dot, name);
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        return box;
    }

    private Pane createColorDot(String hex) {

        Pane dot = new Pane();

        dot.getStyleClass().add("color-dot");

        dot.setStyle(
                "-fx-background-color: " + hex + ";" +
                        "-fx-border-color: #f8d77a;");

        return dot;
    }

    @FXML
    private void goBack() {
        Stage stage = (Stage) productNameLabel.getScene().getWindow();
        stage.close();
    }

    public void setProduct(Product product) {

        if (product == null) {
            return;
        }

        this.currentProduct = product;

        productNameLabel.setText(empty(product.getProductName()));
        priceLabel.setText(String.format("$ %.2f", product.getPrice()));

        categoryLabel.setText(empty(product.getCategoryName()));
        materialLabel.setText(empty(product.getMaterial()));
        statusLabel.setText(empty(product.getStatus()));
        warehouseLabel.setText(empty(product.getWarehouseName()));
        supplierLabel.setText(empty(product.getSupplierName()));
        stockLabel.setText(String.valueOf(product.getStock()));
        descriptionLabel.setText(empty(product.getDescription()));
        productIdLabel.setText(String.valueOf(product.getProductID()));

        if (product.getCreatedDate() != null) {
            createdDateLabel.setText(product.getCreatedDate().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            createdDateLabel.setText("-");
        }

        setStatusStyle(product.getStatus());
        showColors(product.getColor());
        loadImage(product.getImagePath());

        int customerId = Session.getCurrentCustomerId();

        if (customerId != -1) {
            product.setFavorite(
                    favoriteDAO.isFavorite(customerId, product.getProductID()));
        }

        updateFavoriteButton();
    }

    private void updateFavoriteButton() {

        if (currentProduct.isFavorite()) {
            favoriteBtn.setText("♥");
            favoriteBtn.setStyle("-fx-text-fill: #ff4d6d;");
        } else {
            favoriteBtn.setText("♡");
            favoriteBtn.setStyle("-fx-text-fill: #f8d77a;");
        }
    }

    private boolean requireLogin(String message) {

        if (Session.isGuest()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Login Required");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();

            return false;
        }

        return true;
    }

    @FXML
    private void handleFavoriteClick() {

        if (!requireLogin("Login required to save favorites.")) {
            return;
        }

        if (currentProduct == null) {
            return;
        }

        int customerId = Session.getCurrentCustomerId();

        if (customerId == -1) {
            return;
        }

        favoriteDAO.toggleFavorite(
                customerId,
                currentProduct.getProductID());

        boolean fav = favoriteDAO.isFavorite(
                customerId,
                currentProduct.getProductID());

        currentProduct.setFavorite(fav);

        updateFavoriteButton();
    }

    private void loadImage(String path) {
        try {
            if (path == null || path.isBlank()) {
                clearImages();
                return;
            }

            Image image = null;

            File file = new File(path);

            if (file.exists()) {
                image = new Image(file.toURI().toString());
            } else {
                java.net.URL url = getClass().getResource(path);

                if (url != null) {
                    image = new Image(url.toExternalForm());
                }
            }

            if (image == null || image.isError()) {
                clearImages();
                return;
            }

            productImage.setImage(image);

        } catch (Exception e) {
            clearImages();
            System.out.println("Image not found: " + path);
        }
    }

    private void clearImages() {
        productImage.setImage(null);

    }

    private void setStatusStyle(String status) {

        statusLabel.getStyleClass().removeAll("status-available", "status-unavailable");

        if (status != null && status.equalsIgnoreCase("available")) {
            statusLabel.getStyleClass().add("status-available");
        } else {
            statusLabel.getStyleClass().add("status-unavailable");
        }
    }

    private String empty(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    @FXML
    private void handleAddToCart() {

        if (!requireLogin("Login required to add products to cart.")) {
            return;
        }

        if (currentProduct == null) {
            return;
        }

        cartDAO.addToCart(
                Session.getCurrentCustomerId(),
                currentProduct.getProductID());
        updateCartButton(cartTopBtn);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Cart");
        alert.setHeaderText(null);

        alert.setContentText(
                currentProduct.getProductName()
                        + " added to cart successfully");

        alert.showAndWait();
    }

}