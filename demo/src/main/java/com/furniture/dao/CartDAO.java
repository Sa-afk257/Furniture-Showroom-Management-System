package com.furniture.dao;

import com.furniture.DBConnection;
import com.furniture.model.CartItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    public List<CartItem> getCartItems(int customerId) {

        List<CartItem> items = new ArrayList<>();

        String sql = """
                    SELECT
                        p.ProductID,
                        p.ProductName,
                        c.CategoryName,
                        p.imagePath,
                        p.price,
                        p.color,
                        p.material,
                        COALESCE(SUM(i.quantity), 0) AS Stock,
                        ci.Quantity,
                        COALESCE(p.price * d.percentage / 100, 0) AS DiscountAmount
                    FROM CartItem ci
                    JOIN Cart cart ON ci.CartID = cart.CartID
                    JOIN Product p ON ci.ProductID = p.ProductID
                    LEFT JOIN Category c ON p.CategoryID = c.CategoryID
                    LEFT JOIN Inventory i ON p.ProductID = i.ProductID
                    LEFT JOIN Discount d
                        ON d.ProductID = p.ProductID
                        AND CURDATE() BETWEEN d.start_Date AND d.end_Date
                    WHERE cart.CustomerID = ?
                    GROUP BY
                        p.ProductID,
                        p.ProductName,
                        c.CategoryName,
                        p.imagePath,
                        p.price,
                        p.color,
                        p.material,
                        ci.Quantity,
                        d.percentage
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                items.add(new CartItem(
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getString("CategoryName"),
                        rs.getString("imagePath"),
                        rs.getDouble("price"),
                        rs.getInt("Quantity"),
                        rs.getDouble("DiscountAmount"),
                        rs.getString("color"),
                        rs.getString("material"),
                        rs.getDouble("Stock")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public void updateQuantity(int customerId, int productId, int quantity) {

        String sql = """
                    UPDATE CartItem ci
                    JOIN Cart cart ON ci.CartID = cart.CartID
                    SET ci.Quantity = ?
                    WHERE cart.CustomerID = ? AND ci.ProductID = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, customerId);
            ps.setInt(3, productId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeItem(int customerId, int productId) {

        String sql = """
                    DELETE ci FROM CartItem ci
                    JOIN Cart cart ON ci.CartID = cart.CartID
                    WHERE cart.CustomerID = ? AND ci.ProductID = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.setInt(2, productId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearCart(int customerId) {

        String sql = """
                    DELETE ci FROM CartItem ci
                    JOIN Cart cart ON ci.CartID = cart.CartID
                    WHERE cart.CustomerID = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToCart(int customerId, int productId) {

        try (Connection con = DBConnection.getConnection()) {

            int cartId = -1;

            String findCart = """
                    SELECT CartID
                    FROM Cart
                    WHERE CustomerID = ?
                    """;

            PreparedStatement ps1 = con.prepareStatement(findCart);

            ps1.setInt(1, customerId);

            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {

                cartId = rs.getInt("CartID");

            } else {

                String createCart = """
                        INSERT INTO Cart(CustomerID)
                        VALUES(?)
                        """;

                PreparedStatement ps2 = con.prepareStatement(
                        createCart,
                        Statement.RETURN_GENERATED_KEYS);

                ps2.setInt(1, customerId);
                ps2.executeUpdate();

                ResultSet generated = ps2.getGeneratedKeys();

                if (generated.next()) {
                    cartId = generated.getInt(1);
                }
            }

            String checkItem = """
                    SELECT Quantity
                    FROM CartItem
                    WHERE CartID = ? AND ProductID = ?
                    """;

            PreparedStatement ps3 = con.prepareStatement(checkItem);

            ps3.setInt(1, cartId);
            ps3.setInt(2, productId);

            ResultSet rs2 = ps3.executeQuery();

            if (rs2.next()) {

                String updateQty = """
                        UPDATE CartItem
                        SET Quantity = Quantity + 1
                        WHERE CartID = ? AND ProductID = ?
                        """;

                PreparedStatement ps4 = con.prepareStatement(updateQty);

                ps4.setInt(1, cartId);
                ps4.setInt(2, productId);

                ps4.executeUpdate();

            } else {

                String insertItem = """
                        INSERT INTO CartItem(
                            CartID,
                            ProductID,
                            Quantity
                        )
                        VALUES (?, ?, 1)
                        """;

                PreparedStatement ps5 = con.prepareStatement(insertItem);

                ps5.setInt(1, cartId);
                ps5.setInt(2, productId);

                ps5.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCartItemsCount(int customerId) {

        String sql = """
                    SELECT COALESCE(SUM(Quantity),0) AS total
                    FROM CartItem ci
                    JOIN Cart c ON ci.CartID = c.CartID
                    WHERE c.CustomerID = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean addPayment(int saleId, double amount, String paymentMethod) {

        String sql = """
                INSERT INTO Payment
                (SaleID, amount, Payment_Date, Payment_Method)
                VALUES (?, ?, CURDATE(), ?)
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);
            ps.setDouble(2, amount);
            ps.setString(3, paymentMethod);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}