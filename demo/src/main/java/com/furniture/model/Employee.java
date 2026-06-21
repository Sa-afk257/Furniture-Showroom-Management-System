package com.furniture.model;

import java.sql.Date;
import java.util.List;

public class Employee {
    private int EmployeeID;
    private String firstName;
    private String middelInitial;
    private String lastName;
    private String email;
    private String city;
    private double salary;
    private String ShiftTime;
    private Date HireDate;
    private String gender;
    private String Employee_role;
    private List<String> Employee_Phone;

    private int no;
    private String status = "Active";

    private int salesCount;
    private int purchasesCount;
    private int deliveriesCount;
    private int managedWarehouses;

    private double totalSalesAmount;
    private double totalPurchaseAmount;

    public Employee() {
    }

    public Employee(String firstName, String middelInitial, String lastName, String email, String city, double salary,
            String shiftTime, Date hireDate, String gender, String employee_role, List<String> employee_Phone) {
        this.firstName = firstName;
        this.middelInitial = middelInitial;
        this.lastName = lastName;
        this.email = email;
        this.city = city;
        this.salary = salary;
        ShiftTime = shiftTime;
        HireDate = hireDate;
        this.gender = gender;
        Employee_role = employee_role;
        Employee_Phone = employee_Phone;
    }

    public Employee(int employeeID, String firstName, String middelInitial, String lastName, String email, String city,
            double salary, String shiftTime, Date hireDate, String gender, String employee_role,
            List<String> employee_Phone) {
        EmployeeID = employeeID;
        this.firstName = firstName;
        this.middelInitial = middelInitial;
        this.lastName = lastName;
        this.email = email;
        this.city = city;
        this.salary = salary;
        ShiftTime = shiftTime;
        HireDate = hireDate;
        this.gender = gender;
        Employee_role = employee_role;
        Employee_Phone = employee_Phone;
    }

    public int getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(int employeeID) {
        EmployeeID = employeeID;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getShiftTime() {
        return ShiftTime;
    }

    public void setShiftTime(String shiftTime) {
        ShiftTime = shiftTime;
    }

    public Date getHireDate() {
        return HireDate;
    }

    public void setHireDate(Date hireDate) {
        HireDate = hireDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmployee_role() {
        return Employee_role;
    }

    public void setEmployee_role(String employee_role) {
        Employee_role = employee_role;
    }

    public List<String> getEmployee_Phone() {
        return Employee_Phone;
    }

    public void setEmployee_Phone(List<String> employee_Phone) {
        Employee_Phone = employee_Phone;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(int salesCount) {
        this.salesCount = salesCount;
    }

    public int getPurchasesCount() {
        return purchasesCount;
    }

    public void setPurchasesCount(int purchasesCount) {
        this.purchasesCount = purchasesCount;
    }

    public int getDeliveriesCount() {
        return deliveriesCount;
    }

    public void setDeliveriesCount(int deliveriesCount) {
        this.deliveriesCount = deliveriesCount;
    }

    public int getManagedWarehouses() {
        return managedWarehouses;
    }

    public void setManagedWarehouses(int managedWarehouses) {
        this.managedWarehouses = managedWarehouses;
    }

    public double getTotalSalesAmount() {
        return totalSalesAmount;
    }

    public void setTotalSalesAmount(double totalSalesAmount) {
        this.totalSalesAmount = totalSalesAmount;
    }

    public double getTotalPurchaseAmount() {
        return totalPurchaseAmount;
    }

    public void setTotalPurchaseAmount(double totalPurchaseAmount) {
        this.totalPurchaseAmount = totalPurchaseAmount;
    }

    public String getFullName() {
        String middle = (middelInitial == null || middelInitial.trim().isEmpty())
                ? ""
                : " " + middelInitial.trim() + ".";

        return firstName + middle + " " + lastName;
    }

    @Override
    public String toString() {
        return "Employee [EmployeeID=" + EmployeeID + ", firstName=" + firstName + ", middelInitial=" + middelInitial
                + ", lastName=" + lastName + ", email=" + email + ", city=" + city + ", salary=" + salary
                + ", ShiftTime=" + ShiftTime + ", HireDate=" + HireDate + ", gender=" + gender + ", Employee_role="
                + Employee_role + ", Employee_Phone=" + Employee_Phone + "]";
    }

}
