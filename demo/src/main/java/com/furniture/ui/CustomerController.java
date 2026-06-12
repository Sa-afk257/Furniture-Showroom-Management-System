package com.furniture.ui;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import com.furniture.dao.CustomerDAO;
import com.furniture.model.Customer;
import com.furniture.dao.CustomerDAO.CustomerStats;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.poi.ss.usermodel.*;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import javafx.scene.control.TableCell;

public class CustomerController {

    private CustomerDAO customerDAO = new CustomerDAO();

    @FXML
    private Label totalCustomersLabel;
    @FXML
    private Label totalRevenueLabel;
    @FXML
    private Label repeatCustomersLabel;
    @FXML
    private Label pendingDeliveriesLabel;
    @FXML
    private Label outstandingBalanceLabel;

    @FXML
    private ComboBox<String> cityCombo;
    @FXML
    private ComboBox<String> customerTypeCombo;
    @FXML
    private TextField minSpentField;
    @FXML
    private TextField maxSpentField;
    @FXML
    private ComboBox<String> ordersCountCombo;
    @FXML
    private ComboBox<String> balanceStatusCombo;
    @FXML
    private DatePicker fromPurchaseDatePicker;
    @FXML
    private DatePicker toPurchaseDatePicker;
    @FXML
    private ComboBox<String> hasReturnsCombo;

    @FXML
    private Button applyFiltersBtn;
    @FXML
    private Button resetFiltersBtn;
    @FXML
    private Label customersFoundLabel;

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, Integer> colNo;
    @FXML
    private TableColumn<Customer, Integer> colCustomerID;
    @FXML
    private TableColumn<Customer, String> colFullName;
    @FXML
    private TableColumn<Customer, String> colPhone;
    @FXML
    private TableColumn<Customer, String> colCity;
    @FXML
    private TableColumn<Customer, Integer> colTotalOrders;
    @FXML
    private TableColumn<Customer, Double> colTotalSpent;
    @FXML
    private TableColumn<Customer, LocalDateTime> colLastPurchase;
    @FXML
    private TableColumn<Customer, Double> colBalance;
    @FXML
    private TableColumn<Customer, String> colCustomerType;
    @FXML
    private TableColumn<Customer, Void> colAction;

    @FXML
    private VBox filterPanel;
    @FXML
    private VBox addCustomerPanel;
    @FXML
    private VBox customerDetailsPanel;

    @FXML
    private Button filterToggleBtn;
    @FXML
    private Button addCustomerBtn;
    @FXML
    private Button saveCustomerBtn;
    @FXML
    private Button cancelAddBtn;
    @FXML
    private Button closeDetailsBtn;

    @FXML
    private TextField addFirstNameField;
    @FXML
    private TextField addMiddleInitialField;
    @FXML
    private TextField addLastNameField;
    @FXML
    private TextField addPhoneField;
    @FXML
    private TextField addCityField;
    @FXML
    private TextField addTownField;
    @FXML
    private TextField addAreaField;
    @FXML
    private TextField addStreetField;
    @FXML
    private TextField addBuildingField;
    @FXML
    private DatePicker registrationDatePicker;

    @FXML
    private Label firstNameWarningLabel;
    @FXML
    private Label lastNameWarningLabel;
    @FXML
    private Label phoneWarningLabel;
    @FXML
    private Label cityWarningLabel;
    @FXML
    private Label registrationDateWarningLabel;
    @FXML
    private Label formTitleLabel;

    @FXML
    private Label detailsNameLabel;
    @FXML
    private Label detailsTypeBadgeLabel;
    @FXML
    private Label detailsPhoneLabel;
    @FXML
    private Label detailsAddressLabel;
    @FXML
    private Label detailsOrdersLabel;
    @FXML
    private Label detailsSpentLabel;
    @FXML
    private Label detailsPaidLabel;
    @FXML
    private Label detailsBalanceLabel;
    @FXML
    private Label detailsLastPurchaseLabel;
    @FXML
    private Label detailsTypeLabel;
    @FXML
    private Label detailsRegistrationDateLabel;
    @FXML
    private Label detailsHasReturnsLabel;

