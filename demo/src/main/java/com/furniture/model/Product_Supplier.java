package com.furniture.model;

public class Product_Supplier {

    private int SupplierID ;
    private int ProductID ;

    public Product_Supplier() {
    }

    public Product_Supplier(int supplierID, int productID) {
        SupplierID = supplierID;
        ProductID = productID;
    }

    public int getSupplierID() {
        return SupplierID;
    }

    public void setSupplierID(int supplierID) {
        SupplierID = supplierID;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    @Override
    public String toString() {
        return "Product_Supplier [SupplierID=" + SupplierID + ", ProductID=" + ProductID + "]";
    }

    
}
