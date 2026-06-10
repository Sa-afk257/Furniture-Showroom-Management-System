package com.furniture.model;

import java.util.List;

public class Supplier {
    private int SupplierID ;
	private	String firstName;
	private	String middelInitial;
	private	String lastName;
	private	String city;
	private	String town;
	private	String area;
	private	String street;
	private	String building ;
	private	String Supplier_type ; 
    private	String email ;
    private List <String> Supplier_Phone;


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


    @Override
    public String toString() {
        return "Supplier [SupplierID=" + SupplierID + ", firstName=" + firstName + ", middelInitial=" + middelInitial
                + ", lastName=" + lastName + ", city=" + city + ", town=" + town + ", area=" + area + ", street="
                + street + ", building=" + building + ", Supplier_type=" + Supplier_type + ", email=" + email
                + ", Supplier_Phone=" + Supplier_Phone + "]";
    }

    

    

}
