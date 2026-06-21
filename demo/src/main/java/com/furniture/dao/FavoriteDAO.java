package com.furniture.dao;

import java.sql.ResultSet;
import com.furniture.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.furniture.model.Product;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FavoriteDAO {

    public boolean isFavorite(int customerId, int productId) {
        String sql = "SELECT 1 FROM Favorite WHERE CustomerID=? AND ProductID=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.setInt(2, productId);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void toggleFavorite(int customerId, int productId) {
        if (isFavorite(customerId, productId)) {
            removeFavorite(customerId, productId);
        } else {
            addFavorite(customerId, productId);
        }
    }

    public void addFavorite(int customerId, int productId) {
        String sql = "INSERT INTO Favorite(CustomerID, ProductID) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.setInt(2, productId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFavorite(int customerId, int productId) {
        String sql = "DELETE FROM Favorite WHERE CustomerID=? AND ProductID=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.setInt(2, productId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Product> getFavoriteProducts(int customerId) {
        ObservableList<Product> list = FXCollections.observableArrayList();

        String sql = """
            SELECT p.*
            FROM Product p
            JOIN Favorite f ON p.ProductID = f.ProductID
            WHERE f.CustomerID = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Product p = new Product();
                p.setProductID(rs.getInt("ProductID"));
                p.setProductName(rs.getString("ProductName"));
                p.setPrice(rs.getDouble("Price"));
                p.setImagePath(rs.getString("imagePath"));
                p.setFavorite(true);
                list.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
