package com.furniture.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.List;

import com.furniture.dao.SalesWorkbenchDAO;
import com.furniture.model.OrderDetails;
import com.furniture.model.OrderItem;

import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import com.furniture.model.PaymentInfo;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import java.util.Map;

public class SalesWorkbenchController extends BaseController {

    private final SalesWorkbenchDAO salesDAO = new SalesWorkbenchDAO();

    @FXML
    private Label pendingOrdersLabel;

    @FXML
    private Label approvedTodayLabel;

    @FXML
    private Label todayRevenueLabel;

    @FXML
    private Label pendingPaymentsLabel;

    @FXML
    private Label ordersInProgressLabel;

    @FXML
    private PieChart pendingOverviewChart;

    @FXML
    private LineChart<String, Number> performanceChart;

    @FXML
    private HBox ordersWorkbenchPanels;
    @FXML
    private VBox ordersWorkbenchContainer;

    @FXML
    private HBox welcomeOverviewArea;

    @FXML
    private VBox ordersListBox;
    @FXML
    private Label selectedOrderTitleLabel;
    @FXML
    private Label selectedOrderDateLabel;
    @FXML
    private Label selectedOrderTotalLabel;
    @FXML
    private Label selectedPaymentStatusLabel;

    @FXML
    private Label customerNameLabel;
    @FXML
    private Label customerPhoneLabel;
    @FXML
    private Label customerAddressLabel;
    @FXML
    private Label customerEmailLabel;

    @FXML
    private HBox dashboardTopPanels;
    @FXML
    private HBox dashboardBottomPanels;
    @FXML
    private VBox paymentsBox;
    @FXML
    private VBox timelineBox;

    @FXML
    private Label selectedDeliveryStatusLabel;

    @FXML
    private Label deliveryTypeLabel;

    @FXML
    private Label preferredDateLabel;

    @FXML
    private Label preferredTimeLabel;

    @FXML
    private Label deliveryAddressLabel;

    @FXML
    private Label missingPaymentsAlertLabel;
    @FXML
    private Label lowStockAlertLabel;
    @FXML
    private Label incompleteAddressAlertLabel;
    @FXML
    private Label readyToSendAlertLabel;

    @FXML
    private Label selectedOrderChannelLabel;
    @FXML
    private Label availabilityStatusLabel;
    @FXML
    private Label paymentValidationLabel;
    @FXML
    private Label orderItemsTitleLabel;

    @FXML
    private Label pendingOverviewTotalLabel;
    @FXML
    private Label newOrdersLegendLabel;
    @FXML
    private Label missingPaymentLegendLabel;
    @FXML
    private Label lowStockLegendLabel;
    @FXML
    private Label missingAddressLegendLabel;
    @FXML
    private Label pendingOverviewNoteLabel;

    @FXML
    private VBox recentActivityBox;

    @FXML
    private Label weeklyRevenueLabel;
    @FXML
    private Label weeklyApprovedOrdersLabel;
    @FXML
    private Label approvalRateLabel;

    @FXML
    private Label pendingOrdersDescLabel;
    @FXML
    private Label approvedTodayDescLabel;
    @FXML
    private Label todayRevenueDescLabel;
    @FXML
    private Label pendingPaymentsDescLabel;
    @FXML
    private Label ordersInProgressDescLabel;
    @FXML
    private Label missingPaymentsDescLabel;
    @FXML
    private Label lowStockDescLabel;
    @FXML
    private Label incompleteAddressDescLabel;
    @FXML
    private Label readyToSendDescLabel;

    @FXML
    private TextField orderSearchField;
    @FXML
    private Button accountMenuBtn;

    @FXML
    private javafx.scene.layout.GridPane orderItemsGrid;
    @FXML
    private ScrollPane salesScroll;
    @FXML
    private VBox alertActionPanel;

    @FXML
    private Label alertActionTitleLabel;

    @FXML
    private Label alertActionDescLabel;

    @FXML
    private Label alertActionCountLabel;

    @FXML
    private Button alertActionButton;
    @FXML
    private Label pendingTabLabel;
    @FXML
    private Label approvedTabLabel;
    @FXML
    private Label rejectedTabLabel;
    @FXML
    private Label warehouseStatusLabel;

    @FXML
    private Label warehouseResponseLabel;

    private int selectedSaleId = -1;
    private double selectedOrderTotalAmount = 0;
    private AlertFilter openedAlert = null;

