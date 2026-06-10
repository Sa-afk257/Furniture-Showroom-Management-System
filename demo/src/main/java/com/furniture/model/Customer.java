package com.furniture.model;

import java.time.LocalDateTime;
import java.util.List;

public class Customer {
    private int CustomerID;
	private	String firstName;
	private	String middelInitial;
	private	String lastName;
	private	String city;
	private	String town;
	private	String area;
	private	String street;
	private	String building ;
    private List <String> Customer_phone;
    private LocalDateTime RegistrationDate;

    public Customer() {
    }
    

    public Customer(String firstName, String middelInitial, String lastName, String city, String town, String area,
            String street, String building, List<String> customer_phone, LocalDateTime RegistrationDate) {
        this.firstName = firstName;
        this.middelInitial = middelInitial;
        this.lastName = lastName;
        this.city = city;
        this.town = town;
        this.area = area;
        this.street = street;
        this.building = building;
        this.Customer_phone = customer_phone;
        this.RegistrationDate = RegistrationDate;
    }

    public Customer(int customerID, String firstName, String middelInitial, String lastName, String city, String town,
            String area, String street, String building, List<String> customer_phone, LocalDateTime RegistrationDate) {
        CustomerID = customerID;
        this.firstName = firstName;
        this.middelInitial = middelInitial;
        this.lastName = lastName;
        this.city = city;
        this.town = town;
        this.area = area;
        this.street = street;
        this.building = building;
        this.Customer_phone = customer_phone;
        this.RegistrationDate = RegistrationDate;
    }



    public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int customerID) {
        CustomerID = customerID;
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

    public List<String> getCustomer_phone() {
        return Customer_phone;
    }

    public void setCustomer_phone(List<String> customer_phone) {
        Customer_phone = customer_phone;
    }


    public LocalDateTime getRegistrationDate() {
        return RegistrationDate;
    }


    public void setRegistrationDate(LocalDateTime registrationDate) {
        RegistrationDate = registrationDate;
    }


    @Override
    public String toString() {
        return "Customer [CustomerID=" + CustomerID + ", firstName=" + firstName + ", middelInitial=" + middelInitial
                + ", lastName=" + lastName + ", city=" + city + ", town=" + town + ", area=" + area + ", street="
                + street + ", building=" + building + ", Customer_phone=" + Customer_phone + ", RegistrationDate="
                + RegistrationDate + "]";
    }

    

    
    
}
