package com.furniture.dao;

import com.furniture.model.Employee;
import com.furniture.model.Sale;
import com.furniture.model.PurchaseTransaction;
import com.furniture.model.Warehouse;
import com.furniture.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public List<Employee> getAllEmployeesForTable() {
        List<Employee> list = new ArrayList<>();

        String sql = """
                    SELECT
                        e.EmployeeID,
                        e.firstName,
                        e.middelInitial,
                        e.lastName,
                        e.city,
                        e.email,
                        e.salary,
                        e.ShiftTime,
                        e.HireDate,
                        e.gender,
                        e.Employee_role,
                        COALESCE(GROUP_CONCAT(DISTINCT ep.phone SEPARATOR ','), '') AS phones,

                        COUNT(DISTINCT s.SaleID) AS salesCount,
                        COUNT(DISTINCT pt.PurchaseID) AS purchasesCount,
                        COUNT(DISTINCT d.DeliveryID) AS deliveriesCount,
                        COUNT(DISTINCT w.WarehouseID) AS managedWarehouses,

                        COALESCE(SUM(DISTINCT s.total_Amount), 0) AS totalSalesAmount,
                        COALESCE(SUM(DISTINCT pt.total_amount), 0) AS totalPurchaseAmount

                    FROM Employee e
                    LEFT JOIN Employee_Phone ep ON e.EmployeeID = ep.EmployeeID
                    LEFT JOIN Sale s ON e.EmployeeID = s.EmployeeID
                    LEFT JOIN Purchase_Transaction pt ON e.EmployeeID = pt.EmployeeID
                    LEFT JOIN Delivery d ON e.EmployeeID = d.EmployeeID
                    LEFT JOIN Warehouse w ON e.EmployeeID = w.EmployeeID

                    GROUP BY
                        e.EmployeeID,
                        e.firstName,
                        e.middelInitial,
                        e.lastName,
                        e.city,
                        e.email,
                        e.salary,
                        e.ShiftTime,
                        e.HireDate,
                        e.gender,
                        e.Employee_role

                    ORDER BY e.EmployeeID
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Employee e = new Employee();

                e.setEmployeeID(rs.getInt("EmployeeID"));
                e.setFirstName(rs.getString("firstName"));
                e.setMiddelInitial(rs.getString("middelInitial"));
                e.setLastName(rs.getString("lastName"));
                e.setCity(rs.getString("city"));
                e.setEmail(rs.getString("email"));
                e.setSalary(rs.getDouble("salary"));
                e.setShiftTime(rs.getString("ShiftTime"));
                e.setHireDate(rs.getDate("HireDate"));
                e.setGender(rs.getString("gender"));
                e.setEmployee_role(rs.getString("Employee_role"));
                e.setStatus("Active");

                List<String> phones = new ArrayList<>();
                String phonesText = rs.getString("phones");

                if (phonesText != null && !phonesText.isBlank()) {
                    for (String phone : phonesText.split(",")) {
                        phones.add(phone.trim());
                    }
                }

                e.setEmployee_Phone(phones);

                e.setSalesCount(rs.getInt("salesCount"));
                e.setPurchasesCount(rs.getInt("purchasesCount"));
                e.setDeliveriesCount(rs.getInt("deliveriesCount"));
                e.setManagedWarehouses(rs.getInt("managedWarehouses"));
                e.setTotalSalesAmount(rs.getDouble("totalSalesAmount"));
                e.setTotalPurchaseAmount(rs.getDouble("totalPurchaseAmount"));

                list.add(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getRoles() {
        List<String> roles = new ArrayList<>();

        String sql = """
                    SELECT DISTINCT Employee_role
                    FROM Employee
                    ORDER BY Employee_role
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                roles.add(rs.getString("Employee_role"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return roles;
    }

    public List<String> getCities() {
        List<String> cities = new ArrayList<>();

        String sql = """
                    SELECT DISTINCT city
                    FROM Employee
                    ORDER BY city
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cities.add(rs.getString("city"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cities;
    }

    public void insertEmployee(Employee e) {
        String sql = """
                    INSERT INTO Employee
                    (firstName, middelInitial, lastName, city, email, salary, ShiftTime, HireDate, gender, Employee_role)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, e.getFirstName());
            ps.setString(2, e.getMiddelInitial());
            ps.setString(3, e.getLastName());
            ps.setString(4, e.getCity());
            ps.setString(5, e.getEmail());
            ps.setDouble(6, e.getSalary());
            ps.setString(7, e.getShiftTime());
            ps.setDate(8, e.getHireDate());
            ps.setString(9, e.getGender());
            ps.setString(10, e.getEmployee_role());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int newEmployeeID = keys.getInt(1);
                    e.setEmployeeID(newEmployeeID);
                    insertEmployeePhones(con, newEmployeeID, e.getEmployee_Phone());
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateEmployee(Employee e) {
        String sql = """
                    UPDATE Employee
                    SET firstName = ?,
                        middelInitial = ?,
                        lastName = ?,
                        city = ?,
                        email = ?,
                        salary = ?,
                        ShiftTime = ?,
                        HireDate = ?,
                        gender = ?,
                        Employee_role = ?
                    WHERE EmployeeID = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, e.getFirstName());
            ps.setString(2, e.getMiddelInitial());
            ps.setString(3, e.getLastName());
            ps.setString(4, e.getCity());
            ps.setString(5, e.getEmail());
            ps.setDouble(6, e.getSalary());
            ps.setString(7, e.getShiftTime());
            ps.setDate(8, e.getHireDate());
            ps.setString(9, e.getGender());
            ps.setString(10, e.getEmployee_role());
            ps.setInt(11, e.getEmployeeID());

            ps.executeUpdate();

            deleteEmployeePhones(con, e.getEmployeeID());
            insertEmployeePhones(con, e.getEmployeeID(), e.getEmployee_Phone());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteEmployee(int employeeID) {
        try (Connection con = DBConnection.getConnection()) {

            deleteEmployeePhones(con, employeeID);

            String sql = "DELETE FROM Employee WHERE EmployeeID = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, employeeID);
                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertEmployeePhones(Connection con, int employeeID, List<String> phones) throws SQLException {
        if (phones == null || phones.isEmpty()) {
            return;
        }

        String sql = """
                    INSERT INTO Employee_Phone(EmployeeID, phone)
                    VALUES (?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (String phone : phones) {
                if (phone != null && !phone.isBlank()) {
                    ps.setInt(1, employeeID);
                    ps.setString(2, phone.trim());
                    ps.addBatch();
                }
            }

            ps.executeBatch();
        }
    }

    private void deleteEmployeePhones(Connection con, int employeeID) throws SQLException {
        String sql = "DELETE FROM Employee_Phone WHERE EmployeeID = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeID);
            ps.executeUpdate();
        }
    }

    public EmployeeStats getEmployeeStats() {
        EmployeeStats stats = new EmployeeStats();

        String totalSql = "SELECT COUNT(*) AS totalEmployees FROM Employee";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(totalSql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                stats.setTotalEmployees(rs.getInt("totalEmployees"));
                stats.setActiveEmployees(rs.getInt("totalEmployees"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        loadTopSalesEmployee(stats);
        loadTopPurchaseEmployee(stats);
        loadTopDeliveryEmployee(stats);

        return stats;
    }

    private void loadTopSalesEmployee(EmployeeStats stats) {
        String sql = """
                    SELECT
                        CONCAT(e.firstName, ' ', e.lastName) AS fullName,
                        COUNT(s.SaleID) AS salesCount
                    FROM Employee e
                    JOIN Sale s ON e.EmployeeID = s.EmployeeID
                    GROUP BY e.EmployeeID
                    ORDER BY salesCount DESC
                    LIMIT 1
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                stats.setTopSalesEmployeeName(rs.getString("fullName"));
                stats.setTopSalesCount(rs.getInt("salesCount"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTopPurchaseEmployee(EmployeeStats stats) {
        String sql = """
                    SELECT
                        CONCAT(e.firstName, ' ', e.lastName) AS fullName,
                        COUNT(pt.PurchaseID) AS purchaseCount
                    FROM Employee e
                    JOIN Purchase_Transaction pt ON e.EmployeeID = pt.EmployeeID
                    GROUP BY e.EmployeeID
                    ORDER BY purchaseCount DESC
                    LIMIT 1
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                stats.setTopPurchaseEmployeeName(rs.getString("fullName"));
                stats.setTopPurchaseCount(rs.getInt("purchaseCount"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTopDeliveryEmployee(EmployeeStats stats) {
        String sql = """
                    SELECT
                        CONCAT(e.firstName, ' ', e.lastName) AS fullName,
                        COUNT(d.DeliveryID) AS deliveryCount
                    FROM Employee e
                    JOIN Delivery d ON e.EmployeeID = d.EmployeeID
                    GROUP BY e.EmployeeID
                    ORDER BY deliveryCount DESC
                    LIMIT 1
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                stats.setTopDeliveryEmployeeName(rs.getString("fullName"));
                stats.setTopDeliveryCount(rs.getInt("deliveryCount"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Sale> getRecentSalesByEmployee(int employeeID) {
        List<Sale> list = new ArrayList<>();

        String sql = """
                    SELECT
                        s.SaleID,
                        s.CustomerID,
                        s.EmployeeID,
                        s.SaleDate,
                        s.total_Amount
                    FROM Sale s
                    WHERE s.EmployeeID = ?
                    ORDER BY s.SaleDate DESC
                    LIMIT 5
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sale sale = new Sale();

                    sale.setSaleID(rs.getInt("SaleID"));
                    sale.setCustomerID(rs.getInt("CustomerID"));
                    sale.setEmployeeID(rs.getInt("EmployeeID"));
                    sale.setSaleDate(rs.getDate("SaleDate").toLocalDate());
                    sale.setTotal_Amount(rs.getDouble("total_Amount"));

                    list.add(sale);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<PurchaseTransaction> getRecentPurchasesByEmployee(int employeeID) {
        List<PurchaseTransaction> list = new ArrayList<>();

        String sql = """
                    SELECT
                        PurchaseID,
                        SupplierID,
                        EmployeeID,
                        Purchase_Date,
                        total_amount
                    FROM Purchase_Transaction
                    WHERE EmployeeID = ?
                    ORDER BY Purchase_Date DESC
                    LIMIT 5
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PurchaseTransaction p = new PurchaseTransaction();

                    p.setPurchaseID(rs.getInt("PurchaseID"));
                    p.setSupplierID(rs.getInt("SupplierID"));
                    p.setEmployeeID(rs.getInt("EmployeeID"));
                    p.setPurchaseDate(rs.getDate("Purchase_Date").toLocalDate());
                    p.setTotalAmount(rs.getDouble("total_amount"));

                    list.add(p);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Warehouse> getWarehousesManagedByEmployee(int employeeID) {
        List<Warehouse> list = new ArrayList<>();

        String sql = """
                    SELECT
                        WarehouseID,
                        WarehouseName,
                        city,
                        town,
                        area,
                        street,
                        building,
                        capacity,
                        EmployeeID
                    FROM Warehouse
                    WHERE EmployeeID = ?
                    ORDER BY WarehouseID
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeID);

            try (ResultSet rs = ps.executeQuery()) {
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

                    list.add(w);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static class EmployeeStats {
        private int totalEmployees;
        private int activeEmployees;

        private String topSalesEmployeeName = "-";
        private int topSalesCount;

        private String topPurchaseEmployeeName = "-";
        private int topPurchaseCount;

        private String topDeliveryEmployeeName = "-";
        private int topDeliveryCount;

        public int getTotalEmployees() {
            return totalEmployees;
        }

        public void setTotalEmployees(int totalEmployees) {
            this.totalEmployees = totalEmployees;
        }

        public int getActiveEmployees() {
            return activeEmployees;
        }

        public void setActiveEmployees(int activeEmployees) {
            this.activeEmployees = activeEmployees;
        }

        public String getTopSalesEmployeeName() {
            return topSalesEmployeeName;
        }

        public void setTopSalesEmployeeName(String topSalesEmployeeName) {
            this.topSalesEmployeeName = topSalesEmployeeName;
        }

        public int getTopSalesCount() {
            return topSalesCount;
        }

        public void setTopSalesCount(int topSalesCount) {
            this.topSalesCount = topSalesCount;
        }

        public String getTopPurchaseEmployeeName() {
            return topPurchaseEmployeeName;
        }

        public void setTopPurchaseEmployeeName(String topPurchaseEmployeeName) {
            this.topPurchaseEmployeeName = topPurchaseEmployeeName;
        }

        public int getTopPurchaseCount() {
            return topPurchaseCount;
        }

        public void setTopPurchaseCount(int topPurchaseCount) {
            this.topPurchaseCount = topPurchaseCount;
        }

        public String getTopDeliveryEmployeeName() {
            return topDeliveryEmployeeName;
        }

        public void setTopDeliveryEmployeeName(String topDeliveryEmployeeName) {
            this.topDeliveryEmployeeName = topDeliveryEmployeeName;
        }

        public int getTopDeliveryCount() {
            return topDeliveryCount;
        }

        public void setTopDeliveryCount(int topDeliveryCount) {
            this.topDeliveryCount = topDeliveryCount;
        }
    }
}