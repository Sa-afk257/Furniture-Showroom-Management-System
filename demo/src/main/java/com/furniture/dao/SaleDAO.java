package com.furniture.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.furniture.model.Sale;
import com.furniture.model.SaleDetailes;
import com.furniture.DBConnection;

public class SaleDAO {
    public static class SaleStats {
        private int totalSales;
        private double totalRevenue;
        private int productsSold;
        private int pendingDeliveries;
        private double outstandingBalance;

        public SaleStats(int totalSales, double totalRevenue, int productsSold,
                int pendingDeliveries, double outstandingBalance) {
            this.totalSales = totalSales;
            this.totalRevenue = totalRevenue;
            this.productsSold = productsSold;
            this.pendingDeliveries = pendingDeliveries;
            this.outstandingBalance = outstandingBalance;
        }

        public int getTotalSales() {
            return totalSales;
        }

        public double getTotalRevenue() {
            return totalRevenue;
        }

        public int getProductsSold() {
            return productsSold;
        }

        public int getPendingDeliveries() {
            return pendingDeliveries;
        }

        public double getOutstandingBalance() {
            return outstandingBalance;
        }
    }

    public List<String> getCustomerNames() {
        List<String> customers = new ArrayList<>();

        String sql = """
                SELECT CONCAT(firstName, ' ', lastName) AS customerName
                FROM Customer
                ORDER BY firstName, lastName
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                customers.add(rs.getString("customerName"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return customers;
    }

    public List<String> getEmployeeNames() {
        List<String> employees = new ArrayList<>();

        String sql = """
                SELECT CONCAT(firstName, ' ', lastName) AS employeeName
                FROM Employee
                ORDER BY firstName, lastName
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employees.add(rs.getString("employeeName"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    public SaleStats getSaleStats() {

        int totalSales = 0;
        double totalRevenue = 0;
        int productsSold = 0;
        int pendingDeliveries = 0;
        double outstandingBalance = 0;

        String totalSql = """
                SELECT COUNT(*) AS totalSales,
                       COALESCE(SUM(total_Amount), 0) AS totalRevenue
                FROM Sale
                """;

        String productsSql = """
                SELECT COALESCE(SUM(quantity), 0) AS productsSold
                FROM SaleDetails
                """;

        String pendingSql = """
                SELECT COUNT(*) AS pendingDeliveries
                FROM Delivary
                WHERE Delivary_status = 'pending'
                """;

        String balanceSql = """
                SELECT COALESCE(SUM(s.total_Amount), 0) -
                       COALESCE(SUM(p.paidAmount), 0) AS outstandingBalance
                FROM Sale s
                LEFT JOIN (
                    SELECT SaleID, SUM(amount) AS paidAmount
                    FROM Payment
                    GROUP BY SaleID
                ) p ON s.SaleID = p.SaleID
                """;

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(totalSql);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalSales = rs.getInt("totalSales");
                    totalRevenue = rs.getDouble("totalRevenue");
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(productsSql);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    productsSold = rs.getInt("productsSold");
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(pendingSql);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pendingDeliveries = rs.getInt("pendingDeliveries");
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(balanceSql);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    outstandingBalance = rs.getDouble("outstandingBalance");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new SaleStats(
                totalSales,
                totalRevenue,
                productsSold,
                pendingDeliveries,
                outstandingBalance);
    }

    public List<Sale> getAllSalesForTable() {

        List<Sale> sales = new ArrayList<>();

        String sql = """
                SELECT
                    s.SaleID,
                    s.CustomerID,
                    s.EmployeeID,
                    s.SaleDate,
                    s.total_Amount,

                    CONCAT(c.firstName,' ',c.lastName) AS customerName,
                    CONCAT(e.firstName,' ',e.lastName) AS employeeName,

                    COUNT(DISTINCT sd.ProductID) AS itemsCount,

                    COALESCE(SUM(p.amount),0) AS paidAmount,

                    d.Delivary_status,
                    d.Delivary_Date

                FROM Sale s

                JOIN Customer c
                    ON s.CustomerID = c.CustomerID

                JOIN Employee e
                    ON s.EmployeeID = e.EmployeeID

                LEFT JOIN SaleDetails sd
                    ON s.SaleID = sd.SaleID

                LEFT JOIN Payment p
                    ON s.SaleID = p.SaleID

                LEFT JOIN Delivary d
                    ON s.SaleID = d.SaleID

                GROUP BY
                    s.SaleID,
                    s.CustomerID,
                    s.EmployeeID,
                    s.SaleDate,
                    s.total_Amount,
                    customerName,
                    employeeName,
                    d.Delivary_status,
                    d.Delivary_Date

                ORDER BY s.SaleID DESC
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Sale sale = new Sale();

                sale.setSaleID(rs.getInt("SaleID"));

                sale.setCustomerID(rs.getInt("CustomerID"));
                sale.setEmployeeID(rs.getInt("EmployeeID"));

                sale.setSaleDate(
                        rs.getDate("SaleDate").toLocalDate());

                sale.setTotal_Amount(
                        rs.getDouble("total_Amount"));

                sale.setCustomerName(
                        rs.getString("customerName"));

                sale.setEmployeeName(
                        rs.getString("employeeName"));

                sale.setItemsCount(
                        rs.getInt("itemsCount"));

                double paid = rs.getDouble("paidAmount");

                sale.setPaidAmount(paid);

                double total = rs.getDouble("total_Amount");

                sale.setBalance(total - paid);

                if (paid <= 0) {
                    sale.setPaymentStatus("Unpaid");
                } else if (paid < total) {
                    sale.setPaymentStatus("Partial");
                } else {
                    sale.setPaymentStatus("Paid");
                }

                sale.setDeliveryStatus(
                        rs.getString("Delivary_status"));

                if (rs.getDate("Delivary_Date") != null) {
                    sale.setDeliveryDate(
                            rs.getDate("Delivary_Date")
                                    .toLocalDate());
                }

                sales.add(sale);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sales;
    }

    public List<SaleDetailes> getSaleDetailes(int saleID) {

        List<SaleDetailes> details = new ArrayList<>();

        String sql = """
                SELECT
                    sd.SaleID,
                    sd.ProductID,
                    p.ProductName,
                    sd.quantity,
                    sd.price
                FROM SaleDetails sd
                JOIN Product p
                    ON sd.ProductID = p.ProductID
                WHERE sd.SaleID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, saleID);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    SaleDetailes detail = new SaleDetailes();

                    detail.setSale_id(rs.getInt("SaleID"));
                    detail.setProduct_id(rs.getInt("ProductID"));
                    detail.setProductName(rs.getString("ProductName"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setPrice(rs.getDouble("price"));

                    details.add(detail);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return details;
    }

    public void insertSale(Sale sale) {

        String saleSql = """
                INSERT INTO Sale (CustomerID, EmployeeID, SaleDate, total_Amount)
                VALUES (?, ?, ?, ?)
                """;

        String detailsSql = """
                INSERT INTO SaleDetails (SaleID, ProductID, quantity, price)
                VALUES (?, ?, ?, ?)
                """;

        String paymentSql = """
                INSERT INTO Payment (SaleID, amount, Payment_Date)
                VALUES (?, ?, ?)
                """;

        String deliverySql = """
                INSERT INTO Delivary (SaleID, EmployeeID, Delivary_status, Delivary_Date)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            int saleID;

            try (PreparedStatement ps = conn.prepareStatement(
                    saleSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, sale.getCustomerID());
                ps.setInt(2, sale.getEmployeeID());
                ps.setDate(3, java.sql.Date.valueOf(sale.getSaleDate()));
                ps.setDouble(4, sale.getTotal_Amount());

                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                saleID = keys.getInt(1);
            }

            try (PreparedStatement ps = conn.prepareStatement(detailsSql)) {
                for (SaleDetailes item : sale.getItems()) {
                    ps.setInt(1, saleID);
                    ps.setInt(2, item.getProduct_id());
                    ps.setInt(3, item.getQuantity());
                    ps.setDouble(4, item.getPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            if (sale.getPaidAmount() > 0) {
                try (PreparedStatement ps = conn.prepareStatement(paymentSql)) {
                    ps.setInt(1, saleID);
                    ps.setDouble(2, sale.getPaidAmount());
                    ps.setDate(3, java.sql.Date.valueOf(sale.getSaleDate()));
                    ps.executeUpdate();
                }
            }

            if (sale.getDeliveryStatus() != null) {
                try (PreparedStatement ps = conn.prepareStatement(deliverySql)) {
                    ps.setInt(1, saleID);
                    ps.setInt(2, sale.getEmployeeID());
                    ps.setString(3, sale.getDeliveryStatus().toLowerCase());
                    ps.setDate(4, java.sql.Date.valueOf(
                            sale.getDeliveryDate() == null ? sale.getSaleDate() : sale.getDeliveryDate()));
                    ps.executeUpdate();
                }
            }

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSale(int saleID) {

        String deleteDetails = "DELETE FROM SaleDetails WHERE SaleID = ?";
        String deletePayment = "DELETE FROM Payment WHERE SaleID = ?";
        String deleteDelivery = "DELETE FROM Delivary WHERE SaleID = ?";
        String deleteSale = "DELETE FROM Sale WHERE SaleID = ?";

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(deleteDetails)) {
                ps.setInt(1, saleID);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(deletePayment)) {
                ps.setInt(1, saleID);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(deleteDelivery)) {
                ps.setInt(1, saleID);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(deleteSale)) {
                ps.setInt(1, saleID);
                ps.executeUpdate();
            }

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSale(Sale sale) {

        String updateSaleSql = """
                UPDATE Sale
                SET CustomerID = ?,
                    EmployeeID = ?,
                    SaleDate = ?,
                    total_Amount = ?
                WHERE SaleID = ?
                """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(updateSaleSql)) {

                ps.setInt(1, sale.getCustomerID());
                ps.setInt(2, sale.getEmployeeID());
                ps.setDate(3, java.sql.Date.valueOf(sale.getSaleDate()));
                ps.setDouble(4, sale.getTotal_Amount());
                ps.setInt(5, sale.getSaleID());

                ps.executeUpdate();
            }

            deleteSaleDetails(conn, sale.getSaleID());

            insertSaleDetails(conn,
                    sale.getSaleID(),
                    sale.getItems());

            updatePayment(conn, sale);

            updateDelivery(conn, sale);

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteSaleDetails(Connection conn, int saleID)
            throws Exception {

        String sql = "DELETE FROM SaleDetails WHERE SaleID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, saleID);
            ps.executeUpdate();
        }
    }

    private void insertSaleDetails(
            Connection conn,
            int saleID,
            List<SaleDetailes> items)
            throws Exception {

        String sql = """
                INSERT INTO SaleDetails
                (SaleID, ProductID, quantity, price)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            for (SaleDetailes item : items) {

                ps.setInt(1, saleID);
                ps.setInt(2, item.getProduct_id());
                ps.setInt(3, item.getQuantity());
                ps.setDouble(4, item.getPrice());

                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private void updatePayment(
            Connection conn,
            Sale sale)
            throws Exception {

        String deleteSql = "DELETE FROM Payment WHERE SaleID = ?";

        try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {

            ps.setInt(1, sale.getSaleID());
            ps.executeUpdate();
        }

        if (sale.getPaidAmount() <= 0) {
            return;
        }

        String insertSql = """
                INSERT INTO Payment
                (SaleID, amount, Payment_Date)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {

            ps.setInt(1, sale.getSaleID());
            ps.setDouble(2, sale.getPaidAmount());

            ps.setDate(
                    3,
                    java.sql.Date.valueOf(
                            sale.getSaleDate()));

            ps.executeUpdate();
        }
    }

    private void updateDelivery(
            Connection conn,
            Sale sale)
            throws Exception {

        String deleteSql = "DELETE FROM Delivary WHERE SaleID = ?";

        try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {

            ps.setInt(1, sale.getSaleID());
            ps.executeUpdate();
        }

        if (sale.getDeliveryStatus() == null) {
            return;
        }

        String insertSql = """
                INSERT INTO Delivary
                (SaleID, EmployeeID, Delivary_status, Delivary_Date)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {

            ps.setInt(1, sale.getSaleID());
            ps.setInt(2, sale.getEmployeeID());

            ps.setString(
                    3,
                    sale.getDeliveryStatus().toLowerCase());

            ps.setDate(
                    4,
                    java.sql.Date.valueOf(
                            sale.getDeliveryDate() == null
                                    ? sale.getSaleDate()
                                    : sale.getDeliveryDate()));

            ps.executeUpdate();
        }
    }

    public int getCustomerIdByName(String customerName) {

        String sql = """
                SELECT CustomerID,
                       CONCAT(firstName,' ',lastName) AS fullName
                FROM Customer
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                if (customerName.equals(
                        rs.getString("fullName"))) {

                    return rs.getInt("CustomerID");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int getEmployeeIdByName(String employeeName) {

        String sql = """
                SELECT EmployeeID,
                       CONCAT(firstName,' ',lastName) AS fullName
                FROM Employee
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                if (employeeName.equals(
                        rs.getString("fullName"))) {

                    return rs.getInt("EmployeeID");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public List<String> getProductNames() {

        List<String> products = new ArrayList<>();

        String sql = """
                SELECT ProductName
                FROM Product
                WHERE ProductStatus='available'
                ORDER BY ProductName
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(rs.getString("ProductName"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }

    public double getProductPrice(String productName) {

        String sql = "SELECT price FROM Product WHERE ProductName=?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, productName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("price");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public double getProductStock(String productName) {

        String sql = """
                SELECT COALESCE(SUM(i.quantity),0) stock
                FROM Inventory i
                JOIN Product p
                ON i.ProductID=p.ProductID
                WHERE p.ProductName=?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, productName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("stock");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getProductIdByName(String productName) {

        String sql = "SELECT ProductID FROM Product WHERE ProductName = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, productName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ProductID");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

}
