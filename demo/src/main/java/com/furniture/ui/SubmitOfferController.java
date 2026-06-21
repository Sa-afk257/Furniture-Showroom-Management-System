package com.furniture.ui;

import com.furniture.dao.CustomerDAO;
import com.furniture.dao.OrderDAO;
import com.furniture.model.Account;
import com.furniture.model.CartItem;
import com.furniture.model.Customer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SubmitOfferController extends BaseController {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;

    @FXML
    private Label customerNameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label contactMethodLabel;
    @FXML
    private Label bestTimeLabel;
    @FXML
    private Label validUntilLabel;
    @FXML
    private Label notesLabel;

    @FXML
    private Label itemsCountLabel;
    @FXML
    private VBox offerItemsBox;

    @FXML
    private Label originalTotalLabel;
    @FXML
    private Label offerTotalLabel;
    @FXML
    private Label savingsLabel;
    @FXML
    private Label submitMessageLabel;

    @FXML
    private Button submitOfferBtn;

    private List<CartItem> cartItems;
    private String deliveryAddress;
    private String paymentMethod;
    private double totalAmount;

    private double originalTotal = 0;
    private double offerTotal = 0;
    private double totalSavings = 0;

    @FXML
    private void initialize() {

        startDateTime(dateLabel, timeLabel);

        validUntilLabel.setText(
                LocalDate.now()
                        .plusDays(7)
                        .format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

        contactMethodLabel.setText("Phone Call");
        bestTimeLabel.setText("Evening");

        submitMessageLabel.setVisible(false);
        submitMessageLabel.setManaged(false);

        submitOfferBtn.setOnAction(e -> submitOffer());

    }

    public void loadOfferData(
            List<CartItem> cartItems,
            String deliveryAddress,
            String paymentMethod,
            double totalAmount) {

        this.cartItems = cartItems;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;

        loadCustomerInfo();
        loadNotes();
        loadOfferItems();
        updateSummary();
    }

    private void loadCustomerInfo() {

        if (Session.isGuest()) {
            customerNameLabel.setText("Guest");
            emailLabel.setText("Not Available");
            phoneLabel.setText("Not Available");
            return;
        }

        Account account = Session.getCurrentAccount();

        emailLabel.setText(account.getEmail());

        Customer customer = customerDAO.getCustomerById(
                Session.getCurrentCustomerId());

        if (customer == null) {
            return;
        }

        customerNameLabel.setText(
                customer.getFirstName()
                        + " "
                        + customer.getLastName());

        if (customer.getCustomer_phone() != null
                && !customer.getCustomer_phone().isEmpty()) {

            phoneLabel.setText(
                    customer.getCustomer_phone().get(0));
        }

        notesLabel.setText(
                "Delivery Address: "
                        + customer.getCity()
                        + ", "
                        + customer.getTown()
                        + ", "
                        + customer.getArea()
                        + ", Street "
                        + customer.getStreet()
                        + ", Building "
                        + customer.getBuilding());
    }

    private void loadNotes() {

        if (deliveryAddress == null || deliveryAddress.isBlank()) {
            notesLabel.setText("Delivery address was not confirmed.");
            return;
        }

        notesLabel.setText("Delivery Address: " + deliveryAddress);
    }

    private void loadOfferItems() {

        offerItemsBox.getChildren().clear();

        originalTotal = 0;
        offerTotal = 0;
        totalSavings = 0;

        if (cartItems == null || cartItems.isEmpty()) {
            itemsCountLabel.setText("(0)");
            return;
        }

        for (CartItem item : cartItems) {

            double originalLinePrice = item.getPrice() * item.getQuantity();
            double offerLinePrice = item.getLineTotal();
            double saving = item.getLineDiscount();

            originalTotal += originalLinePrice;
            offerTotal += offerLinePrice;
            totalSavings += saving;

            offerItemsBox.getChildren().add(
                    createOfferRow(item, originalLinePrice, offerLinePrice, saving));
        }

        itemsCountLabel.setText("(" + cartItems.size() + ")");
    }

    private HBox createOfferRow(
            CartItem item,
            double originalLinePrice,
            double offerLinePrice,
            double saving) {

        HBox row = new HBox(12);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.getStyleClass().add("cart-item-row");

        VBox productBox = new VBox(4);
        HBox.setHgrow(productBox, Priority.ALWAYS);

        Label nameLabel = new Label(item.getProductName());
        nameLabel.getStyleClass().add("cart-product-name");

        Label detailsLabel = new Label(
                "Category: " + item.getCategoryName()
                        + " | Color: " + item.getColor()
                        + " | Material: " + item.getMaterial()
                        + " | Qty: " + item.getQuantity());
        detailsLabel.getStyleClass().add("cart-product-info");

        productBox.getChildren().addAll(nameLabel, detailsLabel);

        Label originalPriceLabel = new Label(String.format("$%.2f", originalLinePrice));
        originalPriceLabel.setPrefWidth(140);
        originalPriceLabel.getStyleClass().add("summary-value");

        Label offerPriceLabel = new Label(String.format("$%.2f", offerLinePrice));
        offerPriceLabel.setPrefWidth(140);
        offerPriceLabel.getStyleClass().add("gold-text");

        Label savingLabel = new Label(String.format("$%.2f", saving));
        savingLabel.setPrefWidth(120);
        savingLabel.getStyleClass().add("discount-text");

        row.getChildren().addAll(
                productBox,
                originalPriceLabel,
                offerPriceLabel,
                savingLabel);

        return row;
    }

    private void updateSummary() {

        originalTotalLabel.setText(String.format("$%.2f", originalTotal));
        offerTotalLabel.setText(String.format("$%.2f", offerTotal));
        savingsLabel.setText(String.format("$%.2f", totalSavings));
    }

    @FXML
    private void openOrdersPage() {
        openPage(submitOfferBtn, "/view/OrdersView.fxml");
    }

    private void submitOffer() {

        if (cartItems == null || cartItems.isEmpty()) {
            showSubmitMessage("Your cart is empty.", true);
            return;
        }

        int customerId = Session.getCurrentCustomerId();

        if (customerId == -1) {
            showSubmitMessage("Please login before submitting your offer.", true);
            return;
        }

        boolean success = orderDAO.submitOrderFromCart(
                customerId,
                cartItems,
                offerTotal,
                paymentMethod);

        if (!success) {
            showSubmitMessage("Could not submit your offer. Please try again.", true);
            return;
        }

        showSubmitMessage("Your offer has been submitted successfully.", false);

        submitOfferBtn.setDisable(true);

        openOrdersPage();
    }

    private void showSubmitMessage(String message, boolean error) {

        submitMessageLabel.setText(message);

        submitMessageLabel.getStyleClass().removeAll(
                "checkout-message-success",
                "checkout-message-error");

        if (error) {
            submitMessageLabel.getStyleClass().add("checkout-message-error");
        } else {
            submitMessageLabel.getStyleClass().add("checkout-message-success");
        }

        submitMessageLabel.setVisible(true);
        submitMessageLabel.setManaged(true);
    }

}