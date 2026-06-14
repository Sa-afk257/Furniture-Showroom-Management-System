package com.furniture.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.furniture.DBConnection;
import com.furniture.model.PurchaseTransaction;
import com.furniture.model.PurchaseDetails;

public class PurchaseDAO {

    public static class PurchaseStats {
        private int totalPurchases;
        private double totalPurchaseCost;
        private double productsPurchased;
        private int activeSuppliers;
        private int lowStockItems;

        public PurchaseStats(int totalPurchases, double totalPurchaseCost,
                double productsPurchased, int activeSuppliers,
                int lowStockItems) {
            this.totalPurchases = totalPurchases;
            this.totalPurchaseCost = totalPurchaseCost;
            this.productsPurchased = productsPurchased;
            this.activeSuppliers = activeSuppliers;
            this.lowStockItems = lowStockItems;
        }

        public int getTotalPurchases() {
            return totalPurchases;
        }

        public double getTotalPurchaseCost() {
            return totalPurchaseCost;
        }

        public double getProductsPurchased() {
            return productsPurchased;
        }

        public int getActiveSuppliers() {
            return activeSuppliers;
        }

        public int getLowStockItems() {
            return lowStockItems;
        }
    }

    public List<String> getSupplierNames() {
        List<String> suppliers = new ArrayList<>();

        String sql = """
                SELECT CONCAT(firstName, ' ', lastName) AS supplierName
                FROM Supplier
                ORDER BY firstName, lastName
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                suppliers.add(rs.getString("supplierName"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return suppliers;
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

    public List<String> getProductNames() {
        List<String> products = new ArrayList<>();

        String sql = """
                SELECT ProductName
                FROM Product
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

    public List<String> getWarehouseNames() {
        List<String> warehouses = new ArrayList<>();

        String sql = """
                SELECT WarehouseName
                FROM Warehouse
                ORDER BY WarehouseName
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                warehouses.add(rs.getString("WarehouseName"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return warehouses;
    }

    public List<String> getCategoryNames() {
        List<String> categories = new ArrayList<>();

        String sql = """
                SELECT CategoryName
                FROM Category
                ORDER BY CategoryName
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("CategoryName"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }

    public List<PurchaseTransaction> getAllPurchasesForTable() {

        List<PurchaseTransaction> purchases = new ArrayList<>();

        String sql = """
                SELECT
                    pt.PurchaseID,
                    pt.SupplierID,
                    pt.EmployeeID,
                    pt.Purchase_Date,
                    pt.total_amount,

                    CONCAT(s.firstName,' ',s.lastName) AS supplierName,
                    s.Supplier_type,
                    s.email,

                    CONCAT(e.firstName,' ',e.lastName) AS employeeName,

                    COUNT(pd.ProductID) AS itemsCount,
                    COALESCE(SUM(pd.quantity),0) AS totalQuantity

                FROM Purchase_Transaction pt

                JOIN Supplier s
                    ON pt.SupplierID = s.SupplierID

                JOIN Employee e
                    ON pt.EmployeeID = e.EmployeeID

                LEFT JOIN Purchase_Details pd
                    ON pt.PurchaseID = pd.PurchaseID

                GROUP BY
                    pt.PurchaseID,
                    pt.SupplierID,
                    pt.EmployeeID,
                    pt.Purchase_Date,
                    pt.total_amount,
                    supplierName,
                    s.Supplier_type,
                    s.email,
                    employeeName

                ORDER BY pt.PurchaseID DESC
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                PurchaseTransaction p = new PurchaseTransaction();

                p.setPurchaseID(
                        rs.getInt("PurchaseID"));

                p.setSupplierID(
                        rs.getInt("SupplierID"));

                p.setEmployeeID(
                        rs.getInt("EmployeeID"));

                if (rs.getDate("Purchase_Date") != null) {
                    p.setPurchaseDate(
                            rs.getDate("Purchase_Date")
                                    .toLocalDate());
                }

                p.setTotalAmount(
                        rs.getDouble("total_amount"));

                p.setSupplierName(
                        rs.getString("supplierName"));

                p.setSupplierType(
                        rs.getString("Supplier_type"));

                p.setSupplierEmail(
                        rs.getString("email"));

                p.setEmployeeName(
                        rs.getString("employeeName"));

                p.setItemsCount(
                        rs.getInt("itemsCount"));

                p.setTotalQuantity(
                        rs.getDouble("totalQuantity"));

                purchases.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return purchases;
    }

    public PurchaseStats getPurchaseStats() {

        int totalPurchases = 0;
        double totalCost = 0;
        double totalProducts = 0;
        int activeSuppliers = 0;
        int lowStockItems = 0;

        try (Connection conn = DBConnection.getConnection()) {

            ResultSet rs1 = conn.createStatement().executeQuery(
                    """
                            SELECT COUNT(*) totalPurchases,
                                   COALESCE(SUM(total_amount),0) totalCost
                            FROM Purchase_Transaction
                            """);

            if (rs1.next()) {
                totalPurchases = rs1.getInt("totalPurchases");
                totalCost = rs1.getDouble("totalCost");
            }

            ResultSet rs2 = conn.createStatement().executeQuery(
                    """
                            SELECT COALESCE(SUM(quantity),0) totalProducts
                            FROM Purchase_Details
                            """);

            if (rs2.next()) {
                totalProducts = rs2.getDouble("totalProducts");
            }

            ResultSet rs3 = conn.createStatement().executeQuery(
                    """
                            SELECT COUNT(DISTINCT SupplierID) suppliers
                            FROM Purchase_Transaction
                            """);

            if (rs3.next()) {
                activeSuppliers = rs3.getInt("suppliers");
            }

            ResultSet rs4 = conn.createStatement().executeQuery(
                    """
                            SELECT COUNT(*)
                            FROM Inventory
                            WHERE quantity < 10
                            """);

            if (rs4.next()) {
                lowStockItems = rs4.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new PurchaseStats(
                totalPurchases,
                totalCost,
                totalProducts,
                activeSuppliers,
                lowStockItems);
    }

    public List<PurchaseDetails> getPurchaseDetails(int purchaseID) {

        List<PurchaseDetails> details = new ArrayList<>();

        String sql = """
                SELECT

                    pd.PurchaseID,
                    pd.ProductID,
                    pd.quantity,
                    pd.price,

                    p.ProductName,
                    c.CategoryName,

                    w.WarehouseID,
                    w.WarehouseName,

                    COALESCE(i.quantity,0) AS currentStock

                FROM Purchase_Details pd

                JOIN Product p
                    ON pd.ProductID = p.ProductID

                JOIN Category c
                    ON p.CategoryID = c.CategoryID

                LEFT JOIN Inventory i
                    ON pd.ProductID = i.ProductID

                LEFT JOIN Warehouse w
                    ON i.WarehouseID = w.WarehouseID

                WHERE pd.PurchaseID = ?

                ORDER BY p.ProductName
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, purchaseID);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                PurchaseDetails d = new PurchaseDetails();

                d.setPurchase_id(
                        rs.getInt("PurchaseID"));

                d.setProduct_id(
                        rs.getInt("ProductID"));

                d.setProductName(
                        rs.getString("ProductName"));

                d.setCategoryName(
                        rs.getString("CategoryName"));

                d.setWarehouseID(
                        rs.getInt("WarehouseID"));

                d.setWarehouseName(
                        rs.getString("WarehouseName"));

                d.setQuantity(
                        rs.getDouble("quantity"));

                d.setPrice(
                        rs.getDouble("price"));

                d.setCurrentStock(
                        rs.getDouble("currentStock"));

                details.add(d);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return details;
    }

    public int getProductIdByName(String productName) {

        String sql = """
                SELECT ProductID
                FROM Product
                WHERE ProductName = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, productName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("ProductID");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int getWarehouseIdByName(String warehouseName) {

        String sql = """
                SELECT WarehouseID
                FROM Warehouse
                WHERE WarehouseName = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, warehouseName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("WarehouseID");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public String getSupplierTypeByName(String supplierName) {

        String sql = """
                SELECT Supplier_type
                FROM Supplier
                WHERE CONCAT(firstName,' ',lastName) = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, supplierName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("Supplier_type");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public String getSupplierEmailByName(String supplierName) {

        String sql = """
                SELECT email
                FROM Supplier
                WHERE CONCAT(firstName,' ',lastName) = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, supplierName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("email");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public String getProductCategoryName(String productName) {

        String sql = """
                SELECT c.CategoryName
                FROM Product p
                JOIN Category c
                    ON p.CategoryID = c.CategoryID
                WHERE p.ProductName = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, productName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("CategoryName");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public double getProductStockInWarehouse(String productName,
            String warehouseName) {

        String sql = """
                SELECT COALESCE(i.quantity,0) AS stock
                FROM Inventory i
                JOIN Product p
                    ON i.ProductID = p.ProductID
                JOIN Warehouse w
                    ON i.WarehouseID = w.WarehouseID
                WHERE p.ProductName = ?
                  AND w.WarehouseName = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, productName);
            ps.setString(2, warehouseName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("stock");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getSupplierIdByName(String supplierName) {

        String sql = """
                SELECT SupplierID
                FROM Supplier
                WHERE CONCAT(firstName,' ',lastName) = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, supplierName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("SupplierID");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int getEmployeeIdByName(String employeeName) {

        String sql = """
                SELECT EmployeeID
                FROM Employee
                WHERE CONCAT(firstName,' ',lastName) = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("EmployeeID");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void deletePurchase(int purchaseID) {

        String sql = "DELETE FROM Purchase_Transaction WHERE PurchaseID = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, purchaseID);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertPurchase(PurchaseTransaction purchase) {

        Connection conn = null;

        try {

            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String purchaseSql = """
                    INSERT INTO Purchase_Transaction
                    (SupplierID, Purchase_Date, EmployeeID, total_amount)
                    VALUES (?,?,?,?)
                    """;

            PreparedStatement purchaseStmt = conn.prepareStatement(
                    purchaseSql,
                    Statement.RETURN_GENERATED_KEYS);

            purchaseStmt.setInt(1, purchase.getSupplierID());
            purchaseStmt.setDate(
                    2,
                    java.sql.Date.valueOf(
                            purchase.getPurchaseDate()));

            purchaseStmt.setInt(3, purchase.getEmployeeID());
            purchaseStmt.setDouble(4, purchase.getTotalAmount());

            purchaseStmt.executeUpdate();

            ResultSet generatedKeys = purchaseStmt.getGeneratedKeys();

            int purchaseID = -1;

            if (generatedKeys.next()) {
                purchaseID = generatedKeys.getInt(1);
            }

            insertPurchaseDetails(
                    conn,
                    purchaseID,
                    purchase.getItems());

            conn.commit();

        } catch (Exception e) {

            try {
                if (conn != null)
                    conn.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();

        } finally {

            try {
                if (conn != null)
                    conn.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void insertPurchaseDetails(
            Connection conn,
            int purchaseID,
            List<PurchaseDetails> items)
            throws Exception {

        String detailsSql = """
                INSERT INTO Purchase_Details
                (PurchaseID, ProductID, quantity, price)
                VALUES (?,?,?,?)
                """;

        String inventorySql = """
                UPDATE Inventory
                SET quantity = quantity + ?
                WHERE ProductID = ?
                AND WarehouseID = ?
                """;

        String movementSql = """
                INSERT INTO StockMovement
                (ProductID,movementType,quantity,movement_date)
                VALUES (?,?,?,CURDATE())
                """;

        for (PurchaseDetails item : items) {

            PreparedStatement detailsStmt = conn.prepareStatement(detailsSql);

            detailsStmt.setInt(1, purchaseID);
            detailsStmt.setInt(2, item.getProduct_id());
            detailsStmt.setDouble(3, item.getQuantity());
            detailsStmt.setDouble(4, item.getPrice());

            detailsStmt.executeUpdate();

            PreparedStatement inventoryStmt = conn.prepareStatement(inventorySql);

            inventoryStmt.setDouble(
                    1,
                    item.getQuantity());

            inventoryStmt.setInt(
                    2,
                    item.getProduct_id());

            inventoryStmt.setInt(
                    3,
                    item.getWarehouseID());

            inventoryStmt.executeUpdate();

            PreparedStatement movementStmt = conn.prepareStatement(movementSql);

            movementStmt.setInt(
                    1,
                    item.getProduct_id());

            movementStmt.setString(
                    2,
                    "PURCHASE");

            movementStmt.setDouble(
                    3,
                    item.getQuantity());

            movementStmt.executeUpdate();
        }
    }

    public void updatePurchase(PurchaseTransaction purchase) {

        String updatePurchaseSql = """
                UPDATE Purchase_Transaction
                SET SupplierID = ?,
                    Purchase_Date = ?,
                    EmployeeID = ?,
                    total_amount = ?
                WHERE PurchaseID = ?
                """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(updatePurchaseSql)) {

                ps.setInt(1, purchase.getSupplierID());
                ps.setDate(2, java.sql.Date.valueOf(purchase.getPurchaseDate()));
                ps.setInt(3, purchase.getEmployeeID());
                ps.setDouble(4, purchase.getTotalAmount());
                ps.setInt(5, purchase.getPurchaseID());

                ps.executeUpdate();
            }

            updatePurchaseDetails(conn, purchase.getPurchaseID(), purchase.getItems());

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePurchaseDetails(
            Connection conn,
            int purchaseID,
            List<PurchaseDetails> items)
            throws Exception {

        String deleteSql = """
                DELETE FROM Purchase_Details
                WHERE PurchaseID = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
            ps.setInt(1, purchaseID);
            ps.executeUpdate();
        }

        String insertSql = """
                INSERT INTO Purchase_Details
                (PurchaseID, ProductID, WarehouseID, quantity, price)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {

            for (PurchaseDetails item : items) {
                ps.setInt(1, purchaseID);
                ps.setInt(2, item.getProduct_id());
                ps.setInt(3, item.getWarehouseID());
                ps.setDouble(4, item.getQuantity());
                ps.setDouble(5, item.getPrice());

                ps.addBatch();
            }

            ps.executeBatch();
        }
    }
}
