package com.furniture.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.furniture.DBConnection;
import com.furniture.model.Category;
import com.furniture.model.Inventory;
import com.furniture.model.Product;
import com.furniture.model.StockMovement;
import com.furniture.model.Warehouse;

public class InventoryDAO {

    public List<Inventory> getAllInventoryForTable() {

        List<Inventory> inventoryList = new ArrayList<>();

        String sql = """
                SELECT
                    p.ProductID,
                    p.ProductName,
                    p.price,
                    p.color,
                    p.material,
                    p.ProductDescription,
                    p.imagePath,

                    c.CategoryName,

                    w.WarehouseID,
                    w.WarehouseName,
                    w.capacity,

                    CONCAT(e.firstName, ' ', e.lastName) AS managerName,

                    i.quantity,

                    COALESCE(warehouse_usage.usedCapacity, 0) AS usedCapacity,

                    (w.capacity - COALESCE(warehouse_usage.usedCapacity, 0)) AS remainingCapacity,

                    (i.quantity * p.price) AS stockValue,

                    CASE
                        WHEN i.quantity = 0 THEN 'Out Of Stock'
                        WHEN i.quantity <= 10 THEN 'Low Stock'
                        ELSE 'In Stock'
                    END AS Status

                FROM Inventory i

                JOIN Product p
                    ON i.ProductID = p.ProductID

                JOIN Category c
                    ON p.CategoryID = c.CategoryID

                JOIN Warehouse w
                    ON i.WarehouseID = w.WarehouseID

                JOIN Employee e
                    ON w.EmployeeID = e.EmployeeID

                LEFT JOIN (
                    SELECT
                        WarehouseID,
                        SUM(quantity) AS usedCapacity
                    FROM Inventory
                    GROUP BY WarehouseID
                ) warehouse_usage
                    ON w.WarehouseID = warehouse_usage.WarehouseID

                ORDER BY p.ProductName
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            int no = 1;

            while (rs.next()) {

                Inventory inventory = new Inventory();

                inventory.setNo(no++);

                inventory.setProductID(rs.getInt("ProductID"));
                inventory.setProductName(rs.getString("ProductName"));
                inventory.setCategoryName(rs.getString("CategoryName"));

                inventory.setWarehouseID(rs.getInt("WarehouseID"));
                inventory.setWarehouseName(rs.getString("WarehouseName"));

                inventory.setManagerName(rs.getString("managerName"));

                inventory.setQuantity(rs.getDouble("quantity"));
                inventory.setStatus(rs.getString("Status"));

                inventory.setUnitPrice(rs.getDouble("price"));
                inventory.setStockValue(rs.getDouble("stockValue"));

                inventory.setColor(rs.getString("color"));
                inventory.setMaterial(rs.getString("material"));
                inventory.setDescription(rs.getString("ProductDescription"));
                inventory.setImagePath(rs.getString("imagePath"));

                inventory.setWarehouseCapacity(rs.getDouble("capacity"));
                inventory.setUsedCapacity(rs.getDouble("usedCapacity"));
                inventory.setRemainingCapacity(rs.getDouble("remainingCapacity"));

                inventory.setOldWarehouseID(rs.getInt("WarehouseID"));
                inventory.setOldProductID(rs.getInt("ProductID"));

                inventoryList.add(inventory);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return inventoryList;
    }

    public class InventoryStats {

        private int totalRecords;

        private int totalWarehouses;

        private int lowStock;

        private int outOfStock;

        private double inventoryValue;

        public int getTotalRecords() {
            return totalRecords;
        }

        public void setTotalRecords(int totalRecords) {
            this.totalRecords = totalRecords;
        }

        public int getTotalWarehouses() {
            return totalWarehouses;
        }

        public void setTotalWarehouses(int totalWarehouses) {
            this.totalWarehouses = totalWarehouses;
        }

        public int getLowStock() {
            return lowStock;
        }

        public void setLowStock(int lowStock) {
            this.lowStock = lowStock;
        }

        public int getOutOfStock() {
            return outOfStock;
        }

        public void setOutOfStock(int outOfStock) {
            this.outOfStock = outOfStock;
        }

        public double getInventoryValue() {
            return inventoryValue;
        }

        public void setInventoryValue(double inventoryValue) {
            this.inventoryValue = inventoryValue;
        }

    }

    public InventoryStats getInventoryStats() {

        InventoryStats stats = new InventoryStats();

        try (Connection conn = DBConnection.getConnection()) {

            PreparedStatement ps1 = conn.prepareStatement("""
                    SELECT COUNT(*)
                    FROM Inventory
                    """);

            ResultSet rs1 = ps1.executeQuery();

            if (rs1.next()) {
                stats.setTotalRecords(rs1.getInt(1));
            }

            PreparedStatement ps2 = conn.prepareStatement("""
                    SELECT COUNT(*)
                    FROM Warehouse
                    """);

            ResultSet rs2 = ps2.executeQuery();

            if (rs2.next()) {
                stats.setTotalWarehouses(rs2.getInt(1));
            }

            PreparedStatement ps3 = conn.prepareStatement("""
                    SELECT COUNT(*)
                    FROM Inventory
                    WHERE quantity > 0
                    AND quantity <= 10
                    """);

            ResultSet rs3 = ps3.executeQuery();

            if (rs3.next()) {
                stats.setLowStock(rs3.getInt(1));
            }

            PreparedStatement ps4 = conn.prepareStatement("""
                    SELECT COUNT(*)
                    FROM Inventory
                    WHERE quantity = 0
                    """);

            ResultSet rs4 = ps4.executeQuery();

            if (rs4.next()) {
                stats.setOutOfStock(rs4.getInt(1));
            }

            PreparedStatement ps5 = conn.prepareStatement("""
                    SELECT
                    COALESCE(
                    SUM(i.quantity * p.price),0)
                    FROM Inventory i
                    JOIN Product p
                    ON i.ProductID = p.ProductID
                    """);

            ResultSet rs5 = ps5.executeQuery();

            if (rs5.next()) {
                stats.setInventoryValue(rs5.getDouble(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }

    public List<String> getAllProductNames() {

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

    public List<StockMovement> getProductMovements(int productID) {

        List<StockMovement> movements = new ArrayList<>();

        String sql = """
                SELECT
                    movementType,
                    quantity,
                    movement_date
                FROM StockMovement
                WHERE ProductID = ?
                ORDER BY movement_date DESC, movementID DESC
                LIMIT 5
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productID);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                StockMovement movement = new StockMovement();

