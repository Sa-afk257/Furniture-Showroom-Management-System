package com.furniture.model;

public class Category {
	private int CategoryID;
	private String CategoryName;
	private int no;
	private int productCount;
	private double totalStock;
	private double totalValue;

	public Category(int no, int categoryID, String categoryName, int productCount, double totalStock,
			double totalValue) {

		this.no = no;
		this.CategoryID = categoryID;
		this.CategoryName = categoryName;
		this.productCount = productCount;
		this.totalStock = totalStock;
		this.totalValue = totalValue;
	}

	public Category(String categoryName) {
		this.CategoryName = categoryName;
	}

	public Category() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getCategoryID() {
		return CategoryID;
	}

	public void setCategoryID(int categoryID) {
		CategoryID = categoryID;
	}

	public String getCategoryName() {
		return CategoryName;
	}

	public void setCategoryName(String categoryName) {
		CategoryName = categoryName;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public int getProductCount() {
		return productCount;
	}

	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

	public double getTotalStock() {
		return totalStock;
	}

	public void setTotalStock(double totalStock) {
		this.totalStock = totalStock;
	}

	public double getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(double totalValue) {
		this.totalValue = totalValue;
	}

	@Override
	public String toString() {
		return "Category [CategoryID=" + CategoryID + ", CategoryName=" + CategoryName + ", no=" + no
				+ ", productCount=" + productCount + ", totalStock=" + totalStock + ", totalValue=" + totalValue + "]";
	}

}
