package com.furniture.ui;

import com.furniture.dao.WarehouseManagerDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class WarehouseManagerController extends BaseController {

    private final WarehouseManagerDAO warehouseDAO = new WarehouseManagerDAO();

    private int selectedRequestId = -1;
    private int selectedSaleId = -1;

    @FXML
    private Label totalProductsLabel;
    @FXML
    private Label totalStockLabel;
    @FXML
    private Label lowStockLabel;
    @FXML
    private Label pendingRequestsLabel;

    @FXML
    private GridPane requestsGrid;
    @FXML
    private LineChart<String, Number> stockMovementChart;
    @FXML
    private PieChart inventoryStatusChart;
    @FXML
    private Label inventoryCenterLabel;
    @FXML
    private VBox lowStockBox;

    @FXML
    private TextField searchField;
    @FXML
    private Button searchBtn;
    @FXML
    private Button refreshRequestsBtn;

    @FXML
    private VBox requestDetailsPanel;
    @FXML
    private Label selectedRequestHintLabel;
    @FXML
    private Label selectedRequestStatusLabel;
    @FXML
    private Label selectedRequestIdLabel;
    @FXML
    private Label selectedSaleIdLabel;
    @FXML
    private Label selectedSalesEmployeeLabel;
    @FXML
    private Label selectedRequestMessageLabel;

    @FXML
    private GridPane requestItemsGrid;
    @FXML
    private Label availabilitySummaryLabel;
    @FXML
    private TextArea responseMessageArea;

    @FXML
    private Button markAvailableBtn;
    @FXML
    private Button markPartialBtn;
    @FXML
    private Button markNotAvailableBtn;
    @FXML
    private Button accountMenuBtn;

    @FXML
    private ComboBox<String> movementPeriodCombo;

    private List<WarehouseManagerDAO.RequestItemAvailability> currentRequestItems;

    @FXML
    public void initialize() {
        setupButtons();
        clearSelectedRequest();

        loadKpiCards();
        loadWarehouseRequests();
        loadStockMovementChart();
        loadInventoryStatusChart();
        loadLowStockAlerts();
        createAccountMenu(accountMenuBtn);

        setupMovementPeriodCombo();
    }
    
    private void setupButtons() {
        refreshRequestsBtn.setOnAction(e -> refreshAll());

        searchBtn.setOnAction(e -> loadWarehouseRequests());
        searchField.setOnAction(e -> loadWarehouseRequests());

        markAvailableBtn.setOnAction(e -> respondToRequest("available",
                "Items are available in main warehouse."));

        markPartialBtn.setOnAction(e -> respondToRequest("partially_available",
                "Some items are not fully available."));

        markNotAvailableBtn.setOnAction(e -> respondToRequest("not_available",
                "Items are not available in main warehouse."));

    }

    private void refreshAll() {
        clearSelectedRequest();
        loadKpiCards();
        loadWarehouseRequests();
        loadStockMovementChart();
        loadInventoryStatusChart();
        loadLowStockAlerts();
        setupMovementPeriodCombo();
    }

    private void loadKpiCards() {
        totalProductsLabel.setText(String.valueOf(warehouseDAO.getTotalProducts()));
        totalStockLabel.setText(String.valueOf(warehouseDAO.getTotalStock()));
        lowStockLabel.setText(String.valueOf(warehouseDAO.getLowStockCount()));
        pendingRequestsLabel.setText(String.valueOf(warehouseDAO.getPendingRequestsCount()));
    }

    private void loadWarehouseRequests() {
        requestsGrid.getChildren().removeIf(node -> {
            Integer row = GridPane.getRowIndex(node);
            return row != null && row > 0;
        });

        String keyword = searchField.getText() == null ? "" : searchField.getText().trim();

        List<WarehouseManagerDAO.WarehouseRequest> requests = warehouseDAO.getPendingWarehouseRequests(keyword);

        int row = 1;

        for (WarehouseManagerDAO.WarehouseRequest request : requests) {

            Label requestId = new Label("#REQ-" + String.format("%04d", request.getRequestId()));
            Label saleId = new Label("#ORD-" + String.format("%04d", request.getSaleId()));
            Label employee = new Label(request.getSalesEmployeeName());
            Label status = new Label(request.getStatus());
            Label action = new Label("Review");

            requestId.getStyleClass().add("activity-desc");
            saleId.getStyleClass().add("activity-desc");
            employee.getStyleClass().add("activity-desc");
            status.getStyleClass().add("legend-yellow");
            action.getStyleClass().add("view-link");

            action.setOnMouseClicked(e -> loadRequestDetails(request));

            requestsGrid.add(requestId, 0, row);
            requestsGrid.add(saleId, 1, row);
            requestsGrid.add(employee, 2, row);
            requestsGrid.add(status, 3, row);
            requestsGrid.add(action, 4, row);

            row++;
        }

        if (requests.isEmpty()) {
            Label empty = new Label("No pending warehouse requests.");
            empty.getStyleClass().add("empty-text");
            requestsGrid.add(empty, 0, 1, 5, 1);
        }
    }

    private void setupMovementPeriodCombo() {
        movementPeriodCombo.getItems().setAll(
                "Last 6 Months",
                "This Year",
                "All Time");

        movementPeriodCombo.getSelectionModel().select("Last 6 Months");

        movementPeriodCombo.setOnAction(e -> loadStockMovementChart());
    }

    private void loadRequestDetails(WarehouseManagerDAO.WarehouseRequest request) {
        selectedRequestId = request.getRequestId();
        selectedSaleId = request.getSaleId();

        selectedRequestHintLabel.setText("Request selected. Check item availability before responding.");
        selectedRequestStatusLabel.setText(request.getStatus());
        selectedRequestIdLabel.setText("#REQ-" + String.format("%04d", request.getRequestId()));
        selectedSaleIdLabel.setText("#ORD-" + String.format("%04d", request.getSaleId()));
        selectedSalesEmployeeLabel.setText(request.getSalesEmployeeName());
        selectedRequestMessageLabel.setText(request.getRequestMessage());

        responseMessageArea.clear();
        responseMessageArea.setText("Select a response type to generate stock check message.");

        setResponseButtonsDisabled(false);

        loadRequestItems(request.getSaleId());
    }

    private void loadRequestItems(int saleId) {
        requestItemsGrid.getChildren().removeIf(node -> {
            Integer row = GridPane.getRowIndex(node);
            return row != null && row > 0;
        });

        List<WarehouseManagerDAO.RequestItemAvailability> items = warehouseDAO.getRequestItemsAvailability(saleId);
        currentRequestItems = items;

        int row = 1;
        int availableCount = 0;

        for (WarehouseManagerDAO.RequestItemAvailability item : items) {

            Label product = new Label(item.getProductName());
            Label requested = new Label(String.valueOf(item.getRequestedQty()));
            Label available = new Label(String.valueOf(item.getAvailableQty()));
            Label result = new Label(item.getResultText());

            product.getStyleClass().add("activity-desc");
            requested.getStyleClass().add("activity-desc");
            available.getStyleClass().add("activity-desc");

            if (item.isAvailable()) {
                result.getStyleClass().add("success-badge");
                availableCount++;
            } else {
                result.getStyleClass().add("danger-badge");
            }

            requestItemsGrid.add(product, 0, row);
            requestItemsGrid.add(requested, 1, row);
            requestItemsGrid.add(available, 2, row);
            requestItemsGrid.add(result, 3, row);

            row++;
        }

        if (items.isEmpty()) {
            Label empty = new Label("No items found for this sale.");
            empty.getStyleClass().add("empty-text");
            requestItemsGrid.add(empty, 0, 1, 4, 1);
            availabilitySummaryLabel.setText("No items");
            return;
        }

        if (availableCount == items.size()) {
            availabilitySummaryLabel.setText("All Available");
            availabilitySummaryLabel.getStyleClass().setAll("success-badge");
        } else if (availableCount == 0) {
            availabilitySummaryLabel.setText("Not Available");
            availabilitySummaryLabel.getStyleClass().setAll("danger-badge");
        } else {
            availabilitySummaryLabel.setText("Partially Available");
            availabilitySummaryLabel.getStyleClass().setAll("warning-badge");
        }
    }

    private String buildStockResponseMessage(String finalStatus) {

        if (currentRequestItems == null || currentRequestItems.isEmpty()) {
            return "No item details found for this warehouse request.";
        }

        StringBuilder message = new StringBuilder();

        message.append("Stock check result in main warehouse:\n\n");

        for (WarehouseManagerDAO.RequestItemAvailability item : currentRequestItems) {

            message.append("- ")
                    .append(item.getProductName())
                    .append(": requested ")
                    .append(formatQty(item.getRequestedQty()))
                    .append(", available ")
                    .append(formatQty(item.getAvailableQty()))
                    .append(" -> ")
                    .append(item.getResultText())
                    .append("\n");
        }

        message.append("\nFinal warehouse response: ");

        if (finalStatus.equals("available")) {
            message.append("All requested items are available.");
        } else if (finalStatus.equals("partially_available")) {
            message.append("Some items are available and some items are not enough.");
        } else {
            message.append("Requested items are not available.");
        }

        return message.toString();
    }

    private String formatQty(double qty) {
        if (qty == (int) qty) {
            return String.valueOf((int) qty);
        }
        return String.valueOf(qty);
    }

    private void respondToRequest(String status, String defaultMessage) {
        if (selectedRequestId == -1 || selectedSaleId == -1) {
            showError("Please select a warehouse request first.");
            return;
        }

        String message = buildStockResponseMessage(status);

        if (responseMessageArea.getText() != null &&
                !responseMessageArea.getText().trim().isEmpty() &&
                !responseMessageArea.getText().contains("Select a response type")) {

            message += "\nManager note:\n" + responseMessageArea.getText().trim();
        }
        int employeeId = Session.getCurrentEmployeeId();

        if (employeeId == -1) {
            employeeId = 4;
            // showError("No warehouse employee is logged in. Please login using an employee
            // account.");
            // return;
        }

        boolean done = warehouseDAO.respondToWarehouseRequest(
                selectedRequestId,
                employeeId,
                status,
                message.trim());
        if (done) {
            showInfo("Response sent successfully.");
            refreshAll();
        } else {
            showError("Failed to send response.");
        }
    }

    private void clearSelectedRequest() {
        selectedRequestId = -1;
        selectedSaleId = -1;

        selectedRequestHintLabel.setText("No request selected yet.");
        selectedRequestStatusLabel.setText("Waiting");
        selectedRequestStatusLabel.getStyleClass().setAll("neutral-badge");

        selectedRequestIdLabel.setText("-");
        selectedSaleIdLabel.setText("-");
        selectedSalesEmployeeLabel.setText("-");
        selectedRequestMessageLabel.setText("-");

        availabilitySummaryLabel.setText("Select request first");
        availabilitySummaryLabel.getStyleClass().setAll("neutral-badge");

        responseMessageArea.clear();

        requestItemsGrid.getChildren().removeIf(node -> {
            Integer row = GridPane.getRowIndex(node);
            return row != null && row > 0;
        });

        setResponseButtonsDisabled(true);
    }

    private void setResponseButtonsDisabled(boolean disabled) {
        markAvailableBtn.setDisable(disabled);
        markPartialBtn.setDisable(disabled);
        markNotAvailableBtn.setDisable(disabled);
    }

    private void loadStockMovementChart() {
        stockMovementChart.getData().clear();

        String period = movementPeriodCombo.getValue();

        if (period == null || period.isEmpty()) {
            period = "Last 6 Months";
        }

        XYChart.Series<String, Number> stockInSeries = new XYChart.Series<>();
        stockInSeries.setName("Stock In");

        XYChart.Series<String, Number> stockOutSeries = new XYChart.Series<>();
        stockOutSeries.setName("Stock Out");

        List<WarehouseManagerDAO.StockMovementPoint> points = warehouseDAO.getStockMovementOverview(period);

        for (WarehouseManagerDAO.StockMovementPoint point : points) {
            stockInSeries.getData().add(new XYChart.Data<>(point.getMonthName(), point.getStockIn()));
            stockOutSeries.getData().add(new XYChart.Data<>(point.getMonthName(), point.getStockOut()));
        }

        stockMovementChart.getData().setAll(stockInSeries, stockOutSeries);
        stockMovementChart.setLegendVisible(true);
        stockMovementChart.setAnimated(false);

        Platform.runLater(() -> styleStockMovementChart(stockInSeries, stockOutSeries));

    }

    private void styleStockMovementChart(
            XYChart.Series<String, Number> stockInSeries,
            XYChart.Series<String, Number> stockOutSeries) {

        if (stockInSeries.getNode() != null) {
            stockInSeries.getNode().setStyle(
                    "-fx-stroke: #8fd18f;" +
                            "-fx-stroke-width: 2.5px;");
        }

        if (stockOutSeries.getNode() != null) {
            stockOutSeries.getNode().setStyle(
                    "-fx-stroke: #ff6b6b;" +
                            "-fx-stroke-width: 2.5px;");
        }

        for (XYChart.Data<String, Number> data : stockInSeries.getData()) {
            if (data.getNode() != null) {
                data.getNode().setStyle(
                        "-fx-background-color: #8fd18f, #111;" +
                                "-fx-background-radius: 7px;" +
                                "-fx-padding: 5px;");
            }
        }

        for (XYChart.Data<String, Number> data : stockOutSeries.getData()) {
            if (data.getNode() != null) {
                data.getNode().setStyle(
                        "-fx-background-color: #ff6b6b, #111;" +
                                "-fx-background-radius: 7px;" +
                                "-fx-padding: 5px;");
            }
        }
    }

    private void loadInventoryStatusChart() {
        inventoryStatusChart.getData().clear();

        WarehouseManagerDAO.InventoryStatus status = warehouseDAO.getInventoryStatus();

        PieChart.Data available = new PieChart.Data("Available", status.getAvailable());
        PieChart.Data low = new PieChart.Data("Low Stock", status.getLowStock());
        PieChart.Data out = new PieChart.Data("Out of Stock", status.getOutOfStock());

        inventoryStatusChart.getData().addAll(available, low, out);

        int total = status.getAvailable() + status.getLowStock() + status.getOutOfStock();
        inventoryCenterLabel.setText(String.valueOf(total));

        inventoryStatusChart.setLabelsVisible(false);
        inventoryStatusChart.setLegendVisible(false);
        inventoryStatusChart.setStartAngle(90);

        Platform.runLater(() -> {
            String[] colors = { "#8fd18f", "#e0b24d", "#ff6b6b" };

            for (int i = 0; i < inventoryStatusChart.getData().size(); i++) {
                PieChart.Data data = inventoryStatusChart.getData().get(i);
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-pie-color: " + colors[i] + ";");
                }
            }
        });
    }

    private void loadLowStockAlerts() {
        lowStockBox.getChildren().clear();

        List<WarehouseManagerDAO.LowStockItem> items = warehouseDAO.getLowStockItems();

        if (items.isEmpty()) {
            Label empty = new Label("No low stock items yet.");
            empty.getStyleClass().add("empty-text");
            lowStockBox.getChildren().add(empty);
            return;
        }

        for (WarehouseManagerDAO.LowStockItem item : items) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            VBox info = new VBox(3);

            Label name = new Label(item.getProductName());
            name.getStyleClass().add("activity-title");

            Label qty = new Label("Available: " + item.getQuantity());
            qty.getStyleClass().add("activity-desc");

            info.getChildren().addAll(name, qty);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label badge = new Label("Low");
            badge.getStyleClass().add("danger-badge");

            row.getChildren().addAll(info, spacer, badge);
            lowStockBox.getChildren().add(row);
        }
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Warehouse Manager");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Warehouse Manager");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}