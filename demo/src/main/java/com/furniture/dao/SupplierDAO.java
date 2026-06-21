package com.furniture.dao;

import com.furniture.model.Product;
import com.furniture.model.PurchaseDetails;
import com.furniture.model.PurchaseTransaction;
import com.furniture.model.Supplier;
import com.furniture.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    /* ===================== TABLE DATA ===================== */

    public List<Supplier> getAllSuppliersForTable() {

        List<Supplier> suppliers = new ArrayList<>();

        String sql = """
                    SELECT
                        s.SupplierID,
                        s.firstName,
                        s.middelInitial,
                        s.lastName,
                        s.Supplier_type,
                        s.email,
                        s.city,
                        s.town,
                        s.area,
                        s.street,
                        s.building,

                        COALESCE((
                            SELECT sp.phone
                            FROM Supplier_Phone sp
                            WHERE sp.SupplierID = s.SupplierID
                            LIMIT 1
                        ), '-') AS phone,

                        COALESCE(COUNT(DISTINCT ps.ProductID), 0) AS productsCount,
                        COALESCE(COUNT(DISTINCT pt.PurchaseID), 0) AS purchasesCount,
                        COALESCE(SUM(pt.total_amount), 0) AS totalPurchasedAmount,
                        COALESCE((
                            SELECT SUM(pd.quantity)
                            FROM Purchase_Transaction pt2
                            JOIN Purchase_Details pd
                                ON pt2.PurchaseID = pd.PurchaseID
                            WHERE pt2.SupplierID = s.SupplierID
                        ), 0) AS totalPurchasedQuantity,

                        COALESCE(AVG(pt.total_amount), 0) AS averagePurchaseValue,
                        COALESCE(MAX(pt.Purchase_Date), '-') AS lastPurchaseDate

                    FROM Supplier s
                    LEFT JOIN Product_Supplier ps
                        ON s.SupplierID = ps.SupplierID
                    LEFT JOIN Purchase_Transaction pt
                        ON s.SupplierID = pt.SupplierID
                    GROUP BY s.SupplierID
                    ORDER BY s.SupplierID DESC
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Supplier supplier = new Supplier();

                supplier.setSupplierID(rs.getInt("SupplierID"));
                supplier.setFirstName(rs.getString("firstName"));
                supplier.setMiddelInitial(rs.getString("middelInitial"));
                supplier.setLastName(rs.getString("lastName"));

                supplier.setSupplier_type(rs.getString("Supplier_type"));
                supplier.setEmail(rs.getString("email"));

                supplier.setCity(rs.getString("city"));
                supplier.setTown(rs.getString("town"));
                supplier.setArea(rs.getString("area"));
                supplier.setStreet(rs.getString("street"));
                supplier.setBuilding(rs.getString("building"));

                List<String> phones = new ArrayList<>();
                phones.add(rs.getString("phone"));
                supplier.setSupplier_Phone(phones);

                supplier.setProductsCount(rs.getInt("productsCount"));
                supplier.setPurchasesCount(rs.getInt("purchasesCount"));
                supplier.setTotalPurchasedAmount(rs.getDouble("totalPurchasedAmount"));
                supplier.setLastPurchaseDate(rs.getString("lastPurchaseDate"));

                if (supplier.getPurchasesCount() > 0 || supplier.getProductsCount() > 0) {
                    supplier.setStatus("Active");
                } else {
                    supplier.setStatus("New");
                }

                supplier.setTotalPurchasedQuantity(
                        rs.getDouble("totalPurchasedQuantity"));

                supplier.setAveragePurchaseValue(
                        rs.getDouble("averagePurchaseValue"));
                suppliers.add(supplier);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suppliers;
    }
    /* ===================== STATS ===================== */

    public SupplierStats getSupplierStats() {

        SupplierStats stats = new SupplierStats();

        String totalSql = "SELECT COUNT(*) FROM Supplier";

        String localSql = """
                    SELECT COUNT(*)
                    FROM Supplier
                    WHERE Supplier_type = 'Local'
                """;

        String topSupplierSql = """
                    SELECT
                        CONCAT(s.firstName, ' ', s.lastName) AS supplierName,
                        COALESCE(SUM(pt.total_amount), 0) AS totalAmount
                    FROM Supplier s
                    LEFT JOIN Purchase_Transaction pt
                        ON s.SupplierID = pt.SupplierID
                    GROUP BY s.SupplierID
                    ORDER BY totalAmount DESC
                    LIMIT 1
                """;

        String topProductsSql = """
                    SELECT
                        CONCAT(s.firstName, ' ', s.lastName) AS supplierName,
                        COUNT(ps.ProductID) AS productsCount
                    FROM Supplier s
                    LEFT JOIN Product_Supplier ps
                        ON s.SupplierID = ps.SupplierID
                    GROUP BY s.SupplierID
                    ORDER BY productsCount DESC
                    LIMIT 1
                """;

        String suppliedProductsSql = """
                    SELECT COUNT(DISTINCT ProductID)
                    FROM Product_Supplier
                """;

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(totalSql);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    stats.setTotalSuppliers(rs.getInt(1));
            }

            try (PreparedStatement ps = conn.prepareStatement(localSql);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    stats.setLocalSuppliers(rs.getInt(1));
            }

            try (PreparedStatement ps = conn.prepareStatement(topSupplierSql);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setTopSupplierName(rs.getString("supplierName"));
                    stats.setTopSupplierAmount(rs.getDouble("totalAmount"));
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(topProductsSql);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setTopSupplierProductsName(rs.getString("supplierName"));
                    stats.setTopSupplierProductsCount(rs.getInt("productsCount"));
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(suppliedProductsSql);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    stats.setSuppliedProducts(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }

    public static class SupplierStats {

        private int totalSuppliers;
        private int localSuppliers;

        private String topSupplierName = "-";
        private double topSupplierAmount;

        private String topSupplierProductsName = "-";
        private int topSupplierProductsCount;

        private int suppliedProducts;

        public int getTotalSuppliers() {
            return totalSuppliers;
        }

        public void setTotalSuppliers(int totalSuppliers) {
            this.totalSuppliers = totalSuppliers;
        }

        public int getLocalSuppliers() {
            return localSuppliers;
        }

        public void setLocalSuppliers(int localSuppliers) {
            this.localSuppliers = localSuppliers;
        }

        public String getTopSupplierName() {
            return topSupplierName == null ? "-" : topSupplierName;
        }

        public void setTopSupplierName(String topSupplierName) {
            this.topSupplierName = topSupplierName;
        }

        public double getTopSupplierAmount() {
            return topSupplierAmount;
        }

        public void setTopSupplierAmount(double topSupplierAmount) {
            this.topSupplierAmount = topSupplierAmount;
        }

        public String getTopSupplierProductsName() {
            return topSupplierProductsName == null ? "-" : topSupplierProductsName;
        }

        public void setTopSupplierProductsName(String topSupplierProductsName) {
            this.topSupplierProductsName = topSupplierProductsName;
        }

        public int getTopSupplierProductsCount() {
            return topSupplierProductsCount;
        }

        public void setTopSupplierProductsCount(int topSupplierProductsCount) {
            this.topSupplierProductsCount = topSupplierProductsCount;
        }

        public int getSuppliedProducts() {
            return suppliedProducts;
        }

        public void setSuppliedProducts(int suppliedProducts) {
            this.suppliedProducts = suppliedProducts;
        }
    }

    public List<String> getCities() {

        List<String> cities = new ArrayList<>();

        String sql = """
                    SELECT DISTINCT city
                    FROM Supplier
                    ORDER BY city
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cities.add(rs.getString("city"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cities;
    }

    public List<Product> getProductsBySupplier(int supplierId) {

        List<Product> products = new ArrayList<>();

        String sql = """
                    SELECT
                        p.ProductID,
                        p.ProductName,
                        c.CategoryName,
                        p.ProductStatus
                    FROM Product p
                    JOIN Product_Supplier ps
                        ON p.ProductID = ps.ProductID
                    JOIN Category c
                        ON p.CategoryID = c.CategoryID
                    WHERE ps.SupplierID = ?
                    ORDER BY p.ProductName
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, supplierId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Product product = new Product();

                    product.setProductID(
                            rs.getInt("ProductID"));

                    product.setProductName(
                            rs.getString("ProductName"));

                    product.setCategoryName(
                            rs.getString("CategoryName"));

                    product.setStatus(
                            rs.getString("ProductStatus"));

                    products.add(product);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public List<PurchaseTransaction> getPurchasesBySupplier(int supplierId) {

        List<PurchaseTransaction> purchases = new ArrayList<>();

        String sql = """
                    SELECT
                        pt.PurchaseID,
                        pt.Purchase_Date,
                        pt.total_amount,
                        CONCAT(e.firstName,' ',e.lastName) AS employeeName
                    FROM Purchase_Transaction pt
                    JOIN Employee e
                        ON pt.EmployeeID = e.EmployeeID
                    WHERE pt.SupplierID = ?
                    ORDER BY pt.Purchase_Date DESC
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, supplierId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    PurchaseTransaction purchase = new PurchaseTransaction();

                    purchase.setPurchaseID(
                            rs.getInt("PurchaseID"));

                    purchase.setPurchaseDate(
                            rs.getDate("Purchase_Date").toLocalDate());

                    purchase.setTotalAmount(
                            rs.getDouble("total_amount"));

                    purchase.setEmployeeName(
                            rs.getString("employeeName"));

                    purchases.add(purchase);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return purchases;
    }

    public List<PurchaseDetails> getPurchasedProductsSummary(int supplierId) {

        List<PurchaseDetails> list = new ArrayList<>();

        String sql = """
                    SELECT
                        p.ProductName,
                        SUM(pd.quantity) AS totalQty,
                        SUM(pd.quantity * pd.price) AS totalCost
                    FROM Purchase_Details pd
                    JOIN Purchase_Transaction pt
                        ON pd.PurchaseID = pt.PurchaseID
                    JOIN Product p
                        ON pd.ProductID = p.ProductID
                    WHERE pt.SupplierID = ?
                    GROUP BY p.ProductID
                    ORDER BY totalQty DESC
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, supplierId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    PurchaseDetails details = new PurchaseDetails();

                    details.setProductName(
                            rs.getString("ProductName"));

                    details.setQuantity(
                            rs.getDouble("totalQty"));

                    details.setSubtotal(
                            rs.getDouble("totalCost"));

                    list.add(details);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insertSupplier(Supplier supplier) {

        String supplierSql = """
                    INSERT INTO Supplier
                    (
                        firstName,
                        middelInitial,
                        lastName,
                        Supplier_type,
                        email,
                        city,
                        town,
                        area,
                        street,
                        building
                    )
                    VALUES (?,?,?,?,?,?,?,?,?,?)
                """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            int supplierId;

            try (PreparedStatement ps = conn.prepareStatement(
                    supplierSql,
                    Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, supplier.getFirstName());
                ps.setString(2, supplier.getMiddelInitial());
                ps.setString(3, supplier.getLastName());
                ps.setString(4, supplier.getSupplier_type());
                ps.setString(5, supplier.getEmail());
                ps.setString(6, supplier.getCity());
                ps.setString(7, supplier.getTown());
                ps.setString(8, supplier.getArea());
                ps.setString(9, supplier.getStreet());
                ps.setString(10, supplier.getBuilding());

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                rs.next();

                supplierId = rs.getInt(1);
            }

            if (supplier.getSupplier_Phone() != null) {

                String phoneSql = """
                            INSERT INTO Supplier_Phone
                            (SupplierID, phone)
                            VALUES (?,?)
                        """;

                try (PreparedStatement ps = conn.prepareStatement(phoneSql)) {

                    for (String phone : supplier.getSupplier_Phone()) {

                        ps.setInt(1, supplierId);
                        ps.setString(2, phone);
                        ps.addBatch();
                    }

                    ps.executeBatch();
                }
            }

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSupplier(Supplier supplier) {

        String sql = """
                    UPDATE Supplier
                    SET
                        firstName=?,
                        middelInitial=?,
                        lastName=?,
                        Supplier_type=?,
                        email=?,
                        city=?,
                        town=?,
                        area=?,
                        street=?,
                        building=?
                    WHERE SupplierID=?
                """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, supplier.getFirstName());
                ps.setString(2, supplier.getMiddelInitial());
                ps.setString(3, supplier.getLastName());

                ps.setString(4, supplier.getSupplier_type());

                ps.setString(5, supplier.getEmail());

                ps.setString(6, supplier.getCity());
                ps.setString(7, supplier.getTown());
                ps.setString(8, supplier.getArea());
                ps.setString(9, supplier.getStreet());
                ps.setString(10, supplier.getBuilding());

                ps.setInt(11, supplier.getSupplierID());

                ps.executeUpdate();
            }

            String deletePhones = """
                        DELETE FROM Supplier_Phone
                        WHERE SupplierID=?
                    """;

            try (PreparedStatement ps = conn.prepareStatement(deletePhones)) {

                ps.setInt(1, supplier.getSupplierID());
                ps.executeUpdate();
            }

            String insertPhone = """
                        INSERT INTO Supplier_Phone
                        (SupplierID, phone)
                        VALUES (?,?)
                    """;

            try (PreparedStatement ps = conn.prepareStatement(insertPhone)) {

                if (supplier.getSupplier_Phone() != null) {

                    for (String phone : supplier.getSupplier_Phone()) {

                        ps.setInt(1, supplier.getSupplierID());
                        ps.setString(2, phone);

                        ps.addBatch();
                    }

                    ps.executeBatch();
                }
            }

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSupplier(int supplierId) {

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM Supplier_Phone WHERE SupplierID=?")) {

                ps.setInt(1, supplierId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM Product_Supplier WHERE SupplierID=?")) {

                ps.setInt(1, supplierId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM Supplier WHERE SupplierID=?")) {

                ps.setInt(1, supplierId);
                ps.executeUpdate();
            }

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}