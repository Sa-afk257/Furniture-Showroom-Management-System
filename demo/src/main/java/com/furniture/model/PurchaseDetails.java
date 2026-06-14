package com.furniture.model;

public class PurchaseDetails {

    private int purchase_id;
    private int product_id;
    private String productName;
    private String categoryName;
    private int warehouseID;
    private String warehouseName;
    private double quantity;
    private double price;
    private double currentStock;

    public PurchaseDetails() {
    }

    public PurchaseDetails(int product_id, String productName, String categoryName,
            int warehouseID, String warehouseName,
            double quantity, double price) {
        this.product_id = product_id;
        this.productName = productName;
        this.categoryName = categoryName;
        this.warehouseID = warehouseID;
        this.warehouseName = warehouseName;
        this.quantity = quantity;
        this.price = price;
    }

    public double getSubtotal() {
        return quantity * price;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getWarehouseID() {
        return warehouseID;
    }

    public void setWarehouseID(int warehouseID) {
        this.warehouseID = warehouseID;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
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

    public double getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(double currentStock) {
        this.currentStock = currentStock;
    }


}