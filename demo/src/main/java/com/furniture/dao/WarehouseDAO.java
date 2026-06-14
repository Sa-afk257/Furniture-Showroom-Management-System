package com.furniture.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.furniture.DBConnection;
import com.furniture.model.Warehouse;
import com.furniture.model.Product;

public class WarehouseDAO {
    public List<Warehouse> getAllWarehousesForTable() {
        List<Warehouse> list = new ArrayList<>();

        String sql = """
                    SELECT
                        w.WarehouseID,
                        w.WarehouseName,
                        w.city,
                        w.town,
                        w.area,
                        w.street,
                        w.building,
                        w.capacity,
                        w.EmployeeID,

                        CONCAT(e.firstName, ' ', e.middelInitial, ' ', e.lastName) AS managerName,

                        COALESCE(SUM(i.quantity), 0) AS usedCapacity,
                        (w.capacity - COALESCE(SUM(i.quantity), 0)) AS remainingCapacity,

                        CASE
                            WHEN w.capacity = 0 THEN 0
                            ELSE ROUND((COALESCE(SUM(i.quantity), 0) / w.capacity) * 100, 1)
                        END AS usedPercent,

                        COUNT(DISTINCT i.ProductID) AS productsCount,

                        CASE
                            WHEN COALESCE(SUM(i.quantity), 0) = 0 THEN 'Empty'
                            WHEN COALESCE(SUM(i.quantity), 0) >= w.capacity THEN 'Full'
                            WHEN (COALESCE(SUM(i.quantity), 0) / w.capacity) >= 0.80 THEN 'Almost Full'
                            ELSE 'Available'
                        END AS warehouseStatus

                    FROM Warehouse w
                    JOIN Employee e ON w.EmployeeID = e.EmployeeID
                    LEFT JOIN Inventory i ON w.WarehouseID = i.WarehouseID

                    GROUP BY
                        w.WarehouseID, w.WarehouseName, w.city, w.town, w.area,
                        w.street, w.building, w.capacity, w.EmployeeID,
                        e.firstName, e.middelInitial, e.lastName

                    ORDER BY w.WarehouseID DESC
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Warehouse w = new Warehouse();

                w.setWarehouseID(rs.getInt("WarehouseID"));
                w.setWarehouseName(rs.getString("WarehouseName"));
                w.setCity(rs.getString("city"));
                w.setTown(rs.getString("town"));
                w.setArea(rs.getString("area"));
                w.setStreet(rs.getString("street"));
                w.setBuilding(rs.getString("building"));
                w.setCapacity(rs.getInt("capacity"));
                w.setEmployeeID(rs.getInt("EmployeeID"));

                w.setManagerName(rs.getString("managerName"));
                w.setUsedCapacity(rs.getDouble("usedCapacity"));
                w.setRemainingCapacity(rs.getDouble("remainingCapacity"));
                w.setUsedPercent(rs.getDouble("usedPercent"));
                w.setProductsCount(rs.getInt("productsCount"));
                w.setWarehouseStatus(rs.getString("warehouseStatus"));

                list.add(w);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static class WarehouseStats {

        private int totalWarehouses;
        private double totalCapacity;
        private double usedCapacity;
        private double availableCapacity;
        private int fullWarehouses;

        public int getTotalWarehouses() {
            return totalWarehouses;
        }

        public void setTotalWarehouses(int totalWarehouses) {
            this.totalWarehouses = totalWarehouses;
        }

        public double getTotalCapacity() {
            return totalCapacity;
        }

        public void setTotalCapacity(double totalCapacity) {
            this.totalCapacity = totalCapacity;
        }

        public double getUsedCapacity() {
            return usedCapacity;
        }

        public void setUsedCapacity(double usedCapacity) {
            this.usedCapacity = usedCapacity;
        }

        public double getAvailableCapacity() {
            return availableCapacity;
        }

        public void setAvailableCapacity(double availableCapacity) {
            this.availableCapacity = availableCapacity;
        }

        public int getFullWarehouses() {
            return fullWarehouses;
        }

        public void setFullWarehouses(int fullWarehouses) {
            this.fullWarehouses = fullWarehouses;
        }
    }

    public WarehouseStats getWarehouseStats() {

        WarehouseStats stats = new WarehouseStats();

        String sql = """
                    SELECT

                        COUNT(*) AS totalWarehouses,

                        COALESCE(SUM(capacity),0) AS totalCapacity,

                        COALESCE((
                            SELECT SUM(quantity)
                            FROM Inventory
                        ),0) AS usedCapacity

                    FROM Warehouse
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                stats.setTotalWarehouses(
                        rs.getInt("totalWarehouses"));

                stats.setTotalCapacity(
                        rs.getDouble("totalCapacity"));

                stats.setUsedCapacity(
                        rs.getDouble("usedCapacity"));

                stats.setAvailableCapacity(
                        stats.getTotalCapacity()
                                - stats.getUsedCapacity());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String fullSql = """
                    SELECT COUNT(*) AS fullWarehouses
                    FROM (
                        SELECT
                            w.WarehouseID,
                            w.capacity,
                            COALESCE(SUM(i.quantity), 0) AS usedCapacity
                        FROM Warehouse w
                        LEFT JOIN Inventory i
                               ON w.WarehouseID = i.WarehouseID
                        GROUP BY w.WarehouseID, w.capacity
                    ) x
                    WHERE x.usedCapacity >= x.capacity
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(fullSql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                stats.setFullWarehouses(rs.getInt("fullWarehouses"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    public List<String> getWarehouseNames() {

        List<String> list = new ArrayList<>();

        String sql = """
                    SELECT WarehouseName
                    FROM Warehouse
                    ORDER BY WarehouseName
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("WarehouseName"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getWarehouseManagerNames() {

        List<String> list = new ArrayList<>();

        String sql = """
                    SELECT CONCAT(firstName,' ',middelInitial,' ',lastName) AS fullName
                    FROM Employee
                    WHERE Employee_role = 'warehouse_manager'
                    ORDER BY firstName
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("fullName"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getWarehouseCities() {

        List<String> list = new ArrayList<>();

        String sql = """
                    SELECT DISTINCT city
                    FROM Warehouse
                    ORDER BY city
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("city"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int getEmployeeIdByName(String employeeName) {

        String sql = """
                    SELECT EmployeeID
                    FROM Employee
                    WHERE CONCAT(firstName,' ',middelInitial,' ',lastName) = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, employeeName);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("EmployeeID");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean insertWarehouse(Warehouse warehouse) {

        String sql = """
                    INSERT INTO Warehouse
                    (
                        WarehouseName,
                        city,
                        town,
                        area,
                        street,
                        building,
                        capacity,
                        EmployeeID
                    )
                    VALUES (?,?,?,?,?,?,?,?)
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, warehouse.getWarehouseName());
            ps.setString(2, warehouse.getCity());
            ps.setString(3, warehouse.getTown());
            ps.setString(4, warehouse.getArea());
            ps.setString(5, warehouse.getStreet());
            ps.setString(6, warehouse.getBuilding());
            ps.setInt(7, warehouse.getCapacity());
            ps.setInt(8, warehouse.getEmployeeID());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateWarehouse(Warehouse warehouse) {

        String sql = """
                    UPDATE Warehouse
                    SET
                        WarehouseName = ?,
                        city = ?,
                        town = ?,
                        area = ?,
                        street = ?,
                        building = ?,
                        capacity = ?,
                        EmployeeID = ?
                    WHERE WarehouseID = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, warehouse.getWarehouseName());
            ps.setString(2, warehouse.getCity());
            ps.setString(3, warehouse.getTown());
            ps.setString(4, warehouse.getArea());
            ps.setString(5, warehouse.getStreet());
            ps.setString(6, warehouse.getBuilding());
            ps.setInt(7, warehouse.getCapacity());
            ps.setInt(8, warehouse.getEmployeeID());

            ps.setInt(9, warehouse.getWarehouseID());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteWarehouse(int warehouseID) {

        String checkSql = """
                    SELECT COUNT(*)
                    FROM Inventory
                    WHERE WarehouseID = ?
                """;

        String deleteSql = """
                    DELETE FROM Warehouse
                    WHERE WarehouseID = ?
                """;

        try (Connection con = DBConnection.getConnection()) {

            try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {

                checkPs.setInt(1, warehouseID);

                try (ResultSet rs = checkPs.executeQuery()) {

                    if (rs.next() && rs.getInt(1) > 0) {
                        return false;
                    }
                }
            }

            try (PreparedStatement deletePs = con.prepareStatement(deleteSql)) {

                deletePs.setInt(1, warehouseID);

                return deletePs.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Product> getProductsInsideWarehouse(int warehouseID) {

        List<Product> list = new ArrayList<>();

        String sql = """
                    SELECT
                        p.ProductID,
                        p.ProductName,
                        c.CategoryName,
                        i.quantity AS stock,
                        p.price,
                        p.ProductStatus
                    FROM Inventory i
                    JOIN Product p ON i.ProductID = p.ProductID
                    JOIN Category c ON p.CategoryID = c.CategoryID
                    WHERE i.WarehouseID = ?
                    ORDER BY p.ProductName
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, warehouseID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Product p = new Product();

                    p.setProductID(rs.getInt("ProductID"));
                    p.setProductName(rs.getString("ProductName"));
                    p.setCategoryName(rs.getString("CategoryName"));
                    p.setStock(rs.getDouble("stock"));
                    p.setPrice(rs.getDouble("price"));
                    p.setStatus(rs.getString("ProductStatus"));

                    list.add(p);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