    private enum AlertFilter {
        ALL,
        MISSING_PAYMENT,
        LOW_STOCK,
        INCOMPLETE_ADDRESS,
        READY_TO_SEND
    }

    private AlertFilter currentFilter = AlertFilter.ALL;

    private enum OrderTab {
        PENDING,
        APPROVED,
        REJECTED
    }

    private OrderTab currentTab = OrderTab.PENDING;

    @FXML
    public void initialize() {

        loadKpiCards();
        loadPendingOverviewChart();
        loadPerformanceChart();
        loadPendingOrdersList();
        loadPriorityAlerts();
        loadRecentActivity();
        createAccountMenu(accountMenuBtn);
        loadSalesPerformanceSummary();

        hideOrdersWorkbenchPanels();
        hideNode(alertActionPanel);
        orderSearchField.setOnAction(e -> loadPendingOrdersList());
    }

    private void stylePendingOverviewChart() {

        String[] colors = {
                "#a855f7", // New - Purple
                "#ef4444", // Missing Payment - Red
                "#f59e0b", // Low Stock - Orange
                "#facc15" // Missing Address - Yellow
        };

        Platform.runLater(() -> {
            for (int i = 0; i < pendingOverviewChart.getData().size(); i++) {

                PieChart.Data data = pendingOverviewChart.getData().get(i);

                if (data.getNode() != null) {
                    data.getNode().setStyle(
                            "-fx-pie-color: " + colors[i] + ";");
                }
            }
        });
    }

    private void loadKpiCards() {

        int pending = salesDAO.getPendingOrdersCount();
        int approvedToday = salesDAO.getApprovedTodayCount();
        double revenue = salesDAO.getTodayRevenue();
        int pendingPayments = salesDAO.getPendingPaymentsCount();
        int inProgress = salesDAO.getOrdersInProgressCount();

        pendingOrdersLabel.setText(String.valueOf(pending));
        approvedTodayLabel.setText(String.valueOf(approvedToday));
        todayRevenueLabel.setText(String.format("$%,.0f", revenue));
        pendingPaymentsLabel.setText(String.valueOf(pendingPayments));
        ordersInProgressLabel.setText(String.valueOf(inProgress));

        pendingOrdersDescLabel.setText(pending + " orders need review");
        approvedTodayDescLabel.setText(approvedToday + " approved today");
        todayRevenueDescLabel.setText("Revenue from approved/completed sales");
        pendingPaymentsDescLabel.setText(pendingPayments + " orders still unpaid");
        ordersInProgressDescLabel.setText(inProgress + " deliveries pending");
    }

    private void loadPendingOverviewChart() {

        pendingOverviewChart.getData().clear();

        SalesWorkbenchDAO.PendingOverview overview = salesDAO.getPendingOverview();

        int total = overview.getTotal();

        PieChart.Data newOrders = new PieChart.Data("New", overview.getNewOrders());
        PieChart.Data missingPayment = new PieChart.Data("Missing Payment", overview.getMissingPayments());
        PieChart.Data lowStock = new PieChart.Data("Low Stock", overview.getLowStockOrders());
        PieChart.Data missingAddress = new PieChart.Data("Missing Address", overview.getMissingAddresses());

        pendingOverviewChart.getData().addAll(
                newOrders,
                missingPayment,
                lowStock,
                missingAddress);

        pendingOverviewChart.setLabelsVisible(false);
        pendingOverviewChart.setLegendVisible(false);
        pendingOverviewChart.setStartAngle(90);

        if (pendingOverviewTotalLabel != null) {
            pendingOverviewTotalLabel.setText(String.valueOf(total));
        }

        if (newOrdersLegendLabel != null) {
            newOrdersLegendLabel.setText(
                    "● New " + overview.getNewOrders() + " (" + percent(overview.getNewOrders(), total) + "%)");
        }

        if (missingPaymentLegendLabel != null) {
            missingPaymentLegendLabel.setText(
                    "● Missing Payment " + overview.getMissingPayments() + " ("
                            + percent(overview.getMissingPayments(), total) + "%)");
        }

        if (lowStockLegendLabel != null) {
            lowStockLegendLabel.setText(
                    "● Low Stock " + overview.getLowStockOrders() + " (" + percent(overview.getLowStockOrders(), total)
                            + "%)");
        }

        if (missingAddressLegendLabel != null) {
            missingAddressLegendLabel.setText(
                    "● Missing Address " + overview.getMissingAddresses() + " ("
                            + percent(overview.getMissingAddresses(), total) + "%)");
        }

        if (pendingOverviewNoteLabel != null) {
            pendingOverviewNoteLabel.setText(
                    "You have " + total
                            + " orders that require attention. Review payment, address, and stock status before approval.");
        }
        stylePendingOverviewChart();
    }

