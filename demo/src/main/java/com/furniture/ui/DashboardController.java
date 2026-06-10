package com.furniture.ui;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.furniture.dao.DashboardDAO;

public class DashboardController {

    @FXML
    private Label totalProductsLabel;
    @FXML
    private Label totalCustomersLabel;
    @FXML
    private Label totalSalesLabel;
    @FXML
    private Label totalRevenueLabel;
    @FXML
    private Label totalWarehousesLabel;
    @FXML
    private Label productsGrowthLabel;
    @FXML
    private Label customersGrowthLabel;
    @FXML
    private Label salesGrowthLabel;
    @FXML
    private Label revenueGrowthLabel;
    @FXML
    private Label viewAllOrdersLabel;
    @FXML
    private Label deliveredCountLabel;
    @FXML
    private Label pendingCountLabel;
    @FXML
    private Label cancelledCountLabel;
    @FXML
    private Label viewAllProductsLabel;
    @FXML
    private Label viewAllDeliveriesLabel;

    @FXML
    private AreaChart<String, Number> salesChart;
    @FXML
    private ComboBox<Integer> salesPeriodCombo;
    @FXML
    private PieChart categoryPieChart;
    @FXML
    private VBox categoryLegendBox;
    @FXML
    private VBox topProductsBox;
    @FXML
    private VBox recentOrdersBox;
    @FXML
    private VBox inventoryAlertsBox;

    @FXML
    private VBox recentDeliveriesBox;

    private boolean showAllOrders = false;
    private boolean showAllInventory = false;
    private boolean showAllDeliveries = false;

    @FXML
    public void initialize() {


        loadDashboardStats();
        loadGrowthStats();
        loadAvailableYears();
        loadSalesByCategory();
        loadTopSellingProducts();
        loadRecentOrders();
        loadInventoryAlerts();
        loadDeliveryStats();
        loadRecentDeliveries();

        salesPeriodCombo.setOnAction(e -> {
            Integer year = salesPeriodCombo.getValue();
            if (year != null) {
                loadSalesOverview(year);
            }
        });
    }

