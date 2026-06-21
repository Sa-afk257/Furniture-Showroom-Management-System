package com.furniture.dao;

import com.furniture.DBConnection;
import com.furniture.model.Account;
import java.sql.*;

public class LoginDAO {

    public Account login(String email, String password) {

        String sql = """
                SELECT
                    a.account_id,
                    a.email,
                    a.role,
                    ca.CustomerID,
                    ea.EmployeeID,
                    c.firstName AS customerFirstName,
                    c.lastName AS customerLastName,
                    e.firstName AS employeeFirstName,
                    e.lastName AS employeeLastName,
                    e.Employee_role
                FROM `Account` a
                LEFT JOIN Customer_Account ca
                    ON a.account_id = ca.account_id
                LEFT JOIN Customer c
                    ON ca.CustomerID = c.CustomerID
                LEFT JOIN Employee_Account ea
                    ON a.account_id = ea.account_id
                LEFT JOIN Employee e
                    ON ea.EmployeeID = e.EmployeeID
                WHERE a.email = ? AND a.password = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String firstName = rs.getString("customerFirstName");
                String lastName = rs.getString("customerLastName");

                if (firstName == null) {
                    firstName = rs.getString("employeeFirstName");
                }

                if (lastName == null) {
                    lastName = rs.getString("employeeLastName");
                }

                Account account = new Account(
                        rs.getInt("account_id"),
                        rs.getString("email"),
                        rs.getString("role"),
                        null,
                        null,
                        firstName,
                        lastName);

                int customerId = rs.getInt("CustomerID");
                if (!rs.wasNull()) {
                    account.setCustomerId(customerId);
                }

                int employeeId = rs.getInt("EmployeeID");
                if (!rs.wasNull()) {
                    account.setEmployeeId(employeeId);
                    account.setEmployeeRole(rs.getString("Employee_role"));
                }

                return account;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean emailExists(String email) {

        String sql = "SELECT account_id FROM Account WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean createGuestCustomerAccount(
            String firstName,
            String lastName,
            String email,
            String phone,
            String password) {

        String insertCustomer = """
                INSERT INTO Customer
                (firstName, middelInitial, lastName, city, town, area, street, building, RegistrationDate)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())
                """;

        String insertPhone = """
                INSERT INTO Customer_phone
                (CustomerID, phone)
                VALUES (?, ?)
                """;

        String insertAccount = """
                INSERT INTO Account
                (email, password, role)
                VALUES (?, ?, 'Customer')
                """;

        String insertCustomerAccount = """
                INSERT INTO Customer_Account
                (account_id, CustomerID)
                VALUES (?, ?)
                """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (
                    PreparedStatement customerStmt = conn.prepareStatement(insertCustomer,
                            Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement phoneStmt = conn.prepareStatement(insertPhone);
                    PreparedStatement accountStmt = conn.prepareStatement(insertAccount,
                            Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement customerAccountStmt = conn.prepareStatement(insertCustomerAccount)) {

                customerStmt.setString(1, firstName);
                customerStmt.setString(2, "-");
                customerStmt.setString(3, lastName);
                customerStmt.setString(4, "Not specified");
                customerStmt.setString(5, "Not specified");
                customerStmt.setString(6, "Not specified");
                customerStmt.setString(7, "Not specified");
                customerStmt.setString(8, "Not specified");

                customerStmt.executeUpdate();

                ResultSet customerKeys = customerStmt.getGeneratedKeys();

                if (!customerKeys.next()) {
                    conn.rollback();
                    return false;
                }

                int customerId = customerKeys.getInt(1);

                phoneStmt.setInt(1, customerId);
                phoneStmt.setString(2, phone);
                phoneStmt.executeUpdate();

                accountStmt.setString(1, email);
                accountStmt.setString(2, password);
                accountStmt.executeUpdate();

                ResultSet accountKeys = accountStmt.getGeneratedKeys();

                if (!accountKeys.next()) {
                    conn.rollback();
                    return false;
                }

                int accountId = accountKeys.getInt(1);

                customerAccountStmt.setInt(1, accountId);
                customerAccountStmt.setInt(2, customerId);
                customerAccountStmt.executeUpdate();

                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}