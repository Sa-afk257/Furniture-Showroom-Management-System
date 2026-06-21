package com.furniture.dao;

import com.furniture.DBConnection;
import com.furniture.model.CartItem;
import com.furniture.model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public List<OrderItem> getOrdersByCustomerId(int customerId) {

        List<OrderItem> orders = new ArrayList<>();

        String sql = """
                SELECT
                    s.SaleID,
                    s.SaleDate,
                    s.total_Amount,

                    CASE

                        WHEN s.SaleStatus = 'rejected'
                            THEN 'cancelled'

                        WHEN d.Delivery_status = 'delivered'
                            THEN 'delivered'

                        WHEN d.Delivery_status = 'cancelled'
                            THEN 'cancelled'

                        WHEN s.SaleStatus = 'approved'
                            THEN 'approved'

                        WHEN d.Delivery_status IN ('assigned','picked_up')
                            THEN 'approved'

                        ELSE 'pending'

                    END AS deliveryStatus,

                    COUNT(DISTINCT sd.ProductID) AS itemsCount,

                    GROUP_CONCAT(
                        CONCAT(p.ProductName, ' x', sd.quantity)
                        SEPARATOR ', '
                    ) AS firstProductName,

                    (
                        SELECT p2.imagePath
                        FROM SaleDetails sd2
                        JOIN Product p2 ON sd2.ProductID = p2.ProductID
                        WHERE sd2.SaleID = s.SaleID
                        LIMIT 1
                    ) AS firstImagePath

                FROM Sale s
                LEFT JOIN Delivery d ON s.SaleID = d.SaleID
                LEFT JOIN SaleDetails sd ON s.SaleID = sd.SaleID
                LEFT JOIN Product p ON sd.ProductID = p.ProductID
                WHERE s.CustomerID = ?
                GROUP BY
                    s.SaleID,
                    s.SaleDate,
                    s.total_Amount,
                    s.SaleStatus,
                    d.Delivery_status
                ORDER BY s.SaleDate DESC, s.SaleID DESC
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                orders.add(new OrderItem(
                        rs.getInt("SaleID"),
                        rs.getDate("SaleDate").toLocalDate(),
                        rs.getDouble("total_Amount"),
                        rs.getString("deliveryStatus"),
                        rs.getInt("itemsCount"),
                        rs.getString("firstProductName"),
                        rs.getString("firstImagePath")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    public boolean cancelOrder(int saleId) {

        String updateSale = """
                UPDATE Sale
                SET SaleStatus = 'rejected',
                    ReviewNote = 'Cancelled by customer.'
                WHERE SaleID = ?
                AND SaleStatus IN ('pending', 'under_review', 'waiting_warehouse')
                """;

        String updateDelivery = """
                UPDATE Delivery
                SET Delivery_status = 'cancelled'
                WHERE SaleID = ?
                AND Delivery_status IN ('pending', 'assigned')
                """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            int saleUpdated;

            try (PreparedStatement ps = conn.prepareStatement(updateSale)) {
                ps.setInt(1, saleId);
                saleUpdated = ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(updateDelivery)) {
                ps.setInt(1, saleId);
                ps.executeUpdate();
            }

            if (saleUpdated > 0) {
                conn.commit();
                return true;
            }

            conn.rollback();
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean submitOrderFromCart(
            int customerId,
            List<CartItem> cartItems,
            double totalAmount,
            String paymentMethod) {

        String insertSale = """
                INSERT INTO Sale
                (CustomerID, EmployeeID, SaleDate, total_Amount, SaleStatus)
                VALUES (?, ?, CURDATE(), ?, 'pending')
                """;

        String insertSaleDetails = """
                INSERT INTO SaleDetails
                (SaleID, ProductID, quantity, price)
                VALUES (?, ?, ?, ?)
                """;

        String insertPayment = """
                INSERT INTO Payment
                (SaleID, amount, Payment_Date, Payment_Method)
                VALUES (?, ?, CURDATE(), ?)
                """;

        String clearCart = """
                DELETE ci
                FROM CartItem ci
                JOIN Cart c ON ci.CartID = c.CartID
                WHERE c.CustomerID = ?
                """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (
                    PreparedStatement saleStmt = conn.prepareStatement(insertSale, Statement.RETURN_GENERATED_KEYS);

                    PreparedStatement detailsStmt = conn.prepareStatement(insertSaleDetails);

                    PreparedStatement paymentStmt = conn.prepareStatement(insertPayment);

                    PreparedStatement clearCartStmt = conn.prepareStatement(clearCart)) {

                int employeeId = getDefaultEmployeeId(conn);

                saleStmt.setInt(1, customerId);
                saleStmt.setInt(2, employeeId);
                saleStmt.setDouble(3, totalAmount);
                saleStmt.executeUpdate();

                ResultSet saleKeys = saleStmt.getGeneratedKeys();

                if (!saleKeys.next()) {
                    conn.rollback();
                    return false;
                }

                int saleId = saleKeys.getInt(1);

                for (CartItem item : cartItems) {

                    detailsStmt.setInt(1, saleId);
                    detailsStmt.setInt(2, item.getProductID());
                    detailsStmt.setInt(3, item.getQuantity());
                    detailsStmt.setDouble(4, item.getPrice());

                    detailsStmt.addBatch();
                }

                detailsStmt.executeBatch();

                paymentStmt.setInt(1, saleId);
                paymentStmt.setDouble(2, totalAmount);
                paymentStmt.setString(3, paymentMethod);
                paymentStmt.executeUpdate();

                clearCartStmt.setInt(1, customerId);
                clearCartStmt.executeUpdate();

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

    private int getDefaultEmployeeId(Connection conn) throws SQLException {

        String sql = """
                SELECT EmployeeID
                FROM Employee
                ORDER BY EmployeeID
                LIMIT 1
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("EmployeeID");
            }
        }

        throw new SQLException("No employee found to assign this order.");
    }

    public List<String> getOrderProducts(int saleId) {

        List<String> items = new ArrayList<>();

        String sql = """
                SELECT p.ProductName, sd.quantity, sd.price
                FROM SaleDetails sd
                JOIN Product p ON sd.ProductID = p.ProductID
                WHERE sd.SaleID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, saleId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                items.add(
                        rs.getString("ProductName")
                                + " x" + rs.getInt("quantity")
                                + " - $" + rs.getDouble("price"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }
}