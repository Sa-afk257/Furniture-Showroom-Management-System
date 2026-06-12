package com.furniture.model;

import java.time.LocalDateTime;

public class Product {

    private int productID;
    private String productName;
    private double price;
    private int category_id;
    private String color;
    private String material;
    private String description;
    private String status;
    private LocalDateTime CreatedDate;
    private String imagePath;
    private int no;
    private String categoryName;
    private String warehouseName;
    private String supplierName;
    private double stock;
    

    public Product() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Product(int no, int productID, String productName, double price,
            String categoryName, String warehouseName, String supplierName, String color, String material,String description,
            String status, String imagePath, double stock) {

        this.no = no;
        this.productID = productID;
        this.productName = productName;
        this.price = price;
        this.categoryName = categoryName;
        this.warehouseName = warehouseName;
        this.supplierName = supplierName;
        this.color = color;
        this.material = material;
        this.description = description;
        this.status = status;
        this.imagePath = imagePath;
        this.stock = stock;
    }

    public Product(int productID, String productName, double price, int category_id, String color, String material,
            String description, String status, LocalDateTime CreatedDate, String imagePath) {
        this.productID = productID;
        this.productName = productName;
        this.price = price;
        this.category_id = category_id;
        this.color = color;
        this.material = material;
        this.description = description;
        this.status = status;
        this.CreatedDate = CreatedDate;
        this.imagePath = imagePath;
    }

    public Product(
            String productName,
            double price,
            int category_id,
            String color,
            String material,
            String description,
            String status,
            LocalDateTime createdDate,
            String imagePath) {

        this.productName = productName;
        this.price = price;
        this.category_id = category_id;
        this.color = color;
        this.material = material;
        this.description = description;
        this.status = status;
        this.CreatedDate = createdDate;
        this.imagePath = imagePath;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(LocalDateTime CreatedDate) {
        this.CreatedDate = CreatedDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    @Override
    public String toString() {
        return "Product [productID=" + productID + ", productName=" + productName + ", price=" + price
                + ", category_id=" + category_id + ", color=" + color + ", material=" + material + ", description="
                + description + ", status=" + status + ", CreatedDate=" + CreatedDate + ", imagePath=" + imagePath
                + ", no=" + no + ", categoryName=" + categoryName + ", stock=" + stock + "]";
    }

}
