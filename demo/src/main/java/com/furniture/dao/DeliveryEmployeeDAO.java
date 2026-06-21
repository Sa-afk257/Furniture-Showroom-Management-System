package com.furniture.dao;

import com.furniture.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DeliveryEmployeeDAO {

    public int getAssignedTodayCount(int employeeId) {
        String sql = """
                SELECT COUNT(*)
                FROM Delivery
                WHERE EmployeeID = ?
                AND Delivery_status IN ('pending', 'assigned', 'picked_up', 'in_progress')
                """;

        return getInt(sql, employeeId);
    }

    public int getCompletedTodayCount(int employeeId) {
        String sql = """
                SELECT COUNT(*)
                FROM Delivery
                WHERE EmployeeID = ?
                AND Delivery_status = 'delivered'
                """;

        return getInt(sql, employeeId);
    }

    public int getInProgressCount(int employeeId) {
        String sql = """
                SELECT COUNT(*)
                FROM Delivery
                WHERE EmployeeID = ?
                AND Delivery_status IN ('in_progress', 'picked_up')
                """;

        return getInt(sql, employeeId);
    }

    public int getPendingPickupCount(int employeeId) {
        String sql = """
                SELECT COUNT(*)
                FROM Delivery
                WHERE EmployeeID = ?
                AND Delivery_status IN ('pending', 'assigned')
                """;

        return getInt(sql, employeeId);
    }

    private int getInt(String sql, int employeeId) {
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<DeliverySummary> getDeliveries(int employeeId, String keyword, String filter) {

        List<DeliverySummary> list = new ArrayList<>();

        String statusCondition = "";

        if ("pickup".equals(filter)) {
            statusCondition = " AND d.Delivery_status IN ('pending', 'assigned', 'picked_up') ";

        } else if ("in_progress".equals(filter)) {
            statusCondition = " AND d.Delivery_status = 'in_progress' ";
        } else if ("completed".equals(filter)) {
            statusCondition = " AND d.Delivery_status = 'delivered' ";
        }

        String sql = """
                SELECT
                    d.DeliveryID,
                    d.SaleID,
                    d.Delivery_status,
                    d.Delivery_Date,
                    CONCAT(c.firstName, ' ', c.lastName) AS customerName,
                    cp.phone,
                    CONCAT(c.city, ', ', c.town, ', ', c.area, ', ', c.street, ', ', c.building) AS address
                FROM Delivery d
                JOIN Sale s ON d.SaleID = s.SaleID
                JOIN Customer c ON s.CustomerID = c.CustomerID
                LEFT JOIN (
                    SELECT CustomerID, MIN(phone) AS phone
                    FROM Customer_phone
                    GROUP BY CustomerID
                ) cp ON c.CustomerID = cp.CustomerID
                WHERE d.EmployeeID = ?
                """ + statusCondition + """
                AND (
                    CAST(d.SaleID AS CHAR) LIKE ?
                    OR CONCAT(c.firstName, ' ', c.lastName) LIKE ?
                    OR c.city LIKE ?
                    OR c.street LIKE ?
                )
                ORDER BY
                    CASE
                        WHEN d.Delivery_status = 'in_progress' THEN 1
                        WHEN d.Delivery_status IN ('pending', 'assigned') THEN 2
                        WHEN d.Delivery_status = 'delivered' THEN 3
                        ELSE 4
                    END,
                    d.Delivery_Date DESC,
                    d.DeliveryID DESC
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            String key = "%" + keyword + "%";

            ps.setInt(1, employeeId);
            ps.setString(2, key);
            ps.setString(3, key);
            ps.setString(4, key);
            ps.setString(5, key);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new DeliverySummary(
                            rs.getInt("DeliveryID"),
                            rs.getInt("SaleID"),
                            rs.getString("customerName"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            rs.getString("Delivery_Date"),
                            rs.getString("Delivery_status")));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<DeliveryItem> getDeliveryItems(int saleId) {

        List<DeliveryItem> items = new ArrayList<>();

        String sql = """
                SELECT
                    p.ProductName,
                    sd.quantity
                FROM SaleDetails sd
                JOIN Product p ON sd.ProductID = p.ProductID
                WHERE sd.SaleID = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new DeliveryItem(
                            rs.getString("ProductName"),
                            rs.getInt("quantity")));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public boolean updateDeliveryStatus(int deliveryId, String status) {

        String sql = """
                UPDATE Delivery
                SET Delivery_status = ?
                WHERE DeliveryID = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, deliveryId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markDelivered(int deliveryId, int saleId) {

        String updateDelivery = """
                UPDATE Delivery
                SET Delivery_status = 'delivered'
                WHERE DeliveryID = ?
                """;

        String updateSale = """
                UPDATE Sale
                SET SaleStatus = 'completed',
                    ReviewNote = 'Order delivered successfully.'
                WHERE SaleID = ?
                """;

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(updateDelivery)) {
                ps.setInt(1, deliveryId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(updateSale)) {
                ps.setInt(1, saleId);
                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class DeliverySummary {
        private int deliveryId;
        private int saleId;
        private String customerName;
        private String phone;
        private String address;
        private String deliveryDate;
        private String status;

        public DeliverySummary(int deliveryId, int saleId, String customerName,
                String phone, String address, String deliveryDate, String status) {
            this.deliveryId = deliveryId;
            this.saleId = saleId;
            this.customerName = customerName;
            this.phone = phone;
            this.address = address;
            this.deliveryDate = deliveryDate;
            this.status = status;
        }

        public int getDeliveryId() {
            return deliveryId;
        }

        public int getSaleId() {
            return saleId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getPhone() {
            return phone;
        }

        public String getAddress() {
            return address;
        }

        public String getDeliveryDate() {
            return deliveryDate;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class DeliveryItem {
        private String productName;
        private int quantity;

        public DeliveryItem(String productName, int quantity) {
            this.productName = productName;
            this.quantity = quantity;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    public List<String> getRecentActivity(int employeeId) {

        List<String> activities = new ArrayList<>();

        String sql = """
                SELECT
                    d.SaleID,
                    d.Delivery_status,
                    d.Delivery_Date,
                    CONCAT(c.firstName, ' ', c.lastName) AS customerName
                FROM Delivery d
                JOIN Sale s ON d.SaleID = s.SaleID
                JOIN Customer c ON s.CustomerID = c.CustomerID
                WHERE d.EmployeeID = ?
                ORDER BY d.Delivery_Date DESC, d.DeliveryID DESC
                LIMIT 4
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    activities.add(
                            formatStatus(rs.getString("Delivery_status"))
                                    + " order #ORD-"
                                    + String.format("%04d", rs.getInt("SaleID"))
                                    + " - "
                                    + rs.getString("customerName"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return activities;
    }

    public int getDelayedCount(int employeeId) {

        String sql = """
                SELECT COUNT(*)
                FROM Delivery
                WHERE EmployeeID = ?
                AND Delivery_Date < CURDATE()
                AND Delivery_status <> 'delivered'
                """;

        return getInt(sql, employeeId);
    }

    public int getTotalAssignedCount(int employeeId) {

        String sql = """
                SELECT COUNT(*)
                FROM Delivery
                WHERE EmployeeID = ?
                """;

        return getInt(sql, employeeId);
    }

    private String formatStatus(String status) {

        if (status == null)
            return "Updated";

        switch (status) {
            case "pending":
                return "Pending";
            case "assigned":
                return "Assigned";
            case "picked_up":
                return "Picked up";
            case "in_progress":
                return "In progress";
            case "delivered":
                return "Delivered";
            default:
                return status;
        }
    }

    public boolean createDeliveryForApprovedSale(int saleId) {

        String checkSql = """
                SELECT COUNT(*)
                FROM Delivery
                WHERE SaleID = ?
                """;

        String employeeSql = """
                SELECT EmployeeID
                FROM Employee
                WHERE Employee_role = 'Delivery Employee'
                ORDER BY EmployeeID
                LIMIT 1
                """;

        String insertSql = """
                INSERT INTO Delivery
                (SaleID, EmployeeID, Delivery_Date, Delivery_status)
                VALUES (?, ?, CURDATE(), 'assigned')
                """;

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement check = con.prepareStatement(checkSql)) {
                check.setInt(1, saleId);

                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        con.rollback();
                        return true;
                    }
                }
            }

            int deliveryEmployeeId = -1;

            try (PreparedStatement ps = con.prepareStatement(employeeSql);
                    ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    deliveryEmployeeId = rs.getInt("EmployeeID");
                }
            }

            if (deliveryEmployeeId <= 0) {
                con.rollback();
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setInt(1, saleId);
                ps.setInt(2, deliveryEmployeeId);
                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}