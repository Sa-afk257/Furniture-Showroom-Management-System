package com.furniture.dao;

import com.furniture.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseManagerDAO {

    private static final int MAIN_WAREHOUSE_ID = 1;

    public int getTotalProducts() {

        String sql = "SELECT COUNT(*) FROM Product";

        return getInt(sql);
    }

    public int getTotalStock() {

        String sql = """
                SELECT IFNULL(SUM(quantity),0)
                FROM Inventory
                WHERE WarehouseID = 1
                """;

        return getInt(sql);
    }

    public int getLowStockCount() {

        String sql = """
                SELECT COUNT(*)
                FROM Inventory
                WHERE WarehouseID = 1
                  AND quantity BETWEEN 1 AND 5
                """;

        return getInt(sql);
    }

    public static class WarehouseRequest {

        private final int requestId;
        private final int saleId;
        private final String salesEmployeeName;
        private final String requestMessage;
        private final String status;

        public WarehouseRequest(
                int requestId,
                int saleId,
                String salesEmployeeName,
                String requestMessage,
                String status) {

            this.requestId = requestId;
            this.saleId = saleId;
            this.salesEmployeeName = salesEmployeeName;
            this.requestMessage = requestMessage;
            this.status = status;
        }

        public int getRequestId() {
            return requestId;
        }

        public int getSaleId() {
            return saleId;
        }

        public String getSalesEmployeeName() {
            return salesEmployeeName;
        }

        public String getRequestMessage() {
            return requestMessage;
        }

        public String getStatus() {
            return status;
        }
    }

    public int getPendingRequestsCount() {

        String sql = """
                SELECT COUNT(*)
                FROM Warehouse_Request
                WHERE RequestStatus = 'pending'
                """;

        return getInt(sql);
    }

    public List<WarehouseRequest> getPendingWarehouseRequests(String keyword) {

        List<WarehouseRequest> requests = new ArrayList<>();

        String sql = """
                SELECT
                    wr.RequestID,
                    wr.SaleID,
                    CONCAT(e.firstName,' ',e.lastName) AS salesEmployee,
                    wr.RequestMessage,
                    wr.RequestStatus
                FROM Warehouse_Request wr
                JOIN Employee e
                    ON wr.SalesEmployeeID = e.EmployeeID
                WHERE wr.RequestStatus = 'pending'
                  AND (
                        ? = ''
                        OR LPAD(wr.RequestID, 4, '0') LIKE ?
                        OR LPAD(wr.SaleID, 4, '0') LIKE ?
                        OR wr.RequestID LIKE ?
                        OR wr.SaleID LIKE ?
                        OR CONCAT(e.firstName,' ',e.lastName) LIKE ?
                        OR EXISTS (
                            SELECT 1
                            FROM SaleDetails sd
                            JOIN Product p ON sd.ProductID = p.ProductID
                            WHERE sd.SaleID = wr.SaleID
                            AND p.ProductName LIKE ?
                        )
                    )
                ORDER BY wr.RequestDate DESC
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            String like = "%" + keyword + "%";

            ps.setString(1, keyword);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            ps.setString(5, like);
            ps.setString(6, like);
            ps.setString(7, like);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                requests.add(new WarehouseRequest(
                        rs.getInt("RequestID"),
                        rs.getInt("SaleID"),
                        rs.getString("salesEmployee"),
                        rs.getString("RequestMessage"),
                        rs.getString("RequestStatus")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return requests;
    }

    private int getInt(String sql) {

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<StockMovementPoint> getStockMovementOverview(String period) {

        List<StockMovementPoint> points = new ArrayList<>();

        String filter = "";

        if ("Last 6 Months".equals(period)) {
            filter = """
                    WHERE movement_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
                    AND CURDATE()
                    """;
        } else if ("This Year".equals(period)) {
            filter = """
                    WHERE YEAR(movement_date) = YEAR(CURDATE())
                    AND movement_date <= CURDATE()
                    """;
        } else {
            filter = "";
        }

        String sql = """
                SELECT
                    YEAR(movement_date) AS movementYear,
                    MONTH(movement_date) AS movementMonth,
                    DATE_FORMAT(movement_date,'%b') AS monthName,

                    SUM(CASE WHEN movementType='IN' THEN quantity ELSE 0 END) AS stockIn,
                    SUM(CASE WHEN movementType='OUT' THEN quantity ELSE 0 END) AS stockOut

                FROM StockMovement
                """ + filter + """
                GROUP BY
                    YEAR(movement_date),
                    MONTH(movement_date),
                    DATE_FORMAT(movement_date,'%b')
                ORDER BY
                    movementYear,
                    movementMonth
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                points.add(new StockMovementPoint(
                        rs.getString("monthName"),
                        rs.getDouble("stockIn"),
                        rs.getDouble("stockOut")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return points;
    }

    public InventoryStatus getInventoryStatus() {

        String sql = """
                SELECT
                    SUM(CASE WHEN quantity > 5 THEN 1 ELSE 0 END) availableItems,
                    SUM(CASE WHEN quantity BETWEEN 1 AND 5 THEN 1 ELSE 0 END) lowItems,
                    SUM(CASE WHEN quantity = 0 THEN 1 ELSE 0 END) outItems
                FROM Inventory
                WHERE WarehouseID = 1
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new InventoryStatus(
                        rs.getInt("availableItems"),
                        rs.getInt("lowItems"),
                        rs.getInt("outItems"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new InventoryStatus(0, 0, 0);
    }

    public List<LowStockItem> getLowStockItems() {

        List<LowStockItem> items = new ArrayList<>();

        String sql = """
                SELECT
                    p.ProductName,
                    i.quantity
                FROM Inventory i
                JOIN Product p
                    ON i.ProductID = p.ProductID
                WHERE i.WarehouseID = 1
                  AND i.quantity BETWEEN 1 AND 5
                ORDER BY i.quantity ASC
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                items.add(new LowStockItem(
                        rs.getString("ProductName"),
                        rs.getDouble("quantity")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }
    /*
     * ===========================
     * INNER CLASSES
     * ===========================
     */

    public static class StockMovementPoint {

        private final String monthName;
        private final double stockIn;
        private final double stockOut;

        public StockMovementPoint(
                String monthName,
                double stockIn,
                double stockOut) {

            this.monthName = monthName;
            this.stockIn = stockIn;
            this.stockOut = stockOut;
        }

        public String getMonthName() {
            return monthName;
        }

        public double getStockIn() {
            return stockIn;
        }

        public double getStockOut() {
            return stockOut;
        }
    }

    public static class InventoryStatus {

        private final int available;
        private final int lowStock;
        private final int outOfStock;

        public InventoryStatus(
                int available,
                int lowStock,
                int outOfStock) {

            this.available = available;
            this.lowStock = lowStock;
            this.outOfStock = outOfStock;
        }

        public int getAvailable() {
            return available;
        }

        public int getLowStock() {
            return lowStock;
        }

        public int getOutOfStock() {
            return outOfStock;
        }
    }

    public static class LowStockItem {

        private final String productName;
        private final double quantity;

        public LowStockItem(
                String productName,
                double quantity) {

            this.productName = productName;
            this.quantity = quantity;
        }

        public String getProductName() {
            return productName;
        }

        public double getQuantity() {
            return quantity;
        }
    }

    public List<RequestItemAvailability> getRequestItemsAvailability(int saleId) {

        List<RequestItemAvailability> items = new ArrayList<>();

        String sql = """
                SELECT
                    p.ProductID,
                    p.ProductName,
                    sd.quantity AS requestedQty,
                    IFNULL(SUM(i.quantity), 0) AS availableQty
                FROM SaleDetails sd
                JOIN Product p
                    ON sd.ProductID = p.ProductID
                LEFT JOIN Inventory i
                    ON i.ProductID = p.ProductID
                WHERE sd.SaleID = ?
                GROUP BY
                    p.ProductID,
                    p.ProductName,
                    sd.quantity
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                items.add(new RequestItemAvailability(
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getDouble("requestedQty"),
                        rs.getDouble("availableQty")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public boolean respondToWarehouseRequest(
            int requestId,
            int warehouseEmployeeId,
            String status,
            String responseMessage) {

        String getSaleSql = """
                    SELECT SaleID
                    FROM Warehouse_Request
                    WHERE RequestID = ?
                """;

        String updateRequestSql = """
                    UPDATE Warehouse_Request
                    SET
                        RequestStatus = ?,
                        WarehouseEmployeeID = ?,
                        ResponseMessage = ?,
                        ResponseDate = NOW()
                    WHERE RequestID = ?
                """;

        String updateSaleSql = """
                    UPDATE Sale
                    SET
                        SaleStatus = 'under_review',
                        ReviewNote = ?
                    WHERE SaleID = ?
                """;

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            int saleId = -1;

            try (PreparedStatement ps = con.prepareStatement(getSaleSql)) {
                ps.setInt(1, requestId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        saleId = rs.getInt("SaleID");
                    }
                }
            }

            if (saleId == -1) {
                con.rollback();
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(updateRequestSql)) {
                ps.setString(1, status);
                ps.setInt(2, warehouseEmployeeId);
                ps.setString(3, responseMessage);
                ps.setInt(4, requestId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(updateSaleSql)) {
                ps.setString(1, "Warehouse response: " + status + "\n" + responseMessage);
                ps.setInt(2, saleId);
                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class RequestItemAvailability {

        private final int productId;
        private final String productName;
        private final double requestedQty;
        private final double availableQty;

        public RequestItemAvailability(
                int productId,
                String productName,
                double requestedQty,
                double availableQty) {

            this.productId = productId;
            this.productName = productName;
            this.requestedQty = requestedQty;
            this.availableQty = availableQty;
        }

        public int getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public double getRequestedQty() {
            return requestedQty;
        }

        public double getAvailableQty() {
            return availableQty;
        }

        public boolean isAvailable() {
            return availableQty >= requestedQty;
        }

        public String getResultText() {
            return isAvailable() ? "Available" : "Not Enough";
        }
    }

}