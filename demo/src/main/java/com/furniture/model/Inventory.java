package com.furniture.model;

public class Inventory {

    private int no;
    private int productID;
    private String productName;
    private String categoryName;
    private int warehouseID;
    private String warehouseName;
    private double quantity;
    private String status;
    private int oldWarehouseID;
    private int oldProductID;
    private String imagePath;
    private String managerName;
    private double unitPrice;
    private double stockValue;
    private String color;
    private String material;
    private String description;

    private double warehouseCapacity;
    private double usedCapacity;
    private double remainingCapacity;

    private String movementType;
    private java.time.LocalDate movement_date;

    public Inventory() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Inventory(int no, int productID, String productName, String categoryName, int warehouseID,
            String warehouseName, double quantity, String status) {
        super();
        this.no = no;
        this.productID = productID;
        this.productName = productName;
        this.categoryName = categoryName;
        this.warehouseID = warehouseID;
        this.warehouseName = warehouseName;
        this.quantity = quantity;
        this.status = status;
    }

    public int getOldWarehouseID() {
        return oldWarehouseID;
    }

    public void setOldWarehouseID(int oldWarehouseID) {
        this.oldWarehouseID = oldWarehouseID;
    }

    public int getOldProductID() {
        return oldProductID;
    }

    public void setOldProductID(int oldProductID) {
        this.oldProductID = oldProductID;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getWarehouseID() {
        return warehouseID;
    }

    public void setWarehouseID(int warehouseID) {
        this.warehouseID = warehouseID;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getStockValue() {
        return stockValue;
    }

    public void setStockValue(double stockValue) {
        this.stockValue = stockValue;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getWarehouseCapacity() {
        return warehouseCapacity;
    }

    public void setWarehouseCapacity(double warehouseCapacity) {
        this.warehouseCapacity = warehouseCapacity;
    }

    public double getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(double usedCapacity) {
        this.usedCapacity = usedCapacity;
    }

    public double getRemainingCapacity() {
        return remainingCapacity;
    }

    public void setRemainingCapacity(double remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public java.time.LocalDate getmovement_date() {
        return movement_date;
    }

    public void setmovement_date(java.time.LocalDate movement_date) {
        this.movement_date = movement_date;
    }

    @Override
    public String toString() {
        return "Inventory [no=" + no + ", productID=" + productID + ", productName=" + productName + ", categoryName="
                + categoryName + ", warehouseID=" + warehouseID + ", warehouseName=" + warehouseName + ", quantity="
                + quantity + ", status=" + status + "]";
    }

}
