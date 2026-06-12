package com.furniture.model;

import java.time.LocalDateTime;
import java.util.List;

public class Customer {
    private int CustomerID;
    private String firstName;
    private String middelInitial;
    private String lastName;
    private String city;
    private String town;
    private String area;
    private String street;
    private String building;
    private List<String> Customer_phone;
    private LocalDateTime RegistrationDate;
    private int no;
    private int totalOrders;
    private double totalSpent;
    private double paidAmount;
    private double balance;
    private LocalDateTime lastPurchaseDate;
    private String customerType;
    private int returnsCount;

    public Customer() {
    }

    public Customer(
            String firstName,
            String middelInitial,
            String lastName,
            String city,
            String town,
            String area,
            String street,
            String building,
            List<String> customer_phone,
            LocalDateTime registrationDate) {

        this.firstName = firstName;
        this.middelInitial = middelInitial;
        this.lastName = lastName;
        this.city = city;
        this.town = town;
        this.area = area;
        this.street = street;
        this.building = building;
        this.Customer_phone = customer_phone;
        this.RegistrationDate = registrationDate;
    }

    public Customer(String firstName, String middelInitial, String lastName, String city, String town, String area,
            String street, String building, List<String> customer_phone, LocalDateTime registrationDate, int no,
            int totalOrders, double totalSpent, double paidAmount, double balance, LocalDateTime lastPurchaseDate,
            String customerType) {
        this.firstName = firstName;
        this.middelInitial = middelInitial;
        this.lastName = lastName;
        this.city = city;
        this.town = town;
        this.area = area;
        this.street = street;
        this.building = building;
        Customer_phone = customer_phone;
        RegistrationDate = registrationDate;
        this.no = no;
        this.totalOrders = totalOrders;
        this.totalSpent = totalSpent;
        this.paidAmount = paidAmount;
        this.balance = balance;
        this.lastPurchaseDate = lastPurchaseDate;
        this.customerType = customerType;
    }

    public Customer(int customerID, String firstName, String middelInitial, String lastName, String city, String town,
            String area, String street, String building, List<String> customer_phone, LocalDateTime registrationDate,
            int no, int totalOrders, double totalSpent, double paidAmount, double balance,
            LocalDateTime lastPurchaseDate, String customerType) {
        CustomerID = customerID;
        this.firstName = firstName;
        this.middelInitial = middelInitial;
        this.lastName = lastName;
        this.city = city;
        this.town = town;
        this.area = area;
        this.street = street;
        this.building = building;
        Customer_phone = customer_phone;
        RegistrationDate = registrationDate;
        this.no = no;
        this.totalOrders = totalOrders;
        this.totalSpent = totalSpent;
        this.paidAmount = paidAmount;
        this.balance = balance;
        this.lastPurchaseDate = lastPurchaseDate;
        this.customerType = customerType;
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

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public LocalDateTime getLastPurchaseDate() {
        return lastPurchaseDate;
    }

    public void setLastPurchaseDate(LocalDateTime lastPurchaseDate) {
        this.lastPurchaseDate = lastPurchaseDate;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getFullName() {

        String middle = middelInitial == null
                ? ""
                : middelInitial + " ";

        return firstName + " "
                + middle
                + lastName;
    }

    public LocalDateTime getLastPurchase() {
        return lastPurchaseDate;
    }

    public void setLastPurchase(LocalDateTime lastPurchaseDate) {
        this.lastPurchaseDate = lastPurchaseDate;
    }

    public int getReturnsCount() {
        return returnsCount;
    }

    public void setReturnsCount(int returnsCount) {
        this.returnsCount = returnsCount;
    }

    public String getPrimaryPhone() {
        if (Customer_phone == null || Customer_phone.isEmpty()) {
            return "";
        }
        return Customer_phone.get(0);
    }

    public String getFullAddress() {
        return String.join(", ",
                city == null ? "" : city,
                town == null ? "" : town,
                area == null ? "" : area,
                street == null ? "" : street,
                building == null ? "" : building).replaceAll("(,\\s*)+$", "");
    }

    @Override
    public String toString() {
        return "Customer [CustomerID=" + CustomerID + ", firstName=" + firstName + ", middelInitial=" + middelInitial
                + ", lastName=" + lastName + ", city=" + city + ", town=" + town + ", area=" + area + ", street="
                + street + ", building=" + building + ", Customer_phone=" + Customer_phone + ", RegistrationDate="
                + RegistrationDate + ", no=" + no + ", totalOrders=" + totalOrders + ", totalSpent=" + totalSpent
                + ", paidAmount=" + paidAmount + ", balance=" + balance + ", lastPurchaseDate=" + lastPurchaseDate
                + ", customerType=" + customerType + "]";
    }

}
