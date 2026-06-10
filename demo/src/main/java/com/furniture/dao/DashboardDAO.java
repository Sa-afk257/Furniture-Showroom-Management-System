package com.furniture.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.furniture.DBConnection;


public class DashboardDAO {

    public static int getTotalProducts() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM Product";

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt("total") : 0;
        }
    }

    public static int getTotalCustomers() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM Customer";

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt("total") : 0;
        }
    }

    public static int getTotalSales() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM Sale";

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt("total") : 0;
        }
    }

    public static double getTotalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_Amount), 0) AS total FROM Sale";

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getDouble("total") : 0.0;
        }
    }

    public static int getTotalWarehouses() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM Warehouse";

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt("total") : 0;
        }
    }

    private static double calculateGrowth(double current, double previous) {

        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }

        return ((current - previous) / previous) * 100.0;
    }

    public static double getProductsGrowth() throws SQLException {

        String sql = """
            SELECT
                SUM(CASE
                    WHEN MONTH(CreatedDate) = MONTH(CURDATE())
                    AND YEAR(CreatedDate) = YEAR(CURDATE())
                    THEN 1 ELSE 0 END) AS current_month,

                SUM(CASE
                    WHEN MONTH(CreatedDate) = MONTH(CURDATE() - INTERVAL 1 MONTH)
                    AND YEAR(CreatedDate) = YEAR(CURDATE() - INTERVAL 1 MONTH)
                    THEN 1 ELSE 0 END) AS previous_month
            FROM Product
        """;

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return calculateGrowth(
                        rs.getDouble("current_month"),
                        rs.getDouble("previous_month")
                );
            }
        }

        return 0.0;
    }

    public static double getCustomersGrowth() throws SQLException {

        String sql = """
            SELECT
                SUM(CASE
                    WHEN MONTH(RegistrationDate) = MONTH(CURDATE())
                    AND YEAR(RegistrationDate) = YEAR(CURDATE())
                    THEN 1 ELSE 0 END) AS current_month,

                SUM(CASE
                    WHEN MONTH(RegistrationDate) = MONTH(CURDATE() - INTERVAL 1 MONTH)
                    AND YEAR(RegistrationDate) = YEAR(CURDATE() - INTERVAL 1 MONTH)
                    THEN 1 ELSE 0 END) AS previous_month
            FROM Customer
        """;

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return calculateGrowth(
                        rs.getDouble("current_month"),
                        rs.getDouble("previous_month")
                );
            }
        }

        return 0.0;
    }

    public static double getSalesGrowth() throws SQLException {

        String sql = """
            SELECT
                SUM(CASE
                    WHEN MONTH(SaleDate) = MONTH(CURDATE())
                    AND YEAR(SaleDate) = YEAR(CURDATE())
                    THEN 1 ELSE 0 END) AS current_month,

                SUM(CASE
                    WHEN MONTH(SaleDate) = MONTH(CURDATE() - INTERVAL 1 MONTH)
                    AND YEAR(SaleDate) = YEAR(CURDATE() - INTERVAL 1 MONTH)
                    THEN 1 ELSE 0 END) AS previous_month
            FROM Sale
        """;

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return calculateGrowth(
                        rs.getDouble("current_month"),
                        rs.getDouble("previous_month")
                );
            }
        }

        return 0.0;
    }

    public static double getRevenueGrowth() throws SQLException {

        String sql = """
            SELECT
                SUM(CASE
                    WHEN MONTH(SaleDate) = MONTH(CURDATE())
                    AND YEAR(SaleDate) = YEAR(CURDATE())
                    THEN total_Amount ELSE 0 END) AS current_month,

                SUM(CASE
                    WHEN MONTH(SaleDate) = MONTH(CURDATE() - INTERVAL 1 MONTH)
                    AND YEAR(SaleDate) = YEAR(CURDATE() - INTERVAL 1 MONTH)
                    THEN total_Amount ELSE 0 END) AS previous_month
            FROM Sale
        """;

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return calculateGrowth(
                        rs.getDouble("current_month"),
                        rs.getDouble("previous_month")
                );
            }
        }

        return 0.0;
    }

    public static List<Integer> getAvailableSalesYears() throws SQLException {
        List<Integer> years = new ArrayList<>();

        String sql = """
            SELECT DISTINCT YEAR(SaleDate) AS sale_year
            FROM Sale
            ORDER BY sale_year DESC
        """;

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                years.add(rs.getInt("sale_year"));
            }
        }

        return years;
    }

    public static Map<Integer, Double> getMonthlyRevenue(int year) throws SQLException {
        Map<Integer, Double> data = new HashMap<>();

        String sql = """
            SELECT
                MONTH(SaleDate) AS month_num,
                SUM(total_Amount) AS monthly_revenue
            FROM Sale
            WHERE YEAR(SaleDate)=?
            GROUP BY MONTH(SaleDate)
            ORDER BY month_num;
        """;

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.put(
                        rs.getInt("month_num"),
                        rs.getDouble("monthly_revenue")
                    );
                }
            }
        }

        return data;
    }

    public static Map<String, Double> getSalesByCategory()
        throws SQLException {

        Map<String, Double> result = new LinkedHashMap<>();

        String sql = """
            SELECT
                c.CategoryName,
                SUM(sd.quantity * sd.price) AS total_sales
            FROM SaleDetails sd
            JOIN Product p
            ON p.ProductID = sd.ProductID
            JOIN Category c
            ON c.CategoryID = p.CategoryID
            GROUP BY c.CategoryID,c.CategoryName
            ORDER BY total_sales DESC;
            """;

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                result.put(
                    rs.getString("CategoryName"),
                    rs.getDouble("total_sales")
                );
            }
        }

        return result;
    }
    
    public static class TopProduct {

        private String productName;
        private int soldQuantity;
        private double price;
        private String imagePath;

        public TopProduct(String productName, int soldQuantity, double price, String imagePath) {
            this.productName = productName;
            this.price = price;
            this.soldQuantity = soldQuantity;
            this.imagePath = imagePath;
        }

        public String getProductName() {
            return productName;
        }

        public int getSoldQuantity() {
            return soldQuantity;
        }

        public double getPrice() {
            return price;
        }

        public String getImagePath() {
            return imagePath;
}
    }

    public static List<TopProduct> getTopSellingProducts()
        throws SQLException {

        List<TopProduct> products = new ArrayList<>();

        String sql = """
            SELECT
                p.ProductName,
                p.price,
                p.imagePath,
                SUM(sd.quantity) AS totalSold
            FROM SaleDetails sd
            JOIN Product p
                ON p.ProductID = sd.ProductID
            GROUP BY p.ProductID,p.ProductName
            ORDER BY totalSold DESC
            LIMIT 5
        """;

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                products.add(
                    new TopProduct(
                        rs.getString("ProductName"),
                        rs.getInt("totalSold"),
                        rs.getDouble("price"),
                        rs.getString("imagePath")
                    )
                );
            }
        }

        return products;
    }

    public static class RecentOrder {

        private int saleId;
        private String customerName;
        private String saleDate;
        private double amount;
        private String status;

        public RecentOrder(int saleId, String customerName,
                        String saleDate, double amount, String status) {
            this.saleId = saleId;
            this.customerName = customerName;
            this.saleDate = saleDate;
            this.amount = amount;
            this.status = status;
        }

        public int getSaleId() { return saleId; }
        public String getCustomerName() { return customerName; }
        public String getSaleDate() { return saleDate; }
        public double getAmount() { return amount; }
        public String getStatus() { return status; }
    }

    public static List<RecentOrder> getRecentOrders(int limit)
        throws SQLException {

        List<RecentOrder> orders = new ArrayList<>();

        String sql = """
            SELECT
                s.SaleID,
                CONCAT(c.firstName, ' ', c.lastName) AS customerName,
                s.SaleDate,
                s.total_Amount,
                d.Delivary_status AS status
            FROM Sale s
            JOIN Customer c ON c.CustomerID = s.CustomerID
            LEFT JOIN Delivary d ON d.SaleID = s.SaleID
            ORDER BY s.SaleDate DESC, s.SaleID DESC
            LIMIT ?
        """;

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(new RecentOrder(
                            rs.getInt("SaleID"),
                            rs.getString("customerName"),
                            rs.getString("SaleDate"),
                            rs.getDouble("total_Amount"),
                            rs.getString("status")
                    ));
                }
            }
        }

        return orders;
    }

    public static class InventoryAlert {
        private String productName;
        private double quantity;

        public InventoryAlert(String productName, double quantity) {
            this.productName = productName;
            this.quantity = quantity;
        }

        public String getProductName() { return productName; }
        public double getQuantity() { return quantity; }
    }

    public static List<InventoryAlert> getInventoryAlerts(int limit) throws SQLException {
        List<InventoryAlert> alerts = new ArrayList<>();

        String sql = """
            SELECT
                p.ProductName,
                SUM(i.quantity) AS totalQty
            FROM Inventory i
            JOIN Product p ON p.ProductID = i.ProductID
            GROUP BY p.ProductID, p.ProductName
            ORDER BY totalQty ASC
            LIMIT ?
        """;

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    alerts.add(new InventoryAlert(
                            rs.getString("ProductName"),
                            rs.getDouble("totalQty")
                    ));
                }
            }
        }

        return alerts;
    }

     public static class DeliveryStats {

        private int delivered;
        private int pending;
        private int cancelled;

        public DeliveryStats(int delivered, int pending, int cancelled) {
            this.delivered = delivered;
            this.pending = pending;
            this.cancelled = cancelled;
        }

        public int getDelivered() {
            return delivered;
        }

        public int getPending() {
            return pending;
        }

        public int getCancelled() {
            return cancelled;
        }
    }
    
    public static class RecentDelivery {

        private int deliveryId;
        private String customerName;
        private String deliveryDate;
        private String status;

        public RecentDelivery(int deliveryId,
                            String customerName,
                            String deliveryDate,
                            String status) {

            this.deliveryId = deliveryId;
            this.customerName = customerName;
            this.deliveryDate = deliveryDate;
            this.status = status;
        }

        public int getDeliveryId() {
            return deliveryId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getDeliveryDate() {
            return deliveryDate;
        }

        public String getStatus() {
            return status;
        }
    }
  
    public static DeliveryStats getDeliveryStats()
        throws SQLException {

        String sql = """
            SELECT
                SUM(CASE WHEN Delivary_status='delivered' THEN 1 ELSE 0 END) delivered,
                SUM(CASE WHEN Delivary_status='pending' THEN 1 ELSE 0 END) pending,
                SUM(CASE WHEN Delivary_status='cancelled' THEN 1 ELSE 0 END) cancelled
            FROM Delivary
        """;

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                return new DeliveryStats(
                    rs.getInt("delivered"),
                    rs.getInt("pending"),
                    rs.getInt("cancelled")
                );
            }
        }

        return new DeliveryStats(0,0,0);
    }

    public static List<RecentDelivery> getRecentDeliveries(int limit)
        throws SQLException {

        List<RecentDelivery> deliveries = new ArrayList<>();

        String sql = """
            SELECT
                d.DelivaryID,
                CONCAT(c.firstName,' ',c.lastName) AS customerName,
                d.Delivary_Date,
                d.Delivary_status
            FROM Delivary d
            JOIN Sale s
                ON s.SaleID = d.SaleID
            JOIN Customer c
                ON c.CustomerID = s.CustomerID
            ORDER BY d.Delivary_Date DESC
            LIMIT ?
        """;

        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    deliveries.add(
                        new RecentDelivery(
                            rs.getInt("DelivaryID"),
                            rs.getString("customerName"),
                            rs.getString("Delivary_Date"),
                            rs.getString("Delivary_status")
                        )
                    );
                }
            }
        }

        return deliveries;
    }




}
