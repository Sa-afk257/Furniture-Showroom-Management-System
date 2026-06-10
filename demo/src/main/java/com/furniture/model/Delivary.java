package com.furniture.model;

import java.sql.Date;

public class Delivary {
    private int DelivaryID ;
	private int	SaleID ;
	private int	EmployeeID;
	private String Delivary_status ;
	private Date Delivary_Date;
    
    public Delivary() {
    }
    
    public Delivary(int saleID, int employeeID, String delivary_status, Date delivary_Date) {
        SaleID = saleID;
        EmployeeID = employeeID;
        Delivary_status = delivary_status;
        Delivary_Date = delivary_Date;
    }

    public Delivary(int delivaryID, int saleID, int employeeID, String delivary_status, Date delivary_Date) {
        DelivaryID = delivaryID;
        SaleID = saleID;
        EmployeeID = employeeID;
        Delivary_status = delivary_status;
        Delivary_Date = delivary_Date;
    }

    public int getDelivaryID() {
        return DelivaryID;
    }

    public void setDelivaryID(int delivaryID) {
        DelivaryID = delivaryID;
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

    public String getDelivary_status() {
        return Delivary_status;
    }

    public void setDelivary_status(String delivary_status) {
        Delivary_status = delivary_status;
    }

    public Date getDelivary_Date() {
        return Delivary_Date;
    }

    public void setDelivary_Date(Date delivary_Date) {
        Delivary_Date = delivary_Date;
    }

    @Override
    public String toString() {
        return "Delivary [DelivaryID=" + DelivaryID + ", SaleID=" + SaleID + ", EmployeeID=" + EmployeeID
                + ", Delivary_status=" + Delivary_status + ", Delivary_Date=" + Delivary_Date + "]";
    }

    
}
