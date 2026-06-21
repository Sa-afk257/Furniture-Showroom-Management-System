package com.furniture.model;

public class Account {

    private int accountId;
    private String email;
    private String role;

    private Integer customerId;
    private Integer employeeId;

    private String firstName;
    private String lastName;
    private String employeeRole;

    public Account(int accountId, String email, String role) {
        this.accountId = accountId;
        this.email = email;
        this.role = role;
    }

    public Account(int accountId, String email, String role,
            Integer customerId, Integer employeeId,
            String firstName, String lastName) {
        this.accountId = accountId;
        this.email = email;
        this.role = role;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isCustomer() {
        return customerId != null;
    }

    public boolean isEmployee() {
        return employeeId != null;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmployeeRole() {
        return employeeRole;
    }

    public void setEmployeeRole(String employeeRole) {
        this.employeeRole = employeeRole;
    }

}