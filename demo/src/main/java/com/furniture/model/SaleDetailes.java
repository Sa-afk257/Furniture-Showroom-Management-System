package com.furniture.model;

public class SaleDetailes {

    private int sale_id;
    private int product_id;
    private String productName;
    private int quantity;
    private double price;

    public SaleDetailes() {
    }

    public SaleDetailes(int product_id, String productName, int quantity, double price) {
        this.product_id = product_id;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public SaleDetailes(int sale_id, int product_id, String productName, int quantity, double price) {
        this.sale_id = sale_id;
        this.product_id = product_id;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public int getSale_id() {
        return sale_id;
    }

    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSubtotal() {
        return quantity * price;
    }
}