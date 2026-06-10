package com.furniture.model;

import java.sql.Date;

public class Discount {

	private int discount_id;
	private int product_id;
	private double percentage;
	private Date start_date;
	private Date end_date;
    
	public Discount() {
		super();
		// TODO Auto-generated constructor stub
	}
    
	public Discount(int product_id, double percentage, Date start_date, Date end_date) {
        this.product_id = product_id;
        this.percentage = percentage;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public Discount(int discount_id, int product_id, double percentage, Date start_date, Date end_date) {
		super();
		this.discount_id = discount_id;
		this.product_id = product_id;
		this.percentage = percentage;
		this.start_date = start_date;
		this.end_date = end_date;
	}
	public int getDiscount_id() {
		return discount_id;
	}
	public void setDiscount_id(int discount_id) {
		this.discount_id = discount_id;
	}
	public int getProduct_id() {
		return product_id;
	}
	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	public Date getStart_date() {
		return start_date;
	}
	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}
	public Date getEnd_date() {
		return end_date;
	}
	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}
	@Override
	public String toString() {
		return "Discount [discount_id=" + discount_id + ", product_id=" + product_id + ", percentage=" + percentage
				+ ", start_date=" + start_date + ", end_date=" + end_date + "]";
	}
	
	
}

