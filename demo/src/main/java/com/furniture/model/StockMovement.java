package com.furniture.model;

import java.sql.Date;

public class StockMovement {

    private int movement_id;
    private int product_id;
    private String movementType;
    private double quantity;
    private Date movement_date;

    public StockMovement() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public StockMovement(int product_id, String movementType, double quantity, Date movement_date) {
        this.product_id = product_id;
        this.movementType = movementType;
        this.quantity = quantity;
        this.movement_date = movement_date;
    }

    public StockMovement(int movement_id, int product_id, String movementType, double quantity, Date movement_date) {
        super();
        this.movement_id = movement_id;
        this.product_id = product_id;
        this.movementType = movementType;
        this.quantity = quantity;
        this.movement_date = movement_date;
    }
    public int getMovement_id() {
        return movement_id;
    }
    public void setMovement_id(int movement_id) {
        this.movement_id = movement_id;
    }
    public int getProduct_id() {
        return product_id;
    }
    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }
    public String getMovementType() {
        return movementType;
    }
    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }
    public double getQuantity() {
        return quantity;
    }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    public Date getMovement_date() {
        return movement_date;
    }
    public void setMovement_date(Date movement_date) {
        this.movement_date = movement_date;
    }
    @Override
    public String toString() {
        return "StockMovement [movement_id=" + movement_id + ", product_id=" + product_id + ", movementType=" + movementType
                + ", quantity=" + quantity + ", movement_date=" + movement_date + "]";
    }

}
