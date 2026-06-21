package com.furniture.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import com.furniture.dao.OrderDAO;
import com.furniture.model.OrderItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class OrdersController extends BaseController {

    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;

    @FXML
    private TextField orderSearchField;

    @FXML
    private Button allProductsBtn;
    @FXML
    private Button wishlistBtn;
    @FXML
    private Button cartTopBtn;
    @FXML
    private Button accountTopBtn;

    @FXML
    private Button allOrdersFilterBtn;
    @FXML
    private Button pendingFilterBtn;
    @FXML
    private Button deliveredFilterBtn;
    @FXML
    private Button cancelledFilterBtn;
    @FXML
    private Button clearFiltersBtn;

    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;

    @FXML
    private Label totalOrdersLabel;
    @FXML
    private Label pendingOrdersLabel;
    @FXML
    private Label deliveredOrdersLabel;
    @FXML
    private Label cancelledOrdersLabel;

    @FXML
    private VBox orderDetailsPanel;
    @FXML
    private Label detailsOrderIdLabel;
    @FXML
    private Label detailsStatusLabel;
    @FXML
    private Label detailsDateLabel;
    @FXML
    private Label detailsItemsCountLabel;
    @FXML
    private Label detailsTotalLabel;
    @FXML
    private Label detailsDeliveryLabel;
    @FXML
    private ImageView detailsProductImage;
    @FXML
    private Label detailsProductNameLabel;
    @FXML
    private Label detailsNoteLabel;
    @FXML
    private Label detailsStatusMessageLabel;
    @FXML
    private Button closeDetailsBtn;
    @FXML
    private VBox detailsItemsBox;

    @FXML
    private ComboBox<String> sortCombo;
    @FXML
    private VBox ordersBox;

    private final OrderDAO orderDAO = new OrderDAO();
    private List<OrderItem> allOrders;

    private String selectedStatus = "all";

    @FXML
    private void initialize() {
        startDateTime(dateLabel, timeLabel);
        createAccountMenu(accountTopBtn);
        updateCartButton(cartTopBtn);

        sortCombo.getItems().addAll(
                "Newest First",
                "Oldest First",
                "Highest Amount",
                "Lowest Amount");
        sortCombo.setValue("Newest First");

        allProductsBtn.setOnAction(e -> openAllProductsPage());
        wishlistBtn.setOnAction(e -> openWishlistPage());
        cartTopBtn.setOnAction(e -> openCartPage());

        orderSearchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        fromDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        toDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        sortCombo.setOnAction(e -> applyFilters());

        allOrdersFilterBtn.setOnAction(e -> setStatusFilter("all"));
        pendingFilterBtn.setOnAction(e -> setStatusFilter("pending"));
        deliveredFilterBtn.setOnAction(e -> setStatusFilter("delivered"));
        cancelledFilterBtn.setOnAction(e -> setStatusFilter("cancelled"));

        clearFiltersBtn.setOnAction(e -> clearFilters());

        closeDetailsBtn.setOnAction(e -> {
            orderDetailsPanel.setVisible(false);
            orderDetailsPanel.setManaged(false);
        });

        loadOrdersFromDatabase();
    }

    private void loadOrdersFromDatabase() {
        int customerId = Session.getCurrentCustomerId();

        if (customerId == -1) {
            ordersBox.getChildren().clear();
            updateStats(List.of());
            return;
        }

        allOrders = orderDAO.getOrdersByCustomerId(customerId);

        updateStats(allOrders);
        applyFilters();
    }

    private void applyFilters() {
        if (allOrders == null) {
            return;
        }

        String keyword = orderSearchField.getText() == null
                ? ""
                : orderSearchField.getText().trim().toLowerCase();

        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        List<OrderItem> filtered = allOrders.stream()
                .filter(order -> {
                    if (keyword.isBlank()) {
                        return true;
                    }

                    return String.valueOf(order.getSaleId()).contains(keyword)
                            || safe(order.getFirstProductName()).toLowerCase().contains(keyword)
                            || safe(order.getDeliveryStatus()).toLowerCase().contains(keyword);
                })
                .filter(order -> selectedStatus.equals("all")
                        || safe(order.getDeliveryStatus()).equalsIgnoreCase(selectedStatus))
                .filter(order -> fromDate == null
                        || !order.getSaleDate().isBefore(fromDate))
                .filter(order -> toDate == null
                        || !order.getSaleDate().isAfter(toDate))
                .toList();

        filtered = sortOrders(filtered);

        displayOrders(filtered);
    }

    private List<OrderItem> sortOrders(List<OrderItem> orders) {
        String sort = sortCombo.getValue();

        if ("Oldest First".equals(sort)) {
            return orders.stream()
                    .sorted(Comparator.comparing(OrderItem::getSaleDate)
                            .thenComparing(OrderItem::getSaleId))
                    .toList();
        }

        if ("Highest Amount".equals(sort)) {
            return orders.stream()
                    .sorted(Comparator.comparing(OrderItem::getTotalAmount).reversed())
                    .toList();
        }

        if ("Lowest Amount".equals(sort)) {
            return orders.stream()
                    .sorted(Comparator.comparing(OrderItem::getTotalAmount))
                    .toList();
        }

        return orders.stream()
                .sorted(
                        Comparator.comparing(OrderItem::getSaleDate).reversed()
                                .thenComparing(
                                        Comparator.comparing(OrderItem::getSaleId).reversed()))
                .toList();
    }

    private void displayOrders(List<OrderItem> orders) {
        ordersBox.getChildren().clear();

        if (orders.isEmpty()) {
            Label emptyLabel = new Label("No orders found.");
            emptyLabel.getStyleClass().add("order-meta-label");
            ordersBox.getChildren().add(emptyLabel);
            return;
        }

        for (OrderItem order : orders) {
            ordersBox.getChildren().add(createOrderCard(order));
        }
    }

    private void updateStats(List<OrderItem> orders) {
        totalOrdersLabel.setText(String.valueOf(orders.size()));

        pendingOrdersLabel.setText(String.valueOf(
                orders.stream().filter(o -> safe(o.getDeliveryStatus()).equalsIgnoreCase("pending")).count()));

        deliveredOrdersLabel.setText(String.valueOf(
                orders.stream().filter(o -> safe(o.getDeliveryStatus()).equalsIgnoreCase("delivered")).count()));

        cancelledOrdersLabel.setText(String.valueOf(
                orders.stream().filter(o -> safe(o.getDeliveryStatus()).equalsIgnoreCase("cancelled")).count()));
    }

    private HBox createOrderCard(OrderItem order) {
        HBox card = new HBox(18);
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        card.getStyleClass().add("order-row-card");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(135);
        imageView.setFitHeight(105);
        imageView.setPreserveRatio(false);

        try {
            if (order.getFirstImagePath() != null && !order.getFirstImagePath().isBlank()) {
                imageView.setImage(new Image(
                        getClass().getResource(order.getFirstImagePath()).toExternalForm()));
            }
        } catch (Exception e) {
            imageView.setImage(null);
        }

        VBox infoBox = new VBox(10);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label orderIdLabel = new Label("Order #" + order.getSaleId());
        orderIdLabel.getStyleClass().add("order-id-label");

        Label dateLabel = new Label(
                "📅 " + order.getSaleDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        dateLabel.getStyleClass().add("order-meta-label");

        Label itemLabel = new Label(
                safe(order.getFirstProductName()) + "  •  " + order.getItemsCount() + " Items");
        itemLabel.getStyleClass().add("order-meta-label");

        infoBox.getChildren().addAll(orderIdLabel, dateLabel, itemLabel);

        VBox statusBox = new VBox(12);
        statusBox.setPrefWidth(230);

        Label statusBadge = new Label(getStatusText(order.getDeliveryStatus()));
        statusBadge.getStyleClass().addAll("status-badge", getStatusStyle(order.getDeliveryStatus()));

        Label totalTitle = new Label("Total Amount");
        totalTitle.getStyleClass().add("order-total-title");

        Label totalValue = new Label(String.format("$%,.2f", order.getTotalAmount()));
        totalValue.getStyleClass().add("order-total-value");

        statusBox.getChildren().addAll(statusBadge, totalTitle, totalValue);

        VBox actionBox = new VBox(10);
        actionBox.setPrefWidth(170);

        Button detailsBtn = new Button("👁 View Details");
        detailsBtn.setMaxWidth(Double.MAX_VALUE);
        detailsBtn.getStyleClass().add("view-details-btn");
        detailsBtn.setOnAction(e -> showOrderDetails(order));

        actionBox.getChildren().add(detailsBtn);

        if (safe(order.getDeliveryStatus()).equalsIgnoreCase("pending")) {
            Button cancelBtn = new Button("🗑 Cancel Order");
            cancelBtn.setMaxWidth(Double.MAX_VALUE);
            cancelBtn.getStyleClass().add("cancel-order-btn");

            cancelBtn.setOnAction(e -> cancelOrder(order));

            actionBox.getChildren().add(cancelBtn);
        }

        card.getChildren().addAll(imageView, infoBox, statusBox, actionBox);

        return card;
    }

    private void cancelOrder(OrderItem order) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Order");
        confirm.setHeaderText("Cancel Order #" + order.getSaleId());
        confirm.setContentText("Are you sure you want to cancel this order?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        boolean cancelled = orderDAO.cancelOrder(order.getSaleId());

        if (cancelled) {
            loadOrdersFromDatabase();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cancel Failed");
            alert.setHeaderText(null);
            alert.setContentText("This order cannot be cancelled.");
            alert.showAndWait();
        }
    }

    private void showOrderDetails(OrderItem order) {

        detailsOrderIdLabel.setText("Order #" + order.getSaleId());

        detailsStatusLabel.setText(getStatusText(order.getDeliveryStatus()));
        detailsStatusLabel.getStyleClass().removeAll(
                "status-delivered",
                "status-pending",
                "status-cancelled");
        detailsStatusLabel.getStyleClass().add(getStatusStyle(order.getDeliveryStatus()));

        detailsDateLabel.setText(
                order.getSaleDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

        detailsItemsCountLabel.setText(order.getItemsCount() + " items");

        detailsTotalLabel.setText(String.format("$%,.2f", order.getTotalAmount()));

        detailsDeliveryLabel.setText(getStatusText(order.getDeliveryStatus()));

        detailsProductNameLabel.setText(safe(order.getFirstProductName()));

        detailsNoteLabel.setText(
                "This panel shows the main product preview. Full item details can be loaded from SaleDetails later.");

        detailsStatusMessageLabel.setText(getStatusMessage(order.getDeliveryStatus()));

        try {
            if (order.getFirstImagePath() != null && !order.getFirstImagePath().isBlank()) {
                detailsProductImage.setImage(new Image(
                        getClass().getResource(order.getFirstImagePath()).toExternalForm()));
            } else {
                detailsProductImage.setImage(null);
            }
        } catch (Exception e) {
            detailsProductImage.setImage(null);
        }

        orderDetailsPanel.setVisible(true);
        orderDetailsPanel.setManaged(true);
        detailsItemsBox.getChildren().clear();

        for (String item : orderDAO.getOrderProducts(order.getSaleId())) {
            Label label = new Label(item);
            label.getStyleClass().add("order-meta-label");
            detailsItemsBox.getChildren().add(label);
        }
    }

    private String getStatusMessage(String status) {

        if (safe(status).equalsIgnoreCase("delivered")) {
            return "Your order has been delivered successfully.";
        }

        if (safe(status).equalsIgnoreCase("cancelled")) {
            return "This order was cancelled and can no longer be changed.";
        }

        return "Your order is currently pending review. You can cancel it before delivery starts.";
    }

    private void setStatusFilter(String status) {
        selectedStatus = status;
        updateStatusButtons();
        applyFilters();
    }

    private void updateStatusButtons() {
        allOrdersFilterBtn.getStyleClass().remove("status-filter-active");
        pendingFilterBtn.getStyleClass().remove("status-filter-active");
        deliveredFilterBtn.getStyleClass().remove("status-filter-active");
        cancelledFilterBtn.getStyleClass().remove("status-filter-active");

        switch (selectedStatus) {
            case "pending" -> pendingFilterBtn.getStyleClass().add("status-filter-active");
            case "delivered" -> deliveredFilterBtn.getStyleClass().add("status-filter-active");
            case "cancelled" -> cancelledFilterBtn.getStyleClass().add("status-filter-active");
            default -> allOrdersFilterBtn.getStyleClass().add("status-filter-active");
        }
    }

    private void clearFilters() {
        selectedStatus = "all";
        orderSearchField.clear();
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        sortCombo.setValue("Newest First");

        updateStatusButtons();
        applyFilters();
    }

    private String getStatusText(String status) {
        if (safe(status).equalsIgnoreCase("delivered")) {
            return "✓ Delivered";
        }

        if (safe(status).equalsIgnoreCase("cancelled")) {
            return "✕ Cancelled";
        }

        if (safe(status).equalsIgnoreCase("approved")) {
            return "✓ Approved";
        }

        return "⏳ Pending Review";
    }

    private String getStatusStyle(String status) {
        if (safe(status).equalsIgnoreCase("delivered")) {
            return "status-delivered";
        }

        if (safe(status).equalsIgnoreCase("cancelled")) {
            return "status-cancelled";
        }

        if (safe(status).equalsIgnoreCase("approved")) {
            return "status-approved";
        }

        return "status-pending";
    }

    private String safe(String value) {
        return value == null ? "" : value;
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
    private void openCartPage() {
        openPage(cartTopBtn, "/view/CartView.fxml");
    }
}