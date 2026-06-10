package com.furniture.model;

import java.sql.Date;

public class Sale {
    private int SaleID ;
	private int	CustomerID ;
	private int	EmployeeID ;
	private Date SaleDate ;
	private double total_Amount;

    public Sale() {
    }
    
    public Sale(int customerID, int employeeID, Date saleDate, double total_Amount) {
        CustomerID = customerID;
        EmployeeID = employeeID;
        SaleDate = saleDate;
        this.total_Amount = total_Amount;
    }

    public Sale(int saleID, int customerID, int employeeID, Date saleDate, double total_Amount) {
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
    public Date getSaleDate() {
        return SaleDate;
    }
    public void setSaleDate(Date saleDate) {
        SaleDate = saleDate;
    }
    public double getTotal_Amount() {
        return total_Amount;
    }
    public void setTotal_Amount(double total_Amount) {
        this.total_Amount = total_Amount;
    }
    @Override
    public String toString() {
        return "Sale [SaleID=" + SaleID + ", CustomerID=" + CustomerID + ", EmployeeID=" + EmployeeID + ", SaleDate="
                + SaleDate + ", total_Amount=" + total_Amount + "]";
    }
	
    
}
