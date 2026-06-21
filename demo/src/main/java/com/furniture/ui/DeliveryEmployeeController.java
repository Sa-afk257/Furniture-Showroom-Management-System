package com.furniture.ui;

import java.util.List;

import com.furniture.dao.DeliveryEmployeeDAO;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DeliveryEmployeeController extends BaseController {

    private final DeliveryEmployeeDAO deliveryDAO = new DeliveryEmployeeDAO();
    @FXML
    private ScrollPane deliveryScroll;

    @FXML
    private Button accountMenuBtn;
    @FXML
    private Label assignedTodayLabel;
    @FXML
    private Label completedTodayLabel;
    @FXML
    private Label inProgressLabel;
    @FXML
    private Label pendingPickupLabel;

    @FXML
    private Label assignedTodayDescLabel;
    @FXML
    private Label completedTodayDescLabel;
    @FXML
    private Label inProgressDescLabel;
    @FXML
    private Label pendingPickupDescLabel;

    @FXML
    private TextField deliverySearchField;
    @FXML
    private Button searchBtn;

    @FXML
    private Label allTabLabel;
    @FXML
    private Label pickupTabLabel;
    @FXML
    private Label progressTabLabel;
    @FXML
    private Label completedTabLabel;

    @FXML
    private VBox deliveriesListBox;

    @FXML
    private Label currentDeliveryStatusLabel;
    @FXML
    private Label selectedOrderIdLabel;
    @FXML
    private Label customerNameLabel;
    @FXML
    private Label customerPhoneLabel;
    @FXML
    private Label customerAddressLabel;
    @FXML
    private Label deliveryTimeLabel;

    @FXML
    private Button callCustomerBtn;
    @FXML
    private Button markDeliveredBtn;
    @FXML
    private Button startDeliveryBtn;
    @FXML
    private Button markPickedUpBtn;
    @FXML
    private Button markDeliveredSideBtn;
    @FXML
    private Button reportIssueBtn;

    @FXML
    private GridPane deliveryItemsGrid;
    @FXML
    private Label itemsCountLabel;

    @FXML
    private VBox recentActivityBox;

    @FXML
    private Label routeAddressLabel;

    @FXML
    private LineChart<String, Number> deliveryPerformanceChart;
    private boolean showAllDeliveries = false;
    private int selectedDeliveryId = -1;
    private int selectedSaleId = -1;

    private DeliveryTab currentTab = DeliveryTab.ALL;

    private enum DeliveryTab {
        ALL, PICKUP, IN_PROGRESS, COMPLETED
    }

    @FXML
    public void initialize() {
        createAccountMenu(accountMenuBtn);
        setupActions();
        if (Session.getCurrentEmployeeId() <= 0) {
            Session.setCurrentEmployeeId(3);
        }
        loadKpiCards();
        loadDeliveriesList();
        loadRecentActivity();

        clearSelectedDelivery();
    }

    private void setupActions() {
        searchBtn.setOnAction(e -> loadDeliveriesList());
        deliverySearchField.setOnAction(e -> loadDeliveriesList());
        startDeliveryBtn.setOnAction(e -> handleStartDelivery());
        markPickedUpBtn.setOnAction(e -> handleMarkPickedUp());
        markDeliveredBtn.setOnAction(e -> handleMarkDelivered());
        markDeliveredSideBtn.setOnAction(e -> handleMarkDelivered());
        reportIssueBtn.setOnAction(e -> handleReportIssue());
        callCustomerBtn.setOnAction(e -> handleCallCustomer());
    }

    @FXML
    private void showAllTab() {
        currentTab = DeliveryTab.ALL;
        updateTabStyles();
        loadDeliveriesList();
    }

    @FXML
    private void showPickupTab() {
        currentTab = DeliveryTab.PICKUP;
        updateTabStyles();
        loadDeliveriesList();
    }

    @FXML
    private void showProgressTab() {
        currentTab = DeliveryTab.IN_PROGRESS;
        updateTabStyles();
        loadDeliveriesList();
    }

    @FXML
    private void showCompletedTab() {
        currentTab = DeliveryTab.COMPLETED;
        updateTabStyles();
        loadDeliveriesList();
    }

    private void updateTabStyles() {
        allTabLabel.getStyleClass().setAll(currentTab == DeliveryTab.ALL ? "tab-active" : "tab-text");
        pickupTabLabel.getStyleClass().setAll(currentTab == DeliveryTab.PICKUP ? "tab-active" : "tab-text");
        progressTabLabel.getStyleClass().setAll(currentTab == DeliveryTab.IN_PROGRESS ? "tab-active" : "tab-text");
        completedTabLabel.getStyleClass().setAll(currentTab == DeliveryTab.COMPLETED ? "tab-active" : "tab-text");
    }

    private void loadKpiCards() {

        int employeeId = Session.getCurrentEmployeeId();

        assignedTodayLabel.setText(String.valueOf(deliveryDAO.getAssignedTodayCount(employeeId)));
        completedTodayLabel.setText(String.valueOf(deliveryDAO.getCompletedTodayCount(employeeId)));
        inProgressLabel.setText(String.valueOf(deliveryDAO.getInProgressCount(employeeId)));
        pendingPickupLabel.setText(String.valueOf(deliveryDAO.getPendingPickupCount(employeeId)));

        assignedTodayDescLabel.setText("Deliveries assigned today");
        completedTodayDescLabel.setText("Completed today");
        inProgressDescLabel.setText("Currently active");
        pendingPickupDescLabel.setText("Need pickup from warehouse");
    }

    private void loadDeliveriesList() {

        deliveriesListBox.getChildren().clear();

        int employeeId = Session.getCurrentEmployeeId();
        if (employeeId <= 0) {
            employeeId = 3;
        }

        String keyword = "";
        if (deliverySearchField != null && deliverySearchField.getText() != null) {
            keyword = deliverySearchField.getText().trim();
        }

        String filter = "all";

        if (currentTab == DeliveryTab.PICKUP) {
            filter = "pickup";
        } else if (currentTab == DeliveryTab.IN_PROGRESS) {
            filter = "in_progress";
        } else if (currentTab == DeliveryTab.COMPLETED) {
            filter = "completed";
        }

        List<DeliveryEmployeeDAO.DeliverySummary> deliveries = deliveryDAO.getDeliveries(employeeId, keyword, filter);

        if (deliveries.isEmpty()) {
            Label empty = new Label("No deliveries found.");
            empty.getStyleClass().add("activity-desc");
            deliveriesListBox.getChildren().add(empty);
            return;
        }

        int limit = showAllDeliveries ? deliveries.size() : Math.min(3, deliveries.size());

        for (int i = 0; i < limit; i++) {
            deliveriesListBox.getChildren().add(createDeliveryCard(deliveries.get(i)));
        }

        if (!showAllDeliveries && deliveries.size() > 3) {
            Label more = new Label("+" + (deliveries.size() - 3) + " more deliveries");
            more.getStyleClass().add("activity-desc");
            deliveriesListBox.getChildren().add(more);
        }
    }

    private VBox createDeliveryCard(DeliveryEmployeeDAO.DeliverySummary delivery) {

        VBox card = new VBox(7);
        card.getStyleClass().add("delivery-card");

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label orderId = new Label("#ORD-" + String.format("%04d", delivery.getSaleId()));
        orderId.getStyleClass().add("order-id");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label status = new Label(formatStatus(delivery.getStatus()));
        status.getStyleClass().add(getDeliveryStatusStyle(delivery.getStatus()));

        top.getChildren().addAll(orderId, spacer, status);

        Label customer = new Label(delivery.getCustomerName());
        customer.getStyleClass().add("activity-title");

        Label address = new Label(delivery.getAddress());
        address.getStyleClass().add("activity-desc");

        Label date = new Label("Delivery Date: " + delivery.getDeliveryDate());
        date.getStyleClass().add("activity-desc");

        card.getChildren().addAll(top, customer, address, date);

        card.setOnMouseClicked(e -> {
            selectedDeliveryId = delivery.getDeliveryId();
            selectedSaleId = delivery.getSaleId();

            loadSelectedDelivery(delivery);
            markSelectedDeliveryCard(card);
        });

        return card;
    }

    private void loadSelectedDelivery(DeliveryEmployeeDAO.DeliverySummary delivery) {

        selectedOrderIdLabel.setText("#ORD-" + String.format("%04d", delivery.getSaleId()));
        customerNameLabel.setText(delivery.getCustomerName());
        customerPhoneLabel.setText(delivery.getPhone() == null ? "-" : delivery.getPhone());
        customerAddressLabel.setText(delivery.getAddress());
        deliveryTimeLabel.setText(delivery.getDeliveryDate());

        currentDeliveryStatusLabel.setText(formatStatus(delivery.getStatus()));
        currentDeliveryStatusLabel.getStyleClass().setAll(getDeliveryStatusStyle(delivery.getStatus()));

        loadDeliveryItems(delivery.getSaleId());
        routeAddressLabel.setText(delivery.getAddress());
    }

    @FXML
    private void handleOpenMap() {

        try {

            String address = customerAddressLabel.getText();

            if (address == null || address.equals("-")) {
                showError("Please select a delivery first.");
                return;
            }

            String query = address.replace(" ", "+");

            java.awt.Desktop.getDesktop().browse(
                    new java.net.URI(
                            "https://www.google.com/maps/search/?api=1&query=" + query));

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not open Google Maps.");
        }
    }

    private void loadDeliveryItems(int saleId) {

        deliveryItemsGrid.getChildren().removeIf(node -> {
            Integer row = GridPane.getRowIndex(node);
            return row != null && row > 0;
        });

        List<DeliveryEmployeeDAO.DeliveryItem> items = deliveryDAO.getDeliveryItems(saleId);

        itemsCountLabel.setText(items.size() + " items");

        int row = 1;

        for (DeliveryEmployeeDAO.DeliveryItem item : items) {

            Label no = new Label(String.valueOf(row));
            Label product = new Label(item.getProductName());
            Label qty = new Label(String.valueOf(item.getQuantity()));
            Label status = new Label("Ready");

            no.getStyleClass().add("activity-desc");
            product.getStyleClass().add("activity-title");
            qty.getStyleClass().add("activity-desc");
            status.getStyleClass().add("success-badge");

            deliveryItemsGrid.add(no, 0, row);
            deliveryItemsGrid.add(product, 1, row);
            deliveryItemsGrid.add(qty, 2, row);
            deliveryItemsGrid.add(status, 3, row);

            row++;
        }
    }

    private void markSelectedDeliveryCard(VBox selectedCard) {

        for (Node node : deliveriesListBox.getChildren()) {
            node.getStyleClass().remove("delivery-card-active");

            if (!node.getStyleClass().contains("delivery-card")) {
                node.getStyleClass().add("delivery-card");
            }
        }

        selectedCard.getStyleClass().remove("delivery-card");
        selectedCard.getStyleClass().add("delivery-card-active");
    }

    private void loadRecentActivity() {

        recentActivityBox.getChildren().clear();

        int employeeId = Session.getCurrentEmployeeId();

        List<String> activities = deliveryDAO.getRecentActivity(employeeId);

        if (activities.isEmpty()) {
            Label empty = new Label("No recent activity yet.");
            empty.getStyleClass().add("activity-desc");
            recentActivityBox.getChildren().add(empty);
            return;
        }

        for (String text : activities) {
            Label row = new Label("● " + text);
            row.getStyleClass().add("activity-desc");
            recentActivityBox.getChildren().add(row);
        }
    }

    private void clearSelectedDelivery() {
        selectedDeliveryId = -1;
        selectedSaleId = -1;

        currentDeliveryStatusLabel.setText("Not Selected");
        currentDeliveryStatusLabel.getStyleClass().setAll("neutral-badge");

        selectedOrderIdLabel.setText("-");
        customerNameLabel.setText("-");
        customerPhoneLabel.setText("-");
        customerAddressLabel.setText("-");
        deliveryTimeLabel.setText("-");
        itemsCountLabel.setText("0 items");

        deliveryItemsGrid.getChildren().removeIf(node -> {
            Integer row = GridPane.getRowIndex(node);
            return row != null && row > 0;
        });
        routeAddressLabel.setText("-");

    }

    @FXML
    private void handleViewAllDeliveries() {
        showAllDeliveries = !showAllDeliveries;
        loadDeliveriesList();
    }

    @FXML
    private void handleSearchDeliveries() {
        loadDeliveriesList();
    }

    @FXML
    private void handleCallCustomer() {
        if (selectedDeliveryId <= 0) {
            showError("Please select a delivery first.");
            return;
        }
        showInfo("Customer phone: " + customerPhoneLabel.getText());
    }

    @FXML
    private void handleStartDelivery() {

        if (selectedDeliveryId <= 0) {
            showError("Please select a delivery first.");
            return;
        }

        boolean done = deliveryDAO.updateDeliveryStatus(selectedDeliveryId, "in_progress");

        if (done) {
            showInfo("Delivery started.");
            refreshPage();
        } else {
            showError("Could not start delivery.");
        }
    }

    @FXML
    private void handleMarkPickedUp() {

        if (selectedDeliveryId <= 0) {
            showError("Please select a delivery first.");
            return;
        }

        boolean done = deliveryDAO.updateDeliveryStatus(selectedDeliveryId, "picked_up");

        if (done) {
            showInfo("Delivery marked as picked up.");
            refreshPage();
        } else {
            showError("Could not update delivery.");
        }
    }

    @FXML
    private void handleMarkDelivered() {

        if (selectedDeliveryId <= 0 || selectedSaleId <= 0) {
            showError("Please select a delivery first.");
            return;
        }

        boolean done = deliveryDAO.markDelivered(selectedDeliveryId, selectedSaleId);

        if (done) {
            showInfo("Order delivered successfully.");
            clearSelectedDelivery();
            refreshPage();
        } else {
            showError("Could not mark order as delivered.");
        }
    }

    private void refreshPage() {
        loadKpiCards();
        loadDeliveriesList();
        loadRecentActivity();
    }

    private String formatStatus(String status) {

        if (status == null) {
            return "Unknown";
        }

        switch (status) {
            case "pending":
                return "Pending";
            case "assigned":
                return "Assigned";
            case "picked_up":
                return "Picked Up";
            case "in_progress":
                return "In Progress";
            case "delivered":
                return "Delivered";
            case "issue_reported":
                return "Issue Reported";
            default:
                return status;
        }
    }

    private String getDeliveryStatusStyle(String status) {

        if (status == null) {
            return "neutral-badge";
        }

        switch (status) {
            case "delivered":
                return "success-badge";
            case "in_progress":
                return "warning-badge";
            case "picked_up":
            case "assigned":
            case "pending":
                return "neutral-badge";
            case "issue_reported":
                return "danger-badge";
            default:
                return "neutral-badge";
        }
    }

    @FXML
    private void handleReportIssue() {

        if (selectedDeliveryId <= 0) {
            showError("Please select a delivery first.");
            return;
        }

        boolean done = deliveryDAO.updateDeliveryStatus(selectedDeliveryId, "issue_reported");

        if (done) {
            showInfo("Issue reported successfully.");
            refreshPage();
        } else {
            showError("Could not report issue.");
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Delivery Employee");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Delivery Employee");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}