    private void loadPendingOrdersList() {

        if (ordersListBox == null) {
            return;
        }

        ordersListBox.getChildren().clear();

        String keyword = "";

        if (orderSearchField != null && orderSearchField.getText() != null) {
            keyword = orderSearchField.getText().trim().toLowerCase();
        }

        List<SalesWorkbenchDAO.OrderSummary> orders;

        if (currentTab == OrderTab.APPROVED) {
            orders = salesDAO.getApprovedOrders(keyword);
        } else if (currentTab == OrderTab.REJECTED) {
            orders = salesDAO.getRejectedOrders(keyword);
        } else {
            switch (currentFilter) {
                case MISSING_PAYMENT:
                    orders = salesDAO.getMissingPaymentOrders(keyword);
                    break;
                case LOW_STOCK:
                    orders = salesDAO.getLowStockOrders(keyword);
                    break;
                case INCOMPLETE_ADDRESS:
                    orders = salesDAO.getIncompleteAddressOrders(keyword);
                    break;
                case READY_TO_SEND:
                    orders = salesDAO.getReadyToSendOrders(keyword);
                    break;
                default:
                    orders = salesDAO.getPendingOrders(keyword);
            }
        }

        if (orders.isEmpty()) {
            Label empty = new Label("No orders found.");
            empty.getStyleClass().add("activity-desc");
            ordersListBox.getChildren().add(empty);
            return;
        }

        for (SalesWorkbenchDAO.OrderSummary order : orders) {
            ordersListBox.getChildren().add(createOrderCard(order));
        }
    }

    private int percent(int value, int total) {
        if (total <= 0) {
            return 0;
        }
        return (int) Math.round((value * 100.0) / total);
    }

    @FXML
    private void showPendingTab() {
        currentTab = OrderTab.PENDING;
        currentFilter = AlertFilter.ALL;
        updateTabStyles();
        loadPendingOrdersList();
    }

    @FXML
    private void showApprovedTab() {
        currentTab = OrderTab.APPROVED;
        currentFilter = AlertFilter.ALL;
        updateTabStyles();
        loadPendingOrdersList();
    }

    @FXML
    private void showRejectedTab() {
        currentTab = OrderTab.REJECTED;
        currentFilter = AlertFilter.ALL;
        updateTabStyles();
        loadPendingOrdersList();
    }

    private void updateTabStyles() {
        pendingTabLabel.getStyleClass().setAll(currentTab == OrderTab.PENDING ? "tab-active" : "tab-text");
        approvedTabLabel.getStyleClass().setAll(currentTab == OrderTab.APPROVED ? "tab-active" : "tab-text");
        rejectedTabLabel.getStyleClass().setAll(currentTab == OrderTab.REJECTED ? "tab-active" : "tab-text");
    }

    @FXML
    private void handleOrderSearch() {
        loadPendingOrdersList();
    }

    private VBox createOrderCard(SalesWorkbenchDAO.OrderSummary order) {

        VBox card = new VBox(8);
        card.getStyleClass().add("order-card");

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label id = new Label("#ORD-" + String.format("%04d", order.getSaleId()));
        id.getStyleClass().add("order-id");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label total = new Label(String.format("$%,.0f", order.getTotalAmount()));
        total.getStyleClass().add("order-total");

        top.getChildren().addAll(id, spacer, total);

        Label customer = new Label(order.getCustomerName());
        customer.getStyleClass().add("order-customer");

        Label date = new Label(order.getSaleDate());
        date.getStyleClass().add("order-date");

        Label status = new Label(getTabStatusText());
        status.getStyleClass().add(getTabStatusStyle());

        card.getChildren().addAll(top, customer, date, status);

        card.setOnMouseClicked(e -> {
            selectedSaleId = order.getSaleId();
            loadSelectedOrder(order);
            markSelectedCard(card);
        });

        return card;
    }

