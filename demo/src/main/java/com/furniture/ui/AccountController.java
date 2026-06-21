package com.furniture.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Node;

import com.furniture.dao.AccountDAO;
import com.furniture.model.Customer;
import java.time.format.DateTimeFormatter;

public class AccountController extends BaseController {

    private final AccountDAO accountDAO = new AccountDAO();
    @FXML
    private Label accountNameLabel;

    @FXML
    private Label accountRoleLabel;

    @FXML
    private Label totalOrdersLabel;

    @FXML
    private Label wishlistItemsLabel;

    @FXML
    private Label memberSinceLabel;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField cityField;

    @FXML
    private TextField areaField;

    @FXML
    private TextField buildingField;

    @FXML
    private Button editPersonalBtn;

    @FXML
    private Button editAddressBtn;

    @FXML
    private Button supportBtn;

    @FXML
    private Label viewAllOrdersLabel;

    @FXML
    private Button backBtn;

    private boolean editingPersonal = false;
    private boolean editingAddress = false;

    @FXML
    public void initialize() {

        loadAccountData();

        if (editPersonalBtn != null) {
            editPersonalBtn.setOnAction(e -> enablePersonalEdit());
        }

        if (editAddressBtn != null) {
            editAddressBtn.setOnAction(e -> enableAddressEdit());
        }

        if (supportBtn != null) {
            supportBtn.setOnAction(e -> showInfo("Support", "Please contact us at support@abusalh.com"));
        }

        if (viewAllOrdersLabel != null) {
            viewAllOrdersLabel.setOnMouseClicked(e -> openPage((Node) e.getSource(), "/view/OrdersView.fxml"));
        }
        if (editPersonalBtn != null) {
            editPersonalBtn.setOnAction(e -> handlePersonalEdit());
        }

        if (editAddressBtn != null) {
            editAddressBtn.setOnAction(e -> handleAddressEdit());
        }
        backBtn.setOnAction(e -> {

            if (Session.isGuest() || Session.getCurrentCustomerId() > 0) {
                openAllProductsPage(backBtn);
            } else {
                openPage(backBtn, Session.getHomePageForCurrentUser());
            }
        });
    }

    private void loadAccountData() {

        if (Session.isGuest()) {

            accountNameLabel.setText("Guest");
            accountRoleLabel.setText("Customer");

            firstNameField.setText("Customer");
            lastNameField.setText("");
            emailField.setText("Not logged in");
            phoneField.setText("-");

            addressField.setText("-");
            cityField.setText("-");
            areaField.setText("-");
            buildingField.setText("-");

            totalOrdersLabel.setText("0");
            wishlistItemsLabel.setText("0");
            memberSinceLabel.setText("-");
            return;
        }

        if (Session.getCurrentCustomerId() > 0) {
            loadCustomerAccount();
            return;
        }

        if (Session.getCurrentEmployeeId() > 0) {
            loadEmployeeAccount();
            return;
        }

        showInfo("Account Error",
                "Could not identify account type.");
    }

    private void loadCustomerAccount() {

        int customerId = Session.getCurrentCustomerId();

        Customer customer = accountDAO.getCustomerById(customerId);

        if (customer == null) {
            showInfo("Account Error", "Could not load customer data.");
            return;
        }

        firstNameField.setText(customer.getFirstName());
        lastNameField.setText(customer.getLastName());
        emailField.setText(Session.getCurrentAccount().getEmail());
        phoneField.setText(customer.getPrimaryPhone());

        cityField.setText(customer.getCity());
        areaField.setText(customer.getArea());
        buildingField.setText(customer.getBuilding());

        String fullAddress = customer.getCity() + ", "
                + customer.getTown() + ", "
                + customer.getArea() + ", "
                + customer.getStreet() + ", Building "
                + customer.getBuilding();

        addressField.setText(fullAddress);

        accountNameLabel.setText(customer.getFirstName() + " " + customer.getLastName());
        accountRoleLabel.setText("Customer");

        totalOrdersLabel.setText(String.valueOf(accountDAO.getTotalOrders(customerId)));
        wishlistItemsLabel.setText(String.valueOf(accountDAO.getWishlistCount(customerId)));

        if (customer.getRegistrationDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
            memberSinceLabel.setText(customer.getRegistrationDate().format(formatter));
        } else {
            memberSinceLabel.setText("-");
        }
    }

