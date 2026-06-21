package com.furniture.ui;

import com.furniture.dao.CartDAO;
import com.furniture.model.CartItem;
import com.furniture.ui.SubmitOfferController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class CartController extends BaseController {

    @FXML
    private TextField searchField;

    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;

    @FXML
    private Label cartTitleLabel;
    @FXML
    private VBox cartItemsBox;
    @FXML
    private VBox emptyCartBox;

    @FXML
    private Label subtotalLabel;
    @FXML
    private Label discountLabel;
    @FXML
    private Label deliveryFeeLabel;
    @FXML
    private Label totalLabel;

    @FXML
    private Button allProductsBtn;
    @FXML
    private Button wishlistBtn;
    @FXML
    private Button cartTopBtn;
    @FXML
    private Button ordersTopBtn;
    @FXML
    private Button clearCartBtn;
    @FXML
    private Button checkoutBtn;
    @FXML
    private Label deliveryAddressLabel;
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
    private Label checkoutMessageLabel;

    @FXML
    private Button confirmDeliveryBtn;
    @FXML
    private ComboBox<String> paymentMethodCombo;

    @FXML
    private Button accountTopBtn;

    private final CartDAO cartDAO = new CartDAO();

    private final double DELIVERY_FEE = 20.0;

    private List<CartItem> allItems = new ArrayList<>();

    @FXML
    private void initialize() {

        startDateTime(dateLabel, timeLabel);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applySearch());

        clearCartBtn.setOnAction(e -> clearCart());

        checkoutBtn.setOnAction(e -> proceedToCheckout());

        confirmDeliveryBtn.setOnAction(e -> confirmDeliveryInfo());

        deliveryAddressLabel.setText("Please confirm delivery information.");

        loadCart();

        createAccountMenu(accountTopBtn);

        loadDeliveryAddress();

        updateCartButton(cartTopBtn);
        paymentMethodCombo.getItems().setAll(
                "Cash",
                "Card",
                "Bank Transfer");

        paymentMethodCombo.setValue("Cash");
    }

    private void loadCart() {

        int customerId = Session.getCurrentCustomerId();

        if (customerId == -1) {
            updateEmptyCart(true);
            updateSummary(0, 0);
            return;
        }

        allItems = cartDAO.getCartItems(customerId);

        displayItems(allItems);
    }

    private void applySearch() {

        String keyword = searchField.getText().toLowerCase().trim();

        if (keyword.isBlank()) {
            displayItems(allItems);
            return;
        }

        List<CartItem> filtered = allItems.stream()

                .filter(item -> item.getProductName().toLowerCase().contains(keyword)

                        || item.getCategoryName().toLowerCase().contains(keyword)

                        || item.getColor().toLowerCase().contains(keyword)

                        || item.getMaterial().toLowerCase().contains(keyword))
                .toList();

        displayItems(filtered);
    }

    private void loadDeliveryAddress() {

        deliveryAddressLabel.setText(
                "Address from customer profile will be used for delivery.");
    }

    private void confirmDeliveryInfo() {

        String city = cityField.getText().trim();
        String town = townField.getText().trim();
        String area = areaField.getText().trim();
        String street = streetField.getText().trim();
        String building = buildingField.getText().trim();

        if (city.isBlank() || town.isBlank() || area.isBlank()
                || street.isBlank() || building.isBlank()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all delivery information.");
            alert.showAndWait();
            return;
        }

        String address = city + ", " + town + ", " + area
                + ", Street " + street
                + ", Building " + building;

        deliveryAddressLabel.setText(address);
    }

    private void proceedToCheckout() {

        if (allItems.isEmpty()) {
            showCheckoutMessage("Your cart is empty.", true);
            return;
        }

        if (deliveryAddressLabel.getText() == null
                || deliveryAddressLabel.getText().equals("Please confirm delivery information.")) {

            showCheckoutMessage("Please confirm delivery information first.", true);
            return;
        }

        String paymentMethod = paymentMethodCombo.getValue();

        if (paymentMethod == null || paymentMethod.isBlank()) {
            showCheckoutMessage("Please select payment method.", true);
            return;
        }

        double subtotal = 0;
        double discount = 0;

        for (CartItem item : allItems) {
            subtotal += item.getLineTotal();
            discount += item.getLineDiscount();
        }

        double totalAmount = subtotal - discount + DELIVERY_FEE;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Checkout");
        confirm.setHeaderText("Confirm your order");

        confirm.setContentText(
                "Delivery Address:\n" + deliveryAddressLabel.getText()
                        + "\n\nPayment Method: " + paymentMethod
                        + "\nTotal Amount: $" + String.format("%.2f", totalAmount)
                        + "\n\nDo you want to place this order?");

        if (confirm.showAndWait().isEmpty()
                || confirm.getResult() != ButtonType.OK) {
            return;
        }

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/view/SubmitOrderView.fxml"));

            Parent page = loader.load();

            SubmitOfferController controller = loader.getController();

            controller.loadOfferData(
                    allItems,
                    deliveryAddressLabel.getText(),
                    paymentMethod,
                    totalAmount);

            Stage stage = (Stage) checkoutBtn.getScene().getWindow();
            stage.setScene(new Scene(page));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCheckoutMessage(String message, boolean error) {

        checkoutMessageLabel.setText(message);

        checkoutMessageLabel.getStyleClass().removeAll(
                "checkout-message-success",
                "checkout-message-error");

        if (error) {
            checkoutMessageLabel.getStyleClass().add("checkout-message-error");
        } else {
            checkoutMessageLabel.getStyleClass().add("checkout-message-success");
        }

        checkoutMessageLabel.setVisible(true);
        checkoutMessageLabel.setManaged(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        pause.setOnFinished(e -> {
            checkoutMessageLabel.setVisible(false);
            checkoutMessageLabel.setManaged(false);
        });

        pause.play();
    }

    private void fillFormFromDeliveryLine() {

        String address = deliveryAddressLabel.getText();

        if (address == null
                || address.isBlank()
                || address.equals("Please confirm delivery information.")) {
            return;
        }

        String[] parts = address.split(",");

        if (parts.length >= 5) {

            cityField.setText(parts[0].trim());
            townField.setText(parts[1].trim());
            areaField.setText(parts[2].trim());

            streetField.setText(
                    parts[3].replace("Street", "").trim());

            buildingField.setText(
                    parts[4].replace("Building", "").trim());
        }
    }

    private void displayItems(List<CartItem> items) {

        cartItemsBox.getChildren().clear();

        double subtotal = 0;
        double discount = 0;

        for (CartItem item : items) {
            cartItemsBox.getChildren().add(createCartRow(item));
            subtotal += item.getLineTotal();
            discount += item.getLineDiscount();
        }

        updateSummary(subtotal, discount);
        updateEmptyCart(allItems.isEmpty());

        cartTitleLabel.setText("Your Cart (" + allItems.size() + " items)");
    }

    private HBox createCartRow(CartItem item) {

        HBox row = new HBox(12);
        row.getStyleClass().add("cart-item-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(95);
        imageView.setPreserveRatio(false);

        try {
            if (item.getImagePath() != null && !item.getImagePath().isBlank()) {
                imageView.setImage(new Image(
                        getClass().getResource(item.getImagePath()).toExternalForm()));
            }
        } catch (Exception e) {
            imageView.setImage(null);
        }

        VBox productBox = new VBox(4);

        Label nameLabel = new Label(item.getProductName());
        nameLabel.getStyleClass().add("cart-product-name");

        Label categoryLabel = new Label("Category: " + item.getCategoryName());
        categoryLabel.getStyleClass().add("cart-product-info");

        Label colorLabel = new Label("Color: " + item.getColor());
        colorLabel.getStyleClass().add("cart-product-info");

        Label materialLabel = new Label("Material: " + item.getMaterial());
        materialLabel.getStyleClass().add("cart-product-info");

        Button minusBtn = new Button("-");
        Label qtyLabel = new Label(String.valueOf(item.getQuantity()));
        Button plusBtn = new Button("+");

        Label stockLabel;

        if (item.getStock() <= 0) {

            stockLabel = new Label("✕ Out of Stock");
            stockLabel.getStyleClass().add("cart-stock-out");

            plusBtn.setDisable(true);

        } else if (item.getStock() <= 3) {

            stockLabel = new Label("⚠ Low Stock (" + (int) item.getStock() + " left)");
            stockLabel.getStyleClass().add("cart-stock-low");

        } else {

            stockLabel = new Label("✓ In Stock");
            stockLabel.getStyleClass().add("cart-stock-ok");
        }

        productBox.getChildren().addAll(
                nameLabel,
                categoryLabel,
                colorLabel,
                materialLabel,
                stockLabel);

        HBox.setHgrow(productBox, Priority.ALWAYS);
        Label priceLabel = new Label(String.format("$%.2f", item.getPrice()));
        priceLabel.setPrefWidth(115);
        priceLabel.getStyleClass().add("cart-price-text");

        minusBtn.getStyleClass().add("qty-btn");
        plusBtn.getStyleClass().add("qty-btn");
        qtyLabel.getStyleClass().add("qty-label");

        HBox qtyBox = new HBox(7, minusBtn, qtyLabel, plusBtn);
        qtyBox.setAlignment(javafx.geometry.Pos.CENTER);
        qtyBox.setPrefWidth(145);

        Label totalLabel = new Label(String.format("$%.2f", item.getLineTotal()));
        totalLabel.setPrefWidth(120);
        totalLabel.getStyleClass().add("cart-total-text");

        Button removeBtn = new Button("🗑️");
        removeBtn.setPrefWidth(50);
        removeBtn.getStyleClass().add("remove-cart-btn");

        plusBtn.setOnAction(e -> {

            if (item.getQuantity() >= item.getStock()) {
                showCheckoutMessage("Maximum available quantity reached.", true);
                return;
            }

            int newQty = item.getQuantity() + 1;

            cartDAO.updateQuantity(
                    Session.getCurrentCustomerId(),
                    item.getProductID(),
                    newQty);

            loadCart();
            updateCartButton(cartTopBtn);
        });

        minusBtn.setOnAction(e -> {
            int newQty = item.getQuantity() - 1;

            if (newQty <= 0) {
                cartDAO.removeItem(Session.getCurrentCustomerId(), item.getProductID());
            } else {
                cartDAO.updateQuantity(Session.getCurrentCustomerId(), item.getProductID(), newQty);
            }

            loadCart();
            updateCartButton(cartTopBtn);
        });

        removeBtn.setOnAction(e -> {
            cartDAO.removeItem(Session.getCurrentCustomerId(), item.getProductID());
            loadCart();
            updateCartButton(cartTopBtn);
        });

        row.getChildren().addAll(
                imageView,
                productBox,
                priceLabel,
                qtyBox,
                totalLabel,
                removeBtn);

        return row;
    }

    private void updateSummary(double subtotal, double discount) {

        double deliveryFee = subtotal == 0 ? 0.0 : DELIVERY_FEE;
        double total = subtotal - discount + deliveryFee;

        subtotalLabel.setText(String.format("$%.2f", subtotal));
        discountLabel.setText(String.format("-$%.2f", discount));
        deliveryFeeLabel.setText(String.format("$%.2f", deliveryFee));
        totalLabel.setText(String.format("$%.2f", total));
    }

    private void updateEmptyCart(boolean isEmpty) {

        emptyCartBox.setVisible(isEmpty);
        emptyCartBox.setManaged(isEmpty);

        cartItemsBox.setVisible(!isEmpty);
        cartItemsBox.setManaged(!isEmpty);

        clearCartBtn.setDisable(isEmpty);
        checkoutBtn.setDisable(isEmpty);
    }

    private void clearCart() {

        int customerId = Session.getCurrentCustomerId();

        if (customerId == -1)
            return;

        cartDAO.clearCart(customerId);

        loadCart();
        updateCartButton(cartTopBtn);
    }

    @FXML
    private void openWishlistPage() {
        super.openWishlistPage(wishlistBtn);
    }

    @FXML
    private void openAllProductsPage() {
        super.openAllProductsPage(allProductsBtn);
    }

    @FXML
    private void openOrdersPage() {
        openPage(ordersTopBtn, "/view/OrdersView.fxml");
    }
}