    @FXML
    private Button undoBtn;
    @FXML
    private Button redoBtn;
    @FXML
    private Button resetBtn;
    @FXML
    private Button exportBtn;
    @FXML
    private Button saveAllBtn;
    @FXML
    private TextField searchField;
    @FXML
    private Label detailsIdLabel;

    @FXML
    private Label detailsMiddleInitialLabel;

    @FXML
    private Label detailsCityLabel;

    @FXML
    private Label detailsTownLabel;

    @FXML
    private Label detailsAreaLabel;

    @FXML
    private Label detailsStreetLabel;

    @FXML
    private Label detailsBuildingLabel;

    @FXML
    private VBox phonesContainer;

    private List<Customer> originalCustomers = new ArrayList<>();
    private Stack<List<Customer>> undoStack = new Stack<>();
    private Stack<List<Customer>> redoStack = new Stack<>();

    private List<Customer> pendingAdds = new ArrayList<>();
    private List<Customer> pendingUpdates = new ArrayList<>();
    private List<Customer> pendingDeletes = new ArrayList<>();

    private Customer customerBeingUpdated = null;
    private boolean clearingForm = false;
    private Customer selectedCustomerForDetails = null;

    @FXML
    private void initialize() {

        setupCustomerTable();
        loadCustomersTable();
        loadCustomerStats();
        loadCustomerFilters();

        setupSearch();
        setupAddCustomerButtons();
        setupSaveCustomer();
        setupLiveValidation();
        setupUndoRedoReset();

        applyFiltersBtn.setOnAction(e -> applyFilters());
        resetFiltersBtn.setOnAction(e -> resetFilters());

        filterToggleBtn.setOnAction(e -> {
            if (filterPanel.isVisible()) {
                filterPanel.setVisible(false);
                filterPanel.setManaged(false);
            } else {
                showOnlyPanel(filterPanel);
            }
        });

        addCustomerBtn.setOnAction(e -> {
            if (addCustomerPanel.isVisible() && customerBeingUpdated == null) {
                addCustomerPanel.setVisible(false);
                addCustomerPanel.setManaged(false);
            } else {
                openAddForm();
            }
        });

        closeDetailsBtn.setOnAction(e -> {
            customerDetailsPanel.setVisible(false);
            customerDetailsPanel.setManaged(false);
        });

        exportBtn.setOnAction(e -> exportCustomersToExcel());
        saveAllBtn.setOnAction(e -> saveAllChanges());
    }

