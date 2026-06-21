package com.furniture.model;

import java.util.ArrayList;
import java.util.List;

public class OrderDetails {

    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;

    private List<OrderItem> items = new ArrayList<>();

    public OrderDetails(String customerName,
                        String customerPhone,
                        String customerEmail,
                        String customerAddress) {

        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.customerAddress = customerAddress;
    }

    public OrderDetails(String customerName,
                        String customerPhone,
                        String customerEmail,
                        String customerAddress,
                        List<OrderItem> items) {

        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.customerAddress = customerAddress;

        if (items != null) {
            this.items = items;
        }
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}