    private String getTabStatusText() {
        if (currentTab == OrderTab.APPROVED) {
            return "Approved";
        }

        if (currentTab == OrderTab.REJECTED) {
            return "Rejected";
        }

        return "Pending";
    }

    private String getTabStatusStyle() {
        if (currentTab == OrderTab.APPROVED) {
            return "badge-approved";
        }

        if (currentTab == OrderTab.REJECTED) {
            return "badge-rejected";
        }

        return "badge-progress";
    }

    @FXML
    private void handleViewAllOrders() {

        currentTab = OrderTab.PENDING;
        currentFilter = AlertFilter.ALL;

        if (orderSearchField != null) {
            orderSearchField.clear();
        }

        updateTabStyles();
        loadPendingOrdersList();
    }

    private void loadSelectedOrder(
            SalesWorkbenchDAO.OrderSummary order) {

        selectedOrderTitleLabel.setText(
                "Order #ORD-" +
                        String.format("%04d",
                                order.getSaleId()));

        selectedOrderDateLabel.setText(
                order.getSaleDate());

        selectedOrderTotalAmount = order.getTotalAmount();

        selectedOrderTotalLabel.setText(
                String.format("$%,.0f",
                        order.getTotalAmount()));

        double due = order.getTotalAmount()
                - order.getPaidAmount();

        if (due <= 0) {
            selectedPaymentStatusLabel.setText("Paid");
        } else if (order.getPaidAmount() > 0) {
            selectedPaymentStatusLabel.setText("Partially Paid");
        } else {
            selectedPaymentStatusLabel.setText("Not Paid");
        }

        loadCustomerInfo(order.getSaleId());
        loadOrderItems(order.getSaleId());
        loadPayments(order.getSaleId(), order.getTotalAmount());
        loadTimeline(order.getSaleId());
        loadDeliveryInfo(order.getSaleId());

        loadValidationChecklist(order.getSaleId(), order.getTotalAmount(), order.getPaidAmount());
    }

    private void loadValidationChecklist(int saleId, double totalAmount, double paidAmount) {

        SalesWorkbenchDAO.WarehouseResponse response = salesDAO.getWarehouseResponse(saleId);

        String warehouseStatus = response == null ? null : response.getStatus();
        String warehouseMessage = response == null ? null : response.getMessage();

        if ("available".equals(warehouseStatus)) {
            availabilityStatusLabel.setText("Available");
            availabilityStatusLabel.getStyleClass().setAll("green-change");

            warehouseStatusLabel.setText("Available");
            warehouseStatusLabel.getStyleClass().setAll("green-change");

        } else if ("partially_available".equals(warehouseStatus)) {
            availabilityStatusLabel.setText("Partial");
            availabilityStatusLabel.getStyleClass().setAll("legend-yellow");

            warehouseStatusLabel.setText("Partially Available");
            warehouseStatusLabel.getStyleClass().setAll("legend-yellow");

        } else if ("not_available".equals(warehouseStatus)) {
            availabilityStatusLabel.setText("Not Available");
            availabilityStatusLabel.getStyleClass().setAll("legend-red");

            warehouseStatusLabel.setText("Not Available");
            warehouseStatusLabel.getStyleClass().setAll("legend-red");

        } else if ("pending".equals(warehouseStatus)) {
            availabilityStatusLabel.setText("Waiting");
            availabilityStatusLabel.getStyleClass().setAll("legend-yellow");

            warehouseStatusLabel.setText("Waiting");
            warehouseStatusLabel.getStyleClass().setAll("legend-yellow");

        } else {
            availabilityStatusLabel.setText("Not Sent");
            availabilityStatusLabel.getStyleClass().setAll("neutral-badge");

            warehouseStatusLabel.setText("Not Sent");
            warehouseStatusLabel.getStyleClass().setAll("neutral-badge");
        }

        if (warehouseMessage != null && !warehouseMessage.isBlank()) {
            warehouseResponseLabel.setText(warehouseMessage);
        } else {
            warehouseResponseLabel.setText("No warehouse response yet.");
        }

        double due = totalAmount - paidAmount;

        if (due <= 0) {
            paymentValidationLabel.setText("Paid");
            paymentValidationLabel.getStyleClass().setAll("green-change");
        } else if (paidAmount > 0) {
            paymentValidationLabel.setText("Partial");
            paymentValidationLabel.getStyleClass().setAll("legend-yellow");
        } else {
            paymentValidationLabel.setText("Not Paid");
            paymentValidationLabel.getStyleClass().setAll("legend-red");
        }
    }

