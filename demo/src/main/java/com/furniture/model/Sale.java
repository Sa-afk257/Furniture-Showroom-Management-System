package com.furniture.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Sale {
    private int SaleID;
    private int CustomerID;
    private int EmployeeID;
    private LocalDate SaleDate;
    private double total_Amount;
    private int no;

    private String customerName;
    private String employeeName;

    private int itemsCount;

    private double paidAmount;
    private double balance;

    private String paymentStatus;
    private String paymentMethod;

    private String deliveryStatus;

    private LocalDate deliveryDate;

    private List<SaleDetailes> items = new ArrayList<>();

    public Sale() {
    }

    public Sale(int customerID, int employeeID, LocalDate saleDate, double total_Amount, int no, String customerName,
            String employeeName, int itemsCount, double paidAmount, double balance, String paymentStatus,
            String paymentMethod, String deliveryStatus, LocalDate deliveryDate) {
        CustomerID = customerID;
        EmployeeID = employeeID;
        SaleDate = saleDate;
        this.total_Amount = total_Amount;
        this.no = no;
        this.customerName = customerName;
        this.employeeName = employeeName;
        this.itemsCount = itemsCount;
        this.paidAmount = paidAmount;
        this.balance = balance;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.deliveryStatus = deliveryStatus;
        this.deliveryDate = deliveryDate;
    }

    public Sale(int customerID, int employeeID, LocalDate saleDate, double total_Amount) {
        CustomerID = customerID;
        EmployeeID = employeeID;
        SaleDate = saleDate;
        this.total_Amount = total_Amount;
    }

    public Sale(int saleID, int customerID, int employeeID, LocalDate saleDate, double total_Amount) {
        SaleID = saleID;
        CustomerID = customerID;
        EmployeeID = employeeID;
        SaleDate = saleDate;
        this.total_Amount = total_Amount;
    }

    public int getSaleID() {
        return SaleID;
    }

    public void setSaleID(int saleID) {
        SaleID = saleID;
    }

    public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int customerID) {
        CustomerID = customerID;
    }

    public int getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(int employeeID) {
        EmployeeID = employeeID;
    }

    public LocalDate getSaleDate() {
        return SaleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        SaleDate = saleDate;
    }

    public double getTotal_Amount() {
        return total_Amount;
    }

    public void setTotal_Amount(double total_Amount) {
        this.total_Amount = total_Amount;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(int itemsCount) {
        this.itemsCount = itemsCount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public List<SaleDetailes> getItems() {
        return items;
    }

    public void setItems(List<SaleDetailes> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Sale [SaleID=" + SaleID + ", CustomerID=" + CustomerID + ", EmployeeID=" + EmployeeID + ", SaleDate="
                + SaleDate + ", total_Amount=" + total_Amount + ", no=" + no + ", customerName=" + customerName
                + ", employeeName=" + employeeName + ", itemsCount=" + itemsCount + ", paidAmount=" + paidAmount
                + ", balance=" + balance + ", paymentStatus=" + paymentStatus + ", paymentMethod=" + paymentMethod
                + ", deliveryStatus=" + deliveryStatus + ", deliveryDate=" + deliveryDate + "]";
    }

}
