package com.furniture.model;

import java.sql.Date;

public class PurchaseTransaction {

    private int purchase_id;
    private int supplier_id;
    private Date purchase_date;
    private int employee_id;
    private double total_amount;

    public PurchaseTransaction() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public PurchaseTransaction(int supplier_id, Date purchase_date, int employee_id, double total_amount) {
        this.supplier_id = supplier_id;
        this.purchase_date = purchase_date;
        this.employee_id = employee_id;
        this.total_amount = total_amount;
    }

    public PurchaseTransaction(int purchase_id, int supplier_id, Date purchase_date, int employee_id, double total_amount) {
        super();
        this.purchase_id = purchase_id;
        this.supplier_id = supplier_id;
        this.purchase_date = purchase_date;
        this.employee_id = employee_id;
        this.total_amount = total_amount;
    }
    public int getPurchase_id() {
        return purchase_id;
    }
    public void setPurchase_id(int purchase_id) {
        this.purchase_id = purchase_id;
    }
    public int getSupplier_id() {
        return supplier_id;
    }
    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }
    public Date getPurchase_date() {
        return purchase_date;
    }
    public void setPurchase_date(Date purchase_date) {
        this.purchase_date = purchase_date;
    }
    public int getEmployee_id() {
        return employee_id;
    }
    public void setEmployee_id(int employee_id) {
        this.employee_id = employee_id;
    }
    public double getTotal_amount() {
        return total_amount;
    }
    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }
    @Override
    public String toString() {
        return "PurchaseTransaction [purchase_id=" + purchase_id + ", supplier_id=" + supplier_id + ", purchase_date="
                + purchase_date + ", employee_id=" + employee_id + ", total_amount=" + total_amount + "]";
    }
}

