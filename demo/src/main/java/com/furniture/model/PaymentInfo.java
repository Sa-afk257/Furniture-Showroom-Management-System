package com.furniture.model;

public class PaymentInfo {

    private int paymentId;
    private double amount;
    private String paymentDate;
    private String paymentMethod;

    public PaymentInfo(int paymentId, double amount, String paymentDate, String paymentMethod) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}