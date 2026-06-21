package com.furniture.model;

import java.time.LocalDate;

public class OrderItem {

    private int saleId;
    private LocalDate saleDate;
    private double totalAmount;
    private String deliveryStatus;
    private int itemsCount;
    private String firstProductName;
    private String firstImagePath;
    private String productName;
    private String color;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private String stockStatus;

    public OrderItem(String productName,
            String color,
            int quantity,
            double unitPrice,
            double totalPrice,
            String stockStatus) {

        this.productName = productName;
        this.color = color;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.stockStatus = stockStatus;
    }

    public OrderItem(int saleId,
            LocalDate saleDate,
            double totalAmount,
            String deliveryStatus,
            int itemsCount,
            String firstProductName,
            String firstImagePath) {

        this.saleId = saleId;
        this.saleDate = saleDate;
        this.totalAmount = totalAmount;
        this.deliveryStatus = deliveryStatus;
        this.itemsCount = itemsCount;
        this.firstProductName = firstProductName;
        this.firstImagePath = firstImagePath;
    }

    public int getSaleId() {
        return saleId;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public String getFirstProductName() {
        return firstProductName;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public String getProductName() {
        return productName;
    }

    public String getColor() {
        return color;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStockStatus() {
        return stockStatus;
    }
}