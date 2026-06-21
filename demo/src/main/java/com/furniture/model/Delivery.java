package com.furniture.model;

import java.sql.Date;

public class Delivery {
    private int DeliveryID ;
	private int	SaleID ;
	private int	EmployeeID;
	private String Delivery_status ;
	private Date Delivery_Date;
    
    public Delivery() {
    }
    
    public Delivery(int saleID, int employeeID, String Delivery_status, Date Delivery_Date) {
        SaleID = saleID;
        EmployeeID = employeeID;
        Delivery_status = Delivery_status;
        Delivery_Date = Delivery_Date;
    }

    public Delivery(int DeliveryID, int saleID, int employeeID, String Delivery_status, Date Delivery_Date) {
        DeliveryID = DeliveryID;
        SaleID = saleID;
        EmployeeID = employeeID;
        Delivery_status = Delivery_status;
        Delivery_Date = Delivery_Date;
    }

    public int getDeliveryID() {
        return DeliveryID;
    }

    public void setDeliveryID(int DeliveryID) {
        DeliveryID = DeliveryID;
    }

    public int getSaleID() {
        return SaleID;
    }

    public void setSaleID(int saleID) {
        SaleID = saleID;
    }

    public int getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(int employeeID) {
        EmployeeID = employeeID;
    }

    public String getDelivery_status() {
        return Delivery_status;
    }

    public void setDelivery_status(String Delivery_status) {
        Delivery_status = Delivery_status;
    }

    public Date getDelivery_Date() {
        return Delivery_Date;
    }

    public void setDelivery_Date(Date Delivery_Date) {
        Delivery_Date = Delivery_Date;
    }

    @Override
    public String toString() {
        return "Delivery [DeliveryID=" + DeliveryID + ", SaleID=" + SaleID + ", EmployeeID=" + EmployeeID
                + ", Delivery_status=" + Delivery_status + ", Delivery_Date=" + Delivery_Date + "]";
    }

    
}