    private void loadEmployeeAccount() {

        int employeeId = Session.getCurrentEmployeeId();

        AccountDAO.EmployeeAccount emp = accountDAO.getEmployeeAccountById(employeeId);

        if (emp == null) {
            showInfo("Account Error", "Could not load employee data.");
            return;
        }

        firstNameField.setText(emp.firstName);
        lastNameField.setText(emp.lastName);
        emailField.setText(emp.email);
        phoneField.setText("-");
        cityField.setText(emp.city);

        areaField.setText("-");
        buildingField.setText("-");
        addressField.setText(emp.city);

        accountNameLabel.setText(emp.firstName + " " + emp.lastName);
        accountRoleLabel.setText(
                emp.role.replace("_", " "));

        totalOrdersLabel.setText("-");
        wishlistItemsLabel.setText("-");
        memberSinceLabel.setText(emp.hireDate == null ? "-" : emp.hireDate);

        firstNameField.setEditable(false);
        lastNameField.setEditable(false);
        emailField.setEditable(false);
        phoneField.setEditable(false);
        addressField.setEditable(false);
        cityField.setEditable(false);
        areaField.setEditable(false);
        buildingField.setEditable(false);

        editPersonalBtn.setDisable(true);
        editAddressBtn.setDisable(true);
    }

    private String getNameFromEmail(String email) {

        if (email == null || email.isEmpty()) {
            return "Customer";
        }

        int atIndex = email.indexOf("@");

        if (atIndex <= 0) {
            return email;
        }

        String name = email.substring(0, atIndex);

        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private void enablePersonalEdit() {

        firstNameField.setEditable(true);
        lastNameField.setEditable(true);
        phoneField.setEditable(true);

        showInfo("Edit Mode", "You can now edit your personal information.");
    }

    private void enableAddressEdit() {

        addressField.setEditable(true);
        cityField.setEditable(true);
        areaField.setEditable(true);
        buildingField.setEditable(true);

        showInfo("Edit Mode", "You can now edit your address information.");
    }

    private void showInfo(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handlePersonalEdit() {

        if (!editingPersonal) {

            editingPersonal = true;

            firstNameField.setEditable(true);
            lastNameField.setEditable(true);
            phoneField.setEditable(true);

            editPersonalBtn.setText("Save");

            return;
        }

        boolean success = accountDAO.updatePersonalInfo(
                Session.getCurrentCustomerId(),
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                phoneField.getText().trim());

        if (success) {

            firstNameField.setEditable(false);
            lastNameField.setEditable(false);
            phoneField.setEditable(false);

            editPersonalBtn.setText("✎ Edit");
            editingPersonal = false;

            accountNameLabel.setText(
                    firstNameField.getText().trim() + " " + lastNameField.getText().trim());

            showInfo("Success", "Personal information updated successfully.");

        } else {
            showInfo("Error", "Could not update personal information.");
        }
    }

    private void handleAddressEdit() {

        if (!editingAddress) {

            editingAddress = true;

            cityField.setEditable(true);
            areaField.setEditable(true);
            buildingField.setEditable(true);

            editAddressBtn.setText("Save");

            return;
        }

        boolean success = accountDAO.updateAddressInfo(
                Session.getCurrentCustomerId(),
                cityField.getText().trim(),
                areaField.getText().trim(),
                buildingField.getText().trim());

        if (success) {

            cityField.setEditable(false);
            areaField.setEditable(false);
            buildingField.setEditable(false);

            editAddressBtn.setText("✎ Edit");
            editingAddress = false;

            addressField.setText(
                    cityField.getText().trim() + ", " +
                            areaField.getText().trim() + ", Building " +
                            buildingField.getText().trim());

            showInfo("Success", "Address information updated successfully.");

        } else {
            showInfo("Error", "Could not update address information.");
        }
    }

}