    private void loadDashboardStats() {

        try {

            totalProductsLabel.setText(
                    String.valueOf(DashboardDAO.getTotalProducts()));

            totalCustomersLabel.setText(
                    String.valueOf(DashboardDAO.getTotalCustomers()));

            totalSalesLabel.setText(
                    String.valueOf(DashboardDAO.getTotalSales()));
            double revenue = DashboardDAO.getTotalRevenue();
            setRevenueText(revenue);

            totalWarehousesLabel.setText(
                    String.valueOf(DashboardDAO.getTotalWarehouses()));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setRevenueText(double revenue) {

        String text = String.format("$%,.0f", revenue);

        totalRevenueLabel.setText(text);
        totalRevenueLabel.setTranslateX(-25);

        totalRevenueLabel.setStyle(
                "-fx-font-size:22px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:#f8d77a;");
    }

    private void loadGrowthStats() {

        try {

            double productsGrowth = DashboardDAO.getProductsGrowth();

            double customersGrowth = DashboardDAO.getCustomersGrowth();

            double salesGrowth = DashboardDAO.getSalesGrowth();

            double revenueGrowth = DashboardDAO.getRevenueGrowth();

            setGrowthLabel(productsGrowthLabel, productsGrowth);
            setGrowthLabel(customersGrowthLabel, customersGrowth);
            setGrowthLabel(salesGrowthLabel, salesGrowth);
            setGrowthLabel(revenueGrowthLabel, revenueGrowth);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setGrowthLabel(Label label, double growth) {

        String arrow = growth >= 0 ? "↑" : "↓";

        if (growth > 0) {

            label.setStyle(
                    "-fx-text-fill:#72e38d;" +
                            "-fx-font-size:10px;" +
                            "-fx-font-weight:bold;");
            label.setText(
                    String.format(
                            "%s %.1f%% from last month",
                            arrow,
                            Math.abs(growth)));

        } else if (growth < 0) {

            label.setStyle(
                    "-fx-text-fill:#ff6b6b;" +
                            "-fx-font-size:10px;" +
                            "-fx-font-weight:bold;");
            label.setText(
                    String.format(
                            "%s %.1f%% from last month",
                            arrow,
                            Math.abs(growth)));
        } else {
            label.setStyle(
                    "-fx-text-fill:#D69C37;" +
                            "-fx-font-size:10px;" +
                            "-fx-font-weight:bold;");
            label.setText(
                    String.format("No change from last month"));
        }
    }

    private void loadAvailableYears() {
        try {
            List<Integer> years = DashboardDAO.getAvailableSalesYears();

            salesPeriodCombo.getItems().setAll(years);

            if (!years.isEmpty()) {
                salesPeriodCombo.setValue(years.get(0));
                loadSalesOverview(years.get(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSalesOverview(int year) {
        salesChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        try {
            Map<Integer, Double> data = DashboardDAO.getMonthlyRevenue(year);

            String[] months = {
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            };

            for (int i = 1; i <= 12; i++) {
                series.getData().add(
                        new XYChart.Data<>(
                                months[i - 1],
                                data.getOrDefault(i, 0.0)));
            }

            salesChart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSalesByCategory() {

        try {
            categoryPieChart.getData().clear();
            categoryLegendBox.getChildren().clear();

            Map<String, Double> data = DashboardDAO.getSalesByCategory();

            double total = data.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();

            String[] goldPalette = {
                    "#EAD8A4",
                    "#CFA349",
                    "#9B6A16",
                    "#D8B86A",
                    "#7A5010"
            };

            int i = 0;

            for (Map.Entry<String, Double> entry : data.entrySet()) {

                PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());

                categoryPieChart.getData().add(slice);

                String color = goldPalette[i % goldPalette.length];

                int percent = total == 0 ? 0 : (int) Math.round((entry.getValue() / total) * 100);

                HBox legendItem = new HBox(6);
                legendItem.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                Label colorBox = new Label();
                colorBox.setMinSize(13, 13);
                colorBox.setMaxSize(13, 13);
                colorBox.setTranslateX(-35);
                colorBox.setStyle(
                        "-fx-background-color: " + color + ";" +
                                "-fx-background-radius: 45;");

                Label nameLabel = new Label(entry.getKey() + " " + percent + "%");

                nameLabel.setMinWidth(120);
                nameLabel.setTranslateX(-35);
                nameLabel.setStyle(
                        "-fx-text-fill: #fff4cf;" +
                                "-fx-font-size: 14px;" +
                                "-fx-font-weight: bold;");

                legendItem.getChildren().addAll(colorBox, nameLabel);
                categoryLegendBox.getChildren().add(legendItem);

                final String finalColor = color;

                slice.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle(
                                "-fx-pie-color: " + finalColor + ";" +
                                        "-fx-border-color: rgba(255,255,255,0.08);" +
                                        "-fx-border-width: 0.8;" +
                                        "-fx-effect: dropshadow(gaussian, rgba(255,215,120,0.35), 10, 0.2, 0, 0);");
                    }
                });

                i++;
            }
            Platform.runLater(() -> {

                for (int j = 0; j < categoryPieChart.getData().size(); j++) {

                    PieChart.Data d = categoryPieChart.getData().get(j);

                    d.getNode().setStyle(
                            "-fx-pie-color: " +
                                    goldPalette[j % goldPalette.length] +
                                    ";");
                }
            });
            categoryPieChart.setStartAngle(90);
            categoryPieChart.setClockwise(true);
            categoryLegendBox.setTranslateX(0);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTopSellingProducts() {

        try {
            topProductsBox.getChildren().clear();

            List<DashboardDAO.TopProduct> products = DashboardDAO.getTopSellingProducts();

            for (DashboardDAO.TopProduct product : products) {

                HBox row = new HBox(10);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                row.setStyle(
                        "-fx-background-color: rgba(255,255,255,0.035);" +
                                "-fx-background-radius: 10;" +
                                "-fx-border-color: rgba(214,156,55,0.18);" +
                                "-fx-border-radius: 10;" +
                                "-fx-padding: 6 8 6 8;");

                Image image = new Image(
                        getClass()
                                .getResourceAsStream(
                                        "/images/" + product.getImagePath()));

                ImageView imageView = new ImageView(image);

                imageView.setFitWidth(42);
                imageView.setFitHeight(42);
                imageView.setPreserveRatio(false);

                VBox infoBox = new VBox(2);

                Label name = new Label(product.getProductName());
                name.setStyle(
                        "-fx-text-fill:white;" +
                                "-fx-font-size:12px;" +
                                "-fx-font-weight:bold;");

                Label price = new Label(String.format("$%,.0f", product.getPrice()));
                price.setStyle(
                        "-fx-text-fill:#d4af37;" +
                                "-fx-font-size:11px;" +
                                "-fx-font-weight:bold;");

                infoBox.getChildren().addAll(name, price);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label sold = new Label("Sold: " + product.getSoldQuantity());
                sold.setStyle(
                        "-fx-text-fill:#d4af37;" +
                                "-fx-font-size:11px;" +
                                "-fx-font-weight:bold;");

                row.getChildren().addAll(imageView, infoBox, spacer, sold);

                topProductsBox.getChildren().add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRecentOrders() {
        loadRecentOrders(showAllOrders);
    }

    private void loadRecentOrders(boolean showAll) {

        try {
            recentOrdersBox.getChildren().clear();

            List<DashboardDAO.RecentOrder> orders = DashboardDAO.getRecentOrders(showAll ? 100 : 5);

            for (DashboardDAO.RecentOrder order : orders) {

                GridPane row = new GridPane();
                row.getStyleClass().add("order-row");
                row.setHgap(8);

                row.getColumnConstraints().addAll(
                        new ColumnConstraints(80),
                        new ColumnConstraints(120),
                        new ColumnConstraints(85),
                        new ColumnConstraints(70),
                        new ColumnConstraints(75));

                Label id = new Label("#ORD-" + String.format("%04d", order.getSaleId()));
                id.getStyleClass().add("order-id");

                Label customer = new Label(order.getCustomerName());
                customer.getStyleClass().add("order-text");

                Label date = new Label(order.getSaleDate());
                date.getStyleClass().add("order-text");

                Label amount = new Label(String.format("$%,.0f", order.getAmount()));
                amount.getStyleClass().add("order-amount");

                Label status = new Label(formatStatus(order.getStatus()));
                status.getStyleClass().add(getStatusClass(order.getStatus()));

                row.add(id, 0, 0);
                row.add(customer, 1, 0);
                row.add(date, 2, 0);
                row.add(amount, 3, 0);
                row.add(status, 4, 0);

                recentOrdersBox.getChildren().add(row);
            }

            viewAllOrdersLabel.setText(showAll ? "Show Less  ↑" : "View All Orders  →");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewAllOrders() {
        showAllOrders = !showAllOrders;
        loadRecentOrders(showAllOrders);
    }

    private String formatStatus(String status) {
        if (status == null)
            return "Pending";

        return switch (status.toLowerCase()) {
            case "delivered" -> "Completed";
            case "pending" -> "Pending";
            case "cancelled" -> "Cancelled";
            default -> status;
        };
    }

    private String getStatusClass(String status) {
        if (status == null)
            return "status-pending";

        return switch (status.toLowerCase()) {
            case "delivered" -> "status-completed";
            case "pending" -> "status-pending";
            case "cancelled" -> "status-cancelled";
            default -> "status-pending";
        };
    }

    private void loadInventoryAlerts() {
        loadInventoryAlerts(showAllInventory);
    }

    private void loadInventoryAlerts(boolean showAll) {
        try {
            inventoryAlertsBox.getChildren().clear();

            List<DashboardDAO.InventoryAlert> alerts = DashboardDAO.getInventoryAlerts(showAll ? 100 : 4);

            for (DashboardDAO.InventoryAlert item : alerts) {

                HBox row = new HBox(10);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                row.setMinHeight(48);
                row.setMaxHeight(48);
                row.setStyle(
                        "-fx-background-color: rgba(255,255,255,0.028);" +
                                "-fx-background-radius: 10;" +
                                "-fx-border-color: rgba(214,156,55,0.14);" +
                                "-fx-border-radius: 10;" +
                                "-fx-padding: 7 10;");

                Label icon = new Label();
                Label name = new Label(item.getProductName());
                Label desc = new Label();

                name.setStyle(
                        "-fx-text-fill:white;" +
                                "-fx-font-size:12px;" +
                                "-fx-font-weight:bold;");

                if (item.getQuantity() <= 3) {
                    icon.setText("⚠");
                    icon.setStyle(
                            "-fx-text-fill:#ff6b6b;" +
                                    "-fx-font-size:18px;" +
                                    "-fx-font-weight:bold;" +
                                    "-fx-background-color: rgba(255,80,80,0.08);" +
                                    "-fx-background-radius: 50;" +
                                    "-fx-border-color: rgba(255,80,80,0.55);" +
                                    "-fx-border-radius: 50;");
                    desc.setText("Only " + (int) item.getQuantity() + " items left in stock");
                    desc.setStyle("-fx-text-fill:#ff8a8a; -fx-font-size:11px;");
                } else if (item.getQuantity() <= 6) {
                    icon.setText("⚠");
                    icon.setStyle(
                            "-fx-text-fill:#ffd978;" +
                                    "-fx-font-size:18px;" +
                                    "-fx-font-weight:bold;" +
                                    "-fx-background-color: rgba(214,156,55,0.08);" +
                                    "-fx-background-radius: 50;" +
                                    "-fx-border-color: rgba(214,156,55,0.55);" +
                                    "-fx-border-radius: 50;");
                    desc.setText("Only " + (int) item.getQuantity() + " items left in stock");
                    desc.setStyle("-fx-text-fill:#ffd978; -fx-font-size:11px;");
                } else {
                    icon.setText("✓");
                    icon.setStyle(
                            "-fx-text-fill:#72e38d;" +
                                    "-fx-font-size:18px;" +
                                    "-fx-font-weight:bold;" +
                                    "-fx-background-color: rgba(80,180,100,0.08);" +
                                    "-fx-background-radius: 50;" +
                                    "-fx-border-color: rgba(80,180,100,0.55);" +
                                    "-fx-border-radius: 50;");
                    desc.setText("Stock is good");
                    desc.setStyle("-fx-text-fill:#72e38d; -fx-font-size:11px;");
                }

                VBox textBox = new VBox(2);
                textBox.getChildren().addAll(name, desc);

                row.getChildren().addAll(icon, textBox);
                inventoryAlertsBox.getChildren().add(row);
            }
            viewAllProductsLabel.setText(
                    showAll ? "Show Less  ↑" : "View All Products  →");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewAllProducts() {
        showAllInventory = !showAllInventory;
        viewAllProductsLabel.setText(
                showAllInventory ? "Show Less  ↑" : "View All Products  →");
        loadInventoryAlerts(showAllInventory);
    }

    private void loadDeliveryStats() {

        try {

            DashboardDAO.DeliveryStats stats = DashboardDAO.getDeliveryStats();

            deliveredCountLabel.setText(
                    String.valueOf(stats.getDelivered()));

            pendingCountLabel.setText(
                    String.valueOf(stats.getPending()));

            cancelledCountLabel.setText(
                    String.valueOf(stats.getCancelled()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRecentDeliveries() {

        try {
            recentDeliveriesBox.getChildren().clear();

            List<DashboardDAO.RecentDelivery> deliveries = DashboardDAO
                    .getRecentDeliveries(showAllDeliveries ? 100 : 3);

            for (DashboardDAO.RecentDelivery delivery : deliveries) {
                HBox row = createDeliveryRow(
                        String.valueOf(delivery.getDeliveryId()),
                        delivery.getCustomerName(),
                        delivery.getDeliveryDate(),
                        delivery.getStatus());

                recentDeliveriesBox.getChildren().add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox createDeliveryRow(String deliveryId, String customerName, String date, String status) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("delivery-row");

        Label icon = new Label("🚚");
        icon.setStyle("-fx-text-fill: #f5b735; -fx-font-size: 15px;");

        VBox infoBox = new VBox(2);

        Label orderLabel = new Label("#DEL-" + deliveryId);
        orderLabel.getStyleClass().add("delivery-order");

        Label customerLabel = new Label(customerName + "  •  " + date);
        customerLabel.getStyleClass().add("delivery-customer");

        infoBox.getChildren().addAll(orderLabel, customerLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusLabel = new Label(status);
        statusLabel.getStyleClass().add("delivery-status-badge");

        String statusValue = status.toLowerCase();

        if (statusValue.equals("delivered")) {
            statusLabel.getStyleClass().add("delivery-badge-delivered");
        } else if (statusValue.equals("pending")) {
            statusLabel.getStyleClass().add("delivery-badge-pending");
        } else {
            statusLabel.getStyleClass().add("delivery-badge-cancelled");
        }

        row.getChildren().addAll(icon, infoBox, spacer, statusLabel);

        return row;
    }

    @FXML
    private void handleViewAllDeliveries() {
        showAllDeliveries = !showAllDeliveries;

        viewAllDeliveriesLabel.setText(
                showAllDeliveries ? "Show Less  ↑" : "View All Deliveries  →");

        loadRecentDeliveries();
    }
}
