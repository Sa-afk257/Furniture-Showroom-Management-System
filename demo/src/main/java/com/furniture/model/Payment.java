package com.furniture.model;

import java.sql.Date;

public class Payment {

    private int payment_id;
    private int sale_id;
    private double paymentAmount;
    private Date payment_date;


    public Payment() {
        super();
        // TODO Auto-generated constructor stub
    }
    public Payment(int payment_id, int sale_id, double paymentAmount, Date payment_date) {
        super();
        this.payment_id = payment_id;
        this.sale_id = sale_id;
        this.paymentAmount = paymentAmount;
        this.payment_date = payment_date;
    }
    public int getPayment_id() {
        return payment_id;
    }
    public void setPayment_id(int payment_id) {
        this.payment_id = payment_id;
    }
    public int getSale_id() {
        return sale_id;
    }
    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
    }
    public double getPaymentAmount() {
        return paymentAmount;
    }
    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
    public Date getPayment_date() {
        return payment_date;
    }
    public void setPayment_date(Date payment_date) {
        this.payment_date = payment_date;
    }
    @Override
    public String toString() {
        return "Payment [payment_id=" + payment_id + ", sale_id=" + sale_id + ", paymentAmount=" + paymentAmount
                + ", payment_date=" + payment_date + "]";
    }



}
