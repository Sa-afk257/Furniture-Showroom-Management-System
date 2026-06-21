package com.furniture.model;

import java.sql.Date;

public class StockMovement {

    private int movementId;
    private int productId;
    private String movementType;
    private double quantity;
    private Date movement_date;

    public StockMovement() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public StockMovement(int productId, String movementType, double quantity, Date movement_date) {
        this.productId = productId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.movement_date = movement_date;
    }

    public StockMovement(int movementId, int productId, String movementType, double quantity, Date movement_date) {
        super();
        this.movementId = movementId;
        this.productId = productId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.movement_date = movement_date;
    }
    public int getmovementId() {
        return movementId;
    }
    public void setmovementId(int movementId) {
        this.movementId = movementId;
    }
    public int getproductId() {
        return productId;
    }
    public void setproductId(int productId) {
        this.productId = productId;
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
    public Date getmovement_date() {
        return movement_date;
    }
    public void setmovement_date(Date movement_date) {
        this.movement_date = movement_date;
    }
    @Override
    public String toString() {
        return "StockMovement [movementId=" + movementId + ", productId=" + productId + ", movementType=" + movementType
                + ", quantity=" + quantity + ", movement_date=" + movement_date + "]";
    }

}
