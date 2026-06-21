package com.furniture.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.furniture.DBConnection;
import com.furniture.model.Category;

public class CategoryDAO {

	public List<Category> getAllCategoriesForTable() {

		List<Category> categories = new ArrayList<>();

		String sql = """
				SELECT
				    c.CategoryID,
				    c.CategoryName,

				    COUNT(DISTINCT p.ProductID) AS totalProducts,

				    COALESCE(SUM(i.quantity),0) AS totalStock,

				    COALESCE(SUM(i.quantity * p.price),0) AS totalValue

				FROM Category c

				LEFT JOIN Product p
				ON c.CategoryID = p.CategoryID

				LEFT JOIN Inventory i
				ON p.ProductID = i.ProductID

				GROUP BY c.CategoryID,c.CategoryName

				ORDER BY c.CategoryID
				""";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			int no = 1;

			while (rs.next()) {

				categories.add(new Category(no++, rs.getInt("CategoryID"), rs.getString("CategoryName"),
						rs.getInt("totalProducts"), rs.getDouble("totalStock"), rs.getDouble("totalValue")));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return categories;
	}

	public class CategoryStats {

		private int totalCategories;
		private int usedCategories;
		private int emptyCategories;
		private int totalProducts;
		private double averageProducts;

		public double getAverageProducts() {
			return averageProducts;
		}

		public void setAverageProducts(double averageProducts) {
			this.averageProducts = averageProducts;
		}

		public int getTotalCategories() {
			return totalCategories;
		}

		public void setTotalCategories(int totalCategories) {
			this.totalCategories = totalCategories;
		}

		public int getUsedCategories() {
			return usedCategories;
		}

		public void setUsedCategories(int usedCategories) {
			this.usedCategories = usedCategories;
		}

		public int getEmptyCategories() {
			return emptyCategories;
		}

		public void setEmptyCategories(int emptyCategories) {
			this.emptyCategories = emptyCategories;
		}

		public int getTotalProducts() {
			return totalProducts;
		}

		public void setTotalProducts(int totalProducts) {
			this.totalProducts = totalProducts;
		}
	}

	public CategoryStats getCategoryStats() {

		CategoryStats stats = new CategoryStats();
		if (stats.getTotalCategories() > 0) {

			stats.setAverageProducts((double) stats.getTotalProducts() / stats.getTotalCategories());
		}
		try (Connection conn = DBConnection.getConnection()) {

			PreparedStatement ps1 = conn.prepareStatement("SELECT COUNT(*) FROM Category");

			ResultSet rs1 = ps1.executeQuery();

			if (rs1.next())
				stats.setTotalCategories(rs1.getInt(1));

			PreparedStatement ps2 = conn.prepareStatement("""
					SELECT COUNT(DISTINCT CategoryID)
					FROM Product
					""");

			ResultSet rs2 = ps2.executeQuery();

			if (rs2.next())
				stats.setUsedCategories(rs2.getInt(1));

			PreparedStatement ps3 = conn.prepareStatement("""
					SELECT COUNT(*)
					FROM Category c
					LEFT JOIN Product p
					ON c.CategoryID = p.CategoryID
					WHERE p.ProductID IS NULL
					""");

			ResultSet rs3 = ps3.executeQuery();

			if (rs3.next())
				stats.setEmptyCategories(rs3.getInt(1));

			PreparedStatement ps4 = conn.prepareStatement("""
					SELECT COUNT(*)
					FROM Product
					""");

			ResultSet rs4 = ps4.executeQuery();

			if (rs4.next())
				stats.setTotalProducts(rs4.getInt(1));

			PreparedStatement ps5 = conn.prepareStatement("""
					SELECT
					ROUND(
					    COUNT(*) /
					    NULLIF(COUNT(DISTINCT CategoryID),0),
					    2
					)
					FROM Product
					""");

			ResultSet rs5 = ps5.executeQuery();

			if (rs5.next()) {
				stats.setAverageProducts(rs5.getDouble(1));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return stats;
	}

	public boolean insertCategory(Category category) {

		String sql = """
				INSERT INTO Category(CategoryName)
				VALUES(?)
				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, category.getCategoryName());

			return ps.executeUpdate() > 0;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean updateCategory(Category category) {

		String sql = """
				UPDATE Category
				SET CategoryName = ?
				WHERE CategoryID = ?
				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, category.getCategoryName());
			ps.setInt(2, category.getCategoryID());

			return ps.executeUpdate() > 0;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean deleteCategory(int categoryId) {

		if (countProductsInCategory(categoryId) > 0) {
			return false;
		}

		String sql = """
				DELETE FROM Category
				WHERE CategoryID = ?
				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, categoryId);

			return ps.executeUpdate() > 0;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public int countProductsInCategory(int categoryId) {

		String sql = """
				SELECT COUNT(*)
				FROM Product
				WHERE CategoryID = ?
				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, categoryId);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public List<Category> searchCategories(String keyword) {

		List<Category> categories = new ArrayList<>();

		String sql = """
				SELECT
				    c.CategoryID,
				    c.CategoryName,

				    COUNT(DISTINCT p.ProductID) AS totalProducts,

				    COALESCE(SUM(i.quantity),0) AS totalStock,

				    COALESCE(SUM(i.quantity * p.price),0) AS totalValue

				FROM Category c

				LEFT JOIN Product p
				ON c.CategoryID = p.CategoryID

				LEFT JOIN Inventory i
				ON p.ProductID = i.ProductID

				WHERE c.CategoryName LIKE ?

				GROUP BY c.CategoryID,c.CategoryName
				""";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, "%" + keyword + "%");

			ResultSet rs = ps.executeQuery();

			int no = 1;

			while (rs.next()) {

				categories.add(new Category(no++, rs.getInt("CategoryID"), rs.getString("CategoryName"),
						rs.getInt("totalProducts"), rs.getDouble("totalStock"), rs.getDouble("totalValue")));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return categories;
	}
}