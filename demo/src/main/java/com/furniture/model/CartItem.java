package com.furniture.model;

public class CartItem {

    private int productID;
    private String productName;
    private String categoryName;
    private String imagePath;
    private String color;
    private String material;
    private double stock;
    private double price;
    private int quantity;
    private double discountAmount;

    public CartItem(int productID, String productName, String categoryName,
            String imagePath, double price, int quantity,
            double discountAmount, String color, String material, double stock) {

        this.productID = productID;
        this.productName = productName;
        this.categoryName = categoryName;
        this.imagePath = imagePath;
        this.price = price;
        this.quantity = quantity;
        this.discountAmount = discountAmount;
        this.color = color;
        this.material = material;
        this.stock = stock;
    }

    public int getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getLineTotal() {
        return price * quantity;
    }

    public double getLineDiscount() {
        return discountAmount * quantity;
    }

    public String getColor() {
        return color;
    }

    public String getMaterial() {
        return material;
    }

    public double getStock() {
        return stock;
    }
}