    private void setupCustomerTable() {

        colNo.setCellValueFactory(new PropertyValueFactory<>("no"));
        colCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("primaryPhone"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colTotalOrders.setCellValueFactory(new PropertyValueFactory<>("totalOrders"));
        colTotalSpent.setCellValueFactory(new PropertyValueFactory<>("totalSpent"));
        colTotalSpent.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText("$0");
                } else {
                    setText(String.format("$%,.0f", item));
                }
            }
        });

        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        colBalance.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText("$0");
                } else {
                    setText(String.format("$%,.0f", item));
                }
            }
        });
        colCustomerType.setCellValueFactory(new PropertyValueFactory<>("customerType"));
        colLastPurchase.setCellValueFactory(new PropertyValueFactory<>("lastPurchase"));
        colLastPurchase.setCellFactory(column -> new TableCell<>() {

            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            protected void updateItem(
                    LocalDateTime item,
                    boolean empty) {

                super.updateItem(item, empty);

                if (empty) {
                    setText("");
                } else if (item == null) {
                    setText("-");
                } else {
                    setText(item.format(formatter));
                }
            }
        });

        setupActionColumn();
    }

    private void setupActionColumn() {

        colAction.setCellFactory(column -> new javafx.scene.control.TableCell<>() {

            private final Button viewBtn = new Button("👁");
            private final Button editBtn = new Button("✎");
            private final Button deleteBtn = new Button("🗑");

            private final HBox box = new HBox(8, viewBtn, editBtn, deleteBtn);

            {
                box.setAlignment(javafx.geometry.Pos.CENTER);

                viewBtn.getStyleClass().add("table-show-btn");
                editBtn.getStyleClass().add("table-edit-btn");
                deleteBtn.getStyleClass().add("table-delete-btn");

                viewBtn.setOnAction(e -> {
                    Customer customer = getTableView().getItems().get(getIndex());

                    openCustomerDetails(customer);
                });

                deleteBtn.setOnAction(e -> {

                    Customer customer = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Customer");
                    alert.setHeaderText(null);
                    alert.setContentText(
                            "Are you sure you want to delete "
                                    + customer.getFullName() + "?");

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isEmpty() || result.get() != ButtonType.OK) {
                        return;
                    }

                    saveStateForUndo();
                    if (customerDAO.customerHasSales(customer.getCustomerID())) {

                        alert = new Alert(Alert.AlertType.WARNING);

                        alert.setTitle("Delete Not Allowed");
                        alert.setHeaderText("Customer Has Sales");

                        alert.setContentText(
                                "This customer cannot be deleted because sales records exist.");

                        alert.showAndWait();
                        return;
                    }

                    pendingDeletes.add(customer);
                    customerTable.getItems().remove(customer);
                    pendingDeletes.add(customer);
                    customerTable.getItems().remove(customer);

                    refreshRowNumbers();

                    redoStack.clear();
                });

                editBtn.setOnAction(e -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    openUpdateForm(customer);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });
    }

    private void openCustomerDetails(Customer customer) {

        selectedCustomerForDetails = customer;

        detailsNameLabel.setText(customer.getFullName());
        detailsTypeBadgeLabel.setText(customer.getCustomerType());

        phonesContainer.getChildren().clear();

        for (String phone : customer.getCustomer_phone()) {

            Label phoneLabel = new Label(phone);

            phoneLabel.getStyleClass().add("details-text");

            phonesContainer.getChildren().add(phoneLabel);
        }
        detailsAddressLabel.setText(customer.getFullAddress());

        detailsOrdersLabel.setText(String.valueOf(customer.getTotalOrders()));
        detailsSpentLabel.setText(String.format("$%,.0f", customer.getTotalSpent()));
        detailsPaidLabel.setText(String.format("$%,.0f", customer.getPaidAmount()));
        detailsBalanceLabel.setText(String.format("$%,.0f", customer.getBalance()));

        if (customer.getLastPurchase() == null) {
            detailsLastPurchaseLabel.setText("-");
        } else {
            detailsLastPurchaseLabel.setText(
                    customer.getLastPurchase()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        detailsTypeLabel.setText(customer.getCustomerType());

        if (customer.getRegistrationDate() == null) {
            detailsRegistrationDateLabel.setText("-");
        } else {
            detailsRegistrationDateLabel.setText(
                    customer.getRegistrationDate()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        detailsHasReturnsLabel.setText(
                customer.getReturnsCount() > 0 ? "Yes" : "No");

        detailsIdLabel.setText(String.valueOf(customer.getCustomerID()));

        detailsMiddleInitialLabel.setText(
                emptyToDash(customer.getMiddelInitial()));

        detailsCityLabel.setText(emptyToDash(customer.getCity()));
        detailsTownLabel.setText(emptyToDash(customer.getTown()));
        detailsAreaLabel.setText(emptyToDash(customer.getArea()));
        detailsStreetLabel.setText(emptyToDash(customer.getStreet()));
        detailsBuildingLabel.setText(emptyToDash(customer.getBuilding()));

        showOnlyPanel(customerDetailsPanel);
    }

    private String emptyToDash(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        return value;
    }

    private void refreshRowNumbers() {
        for (int i = 0; i < customerTable.getItems().size(); i++) {
            customerTable.getItems().get(i).setNo(i + 1);
        }

        customerTable.refresh();
    }

    private void loadCustomersTable() {
        List<Customer> customers = customerDAO.getAllCustomersForTable();
        for (int i = 0; i < customers.size(); i++) {
            customers.get(i).setNo(i + 1);
        }
        originalCustomers.clear();
        originalCustomers.addAll(customers);
        customerTable.setItems(FXCollections.observableArrayList(customers));
    }

    private void loadCustomerStats() {

        CustomerStats stats = customerDAO.getCustomerStats();

        totalCustomersLabel.setText(String.valueOf(stats.getTotalCustomers()));
        totalRevenueLabel.setText(String.format("$%,.0f", stats.getTotalRevenue()));
        repeatCustomersLabel.setText(String.valueOf(stats.getRepeatCustomers()));
        pendingDeliveriesLabel.setText(String.valueOf(stats.getPendingDeliveries()));
        outstandingBalanceLabel.setText(String.format("$%,.0f", stats.getOutstandingBalance()));
    }

    private void setupAddCustomerButtons() {
        cancelAddBtn.setOnAction(e -> {
            clearAddForm();
            addCustomerPanel.setVisible(false);
            addCustomerPanel.setManaged(false);
        });
    }

    private void setupSaveCustomer() {
        saveCustomerBtn.setOnAction(e -> {
            if (customerBeingUpdated == null) {
                addCustomer();
            } else {
                updateCustomer();
            }
        });
    }

    private void setupSearch() {

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {

            String keyword = newValue.toLowerCase().trim();

            if (keyword.isEmpty()) {
                customerTable.setItems(FXCollections.observableArrayList(originalCustomers));
                refreshRowNumbers();
                return;
            }

            List<Customer> filtered = originalCustomers.stream()
                    .filter(c -> contains(c.getFullName(), keyword)
                            || contains(String.valueOf(c.getCustomerID()), keyword)
                            || contains(c.getPrimaryPhone(), keyword)
                            || contains(c.getCity(), keyword)
                            || contains(c.getFullAddress(), keyword)
                            || contains(c.getCustomerType(), keyword)
                            || contains(String.valueOf(c.getTotalOrders()), keyword)
                            || contains(String.valueOf(c.getTotalSpent()), keyword)
                            || contains(String.valueOf(c.getBalance()), keyword))
                    .toList();

            customerTable.setItems(FXCollections.observableArrayList(filtered));
            refreshRowNumbers();
        });
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private void openAddForm() {
        customerBeingUpdated = null;

        clearAddForm();

        registrationDatePicker.setValue(LocalDate.now());

        formTitleLabel.setText("✚ ADD CUSTOMER");
        saveCustomerBtn.setText("ADD");

        showOnlyPanel(addCustomerPanel);
    }

    private void addCustomer() {

        boolean valid = validateAddCustomerForm();

        if (!valid) {
            return;
        }

        String firstName = addFirstNameField.getText().trim();
        String middleInitial = addMiddleInitialField.getText().trim();
        String lastName = addLastNameField.getText().trim();

        String phone = addPhoneField.getText().trim();

        String city = addCityField.getText().trim();
        String town = addTownField.getText().trim();
        String area = addAreaField.getText().trim();
        String street = addStreetField.getText().trim();
        String building = addBuildingField.getText().trim();

        LocalDate selectedDate = registrationDatePicker.getValue();
        LocalDateTime registrationDate = selectedDate.atStartOfDay();

        List<String> phones = new ArrayList<>();
        phones.add(phone);

        Customer customer = new Customer(
                firstName,
                middleInitial,
                lastName,
                city,
                town,
                area,
                street,
                building,
                phones,
                registrationDate);

        saveStateForUndo();

        customer.setNo(customerTable.getItems().size() + 1);
        customer.setTotalOrders(0);
        customer.setTotalSpent(0);
        customer.setPaidAmount(0);
        customer.setBalance(0);
        customer.setCustomerType("New");
        customer.setLastPurchase(null);
        customer.setReturnsCount(0);

        pendingAdds.add(customer);

        customerTable.getItems().add(customer);

        originalCustomers.add(customer);

        customersFoundLabel.setText(
                String.valueOf(customerTable.getItems().size()));

        loadCustomerFilters();

        redoStack.clear();

        clearAddForm();
    }

    private boolean validateAddCustomerForm() {

        boolean valid = true;

        valid &= validateTextField(addFirstNameField, firstNameWarningLabel);
        valid &= validateTextField(addLastNameField, lastNameWarningLabel);
        valid &= validatePhoneField(addPhoneField, phoneWarningLabel);
        valid &= validateTextField(addCityField, cityWarningLabel);
        valid &= validateDatePicker(registrationDatePicker, registrationDateWarningLabel);

        return valid;
    }

    private boolean validatePhoneField(TextField field, Label warningLabel) {

        boolean valid = field.getText().trim().matches("\\d{9,10}");

        field.getStyleClass().remove("validation-error");

        if (!valid) {
            field.getStyleClass().add("validation-error");
        }

        warningLabel.setVisible(!valid);
        warningLabel.setManaged(!valid);

        return valid;
    }

    private boolean validateTextField(TextField field, Label warningLabel) {
        boolean valid = !field.getText().trim().isEmpty();

        field.getStyleClass().remove("validation-error");

        if (!valid) {
            field.getStyleClass().add("validation-error");
        }

        warningLabel.setVisible(!valid);
        warningLabel.setManaged(!valid);

        return valid;
    }

    private boolean validateDatePicker(DatePicker datePicker, Label warningLabel) {
        boolean valid = datePicker.getValue() != null;

        datePicker.getStyleClass().remove("validation-error");

        if (!valid) {
            datePicker.getStyleClass().add("validation-error");
        }

        warningLabel.setVisible(!valid);
        warningLabel.setManaged(!valid);

        return valid;
    }

    private void setupLiveValidation() {

        addFirstNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateTextField(addFirstNameField, firstNameWarningLabel);
            }
        });

        addLastNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateTextField(addLastNameField, lastNameWarningLabel);
            }
        });

        addPhoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validatePhoneField(addPhoneField, phoneWarningLabel);
            }
        });

        addCityField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateTextField(addCityField, cityWarningLabel);
            }
        });

        registrationDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm) {
                validateDatePicker(registrationDatePicker, registrationDateWarningLabel);
            }
        });
    }

    private void clearAddForm() {

        clearingForm = true;

        addFirstNameField.clear();
        addMiddleInitialField.clear();
        addLastNameField.clear();
        addPhoneField.clear();
        addCityField.clear();
        addTownField.clear();
        addAreaField.clear();
        addStreetField.clear();
        addBuildingField.clear();

        registrationDatePicker.setValue(LocalDate.now());

        clearingForm = false;

        hideAllWarnings();
        removeAllValidationErrors();

        customerBeingUpdated = null;
        formTitleLabel.setText("✚ ADD CUSTOMER");
        saveCustomerBtn.setText("✚  ADD");
    }

    private void hideAllWarnings() {

        Label[] warnings = {
                firstNameWarningLabel,
                lastNameWarningLabel,
                phoneWarningLabel,
                cityWarningLabel,
                registrationDateWarningLabel
        };

        for (Label warning : warnings) {
            warning.setVisible(false);
            warning.setManaged(false);
        }
    }

    private void removeAllValidationErrors() {

        addFirstNameField.getStyleClass().remove("validation-error");
        addLastNameField.getStyleClass().remove("validation-error");
        addPhoneField.getStyleClass().remove("validation-error");
        addCityField.getStyleClass().remove("validation-error");
        registrationDatePicker.getStyleClass().remove("validation-error");
    }

    private void openUpdateForm(Customer customer) {

        customerBeingUpdated = customer;

        showOnlyPanel(addCustomerPanel);

        addFirstNameField.setText(customer.getFirstName());
        addMiddleInitialField.setText(customer.getMiddelInitial());
        addLastNameField.setText(customer.getLastName());

        addPhoneField.setText(customer.getPrimaryPhone());

        addCityField.setText(customer.getCity());
        addTownField.setText(customer.getTown());
        addAreaField.setText(customer.getArea());
        addStreetField.setText(customer.getStreet());
        addBuildingField.setText(customer.getBuilding());

        if (customer.getRegistrationDate() != null) {
            registrationDatePicker.setValue(
                    customer.getRegistrationDate().toLocalDate());
        }

        formTitleLabel.setText("✏ UPDATE CUSTOMER");
        saveCustomerBtn.setText("UPDATE");
    }

    private void updateCustomer() {

        boolean valid = validateAddCustomerForm();

        if (!valid) {
            return;
        }

        saveStateForUndo();

        customerBeingUpdated.setFirstName(addFirstNameField.getText().trim());
        customerBeingUpdated.setMiddelInitial(addMiddleInitialField.getText().trim());
        customerBeingUpdated.setLastName(addLastNameField.getText().trim());

        List<String> phones = new ArrayList<>();
        phones.add(addPhoneField.getText().trim());
        customerBeingUpdated.setCustomer_phone(phones);

        customerBeingUpdated.setCity(addCityField.getText().trim());
        customerBeingUpdated.setTown(addTownField.getText().trim());
        customerBeingUpdated.setArea(addAreaField.getText().trim());
        customerBeingUpdated.setStreet(addStreetField.getText().trim());
        customerBeingUpdated.setBuilding(addBuildingField.getText().trim());

        customerBeingUpdated.setRegistrationDate(
                registrationDatePicker.getValue().atStartOfDay());

        if (!pendingUpdates.contains(customerBeingUpdated)) {
            pendingUpdates.add(customerBeingUpdated);
        }

        customerTable.refresh();

        customerBeingUpdated = null;

        clearAddForm();

        addCustomerPanel.setVisible(false);
        addCustomerPanel.setManaged(false);

        redoStack.clear();
    }

    private void saveStateForUndo() {
        undoStack.push(new ArrayList<>(customerTable.getItems()));
    }

    private void applyFilters() {

        String city = cityCombo.getValue();
        String type = customerTypeCombo.getValue();
        String ordersCount = ordersCountCombo.getValue();
        String balanceStatus = balanceStatusCombo.getValue();
        String hasReturns = hasReturnsCombo.getValue();

        double minSpent = parseDoubleOrDefault(minSpentField.getText(), 0);
        double maxSpent = parseDoubleOrDefault(maxSpentField.getText(), Double.MAX_VALUE);

        LocalDate fromDate = fromPurchaseDatePicker.getValue();
        LocalDate toDate = toPurchaseDatePicker.getValue();

        List<Customer> filtered = originalCustomers.stream()

                .filter(c -> city == null
                        || city.equals("All Cities")
                        || city.equals(c.getCity()))

                .filter(c -> type == null
                        || type.equals("All")
                        || type.equals(c.getCustomerType()))

                .filter(c -> c.getTotalSpent() >= minSpent
                        && c.getTotalSpent() <= maxSpent)

                .filter(c -> ordersCountMatches(c, ordersCount))

                .filter(c -> balanceStatusMatches(c, balanceStatus))

                .filter(c -> returnsMatches(c, hasReturns))

                .filter(c -> {

                    if (fromDate == null && toDate == null) {
                        return true;
                    }

                    if (c.getLastPurchase() == null) {
                        return false;
                    }

                    LocalDate purchaseDate = c.getLastPurchase().toLocalDate();

                    boolean afterFrom = fromDate == null || !purchaseDate.isBefore(fromDate);

                    boolean beforeTo = toDate == null || !purchaseDate.isAfter(toDate);

                    return afterFrom && beforeTo;
                })

                .toList();

        customerTable.setItems(FXCollections.observableArrayList(filtered));
        customersFoundLabel.setText(String.valueOf(filtered.size()));

        refreshRowNumbers();
    }

    private double parseDoubleOrDefault(String text, double defaultValue) {
        try {
            return Double.parseDouble(text.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private boolean ordersCountMatches(Customer c, String ordersCount) {

        if (ordersCount == null || ordersCount.equals("All")) {
            return true;
        }

        int orders = c.getTotalOrders();

        return switch (ordersCount) {
            case "0 Orders" -> orders == 0;
            case "1-5 Orders" -> orders >= 1 && orders <= 5;
            case "6-10 Orders" -> orders >= 6 && orders <= 10;
            case "10+ Orders" -> orders > 10;
            default -> true;
        };
    }

    private boolean balanceStatusMatches(Customer c, String balanceStatus) {

        if (balanceStatus == null || balanceStatus.equals("All")) {
            return true;
        }

        double balance = c.getBalance();

        return switch (balanceStatus) {
            case "No Balance" -> balance == 0;
            case "Has Balance" -> balance > 0;
            default -> true;
        };
    }

    private boolean returnsMatches(Customer c, String hasReturns) {

        if (hasReturns == null || hasReturns.equals("All")) {
            return true;
        }

        int returns = c.getReturnsCount();

        return switch (hasReturns) {
            case "Has Returns" -> returns > 0;
            case "No Returns" -> returns == 0;
            default -> true;
        };
    }

    private void resetFilters() {

        cityCombo.getSelectionModel().selectFirst();
        customerTypeCombo.getSelectionModel().selectFirst();
        ordersCountCombo.getSelectionModel().selectFirst();
        balanceStatusCombo.getSelectionModel().selectFirst();
        hasReturnsCombo.getSelectionModel().selectFirst();

        minSpentField.clear();
        maxSpentField.clear();

        fromPurchaseDatePicker.setValue(null);
        toPurchaseDatePicker.setValue(null);

        customerTable.setItems(FXCollections.observableArrayList(originalCustomers));

        customersFoundLabel.setText(
                String.valueOf(originalCustomers.size()));

        refreshRowNumbers();
    }

    private void loadCustomerFilters() {

        cityCombo.getItems().clear();
        cityCombo.getItems().add("All Cities");
        cityCombo.getItems().addAll(
                customerDAO.getCustomerCities());
        cityCombo.getSelectionModel().selectFirst();

        customerTypeCombo.getItems().setAll(
                "All",
                "VIP",
                "Regular",
                "New");

        customerTypeCombo.getSelectionModel().selectFirst();

        ordersCountCombo.getItems().setAll(
                "All",
                "0 Orders",
                "1-5 Orders",
                "6-10 Orders",
                "10+ Orders");

        ordersCountCombo.getSelectionModel().selectFirst();

        balanceStatusCombo.getItems().setAll(
                "All",
                "No Balance",
                "Has Balance");

        balanceStatusCombo.getSelectionModel().selectFirst();

        hasReturnsCombo.getItems().setAll(
                "All",
                "Has Returns",
                "No Returns");

        hasReturnsCombo.getSelectionModel().selectFirst();

        customersFoundLabel.setText(
                String.valueOf(
                        customerTable.getItems().size()));
    }

    private void showOnlyPanel(VBox panel) {
        filterPanel.setVisible(false);
        filterPanel.setManaged(false);

        addCustomerPanel.setVisible(false);
        addCustomerPanel.setManaged(false);

        customerDetailsPanel.setVisible(false);
        customerDetailsPanel.setManaged(false);

        panel.setVisible(true);
        panel.setManaged(true);
    }

    private void setupUndoRedoReset() {

        undoBtn.setOnAction(e -> undoAction());

        redoBtn.setOnAction(e -> redoAction());

        resetBtn.setOnAction(e -> resetTableChanges());
    }

    private void undoAction() {

        if (undoStack.isEmpty()) {
            return;
        }

        redoStack.push(new ArrayList<>(customerTable.getItems()));

        List<Customer> previousState = undoStack.pop();

        customerTable.setItems(
                FXCollections.observableArrayList(previousState));

        refreshRowNumbers();
    }

    private void redoAction() {

        if (redoStack.isEmpty()) {
            return;
        }

        undoStack.push(new ArrayList<>(customerTable.getItems()));

        List<Customer> nextState = redoStack.pop();

        customerTable.setItems(
                FXCollections.observableArrayList(nextState));

        refreshRowNumbers();
    }

    private void resetTableChanges() {

        pendingAdds.clear();
        pendingUpdates.clear();
        pendingDeletes.clear();

        undoStack.clear();
        redoStack.clear();

        loadCustomersTable();
        loadCustomerStats();
        loadCustomerFilters();

        clearAddForm();

        addCustomerPanel.setVisible(false);
        addCustomerPanel.setManaged(false);

        customerDetailsPanel.setVisible(false);
        customerDetailsPanel.setManaged(false);
    }

    private void saveAllChanges() {

        try {

            for (Customer customer : pendingAdds) {
                customerDAO.insertCustomer(customer);
            }

            for (Customer customer : pendingUpdates) {
                customerDAO.updateCustomer(customer);
            }

            for (Customer customer : pendingDeletes) {

                boolean deleted = customerDAO.deleteCustomer(customer.getCustomerID());

                if (!deleted) {

                    Alert alert = new Alert(Alert.AlertType.WARNING);

                    alert.setTitle("Delete Not Allowed");
                    alert.setHeaderText("Customer Has Sales");

                    alert.setContentText(
                            customer.getFullName()
                                    + " cannot be deleted because sales records exist.");

                    alert.showAndWait();
                }
            }

            pendingAdds.clear();
            pendingUpdates.clear();
            pendingDeletes.clear();

            undoStack.clear();
            redoStack.clear();

            loadCustomersTable();
            loadCustomerStats();
            loadCustomerFilters();

            clearAddForm();

            addCustomerPanel.setVisible(false);
            addCustomerPanel.setManaged(false);

            customerDetailsPanel.setVisible(false);
            customerDetailsPanel.setManaged(false);

            System.out.println("All customer changes saved successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportCustomersToExcel() {

        try {
            File exportFolder = new File("src/main/resources/exports");

            if (!exportFolder.exists()) {
                exportFolder.mkdirs();
            }

            String fileName = "customers_" + java.time.LocalDate.now() + ".xlsx";
            File file = new File(exportFolder, fileName);

            Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Customers");

            String[] headers = {
                    "Customer ID",
                    "Full Name",
                    "Phone",
                    "City",
                    "Full Address",
                    "Registration Date",
                    "Total Orders",
                    "Total Spent",
                    "Paid Amount",
                    "Balance",
                    "Last Purchase",
                    "Customer Type",
                    "Returns Count"
            };

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowIndex = 1;

            for (Customer c : customerTable.getItems()) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(c.getCustomerID());
                row.createCell(1).setCellValue(c.getFullName());
                row.createCell(2).setCellValue(c.getPrimaryPhone());
                row.createCell(3).setCellValue(c.getCity());
                row.createCell(4).setCellValue(c.getFullAddress());

                row.createCell(5).setCellValue(
                        c.getRegistrationDate() == null
                                ? ""
                                : c.getRegistrationDate().toLocalDate().toString());

                row.createCell(6).setCellValue(c.getTotalOrders());
                row.createCell(7).setCellValue(c.getTotalSpent());
                row.createCell(8).setCellValue(c.getPaidAmount());
                row.createCell(9).setCellValue(c.getBalance());

                row.createCell(10).setCellValue(
                        c.getLastPurchase() == null
                                ? ""
                                : c.getLastPurchase().toLocalDate().toString());

                row.createCell(11).setCellValue(c.getCustomerType());
                row.createCell(12).setCellValue(c.getReturnsCount());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (java.io.FileOutputStream outputStream = new java.io.FileOutputStream(file)) {
                workbook.write(outputStream);
            }

            workbook.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Success");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Customers exported successfully!\n\nSaved to:\n"
                            + file.getAbsolutePath());
            alert.showAndWait();

        } catch (Exception ex) {
            ex.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to export customers file.");
            alert.showAndWait();
        }
    }
}
