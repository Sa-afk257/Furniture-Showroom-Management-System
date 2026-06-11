package com.furniture.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.furniture.DBConnection;
import com.furniture.model.Product;

public class ProductDAO {

    public List<Product> getAllProductsForTable() {
        List<Product> products = new ArrayList<>();

        String sql = """
                    SELECT
                        p.ProductID,
                        p.ProductName,
                        p.imagePath,
                        p.price,
                        c.CategoryName,
                        p.color,
                        p.material,
                        p.ProductStatus,
                        COALESCE(SUM(i.quantity), 0) AS totalStock
                    FROM Product p
                    JOIN Category c ON p.CategoryID = c.CategoryID
                    LEFT JOIN Inventory i ON p.ProductID = i.ProductID
                    GROUP BY
                        p.ProductID,
                        p.ProductName,
                        p.imagePath,
                        p.price,
                        c.CategoryName,
                        p.color,
                        p.material,
                        p.ProductStatus
                    ORDER BY p.ProductID
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            int no = 1;

            while (rs.next()) {
                products.add(new Product(
                        no++,
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getDouble("price"),
                        rs.getString("CategoryName"),
                        rs.getString("color"),
                        rs.getString("material"),
                        rs.getString("ProductStatus"),
                        rs.getString("imagePath"),
                        rs.getDouble("totalStock")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public class ProductStats {

        private int totalProducts;
        private int inStock;
        private int lowStock;
        private int outOfStock;
        private double totalValue;

        public int getTotalProducts() {
            return totalProducts;
        }

        public void setTotalProducts(int totalProducts) {
            this.totalProducts = totalProducts;
        }

        public int getInStock() {
            return inStock;
        }

        public void setInStock(int inStock) {
            this.inStock = inStock;
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

        public double getTotalValue() {
            return totalValue;
        }

        public void setTotalValue(double totalValue) {
            this.totalValue = totalValue;
        }

    }

    public ProductStats getProductStats() {

        ProductStats stats = new ProductStats();

        try (Connection conn = DBConnection.getConnection()) {

            // Total Products

            PreparedStatement ps1 = conn.prepareStatement(
                    "SELECT COUNT(*) FROM Product");

            ResultSet rs1 = ps1.executeQuery();

            if (rs1.next())
                stats.setTotalProducts(rs1.getInt(1));

            // In Stock

            PreparedStatement ps2 = conn.prepareStatement(
                    """
                            SELECT COUNT(DISTINCT ProductID)
                            FROM Inventory
                            WHERE quantity > 3
                            """);

            ResultSet rs2 = ps2.executeQuery();

            if (rs2.next())
                stats.setInStock(rs2.getInt(1));

            // Low Stock

            PreparedStatement ps3 = conn.prepareStatement(
                    """
                            SELECT COUNT(DISTINCT ProductID)
                            FROM Inventory
                            WHERE quantity BETWEEN 1 AND 3
                            """);

            ResultSet rs3 = ps3.executeQuery();

            if (rs3.next())
                stats.setLowStock(rs3.getInt(1));

            // Out Stock

            PreparedStatement ps4 = conn.prepareStatement(
                    """
                            SELECT COUNT(*)
                            FROM Product p
                            LEFT JOIN Inventory i
                            ON p.ProductID=i.ProductID
                            WHERE COALESCE(i.quantity,0)=0
                            """);

            ResultSet rs4 = ps4.executeQuery();

            if (rs4.next())
                stats.setOutOfStock(rs4.getInt(1));

            // Total Value

            PreparedStatement ps5 = conn.prepareStatement(
                    """
                            SELECT SUM(price * quantity)
                            FROM Product p
                            JOIN Inventory i
                            ON p.ProductID=i.ProductID
                            """);

            ResultSet rs5 = ps5.executeQuery();

            if (rs5.next())
                stats.setTotalValue(rs5.getDouble(1));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }

    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();

        String sql = """
                SELECT CategoryName
                FROM Category
                ORDER BY CategoryName
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("CategoryName"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public List<String> getWarehouses() {
        List<String> warehouses = new ArrayList<>();

        String sql = """
                SELECT WarehouseName
                FROM Warehouse
                ORDER BY WarehouseName
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                warehouses.add(rs.getString("WarehouseName"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return warehouses;
    }

    public List<String> getSuppliers() {
        List<String> suppliers = new ArrayList<>();

        String sql = """
                SELECT CONCAT(firstName, ' ', middelInitial, ' ', lastName) AS SupplierName
                FROM Supplier
                ORDER BY firstName, lastName
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                suppliers.add(rs.getString("SupplierName"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suppliers;
    }

    public List<String> getColors() {
        List<String> colors = new ArrayList<>();

        String sql = """
                SELECT DISTINCT color
                FROM Product
                WHERE color IS NOT NULL
                  AND color <> ''
                ORDER BY color
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                colors.add(rs.getString("color"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return colors;
    }

    public List<String> getMaterials() {
        List<String> materials = new ArrayList<>();

        String sql = """
                SELECT DISTINCT material
                FROM Product
                WHERE material IS NOT NULL
                  AND material <> ''
                ORDER BY material
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                materials.add(rs.getString("material"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return materials;
    }

    public double[] getPriceRange() {
        double[] range = new double[2];

        String sql = """
                SELECT
                    COALESCE(MIN(price), 0) AS min_price,
                    COALESCE(MAX(price), 0) AS max_price
                FROM Product
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                range[0] = rs.getDouble("min_price");
                range[1] = rs.getDouble("max_price");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return range;
    }

    public int countProducts() {
        String sql = """
                SELECT COUNT(*) AS total
                FROM Product
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getCategoryIdByName(String categoryName) {
        String sql = """
                SELECT CategoryID
                FROM Category
                WHERE CategoryName = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, categoryName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("CategoryID");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void insertProduct(Product product, double stock) {
        String insertProductSql = """
                                INSERT INTO Product
                (ProductName, price, CategoryID, color, material, ProductStatus, CreatedDate, imagePath)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                                """;

        String insertInventorySql = """
                INSERT INTO Inventory
                (ProductID, quantity)
                VALUES (?, ?)
                """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    insertProductSql,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, product.getProductName());
                ps.setDouble(2, product.getPrice());
                ps.setInt(3, product.getCategory_id());
                ps.setString(4, product.getColor());
                ps.setString(5, product.getMaterial());
                ps.setString(7, product.getStatus());
                ps.setObject(8, product.getCreatedDate());
                ps.setString(9, product.getImagePath());

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int productId = rs.getInt(1);

                        try (PreparedStatement ps2 = conn.prepareStatement(insertInventorySql)) {
                            ps2.setInt(1, productId);
                            ps2.setDouble(2, stock);
                            ps2.executeUpdate();
                        }
                    }
                }

                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteProduct(int productId) {

        String sql = "DELETE FROM Product WHERE ProductID = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateProduct(Product product) {

        String productSql = """
                UPDATE Product
                SET ProductName = ?,
                    price = ?,
                    CategoryID = ?,
                    color = ?,
                    material = ?,
                    ProductStatus = ?,
                    imagePath = ?
                WHERE ProductID = ?
                """;

        String inventorySql = """
                UPDATE Inventory
                SET quantity = ?
                WHERE ProductID = ?
                """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {

                try (PreparedStatement ps = conn.prepareStatement(productSql)) {

                    ps.setString(1, product.getProductName());
                    ps.setDouble(2, product.getPrice());
                    ps.setInt(3, product.getCategory_id());
                    ps.setString(4, product.getColor());
                    ps.setString(5, product.getMaterial());
                    ps.setString(6, product.getStatus());
                    ps.setString(7, product.getImagePath());
                    ps.setInt(8, product.getProductID());

                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(inventorySql)) {

                    ps.setDouble(1, product.getStock());
                    ps.setInt(2, product.getProductID());

                    ps.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (Exception e) {

                conn.rollback();
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}