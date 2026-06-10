package com.furniture.model;

import java.sql.Date;

public class Return {

    private int return_id;
    private int sale_id;
    private int product_id;
    private double quantity;
    private Date return_date;
    private String comments;

    public Return() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public Return(int sale_id, int product_id, double quantity, Date return_date, String comments) {
        this.sale_id = sale_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.return_date = return_date;
        this.comments = comments;
    }

    public Return(int return_id, int sale_id, int product_id, double quantity, Date return_date, String comments) {
        super();
        this.return_id = return_id;
        this.sale_id = sale_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.return_date = return_date;
        this.comments = comments;
    }
    public int getReturn_id() {
        return return_id;
    }
    public void setReturn_id(int return_id) {
        this.return_id = return_id;
    }
    public int getSale_id() {
        return sale_id;
    }
    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
    }
    public int getProduct_id() {
        return product_id;
    }
    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }
    public double getQuantity() {
        return quantity;
    }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    public Date getReturn_date() {
        return return_date;
    }
    public void setReturn_date(Date return_date) {
        this.return_date = return_date;
    }
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    @Override
    public String toString() {
        return "Return [return_id=" + return_id + ", sale_id=" + sale_id + ", product_id=" + product_id + ", quantity="
                + quantity + ", return_date=" + return_date + ", comments=" + comments + "]";
    }



}
