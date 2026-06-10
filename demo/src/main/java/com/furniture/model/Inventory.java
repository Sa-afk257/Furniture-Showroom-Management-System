package com.furniture.model;

public class Inventory {
    private int WarehouseID ;
	private int ProductID ;
	private double quantity ;


    public Inventory() {
    }

    public Inventory(int warehouseID, int productID, double quantity) {
        WarehouseID = warehouseID;
        ProductID = productID;
        this.quantity = quantity;
    }


    public int getWarehouseID() {
        return WarehouseID;
    }


    public void setWarehouseID(int warehouseID) {
        WarehouseID = warehouseID;
    }


    public int getProductID() {
        return ProductID;
    }


    public void setProductID(int productID) {
        ProductID = productID;
    }


    public double getQuantity() {
        return quantity;
    }


    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }


    @Override
    public String toString() {
        return "Inventory [WarehouseID=" + WarehouseID + ", ProductID=" + ProductID + ", quantity=" + quantity + "]";
    }

    
    
}
