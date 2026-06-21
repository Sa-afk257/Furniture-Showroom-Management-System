package com.furniture.ui;

import com.furniture.model.Account;

public class Session {

    private static Account currentAccount;
    private static Integer testEmployeeId = null;

    public static void login(Account account) {
        currentAccount = account;
    }

    public static void logout() {
        currentAccount = null;
        testEmployeeId = null;
    }

    public static void clearTestEmployeeId() {
        testEmployeeId = null;
    }

    public static Account getCurrentAccount() {
        return currentAccount;
    }

    public static boolean isLoggedIn() {
        return currentAccount != null;
    }

    public static boolean isGuest() {
        return currentAccount == null;
    }

    public static int getCurrentCustomerId() {

        if (currentAccount == null || currentAccount.getCustomerId() == null) {
            return -1;
        }

        return currentAccount.getCustomerId();
    }

    public static int getCurrentEmployeeId() {

        if (testEmployeeId != null) {
            return testEmployeeId;
        }

        if (currentAccount == null || currentAccount.getEmployeeId() == null) {
            return -1;
        }

        return currentAccount.getEmployeeId();
    }

    public static String getCurrentUserEmail() {

        if (currentAccount == null) {
            return "";
        }

        return currentAccount.getEmail();
    }

    public static void setCurrentEmployeeId(int employeeId) {
        testEmployeeId = employeeId;
    }

    public static String getHomePageForCurrentUser() {

        if (currentAccount == null) {
            return "/view/GuestView.fxml";
        }

        if (currentAccount.getCustomerId() != null) {
            return "/view/GuestView.fxml";
        }

        if (currentAccount.getEmployeeId() != null) {

            String role = currentAccount.getEmployeeRole();

            if (role == null || role.isBlank()) {
                role = currentAccount.getRole();
            }

            if (role == null) {
                return "/view/DashboardView.fxml";
            }

            role = role.trim().toLowerCase();

            switch (role) {
                case "admin":
                    return "/view/MainLayoutView.fxml";

                case "sales_person":
                case "sales person":
                    return "/view/SalesEmployeeDashboardView.fxml";

                case "warehouse_manager":
                case "warehouse manager":
                    return "/view/WarehouseManagerView.fxml";

                case "delivery employee":
                case "delivery_employee":
                    return "/view/DeliveryEmployeeView.fxml";

                default:
                    System.out.println("Unknown home role = [" + role + "]");
                    return "/view/DashboardView.fxml";
            }
        }

        return "/view/AllProductsView.fxml";
    }
}