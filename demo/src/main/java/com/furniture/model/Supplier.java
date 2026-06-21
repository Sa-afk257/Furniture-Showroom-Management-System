package com.furniture.model;

import java.util.List;

public class Supplier {
    private int SupplierID;
    private String firstName;
    private String middelInitial;
    private String lastName;
    private String city;
    private String town;
    private String area;
    private String street;
    private String building;
    private String Supplier_type;
    private String email;
    private List<String> Supplier_Phone;
    private int no;
    private String fullName;
    private String phone;

    private int productsCount;
    private int purchasesCount;
    private double totalPurchasedAmount;
    private double totalPurchasedQuantity;
    private String lastPurchaseDate;
    private double averagePurchaseValue;
    private String status;

    public Supplier() {
    }

    public Supplier(String firstName, String middelInitial, String lastName, String city, String town, String area,
            String street, String building, String supplier_type, String email, List<String> supplier_Phone) {
        this.firstName = firstName;
        this.middelInitial = middelInitial;
        this.lastName = lastName;
        this.city = city;
        this.town = town;
        this.area = area;
        this.street = street;
        this.building = building;
        Supplier_type = supplier_type;
        this.email = email;
        Supplier_Phone = supplier_Phone;
    }

    public Supplier(int supplierID, String firstName, String middelInitial, String lastName, String city, String town,
            String area, String street, String building, String supplier_type, String email,
            List<String> supplier_Phone) {
        SupplierID = supplierID;
        this.firstName = firstName;
        this.middelInitial = middelInitial;
        this.lastName = lastName;
        this.city = city;
        this.town = town;
        this.area = area;
        this.street = street;
        this.building = building;
        Supplier_type = supplier_type;
        this.email = email;
        Supplier_Phone = supplier_Phone;
    }

    public int getSupplierID() {
        return SupplierID;
    }

    public void setSupplierID(int supplierID) {
        SupplierID = supplierID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddelInitial() {
        return middelInitial;
    }

    public void setMiddelInitial(String middelInitial) {
        this.middelInitial = middelInitial;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getSupplier_type() {
        return Supplier_type;
    }

    public void setSupplier_type(String supplier_type) {
        Supplier_type = supplier_type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getSupplier_Phone() {
        return Supplier_Phone;
    }

    public void setSupplier_Phone(List<String> supplier_Phone) {
        Supplier_Phone = supplier_Phone;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getFullName() {
        String mi = middelInitial == null || middelInitial.isBlank() ? "" : " " + middelInitial + ".";
        return (firstName == null ? "" : firstName)
                + mi
                + " "
                + (lastName == null ? "" : lastName);
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        if (Supplier_Phone != null && !Supplier_Phone.isEmpty()) {
            return Supplier_Phone.get(0);
        }
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getProductsCount() {
        return productsCount;
    }

    public void setProductsCount(int productsCount) {
        this.productsCount = productsCount;
    }

    public int getPurchasesCount() {
        return purchasesCount;
    }

    public void setPurchasesCount(int purchasesCount) {
        this.purchasesCount = purchasesCount;
    }

    public double getTotalPurchasedAmount() {
        return totalPurchasedAmount;
    }

    public void setTotalPurchasedAmount(double totalPurchasedAmount) {
        this.totalPurchasedAmount = totalPurchasedAmount;
    }

    public double getTotalPurchasedQuantity() {
        return totalPurchasedQuantity;
    }

    public void setTotalPurchasedQuantity(double totalPurchasedQuantity) {
        this.totalPurchasedQuantity = totalPurchasedQuantity;
    }

    public String getLastPurchaseDate() {
        return lastPurchaseDate;
    }

    public void setLastPurchaseDate(String lastPurchaseDate) {
        this.lastPurchaseDate = lastPurchaseDate;
    }

    public double getAveragePurchaseValue() {
        return averagePurchaseValue;
    }

    public void setAveragePurchaseValue(double averagePurchaseValue) {
        this.averagePurchaseValue = averagePurchaseValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return getFullName();
    }

}
