package com.furniture.ui;

import com.furniture.dao.CartDAO;
import com.furniture.dao.FavoriteDAO;
import com.furniture.dao.ProductDAO;
import com.furniture.model.Account;
import com.furniture.model.Product;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.poi.ss.usermodel.*;

public class GuestController extends BaseController {

    private PageMode mode = PageMode.ALL_PRODUCTS;
    private CartDAO cartDAO = new CartDAO();

    private FavoriteDAO favoriteDAO = new FavoriteDAO();
    private ProductDAO productDAO = new ProductDAO();

    @FXML
    private TextField searchField;
    @FXML
    private TextField filterSearchField;

    @FXML
    private ComboBox<String> categoryCombo;

    @FXML
    private TextField minPriceField;
    @FXML
    private TextField maxPriceField;
    @FXML
    private Slider priceSlider;

    @FXML
    private Label minPriceLabel;
    @FXML
    private Label maxPriceLabel;

    @FXML
    private FlowPane colorsPane;
    @FXML
    private FlowPane materialsPane;

    @FXML
    private RadioButton allAvailabilityRadio;
    @FXML
    private RadioButton inStockRadio;
    @FXML
    private RadioButton lowStockRadio;
    @FXML
    private RadioButton outOfStockRadio;

    @FXML
    private Button resetFiltersBtn;

    @FXML
    private Label productsFoundLabel;
    @FXML
    private TilePane productsCardsPane;

    @FXML
    private HBox guestActionsBox;

    @FXML
    private HBox userActionsBox;

    @FXML
    private Button accountTopBtn;

    @FXML
    private Button allProductsBtn;
    @FXML
    private Button wishlistBtn;
    @FXML
    private Button cartTopBtn;
    @FXML
    private Button ordersTopBtn;

    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;

    private List<Product> originalProducts = new ArrayList<>();
    private Map<String, String> colorMap = new HashMap<>();

    @FXML
    private void initialize() {

        loadColorsFromExcel();
        super.startDateTime(dateLabel, timeLabel);

        loadProducts();

        loadFilters();

        setupLiveFilters();

        applyFilters();

        updateUserArea();

        createAccountMenu(accountTopBtn);

        updateNavigationButtons();

        updateCartButton(cartTopBtn);

    }

    public void setMode(PageMode mode) {
        this.mode = mode;
        loadProducts();
        updateNavigationButtons();
    }

