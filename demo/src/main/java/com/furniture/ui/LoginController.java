package com.furniture.ui;

import com.furniture.dao.LoginDAO;
import com.furniture.model.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button guestButton;

    @FXML
    private Button backBtn;

    @FXML
    private VBox signupCard;

    @FXML
    private HBox authContainer;

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField signupPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button signupButton;

    private final LoginDAO loginDAO = new LoginDAO();

    @FXML
    private void initialize() {

        loginButton.setOnAction(e -> handleLogin());
        guestButton.setOnAction(e -> openGuestPage());
    }

    private void handleLogin() {
        String email = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Missing Data", "Please enter email and password.");
            return;
        }

        Account account = loginDAO.login(email, password);

        if (account == null) {

            if (!loginDAO.emailExists(email)) {
                showAlert("Account Not Found", "This email does not have an account.");
            } else {
                showAlert("Wrong Password", "The password is incorrect.");
            }

            return;
        }

        Session.login(account);

        switch (account.getRole()) {

            case "ADMIN" ->
                openAdminPage();

            case "EMPLOYEE" -> {

                switch (account.getEmployeeRole()) {

                    case "sales_person" ->
                        openPage("/view/SalesEmployeeDashboardView.fxml");

                    case "warehouse_manager" ->
                        openPage("/view/WarehouseManagerView.fxml");

                    case "Delivery Employee" ->
                        openPage("/view/DeliveryEmployeeView.fxml");

                    default ->
                        showAlert("Error",
                                "Unknown employee role: "
                                        + account.getEmployeeRole());
                }
            }

            case "Customer" ->
                openPage("/view/GuestView.fxml");

            default ->
                showAlert("Error", "Unknown account role.");
        }
    }

    private void openAdminPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainLayout.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Page Error", "Cannot open Admin Dashboard.");
        }
    }

    private void openGuestPage() {
        Session.logout();
        openPage("/view/GuestView.fxml");
    }

    private void openPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Page Error", "Cannot open page: " + fxmlPath);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goBack() {
        Stage stage = (Stage) backBtn.getScene().getWindow();

        if (stage.getUserData() instanceof Scene previousScene) {
            stage.setScene(previousScene);
        } else {
            openGuestPage();
        }
    }

    @FXML
    private void showSignupPanel() {
        signupCard.setVisible(true);
        signupCard.setManaged(true);
    }

    @FXML
    private void hideSignupPanel() {
        signupCard.setVisible(false);
        signupCard.setManaged(false);
    }

    @FXML
    private void handleSignup() {

        if (!validateSignup()) {
            return;
        }

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = signupPasswordField.getText().trim();

        if (loginDAO.emailExists(email)) {
            showAlert("Email Exists", "This email already has an account.");
            return;
        }

        boolean success = loginDAO.createGuestCustomerAccount(
                firstName,
                lastName,
                email,
                phone,
                password);

        if (success) {
            showInfo("Success", "Account created successfully.");

            usernameField.setText(email);
            passwordField.clear();

            clearSignupFields();
            hideSignupPanel();

        } else {
            showAlert("Signup Failed", "Could not create account.");
        }
    }

    private boolean validateSignup() {

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = signupPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()
                || email.isEmpty() || phone.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {

            showAlert("Missing Data", "Please fill all signup fields.");
            return false;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showAlert("Invalid Email", "Please enter a valid email address.");
            return false;
        }

        if (!phone.matches("[0-9]{8,15}")) {
            showAlert("Invalid Phone", "Phone number must contain only numbers.");
            return false;
        }

        if (password.length() < 4) {
            showAlert("Weak Password", "Password must be at least 4 characters.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Password Error", "Passwords do not match.");
            return false;
        }

        return true;
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearSignupFields() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        signupPasswordField.clear();
        confirmPasswordField.clear();
    }

    @FXML
    private void handleForgotPassword() {
        showInfo("Forgot Password", "Please contact the administrator to reset your password.");
    }
}