    private void loadTimeline(int saleId) {

        timelineBox.getChildren().clear();

        List<String> timeline = salesDAO.getOrderTimeline(saleId);

        if (timeline.isEmpty()) {
            Label empty = new Label("No timeline yet.");
            empty.getStyleClass().add("activity-desc");
            timelineBox.getChildren().add(empty);
            return;
        }

        for (String line : timeline) {
            Label label = new Label(line);
            label.getStyleClass().add("activity-desc");

            if (line.startsWith("●")) {
                label.getStyleClass().add("activity-title");
            }

            timelineBox.getChildren().add(label);
        }
    }

    private void loadDeliveryInfo(int saleId) {

        SalesWorkbenchDAO.DeliveryInfo info = salesDAO.getDeliveryInfo(saleId);

        if (info == null) {

            OrderDetails details = salesDAO.getOrderDetails(saleId);

            selectedDeliveryStatusLabel.setText("Not Assigned");
            deliveryTypeLabel.setText("Standard Delivery");
            preferredDateLabel.setText("Delivery Date: Not scheduled yet");
            preferredTimeLabel.setText("Employee: Not assigned yet");

            if (details != null && details.getCustomerAddress() != null) {
                deliveryAddressLabel.setText(details.getCustomerAddress());
            } else {
                deliveryAddressLabel.setText("No delivery address found.");
            }

            return;
        }

        selectedDeliveryStatusLabel.setText(info.getStatus());
        deliveryTypeLabel.setText("Standard Delivery");
        preferredDateLabel.setText("Delivery Date: " + info.getDate());
        preferredTimeLabel.setText("Employee: " + info.getEmployeeName());
        deliveryAddressLabel.setText(info.getAddress());
    }

    private void markSelectedCard(VBox selectedCard) {

        for (Node node : ordersListBox.getChildren()) {
            node.getStyleClass().remove("order-card-active");

            if (!node.getStyleClass().contains("order-card")) {
                node.getStyleClass().add("order-card");
            }
        }

        selectedCard.getStyleClass().remove("order-card");
        selectedCard.getStyleClass().add("order-card-active");
    }

