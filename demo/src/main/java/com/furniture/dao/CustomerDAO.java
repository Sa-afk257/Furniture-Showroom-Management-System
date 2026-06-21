package com.furniture.dao;

import com.furniture.DBConnection;
import com.furniture.model.Customer;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public List<Customer> getAllCustomersForTable() {

        List<Customer> customers = new ArrayList<>();

        String sql = """
                    SELECT
                        c.CustomerID,
                        c.firstName,
                        c.middelInitial,
                        c.lastName,
                        c.city,
                        c.town,
                        c.area,
                        c.street,
                        c.building,
                        c.RegistrationDate,

                        COALESCE(COUNT(DISTINCT s.SaleID), 0) AS totalOrders,
                        COALESCE(SUM(DISTINCT s.total_Amount), 0) AS totalSpent,
                        COALESCE(pay.totalPaid, 0) AS paidAmount,
                        COALESCE(SUM(DISTINCT s.total_Amount), 0) - COALESCE(pay.totalPaid, 0) AS balance,
                        MAX(s.SaleDate) AS lastPurchaseDate,

                        CASE
                            WHEN COALESCE(SUM(DISTINCT s.total_Amount), 0) >= 5000 THEN 'VIP'
                            WHEN COUNT(DISTINCT s.SaleID) = 0 THEN 'New'
                            ELSE 'Regular'
                        END AS customerType,

                        COALESCE(ret.returnsCount, 0) AS returnsCount

                    FROM Customer c

                    LEFT JOIN Sale s
                        ON c.CustomerID = s.CustomerID

                    LEFT JOIN (
                        SELECT
                            s.CustomerID,
                            SUM(p.amount) AS totalPaid
                        FROM Sale s
                        JOIN Payment p ON s.SaleID = p.SaleID
                        GROUP BY s.CustomerID
                    ) pay ON c.CustomerID = pay.CustomerID

                    LEFT JOIN (
                        SELECT
                            s.CustomerID,
                            COUNT(pr.ProductReturnID) AS returnsCount
                        FROM Sale s
                        JOIN ProductReturn pr ON s.SaleID = pr.SaleID
                        GROUP BY s.CustomerID
                    ) ret ON c.CustomerID = ret.CustomerID

                    GROUP BY c.CustomerID
                    ORDER BY c.CustomerID DESC
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int customerID = rs.getInt("CustomerID");

                Customer customer = new Customer(
                        customerID,
                        rs.getString("firstName"),
                        rs.getString("middelInitial"),
                        rs.getString("lastName"),
                        rs.getString("city"),
                        rs.getString("town"),
                        rs.getString("area"),
                        rs.getString("street"),
                        rs.getString("building"),
                        getCustomerPhones(customerID),
                        toLocalDateTime(rs.getTimestamp("RegistrationDate")),
                        0,
                        rs.getInt("totalOrders"),
                        rs.getDouble("totalSpent"),
                        rs.getDouble("paidAmount"),
                        rs.getDouble("balance"),
                        toLocalDateTime(rs.getTimestamp("lastPurchaseDate")),
                        rs.getString("customerType"));

                customer.setReturnsCount(rs.getInt("returnsCount"));
                customers.add(customer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return customers;
    }

    public List<String> getCustomerCities() {

        List<String> cities = new ArrayList<>();

        String sql = """
                    SELECT DISTINCT city
                    FROM Customer
                    WHERE city IS NOT NULL AND city <> ''
                    ORDER BY city
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cities.add(rs.getString("city"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cities;
    }

    public void insertCustomer(Customer customer) {

        String customerSql = """
                    INSERT INTO Customer
                    (firstName, middelInitial, lastName, city, town, area, street, building, RegistrationDate)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String phoneSql = """
                    INSERT INTO Customer_phone
                    (CustomerID, phone)
                    VALUES (?, ?)
                """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(customerSql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, customer.getFirstName());
                ps.setString(2, customer.getMiddelInitial());
                ps.setString(3, customer.getLastName());
                ps.setString(4, customer.getCity());
                ps.setString(5, customer.getTown());
                ps.setString(6, customer.getArea());
                ps.setString(7, customer.getStreet());
                ps.setString(8, customer.getBuilding());
                ps.setTimestamp(9, Timestamp.valueOf(customer.getRegistrationDate()));

                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();

                if (keys.next()) {
                    int newID = keys.getInt(1);
                    customer.setCustomerID(newID);

                    try (PreparedStatement phonePs = conn.prepareStatement(phoneSql)) {
                        for (String phone : customer.getCustomer_phone()) {
                            phonePs.setInt(1, newID);
                            phonePs.setString(2, phone);
                            phonePs.addBatch();
                        }
                        phonePs.executeBatch();
                    }
                }
            }

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCustomer(Customer customer) {

        String sql = """
                    UPDATE Customer
                    SET firstName = ?,
                        middelInitial = ?,
                        lastName = ?,
                        city = ?,
                        town = ?,
                        area = ?,
                        street = ?,
                        building = ?,
                        RegistrationDate = ?
                    WHERE CustomerID = ?
                """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, customer.getFirstName());
                ps.setString(2, customer.getMiddelInitial());
                ps.setString(3, customer.getLastName());
                ps.setString(4, customer.getCity());
                ps.setString(5, customer.getTown());
                ps.setString(6, customer.getArea());
                ps.setString(7, customer.getStreet());
                ps.setString(8, customer.getBuilding());
                ps.setTimestamp(9, Timestamp.valueOf(customer.getRegistrationDate()));
                ps.setInt(10, customer.getCustomerID());

                ps.executeUpdate();
            }

            deleteCustomerPhones(conn, customer.getCustomerID());
            insertCustomerPhones(conn, customer);

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteCustomer(int customerID) {

        if (customerHasSales(customerID)) {
            return false;
        }

        String deletePhones = """
                    DELETE FROM Customer_phone
                    WHERE CustomerID = ?
                """;

        String deleteCustomer = """
                    DELETE FROM Customer
                    WHERE CustomerID = ?
                """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(deletePhones)) {
                ps.setInt(1, customerID);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(deleteCustomer)) {
                ps.setInt(1, customerID);
                ps.executeUpdate();
            }

            conn.commit();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public CustomerStats getCustomerStats() {

        CustomerStats stats = new CustomerStats();

        String sql = """
                    SELECT
                        customerStats.totalCustomers,
                        customerStats.totalRevenue,
                        customerStats.repeatCustomers,
                        COALESCE(deliveryStats.pendingDeliveries, 0) AS pendingDeliveries,
                        customerStats.outstandingBalance
                    FROM
                    (
                        SELECT
                            COUNT(*) AS totalCustomers,
                            COALESCE(SUM(customerTotals.totalSpent), 0) AS totalRevenue,
                            SUM(CASE WHEN customerTotals.ordersCount > 1 THEN 1 ELSE 0 END) AS repeatCustomers,
                            COALESCE(SUM(customerTotals.totalSpent - customerTotals.totalPaid), 0) AS outstandingBalance
                        FROM
                        (
                            SELECT
                                c.CustomerID,
                                COUNT(DISTINCT s.SaleID) AS ordersCount,
                                COALESCE(SUM(s.total_Amount), 0) AS totalSpent,
                                COALESCE(SUM(p.amount), 0) AS totalPaid
                            FROM Customer c
                            LEFT JOIN Sale s ON c.CustomerID = s.CustomerID
                            LEFT JOIN Payment p ON s.SaleID = p.SaleID
                            GROUP BY c.CustomerID
                        ) customerTotals
                    ) customerStats
                    CROSS JOIN
                    (
                        SELECT
                            COUNT(*) AS pendingDeliveries
                        FROM Delivery
                        WHERE Delivery_status = 'pending'
                    ) deliveryStats
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                stats.setTotalCustomers(rs.getInt("totalCustomers"));
                stats.setTotalRevenue(rs.getDouble("totalRevenue"));
                stats.setRepeatCustomers(rs.getInt("repeatCustomers"));
                stats.setPendingDeliveries(rs.getInt("pendingDeliveries"));
                stats.setOutstandingBalance(rs.getDouble("outstandingBalance"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }

    private List<String> getCustomerPhones(int customerID) {

        List<String> phones = new ArrayList<>();

        String sql = """
                    SELECT phone
                    FROM Customer_phone
                    WHERE CustomerID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    phones.add(rs.getString("phone"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return phones;
    }

    private void deleteCustomerPhones(Connection conn, int customerID) throws SQLException {

        String sql = """
                    DELETE FROM Customer_phone
                    WHERE CustomerID = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerID);
            ps.executeUpdate();
        }
    }

    private void insertCustomerPhones(Connection conn, Customer customer) throws SQLException {

        String sql = """
                    INSERT INTO Customer_phone
                    (CustomerID, phone)
                    VALUES (?, ?)
                """;

        if (customer.getCustomer_phone() == null) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String phone : customer.getCustomer_phone()) {
                ps.setInt(1, customer.getCustomerID());
                ps.setString(2, phone);
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    public static class CustomerStats {

        private int totalCustomers;
        private double totalRevenue;
        private int repeatCustomers;
        private int pendingDeliveries;
        private double outstandingBalance;

        public int getTotalCustomers() {
            return totalCustomers;
        }

        public void setTotalCustomers(int totalCustomers) {
            this.totalCustomers = totalCustomers;
        }

        public double getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(double totalRevenue) {
            this.totalRevenue = totalRevenue;
        }

        public int getRepeatCustomers() {
            return repeatCustomers;
        }

        public void setRepeatCustomers(int repeatCustomers) {
            this.repeatCustomers = repeatCustomers;
        }

        public int getPendingDeliveries() {
            return pendingDeliveries;
        }

        public void setPendingDeliveries(int pendingDeliveries) {
            this.pendingDeliveries = pendingDeliveries;
        }

        public double getOutstandingBalance() {
            return outstandingBalance;
        }

        public void setOutstandingBalance(double outstandingBalance) {
            this.outstandingBalance = outstandingBalance;
        }
    }

    public boolean customerHasSales(int customerID) {

        String sql = """
                    SELECT COUNT(*)
                    FROM Sale
                    WHERE CustomerID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerID);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public Customer getCustomerById(int customerId) {

        String sql = """
                SELECT *
                FROM Customer
                WHERE CustomerID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Customer(
                        rs.getInt("CustomerID"),
                        rs.getString("firstName"),
                        rs.getString("middelInitial"),
                        rs.getString("lastName"),
                        rs.getString("city"),
                        rs.getString("town"),
                        rs.getString("area"),
                        rs.getString("street"),
                        rs.getString("building"),
                        getCustomerPhones(customerId),
                        toLocalDateTime(rs.getTimestamp("RegistrationDate")),
                        0,
                        0,
                        0,
                        0,
                        0,
                        null,
                        "Regular");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}