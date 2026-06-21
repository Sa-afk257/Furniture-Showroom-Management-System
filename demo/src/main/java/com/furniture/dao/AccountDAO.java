package com.furniture.dao;

import com.furniture.DBConnection;
import com.furniture.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public Customer getCustomerById(int customerId) {

        String sql = """
                    SELECT
                        CustomerID,
                        firstName,
                        middelInitial,
                        lastName,
                        city,
                        town,
                        area,
                        street,
                        building,
                        RegistrationDate
                    FROM Customer
                    WHERE CustomerID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Customer customer = new Customer();

                customer.setCustomerID(rs.getInt("CustomerID"));
                customer.setFirstName(rs.getString("firstName"));
                customer.setMiddelInitial(rs.getString("middelInitial"));
                customer.setLastName(rs.getString("lastName"));
                customer.setCity(rs.getString("city"));
                customer.setTown(rs.getString("town"));
                customer.setArea(rs.getString("area"));
                customer.setStreet(rs.getString("street"));
                customer.setBuilding(rs.getString("building"));

                Timestamp regDate = rs.getTimestamp("RegistrationDate");
                if (regDate != null) {
                    customer.setRegistrationDate(regDate.toLocalDateTime());
                }

                customer.setCustomer_phone(getCustomerPhones(customerId));

                return customer;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<String> getCustomerPhones(int customerId) {

        List<String> phones = new ArrayList<>();

        String sql = """
                    SELECT phone
                    FROM Customer_phone
                    WHERE CustomerID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                phones.add(rs.getString("phone"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return phones;
    }

    public int getTotalOrders(int customerId) {

        String sql = """
                    SELECT COUNT(*)
                    FROM Sale
                    WHERE CustomerID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getWishlistCount(int customerId) {

        String sql = """
                    SELECT COUNT(*)
                    FROM Favorite
                    WHERE CustomerID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean updatePersonalInfo(int customerId,
            String firstName,
            String lastName,
            String phone) {

        String updateCustomerSql = """
                    UPDATE Customer
                    SET firstName = ?, lastName = ?
                    WHERE CustomerID = ?
                """;

        String deletePhoneSql = """
                    DELETE FROM Customer_phone
                    WHERE CustomerID = ?
                """;

        String insertPhoneSql = """
                    INSERT INTO Customer_phone (CustomerID, phone)
                    VALUES (?, ?)
                """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(updateCustomerSql);
                    PreparedStatement ps2 = conn.prepareStatement(deletePhoneSql);
                    PreparedStatement ps3 = conn.prepareStatement(insertPhoneSql)) {

                ps1.setString(1, firstName);
                ps1.setString(2, lastName);
                ps1.setInt(3, customerId);
                ps1.executeUpdate();

                ps2.setInt(1, customerId);
                ps2.executeUpdate();

                if (phone != null && !phone.trim().isEmpty()) {
                    ps3.setInt(1, customerId);
                    ps3.setString(2, phone.trim());
                    ps3.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateAddressInfo(int customerId,
            String city,
            String area,
            String building) {

        String sql = """
                    UPDATE Customer
                    SET city = ?, area = ?, building = ?
                    WHERE CustomerID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, city);
            ps.setString(2, area);
            ps.setString(3, building);
            ps.setInt(4, customerId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public EmployeeAccount getEmployeeAccountById(int employeeId) {

        String sql = """
                SELECT firstName, lastName, city, email, Employee_role, HireDate
                FROM Employee
                WHERE EmployeeID = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new EmployeeAccount(
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("city"),
                            rs.getString("email"),
                            rs.getString("Employee_role"),
                            rs.getString("HireDate"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static class EmployeeAccount {
        public String firstName;
        public String lastName;
        public String city;
        public String email;
        public String role;
        public String hireDate;

        public EmployeeAccount(String firstName, String lastName, String city,
                String email, String role, String hireDate) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.city = city;
            this.email = email;
            this.role = role;
            this.hireDate = hireDate;
        }
    }
}