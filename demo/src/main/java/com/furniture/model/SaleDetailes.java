package com.furniture.model;

public class SaleDetailes {

    private int sale_id;
    private int product_id;
    private double quantity;
    private double price;

    public SaleDetailes() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public SaleDetailes(int product_id, double quantity, double price) {
        this.product_id = product_id;
        this.quantity = quantity;
        this.price = price;
    }

    public SaleDetailes(int sale_id, int product_id, double quantity, double price) {
        super();
        this.sale_id = sale_id;
        this.product_id = product_id;
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
        return "SaleDetailes [sale_id=" + sale_id + ", product_id=" + product_id + ", quantity=" + quantity + ", price="
                + price + "]";
    }
}