    private void updateNavigationButtons() {

        allProductsBtn.getStyleClass().remove("active-top-btn");
        wishlistBtn.getStyleClass().remove("active-top-btn");

        if (mode == PageMode.ALL_PRODUCTS) {

            allProductsBtn.getStyleClass().add("active-top-btn");

        } else {

            wishlistBtn.getStyleClass().add("active-top-btn");
        }
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

    private void updateUserArea() {

        if (Session.isGuest()) {

            guestActionsBox.setVisible(true);
            guestActionsBox.setManaged(true);

            userActionsBox.setVisible(false);
            userActionsBox.setManaged(false);

        } else {

            guestActionsBox.setVisible(false);
            guestActionsBox.setManaged(false);

            userActionsBox.setVisible(true);
            userActionsBox.setManaged(true);

            Account account = Session.getCurrentAccount();

        }
    }

    private void createColorCircles() {

        colorsPane.getChildren().clear();

        for (String color : productDAO.getColors()) {

            ToggleButton circle = new ToggleButton();

            circle.setUserData(color);
            circle.getStyleClass().add("color-circle");

            String key = color.toLowerCase().trim();

            String hex = colorMap.getOrDefault(key, "#999999");

            circle.setStyle("-fx-background-color: " + hex + ";");

            circle.setOnAction(e -> applyFilters());

            colorsPane.getChildren().add(circle);
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

    private double parseDoubleOrDefault(String text, double defaultValue) {
        try {
            return Double.parseDouble(text.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private boolean stockMatches(Product p, String stockLevel) {

        if (stockLevel == null || stockLevel.equals("All")) {
            return true;
        }

        double stock = p.getStock();

        return switch (stockLevel) {
            case "In Stock" -> stock > 5;
            case "Low Stock" -> stock > 0 && stock <= 5;
            case "Out of Stock" -> stock == 0;
            default -> true;
        };
    }

    private void resetFilters() {

        searchField.clear();
        filterSearchField.clear();

        categoryCombo.getSelectionModel().selectFirst();

        double[] range = productDAO.getPriceRange();

        minPriceField.setText(String.valueOf((int) range[0]));
        maxPriceField.setText(String.valueOf((int) range[1]));

        minPriceLabel.setText("$" + (int) range[0]);
        maxPriceLabel.setText("$" + (int) range[1]);

        priceSlider.setMin(range[0]);
        priceSlider.setMax(range[1]);
        priceSlider.setValue(range[1]);

        clearSelectedColors();
        clearSelectedMaterials();

        allAvailabilityRadio.setSelected(true);

        applyFilters();
    }

    private void clearSelectedColors() {

        for (Node node : colorsPane.getChildren()) {
            if (node instanceof ToggleButton btn) {
                btn.setSelected(false);
            }
        }
    }

    private void clearSelectedMaterials() {

        for (Node node : materialsPane.getChildren()) {
            if (node instanceof CheckBox cb) {
                cb.setSelected(false);
            }
        }
    }

    private void loadProducts() {

        originalProducts.clear();

        int customerId = Session.getCurrentCustomerId();

        if (mode == PageMode.FAVORITES) {

            if (customerId == -1) {
                productsFoundLabel.setText("0");
                displayProductCards(originalProducts);
                return;
            }

            originalProducts.addAll(
                    favoriteDAO.getFavoriteProducts(customerId));

        } else {

            originalProducts.addAll(
                    productDAO.getAllProductsForTable());

            if (customerId != -1) {
                for (Product product : originalProducts) {
                    boolean fav = favoriteDAO.isFavorite(
                            customerId,
                            product.getProductID());

                    product.setFavorite(fav);
                }
            }
        }

        applyFilters();
    }

    private void loadFilters() {

        categoryCombo.getItems().setAll(productDAO.getCategories());
        categoryCombo.getItems().add(0, "All Categories");
        categoryCombo.getSelectionModel().selectFirst();

        createColorCircles();
        createMaterialChecks();

        double[] range = productDAO.getPriceRange();

        minPriceField.setText(String.valueOf((int) range[0]));
        maxPriceField.setText(String.valueOf((int) range[1]));

        minPriceLabel.setText("$" + (int) range[0]);
        maxPriceLabel.setText("$" + (int) range[1]);

        priceSlider.setMin(range[0]);
        priceSlider.setMax(range[1]);
        priceSlider.setValue(range[1]);

        productsFoundLabel.setText(String.valueOf(originalProducts.size()));
    }

    private void createMaterialChecks() {

        materialsPane.getChildren().clear();

        for (String material : productDAO.getMaterials()) {

            CheckBox cb = new CheckBox(material);
            cb.getStyleClass().add("filter-check");

            cb.setOnAction(e -> applyFilters());

            materialsPane.getChildren().add(cb);
        }
    }

    private List<String> getSelectedMaterials() {

        List<String> selected = new ArrayList<>();

        for (Node node : materialsPane.getChildren()) {
            if (node instanceof CheckBox cb && cb.isSelected()) {
                selected.add(cb.getText());
            }
        }

        return selected;
    }

    private void setupLiveFilters() {

        searchField.textProperty().addListener(
                (obs, oldVal, newVal) -> applyFilters());

        filterSearchField.textProperty().addListener(
                (obs, oldVal, newVal) -> applyFilters());

        categoryCombo.setOnAction(
                e -> applyFilters());

        minPriceField.textProperty().addListener(
                (obs, oldVal, newVal) -> applyFilters());

        maxPriceField.textProperty().addListener(
                (obs, oldVal, newVal) -> applyFilters());

        priceSlider.valueProperty().addListener((obs, oldVal, newVal) -> {

            maxPriceField.setText(
                    String.valueOf(newVal.intValue()));

            maxPriceLabel.setText(
                    "$" + newVal.intValue());

            applyFilters();
        });

        ToggleGroup availabilityGroup = new ToggleGroup();

        allAvailabilityRadio.setToggleGroup(availabilityGroup);
        inStockRadio.setToggleGroup(availabilityGroup);
        lowStockRadio.setToggleGroup(availabilityGroup);
        outOfStockRadio.setToggleGroup(availabilityGroup);

        availabilityGroup.selectedToggleProperty().addListener(
                (obs, oldVal, newVal) -> applyFilters());

        resetFiltersBtn.setOnAction(
                e -> resetFilters());
    }

    private void applyFilters() {

        String keyword = (searchField.getText() + " " + filterSearchField.getText())
                .toLowerCase()
                .trim();

        String category = categoryCombo.getValue();

        double minPrice = parseDoubleOrDefault(minPriceField.getText(), 0);

        double maxPrice = parseDoubleOrDefault(maxPriceField.getText(), Double.MAX_VALUE);

        final String availability;

        if (inStockRadio.isSelected()) {
            availability = "In Stock";
        } else if (lowStockRadio.isSelected()) {
            availability = "Low Stock";
        } else if (outOfStockRadio.isSelected()) {
            availability = "Out of Stock";
        } else {
            availability = "All";
        }

        List<String> selectedColors = getSelectedColors();
        List<String> selectedMaterials = getSelectedMaterials();

        List<Product> filtered = originalProducts.stream()

                .filter(p -> keyword.isBlank()
                        || contains(p.getProductName(), keyword)
                        || contains(p.getCategoryName(), keyword)
                        || contains(p.getMaterial(), keyword)
                        || contains(p.getColor(), keyword))

                .filter(p -> category == null
                        || category.equals("All Categories")
                        || category.equals(p.getCategoryName()))

                .filter(p -> p.getPrice() >= minPrice
                        && p.getPrice() <= maxPrice)

                .filter(p -> stockMatches(p, availability))

                .filter(p -> selectedColors.isEmpty()
                        || selectedColors.stream()
                                .anyMatch(c -> c.equalsIgnoreCase(p.getColor())))

                .filter(p -> selectedMaterials.isEmpty()
                        || selectedMaterials.contains(p.getMaterial()))

                .toList();

        productsFoundLabel.setText(
                String.valueOf(filtered.size()));

        displayProductCards(filtered);
    }

    private void displayProductCards(List<Product> products) {

        productsCardsPane.getChildren().clear();

        for (Product product : products) {

            VBox card = createProductCard(product);

            productsCardsPane.getChildren().add(card);
        }
    }

    private VBox createProductCard(Product product) {

        VBox card = new VBox(9);
        card.getStyleClass().add("product-card");

        StackPane imageBox = new StackPane();
        imageBox.getStyleClass().add("product-image-box");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(215);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(false);
        imageView.getStyleClass().add("product-image");

        try {
            if (product.getImagePath() != null && !product.getImagePath().isBlank()) {
                Image image = new Image(
                        getClass().getResource(product.getImagePath()).toExternalForm());
                imageView.setImage(image);
            }
        } catch (Exception e) {
            imageView.setImage(null);
        }

        Button heartBtn = new Button("♡");
        heartBtn.getStyleClass().add("heart-btn");

        updateFavoriteIcon(heartBtn, product);

        heartBtn.setOnAction(e -> {

            int customerId = Session.getCurrentCustomerId();

            if (customerId == -1) {
                openLoginPage();
                return;
            }

            favoriteDAO.toggleFavorite(
                    Session.getCurrentCustomerId(),
                    product.getProductID());

            product.setFavorite(!product.isFavorite());

            updateFavoriteIcon(heartBtn, product);

            if (mode == PageMode.FAVORITES && !product.isFavorite()) {
                originalProducts.remove(product);
                applyFilters();
            }
        });

        StackPane.setAlignment(heartBtn, javafx.geometry.Pos.TOP_RIGHT);
        StackPane.setMargin(heartBtn, new javafx.geometry.Insets(8));

        imageBox.getChildren().addAll(imageView, heartBtn);

        Label nameLabel = new Label(product.getProductName());
        nameLabel.getStyleClass().add("card-name");

        Label priceLabel = new Label(String.format("$%,.2f", product.getPrice()));
        priceLabel.getStyleClass().add("card-price");

        Button detailsBtn = new Button("View Details");
        detailsBtn.getStyleClass().add("card-btn");
        detailsBtn.setMaxWidth(Double.MAX_VALUE);
        detailsBtn.setOnAction(e -> showProductDetails(product));

        Button buyBtn = new Button();

        buyBtn.getStyleClass().add("buy-btn");
        buyBtn.setMaxWidth(Double.MAX_VALUE);

        if (Session.isGuest()) {

            buyBtn.setText("Login To Buy");

            buyBtn.setOnAction(e -> openLoginPage());

        } else {

            buyBtn.setText("Add To Cart");
            buyBtn.setOnAction(e -> {

                int customerId = Session.getCurrentCustomerId();

                cartDAO.addToCart(
                        customerId,
                        product.getProductID());

                updateCartButton(cartTopBtn);

            });

        }

        HBox buttons = new HBox(8, detailsBtn, buyBtn);
        HBox.setHgrow(detailsBtn, Priority.ALWAYS);
        HBox.setHgrow(buyBtn, Priority.ALWAYS);

        card.getChildren().addAll(imageBox, nameLabel, priceLabel, buttons);

        return card;
    }

    @FXML
    private void openWishlistPage() {

        if (Session.isGuest() || Session.getCurrentCustomerId() == -1) {
            openLoginPage();
            return;
        }

        super.openWishlistPage(wishlistBtn);
    }

    @FXML
    private void openAllProductsPage() {
        super.openAllProductsPage(allProductsBtn);
    }

    @FXML
    private void openCartPage() {
        openPage(cartTopBtn, "/view/CartView.fxml");
    }

    @FXML
    private void openOrdersPage() {
        openPage(ordersTopBtn, "/view/OrdersView.fxml");
    }

    private void updateFavoriteIcon(Button heartBtn, Product product) {

        if (product.isFavorite()) {

            heartBtn.setText("♥");

            heartBtn.setStyle("""
                        -fx-text-fill: #ff4d6d;
                    """);

        } else {

            heartBtn.setText("♡");

            heartBtn.setStyle("""
                        -fx-text-fill: #f8d77a;
                    """);
        }
    }

    private void showProductDetails(Product product) {

        try {
            int customerId = Session.getCurrentCustomerId();

            if (customerId != -1) {
                product.setFavorite(
                        favoriteDAO.isFavorite(customerId, product.getProductID()));
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/ProductDetailsView.fxml"));

            Parent root = loader.load();

            ProductDetailsController controller = loader.getController();
            controller.setProduct(product);

            Stage stage = new Stage();
            stage.setTitle("Product Details");
            stage.setScene(new Scene(root));

            stage.setOnHidden(e -> {
                loadProducts();
            });

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openLoginPage() {
        try {
            Stage stage = (Stage) productsCardsPane.getScene().getWindow();

            Scene previousScene = stage.getScene();

            Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
            Scene loginScene = new Scene(root);

            stage.setScene(loginScene);
            stage.setUserData(previousScene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