    private void loadPerformanceChart() {

        performanceChart.getData().clear();

        CategoryAxis xAxis = (CategoryAxis) performanceChart.getXAxis();
        xAxis.getCategories().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        List<SalesWorkbenchDAO.DailyRevenue> dailyRevenue = salesDAO.getWeeklyRevenueByDay();

        for (SalesWorkbenchDAO.DailyRevenue row : dailyRevenue) {
            series.getData().add(new XYChart.Data<>(row.getDayName(), row.getRevenue()));
        }

        performanceChart.getData().add(series);

        performanceChart.setAnimated(false);
        performanceChart.setLegendVisible(false);

        Platform.runLater(() -> {
            if (series.getNode() != null) {
                series.getNode().setStyle(
                        "-fx-stroke: #D69C37;" +
                                "-fx-stroke-width: 2.5px;");
            }

            for (XYChart.Data<String, Number> data : series.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setStyle(
                            "-fx-background-color: #D69C37, #111;" +
                                    "-fx-background-radius: 6px;" +
                                    "-fx-padding: 5px;");
                }
            }
        });
    }

    private void loadPriorityAlerts() {

        SalesWorkbenchDAO.PriorityAlerts alerts = salesDAO.getPriorityAlerts();

        missingPaymentsAlertLabel.setText(String.valueOf(alerts.getMissingPayments()));
        lowStockAlertLabel.setText(String.valueOf(alerts.getLowStockOrders()));
        incompleteAddressAlertLabel.setText(String.valueOf(alerts.getIncompleteAddresses()));
        readyToSendAlertLabel.setText(String.valueOf(alerts.getReadyToSend()));

        missingPaymentsDescLabel.setText(
                alerts.getMissingPayments() + " orders are waiting for payment.");

        lowStockDescLabel.setText(
                alerts.getLowStockOrders() + " orders contain items with low stock.");

        incompleteAddressDescLabel.setText(
                alerts.getIncompleteAddresses() + " orders have incomplete address.");

        readyToSendDescLabel.setText(
                alerts.getReadyToSend() + " orders are ready for warehouse review.");
    }

    private void loadSalesPerformanceSummary() {

        SalesWorkbenchDAO.WeeklyPerformanceSummary summary = salesDAO.getWeeklyPerformanceSummary();

        weeklyRevenueLabel.setText(String.format("$%,.0f", summary.getRevenue()));
        weeklyApprovedOrdersLabel.setText(String.valueOf(summary.getApprovedOrders()));
        approvalRateLabel.setText(summary.getApprovalRate() + "%");
    }

    private void loadRecentActivity() {

        recentActivityBox.getChildren().clear();

        List<SalesWorkbenchDAO.ActivityItem> activities = salesDAO.getRecentActivity();

        if (activities.isEmpty()) {
            Label empty = new Label("No recent activity yet.");
            empty.getStyleClass().add("activity-desc");
            recentActivityBox.getChildren().add(empty);
            return;
        }

        for (SalesWorkbenchDAO.ActivityItem item : activities) {

            HBox row = new HBox(14);
            row.setAlignment(Pos.CENTER_LEFT);

            Label time = new Label(item.getActivityTime());
            time.getStyleClass().add("activity-time");

            Label dot = new Label("●");
            dot.getStyleClass().add(item.getDotStyle());

            VBox textBox = new VBox(2);

            Label title = new Label(item.getTitle());
            title.getStyleClass().add("activity-title");

            Label desc = new Label(item.getDescription());
            desc.getStyleClass().add("activity-desc");

            textBox.getChildren().addAll(title, desc);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label badge = new Label(item.getBadgeText());
            badge.getStyleClass().add(item.getBadgeStyle());

            row.getChildren().addAll(time, dot, textBox, spacer, badge);

            recentActivityBox.getChildren().add(row);
        }
    }

    private void hideOrdersWorkbenchPanels() {
        hideNode(ordersWorkbenchContainer);

        showNode(dashboardTopPanels);
        showNode(dashboardBottomPanels);
    }

    @FXML
    private void handleStartReviewingOrders() {

        showNode(dashboardTopPanels);
        showNode(dashboardBottomPanels);
        showNode(ordersWorkbenchContainer);

        scrollToOrdersWorkbench();
    }

    private void showAlertActionPanel(
            String title,
            String description,
            String count,
            String buttonText) {

        alertActionTitleLabel.setText(title);
        alertActionDescLabel.setText(description);
        alertActionCountLabel.setText(count);
        alertActionButton.setText(buttonText);

        showNode(alertActionPanel);

    }

    @FXML
    private void hideAlertActionPanel() {

        hideNode(alertActionPanel);

        openedAlert = null;
    }

    @FXML
    private void showAllPriorityOrders() {

        toggleAlertPanel(
                AlertFilter.ALL,
                "All Priority Orders",
                "This view includes all orders that need review.",
                String.valueOf(
                        Integer.parseInt(missingPaymentsAlertLabel.getText())
                                + Integer.parseInt(lowStockAlertLabel.getText())
                                + Integer.parseInt(incompleteAddressAlertLabel.getText())
                                + Integer.parseInt(readyToSendAlertLabel.getText())),
                "Review All Priority Orders");
    }

    private void toggleAlertPanel(
            AlertFilter filter,
            String title,
            String description,
            String count,
            String buttonText) {

        if (openedAlert == filter && alertActionPanel.isVisible()) {
            hideNode(alertActionPanel);
            openedAlert = null;
            return;
        }

        openedAlert = filter;
        currentFilter = filter;

        showAlertActionPanel(title, description, count, buttonText);
    }

    @FXML
    private void showMissingPaymentsOrders() {

        toggleAlertPanel(
                AlertFilter.MISSING_PAYMENT,
                "Missing Payments",
                "These orders are waiting for payment.",
                missingPaymentsAlertLabel.getText(),
                "Review Missing Payments");
    }

    @FXML
    private void handleAlertActionButton() {

        currentTab = OrderTab.PENDING;
        updateTabStyles();

        showNode(ordersWorkbenchContainer);

        loadPendingOrdersList();

        scrollToOrdersWorkbench();
    }

    @FXML
    private void showLowStockOrders() {

        toggleAlertPanel(
                AlertFilter.LOW_STOCK,
                "Low Stock Items",
                "These orders contain products with low stock.",
                lowStockAlertLabel.getText(),
                "Review Low Stock Orders");
    }

    @FXML
    private void showIncompleteAddressOrders() {

        toggleAlertPanel(
                AlertFilter.INCOMPLETE_ADDRESS,
                "Incomplete Addresses",
                "These orders have missing delivery information.",
                incompleteAddressAlertLabel.getText(),
                "Review Address Issues");
    }

    @FXML
    private void showReadyToSendOrders() {

        toggleAlertPanel(
                AlertFilter.READY_TO_SEND,
                "Ready To Send",
                "These orders are ready for warehouse review.",
                readyToSendAlertLabel.getText(),
                "Review Ready Orders");
    }

    @FXML
    private void handleBackToDashboard() {

        hideNode(ordersWorkbenchContainer);

        Platform.runLater(() -> {
            if (salesScroll != null) {
                salesScroll.setVvalue(0);
            }
        });
    }

    private void scrollToOrdersWorkbench() {

        Platform.runLater(() -> {

            if (salesScroll != null) {
                salesScroll.setVvalue(1.0);
            }

            if (ordersWorkbenchContainer != null) {
                ordersWorkbenchContainer.requestFocus();
            }
        });
    }

    private void hideNode(javafx.scene.Node node) {
        if (node != null) {
            node.setVisible(false);
            node.setManaged(false);
        }
    }

    private void showNode(javafx.scene.Node node) {
        if (node != null) {
            node.setVisible(true);
            node.setManaged(true);
        }
    }

    @FXML
    private void handleCheckWithWarehouse() {

        if (selectedSaleId <= 0) {
            showError("Please select an order first.");
            return;
        }

        int employeeId = Session.getCurrentEmployeeId();

        if (employeeId <= 0) {
            showError("No sales employee is logged in.");
            return;
        }

        boolean done = salesDAO.sendWarehouseRequest(
                selectedSaleId,
                employeeId,
                "Please check if all order items are available in stock.");

        if (done) {
            showInfo("Request sent to warehouse successfully.");
            loadKpiCards();
            loadPendingOverviewChart();
            loadPriorityAlerts();
            loadRecentActivity();
            loadSalesPerformanceSummary();
            loadPerformanceChart();

            loadPendingOrdersList();
            hideAlertActionPanel();

        } else {
            showError("Could not send warehouse request.");
        }
    }

    @FXML
    private void handleApproveOrder() {

        if (selectedSaleId <= 0) {
            showError("Please select an order first.");
            return;
        }

        boolean done = salesDAO.approveOrder(
                selectedSaleId,
                "Approved by sales employee.");

        if (done) {

            boolean deliveryCreated = salesDAO.createDeliveryForApprovedSale(selectedSaleId);

            if (deliveryCreated) {
                showInfo("Order approved and sent to delivery employee.");
            } else {
                showInfo("Order approved, but delivery assignment was not created.");
            }

            selectedSaleId = -1;

            loadKpiCards();
            loadPendingOverviewChart();
            loadPriorityAlerts();
            loadRecentActivity();
            loadSalesPerformanceSummary();
            loadPerformanceChart();

            loadPendingOrdersList();
            hideAlertActionPanel();
        } else {
            showError("Could not approve order.");
        }

    }

    @FXML
    private void handleRejectOrder() {

        if (selectedSaleId <= 0) {
            showError("Please select an order first.");
            return;
        }

        boolean done = salesDAO.rejectOrder(
                selectedSaleId,
                "Rejected by sales employee.");

        if (done) {
            showInfo("Order rejected successfully.");
            selectedSaleId = -1;

            loadKpiCards();
            loadPendingOverviewChart();
            loadPriorityAlerts();
            loadRecentActivity();
            loadSalesPerformanceSummary();
            loadPerformanceChart();

            loadPendingOrdersList();
            hideAlertActionPanel();

        } else {
            showError("Could not reject order.");
        }
    }

    private void showInfo(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);

        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadCustomerInfo(int saleId) {

        OrderDetails details = salesDAO.getOrderDetails(saleId);

        if (details == null)
            return;

        customerNameLabel.setText(
                details.getCustomerName());

        customerPhoneLabel.setText(
                details.getCustomerPhone());

        customerEmailLabel.setText(
                details.getCustomerEmail());

        customerAddressLabel.setText(
                details.getCustomerAddress());
    }

    private void loadOrderItems(int saleId) {

        List<OrderItem> items = salesDAO.getOrderItems(saleId);

        orderItemsGrid.getChildren().removeIf(node -> {

            Integer row = GridPane.getRowIndex(node);

            return row != null && row > 0;
        });

        int row = 1;

        for (OrderItem item : items) {

            Label no = new Label(String.valueOf(row));

            Label product = new Label(item.getProductName());

            Label color = new Label(item.getColor());

            Label qty = new Label(String.valueOf(
                    item.getQuantity()));

            Label unitPrice = new Label(String.format(
                    "$%,.0f",
                    item.getUnitPrice()));

            Label total = new Label(String.format(
                    "$%,.0f",
                    item.getTotalPrice()));

            Label stock = new Label(item.getStockStatus());

            stock.getStyleClass().add(
                    getStockStyle(
                            item.getStockStatus()));

            orderItemsGrid.add(no, 0, row);
            orderItemsGrid.add(product, 1, row);
            orderItemsGrid.add(color, 2, row);
            orderItemsGrid.add(qty, 3, row);
            orderItemsGrid.add(unitPrice, 4, row);
            orderItemsGrid.add(total, 5, row);
            orderItemsGrid.add(stock, 6, row);

            row++;
        }
    }

    private String getStockStyle(String status) {

        switch (status) {

            case "Available":
                return "badge-approved";

            case "Low Stock":
                return "badge-progress";

            default:
                return "badge-rejected";
        }
    }

    private void loadPayments(int saleId, double totalAmount) {

        paymentsBox.getChildren().clear();

        Label title = new Label("Payments");
        title.getStyleClass().add("panel-title");
        paymentsBox.getChildren().add(title);

        List<PaymentInfo> payments = salesDAO.getOrderPayments(saleId);

        double paid = 0;

        if (payments.isEmpty()) {
            Label empty = new Label("No payments recorded yet.");
            empty.getStyleClass().add("activity-desc");
            paymentsBox.getChildren().add(empty);
        }

        for (PaymentInfo payment : payments) {

            paid += payment.getAmount();

            Label row = new Label(
                    "PAY-" + String.format("%04d", payment.getPaymentId())
                            + "    "
                            + payment.getPaymentMethod()
                            + "    "
                            + String.format("$%,.0f", payment.getAmount())
                            + "    "
                            + payment.getPaymentDate());

            row.getStyleClass().add("activity-title");
            paymentsBox.getChildren().add(row);
        }

        double due = totalAmount - paid;

        Label summary = new Label(
                "Paid: " + String.format("$%,.0f", paid)
                        + "     Due: "
                        + String.format("$%,.0f", Math.max(due, 0)));

        if (due <= 0) {
            summary.getStyleClass().add("green-change");
        } else {
            summary.getStyleClass().add("legend-red");
        }

        paymentsBox.getChildren().add(summary);
    }

    @FXML
    private void handleAddPayment() {
        if (selectedSaleId <= 0) {
            showError("Please select an order first.");
            return;
        }

        TextInputDialog amountDialog = new TextInputDialog();
        amountDialog.setTitle("Add Payment");
        amountDialog.setHeaderText("Enter payment amount");
        amountDialog.setContentText("Amount:");

        Optional<String> amountResult = amountDialog.showAndWait();

        if (amountResult.isEmpty()) {
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(amountResult.get().trim());

            if (amount <= 0) {
                showError("Amount must be greater than zero.");
                return;
            }

        } catch (NumberFormatException e) {
            showError("Please enter a valid number.");
            return;
        }

        TextInputDialog methodDialog = new TextInputDialog("Cash");
        methodDialog.setTitle("Add Payment");
        methodDialog.setHeaderText("Enter payment method");
        methodDialog.setContentText("Method:");

        Optional<String> methodResult = methodDialog.showAndWait();

        if (methodResult.isEmpty() || methodResult.get().trim().isBlank()) {
            showError("Payment method is required.");
            return;
        }

        boolean done = salesDAO.addPayment(
                selectedSaleId,
                amount,
                methodResult.get().trim());

        if (done) {
            showInfo("Payment added successfully.");
            loadKpiCards();
            loadPriorityAlerts();
            loadPayments(selectedSaleId, selectedOrderTotalAmount);
            loadPendingOrdersList();
            loadRecentActivity();
            loadSalesPerformanceSummary();
            loadPendingOrdersList();

        } else {
            showError("Could not add payment.");
        }
    }

}