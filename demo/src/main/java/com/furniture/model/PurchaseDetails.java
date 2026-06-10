package com.furniture.model;

public class PurchaseDetails {

	private int purchase_id;
	private int product_id;
	private double quantity;
	private double price;

	public PurchaseDetails() {
		super();
		// TODO Auto-generated constructor stub
	}
    
	public PurchaseDetails(int purchase_id, int product_id, double quantity, double price) {
		super();
		this.purchase_id = purchase_id;
		this.product_id = product_id;
		this.quantity = quantity;
		this.price = price;
	}
	public int getPurchase_id() {
		return purchase_id;
	}
	public void setPurchase_id(int purchase_id) {
		this.purchase_id = purchase_id;
	}
	public int getProduct_id() {
		return product_id;
	}
	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	@Override
	public String toString() {
		return "PurchaseDetails [purchase_id=" + purchase_id + ", product_id=" + product_id + ", quantity=" + quantity
				+ ", price=" + price + "]";
	}

	
}