                movement.setMovementType(rs.getString("movementType"));
                movement.setQuantity(rs.getDouble("quantity"));

                movement.setmovement_date(
                        rs.getDate("movement_date"));

                movements.add(movement);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movements;
    }

    public boolean insertStockMovement(
            int productID,
            String movementType,
            double quantity,
            LocalDate movement_date) {

        String sql = """
                INSERT INTO StockMovement
                (ProductID, movementType, quantity, movement_date)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productID);
            ps.setString(2, movementType);
            ps.setDouble(3, quantity);
            ps.setDate(
                    4,
                    java.sql.Date.valueOf(
                            movement_date == null ? LocalDate.now() : movement_date));

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<String> getAllManagerNames() {

        List<String> managers = new ArrayList<>();

        String sql = """
                SELECT DISTINCT
                    CONCAT(e.firstName,' ',e.lastName) AS managerName
                FROM Warehouse w
                JOIN Employee e
                ON w.EmployeeID = e.EmployeeID
                ORDER BY managerName
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                managers.add(
                        rs.getString("managerName"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return managers;
    }

    public List<Product> getAllProducts() {

        List<Product> products = new ArrayList<>();

        String sql = """
                        SELECT
                    p.ProductID,
                    p.ProductName,
                    p.price,
                    p.color,
                    p.material,
                    p.ProductDescription,
                    p.imagePath,
                    c.CategoryName
                FROM Product p
                JOIN Category c
                    ON p.CategoryID = c.CategoryID
                ORDER BY p.ProductName
                        """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Product product = new Product();

                product.setProductID(rs.getInt("ProductID"));
                product.setProductName(rs.getString("ProductName"));
                product.setCategoryName(rs.getString("CategoryName"));
                product.setPrice(rs.getDouble("price"));
                product.setColor(rs.getString("color"));
                product.setMaterial(rs.getString("material"));
                product.setDescription(rs.getString("ProductDescription"));
                product.setImagePath(rs.getString("imagePath"));
                products.add(product);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }

    public List<Warehouse> getAllWarehouses() {

        List<Warehouse> warehouses = new ArrayList<>();

        String sql = """
                SELECT
                    w.WarehouseID,
                    w.WarehouseName,
                    w.capacity,
                    CONCAT(e.firstName, ' ', e.lastName) AS managerName
                FROM Warehouse w
                JOIN Employee e
                    ON w.EmployeeID = e.EmployeeID
                ORDER BY w.WarehouseName
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Warehouse warehouse = new Warehouse();

                warehouse.setWarehouseID(rs.getInt("WarehouseID"));

                warehouse.setWarehouseName(rs.getString("WarehouseName"));
                warehouse.setCapacity(rs.getInt("capacity"));
                warehouse.setManagerName(rs.getString("managerName"));

                warehouses.add(warehouse);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return warehouses;
    }

    // do not allow duplicate chooses
    public boolean inventoryExists(int warehouseID, int productID) {

        String sql = """
                SELECT COUNT(*)
                FROM Inventory
                WHERE WarehouseID = ?
                AND ProductID = ?
                """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, warehouseID);
            ps.setInt(2, productID);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Category> getAllCategories() {

        List<Category> categories = new ArrayList<>();

        String sql = """
                SELECT CategoryID,
                       CategoryName
                FROM Category
                ORDER BY CategoryName
                """;

        try (Connection conn = DBConnection.getConnection();

                PreparedStatement ps = conn.prepareStatement(sql);

                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Category category = new Category();

                category.setCategoryID(rs.getInt("CategoryID"));

                category.setCategoryName(rs.getString("CategoryName"));

                categories.add(category);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }

    public boolean insertInventory(int warehouseID, int productID, double quantity) {

        String sql = """
                INSERT INTO Inventory
                (WarehouseID, ProductID, quantity)
                VALUES (?,?,?)
                """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, warehouseID);
            ps.setInt(2, productID);
            ps.setDouble(3, quantity);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateInventory(
            int oldWarehouseID,
            int oldProductID,
            int newWarehouseID,
            int newProductID,
            double quantity) {

        String sql = """
                UPDATE Inventory
                SET WarehouseID = ?,
                    ProductID = ?,
                    quantity = ?
                WHERE WarehouseID = ?
                AND ProductID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newWarehouseID);
            ps.setInt(2, newProductID);
            ps.setDouble(3, quantity);

            ps.setInt(4, oldWarehouseID);
            ps.setInt(5, oldProductID);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteInventory(int warehouseID, int productID) {

        String sql = """
                DELETE FROM Inventory
                WHERE WarehouseID = ?
                AND ProductID = ?
                """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, warehouseID);
            ps.setInt(2, productID);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
