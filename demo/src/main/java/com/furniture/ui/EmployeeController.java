package com.furniture.ui;


import com.furniture.model.PurchaseTransaction;
import com.furniture.model.Sale;
import com.furniture.model.Warehouse;
import com.furniture.model.Employee;
import com.furniture.dao.EmployeeDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class EmployeeController {

    /* ===================== DAO ===================== */
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    /* ===================== DATA ===================== */
    private final List<Employee> originalEmployee = new ArrayList<>();

    private final Stack<List<Employee>> undoStack = new Stack<>();
    private final Stack<List<Employee>> redoStack = new Stack<>();

    private final List<Employee> pendingAdds = new ArrayList<>();
    private final List<Employee> pendingUpdates = new ArrayList<>();
    private final List<Employee> pendingDeletes = new ArrayList<>();

    private Employee employeeBeingUpdated = null;

    /* ===================== STATS CARDS ===================== */
    @FXML
    private Label topSalesEmployeeNameLabel;
    @FXML
    private Label topSalesEmployeeValueLabel;

    @FXML
    private Label topPurchaseEmployeeNameLabel;
    @FXML
    private Label topPurchaseEmployeeValueLabel;

    @FXML
    private Label topDeliveryEmployeeNameLabel;
    @FXML
    private Label topDeliveryEmployeeValueLabel;

    @FXML
    private Label totalEmployeesLabel;

    @FXML
    private Label activeEmployeesLabel;

    /* ===================== TOOLBAR ===================== */
    @FXML
    private TextField txtSearch;

    @FXML
    private Button filterToggleBtn;
    @FXML
    private Button addEmployeeBtn;
    @FXML
    private Button exportBtn;

    /* ===================== MAIN TABLE ===================== */
    @FXML
    private TableView<Employee> EmployeeTable;

    @FXML
    private TableColumn<Employee, Integer> colNo;

    @FXML
    private TableColumn<Employee, Integer> colEmployeeID;

    @FXML
    private TableColumn<Employee, String> colFullName;

    @FXML
    private TableColumn<Employee, String> colRole;

    @FXML
    private TableColumn<Employee, String> colShiftTime;

    @FXML
    private TableColumn<Employee, String> colCity;

    @FXML
    private TableColumn<Employee, Double> colSalary;

    @FXML
    private TableColumn<Employee, String> colStatus;

    @FXML
    private TableColumn<Employee, Void> colAction;

    /* ===================== FILTER PANEL ===================== */
    @FXML
    private VBox filterPanel;

    @FXML
    private ComboBox<String> roleFilterCombo;

    @FXML
    private ComboBox<String> genderFilterCombo;

    @FXML
    private ComboBox<String> shiftTimeFilterCombo;

    @FXML
    private ComboBox<String> cityFilterCombo;

    @FXML
    private ComboBox<String> statusFilterCombo;

    @FXML
    private TextField minSalaryField;

    @FXML
    private TextField maxSalaryField;

    @FXML
    private DatePicker fromHireDatePicker;

    @FXML
    private DatePicker toHireDatePicker;

    @FXML
    private ComboBox<String> performanceFilterCombo;

    @FXML
    private Button applyFiltersBtn;

    @FXML
    private Button resetFiltersBtn;

    @FXML
    private Label recordsFoundLabel;

    /* ===================== ADD / UPDATE PANEL ===================== */

    @FXML
    private VBox addEmployeePanel;

    @FXML
    private Label employeeFormTitleLabel;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField middleInitialField;

    @FXML
    private TextField lastNameField;

    @FXML
    private ComboBox<String> genderCombo;

    @FXML
    private ComboBox<String> roleCombo;

    @FXML
    private ComboBox<String> shiftTimeCombo;

    @FXML
    private DatePicker hireDatePicker;

    @FXML
    private TextField salaryField;

    @FXML
    private TextField cityField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private Button saveEmployeeBtn;

    @FXML
    private Button cancelAddBtn;

    /* ===================== DETAILS PANEL ===================== */

    @FXML
    private VBox employeeDetailsPanel;

    @FXML
    private Label detailsEmployeeNameLabel;

    @FXML
    private Label detailsRoleBadgeLabel;

    @FXML
    private Label detailsEmployeeIdLabel;

    @FXML
    private Label detailsGenderLabel;

    @FXML
    private Label detailsHireDateLabel;

    @FXML
    private Label detailsShiftTimeLabel;

    @FXML
    private Label detailsSalaryLabel;

    @FXML
    private Label detailsCityLabel;

    @FXML
    private Label detailsEmailLabel;

    @FXML
    private Label detailsPhoneLabel;

    /* ===== PERFORMANCE SUMMARY ===== */

    @FXML
    private Label detailsSalesCountLabel;

    @FXML
    private Label detailsPurchasesCountLabel;

    @FXML
    private Label detailsDeliveriesCountLabel;

    @FXML
    private Label detailsManagedWarehousesLabel;

    @FXML
    private Label detailsTotalSalesAmountLabel;

    @FXML
    private Label detailsTotalPurchaseAmountLabel;

    @FXML
    private Button closeDetailsBtn;

    /* ===================== RELATED TABLES ===================== */

    @FXML
    private TableView<Sale> employeeSalesTable;

    @FXML
    private TableColumn<Sale, Integer> colEmpSaleId;

    @FXML
    private TableColumn<Sale, LocalDate> colEmpSaleDate;

    @FXML
    private TableColumn<Sale, Double> colEmpSaleAmount;

    @FXML
    private TableView<PurchaseTransaction> employeePurchasesTable;

    @FXML
    private TableColumn<PurchaseTransaction, Integer> colEmpPurchaseId;

    @FXML
    private TableColumn<PurchaseTransaction, LocalDate> colEmpPurchaseDate;

    @FXML
    private TableColumn<PurchaseTransaction, Double> colEmpPurchaseAmount;

    @FXML
    private TableView<Warehouse> employeeWarehousesTable;

    @FXML
    private TableColumn<Warehouse, Integer> colEmpWarehouseId;

    @FXML
    private TableColumn<Warehouse, String> colEmpWarehouseName;

    @FXML
    private TableColumn<Warehouse, Integer> colEmpWarehouseCapacity;

    /* ===================== TABLE ACTIONS ===================== */

    @FXML
    private Button undoBtn;

    @FXML
    private Button redoBtn;

    @FXML
    private Button resetBtn;

    @FXML
    private Button saveAllBtn;

    /* ===================== WARNING LABELS ===================== */

    @FXML
    private Label firstNameWarningLabel;

    @FXML
    private Label lastNameWarningLabel;

    @FXML
    private Label genderWarningLabel;

    @FXML
    private Label roleWarningLabel;

    @FXML
    private Label shiftTimeWarningLabel;

    @FXML
    private Label hireDateWarningLabel;

    @FXML
    private Label salaryWarningLabel;

    @FXML
    private Label cityWarningLabel;

    @FXML
    private Label emailWarningLabel;

    @FXML
    private Label phoneWarningLabel;

    private boolean clearingForm = false;

    private ObservableList<Employee> originalEmployees = FXCollections.observableArrayList();

    @FXML
    private void initialize() {

        setupTable();

        setupActionColumn();

        setupDetailsTables();

        EmployeeTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        loadEmployees();

        loadStats();

        loadCombos();

        setupLiveValidation();

        setupButtons();

        setupSearch();

        setupFilters();

        closeAllPanels();
    }

    private void setupTable() {

        colNo.setCellValueFactory(
                new PropertyValueFactory<>("no"));

        colEmployeeID.setCellValueFactory(
                new PropertyValueFactory<>("employeeID"));

        colFullName.setCellValueFactory(
                new PropertyValueFactory<>("fullName"));

        colRole.setCellValueFactory(
                new PropertyValueFactory<>("employee_role"));

        colShiftTime.setCellValueFactory(
                new PropertyValueFactory<>("shiftTime"));

        colCity.setCellValueFactory(
                new PropertyValueFactory<>("city"));

        colSalary.setCellValueFactory(
                new PropertyValueFactory<>("salary"));

        colSalary.setCellFactory(column -> new TableCell<>() {

            @Override
            protected void updateItem(Double item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%,.2f", item));
                }
            }
        });

        colStatus.setCellValueFactory(
                new PropertyValueFactory<>("status"));

        colStatus.setCellFactory(column -> new TableCell<>() {

            @Override
            protected void updateItem(String item, boolean empty) {

                super.updateItem(item, empty);

                getStyleClass().removeAll(
                        "status-active",
                        "status-inactive");

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);

                    switch (item) {
                        case "Active" ->
                            getStyleClass().add("status-active");

                        case "Inactive" ->
                            getStyleClass().add("status-inactive");
                    }
                }
            }
        });
    }

    private void saveStateForUndo() {

        undoStack.push(new ArrayList<>(EmployeeTable.getItems()));
    }

    private void undoAction() {

        if (undoStack.isEmpty()) {
            return;
        }

        redoStack.push(new ArrayList<>(EmployeeTable.getItems()));

        List<Employee> previousState = undoStack.pop();

        EmployeeTable.setItems(FXCollections.observableArrayList(previousState));

        recordsFoundLabel.setText(String.valueOf(previousState.size()));

        refreshRowNumbers();
    }

    private void redoAction() {

        if (redoStack.isEmpty()) {
            return;
        }

        undoStack.push(new ArrayList<>(EmployeeTable.getItems()));

        List<Employee> nextState = redoStack.pop();

        EmployeeTable.setItems(FXCollections.observableArrayList(nextState));

        recordsFoundLabel.setText(String.valueOf(nextState.size()));

        refreshRowNumbers();
    }

    private void resetTableChanges() {

        pendingAdds.clear();

        pendingUpdates.clear();

        pendingDeletes.clear();

        undoStack.clear();

        redoStack.clear();

        loadEmployees();

        loadStats();

        clearForm();

        closeAllPanels();
    }

    private void openUpdateForm(Employee employee) {

        employeeBeingUpdated = employee;

        employeeFormTitleLabel.setText("✏ UPDATE EMPLOYEE");
        saveEmployeeBtn.setText("UPDATE EMPLOYEE");

        firstNameField.setText(employee.getFirstName());
        middleInitialField.setText(employee.getMiddelInitial());
        lastNameField.setText(employee.getLastName());

        genderCombo.setValue(employee.getGender());
        roleCombo.setValue(employee.getEmployee_role());
        shiftTimeCombo.setValue(employee.getShiftTime());

        if (employee.getHireDate() != null) {
            hireDatePicker.setValue(employee.getHireDate().toLocalDate());
        } else {
            hireDatePicker.setValue(null);
        }

        salaryField.setText(String.valueOf(employee.getSalary()));

        cityField.setText(employee.getCity());

        emailField.setText(employee.getEmail());
        if (employee.getEmployee_Phone() != null
                && !employee.getEmployee_Phone().isEmpty()) {

            phoneField.setText(
                    employee.getEmployee_Phone().get(0));
        }

        showOnlyPanel(addEmployeePanel);
    }

    private boolean validateForm() {

        boolean valid = true;

        valid &= validateTextField(firstNameField, firstNameWarningLabel);
        valid &= validateTextField(lastNameField, lastNameWarningLabel);

        valid &= validateComboBox(genderCombo, genderWarningLabel);
        valid &= validateComboBox(roleCombo, roleWarningLabel);
        valid &= validateComboBox(shiftTimeCombo, shiftTimeWarningLabel);

        valid &= validateDatePicker(hireDatePicker, hireDateWarningLabel);
        valid &= validateNumberField(salaryField, salaryWarningLabel);

        valid &= validateTextField(cityField, cityWarningLabel);
        valid &= validateEmailField(emailField, emailWarningLabel);
        valid &= validateTextField(phoneField, phoneWarningLabel);

        return valid;
    }

    private boolean validateTextField(TextField field, Label warningLabel) {

        boolean valid = field.getText() != null && !field.getText().trim().isEmpty();

        field.getStyleClass().remove("validation-error");

        if (!valid) {
            field.getStyleClass().add("validation-error");
        }

        warningLabel.setVisible(!valid);
        warningLabel.setManaged(!valid);

        return valid;
    }

    private boolean validateComboBox(ComboBox<?> comboBox, Label warningLabel) {

        boolean valid = comboBox.getValue() != null;

        comboBox.getStyleClass().remove("validation-error");

        if (!valid) {
            comboBox.getStyleClass().add("validation-error");
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

    private boolean validateNumberField(TextField field, Label warningLabel) {

        boolean valid = true;

        try {
            double value = Double.parseDouble(field.getText().trim());
            valid = value > 0;
        } catch (Exception e) {
            valid = false;
        }

        field.getStyleClass().remove("validation-error");

        if (!valid) {
            field.getStyleClass().add("validation-error");
        }

        warningLabel.setVisible(!valid);
        warningLabel.setManaged(!valid);

        return valid;
    }

    private boolean validateEmailField(TextField field, Label warningLabel) {

        String email = field.getText() == null ? "" : field.getText().trim();

        boolean valid = email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

        field.getStyleClass().remove("validation-error");

        if (!valid) {
            field.getStyleClass().add("validation-error");
        }

        warningLabel.setVisible(!valid);
        warningLabel.setManaged(!valid);

        return valid;
    }

    private void hideAllWarnings() {

        Label[] warnings = {
                firstNameWarningLabel,
                lastNameWarningLabel,
                genderWarningLabel,
                roleWarningLabel,
                shiftTimeWarningLabel,
                hireDateWarningLabel,
                salaryWarningLabel,
                cityWarningLabel,
                emailWarningLabel,
                phoneWarningLabel
        };

        for (Label warning : warnings) {
            warning.setVisible(false);
            warning.setManaged(false);
        }
    }

    private void removeAllValidationErrors() {

        firstNameField.getStyleClass().remove("validation-error");
        lastNameField.getStyleClass().remove("validation-error");

        genderCombo.getStyleClass().remove("validation-error");
        roleCombo.getStyleClass().remove("validation-error");
        shiftTimeCombo.getStyleClass().remove("validation-error");

        hireDatePicker.getStyleClass().remove("validation-error");
        salaryField.getStyleClass().remove("validation-error");

        cityField.getStyleClass().remove("validation-error");

        emailField.getStyleClass().remove("validation-error");
        phoneField.getStyleClass().remove("validation-error");
    }

    private void clearForm() {

        clearingForm = true;

        firstNameField.clear();
        middleInitialField.clear();
        lastNameField.clear();

        genderCombo.setValue(null);
        roleCombo.setValue(null);
        shiftTimeCombo.setValue(null);

        hireDatePicker.setValue(LocalDate.now());
        salaryField.clear();

        cityField.clear();

        emailField.clear();
        phoneField.clear();

        employeeBeingUpdated = null;

        employeeFormTitleLabel.setText("✚ ADD EMPLOYEE");
        saveEmployeeBtn.setText("✚ SAVE EMPLOYEE");

        hideAllWarnings();
        removeAllValidationErrors();

        clearingForm = false;
    }

    private void setupLiveValidation() {

        firstNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateTextField(firstNameField, firstNameWarningLabel);
        });

        lastNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateTextField(lastNameField, lastNameWarningLabel);
        });

        genderCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateComboBox(genderCombo, genderWarningLabel);
        });

        roleCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateComboBox(roleCombo, roleWarningLabel);
        });

        shiftTimeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateComboBox(shiftTimeCombo, shiftTimeWarningLabel);
        });

        hireDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateDatePicker(hireDatePicker, hireDateWarningLabel);
        });

        salaryField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateNumberField(salaryField, salaryWarningLabel);
        });

        cityField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateTextField(cityField, cityWarningLabel);
        });

        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateEmailField(emailField, emailWarningLabel);
        });

        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!clearingForm)
                validateTextField(phoneField, phoneWarningLabel);
        });
    }

    private void loadEmployees() {

        List<Employee> employeeList = employeeDAO.getAllEmployeesForTable();

        for (int i = 0; i < employeeList.size(); i++) {
            employeeList.get(i).setNo(i + 1);

            Employee emp = employeeList.get(i);

            if (emp.getStatus() == null || emp.getStatus().isBlank()) {
                emp.setStatus("Active");
            }
        }

        originalEmployees.clear();
        originalEmployees.addAll(employeeList);

        EmployeeTable.setItems(
                FXCollections.observableArrayList(employeeList));

        recordsFoundLabel.setText(
                String.valueOf(employeeList.size()));

        refreshRowNumbers();
    }

    private void loadStats() {

        EmployeeDAO.EmployeeStats stats = employeeDAO.getEmployeeStats();

        totalEmployeesLabel.setText(
                String.valueOf(stats.getTotalEmployees()));

        activeEmployeesLabel.setText(
                String.valueOf(stats.getActiveEmployees()));

        topSalesEmployeeNameLabel.setText(
                 stats.getTopSalesEmployeeName());

        topSalesEmployeeValueLabel.setText(
                 stats.getTopSalesCount() + " Sales");

        topPurchaseEmployeeNameLabel.setText(
                 stats.getTopPurchaseEmployeeName());

        topPurchaseEmployeeValueLabel.setText(
                stats.getTopPurchaseCount() + " Purchases");

        topDeliveryEmployeeNameLabel.setText(
                 stats.getTopDeliveryEmployeeName());

        topDeliveryEmployeeValueLabel.setText(
                stats.getTopDeliveryCount() + " Deliveries");
    }

    private void loadCombos() {

        roleCombo.getItems().setAll(employeeDAO.getRoles());
        roleFilterCombo.getItems().setAll(employeeDAO.getRoles());

        cityFilterCombo.getItems().setAll(employeeDAO.getCities());

        shiftTimeCombo.getItems().setAll(
                "Morning",
                "Evening",
                "Night");

        shiftTimeFilterCombo.getItems().setAll(
                "Morning",
                "Evening",
                "Night");

        genderCombo.getItems().setAll(
                "Male",
                "Female");

        genderFilterCombo.getItems().setAll(
                "Male",
                "Female");
    }

    private void setupButtons() {

        addEmployeeBtn.setOnAction(e -> {

            if (addEmployeePanel.isVisible()) {

                addEmployeePanel.setVisible(false);
                addEmployeePanel.setManaged(false);

                return;
            }

            employeeBeingUpdated = null;

            clearForm();

            employeeFormTitleLabel.setText("✚ ADD EMPLOYEE");

            saveEmployeeBtn.setText("ADD Employee");

            showOnlyPanel(addEmployeePanel);
        });

        cancelAddBtn.setOnAction(e -> {

            clearForm();

            addEmployeePanel.setVisible(false);
            addEmployeePanel.setManaged(false);
        });

        filterToggleBtn.setOnAction(e -> {

            if (filterPanel.isVisible()) {

                filterPanel.setVisible(false);
                filterPanel.setManaged(false);

            } else {

                showOnlyPanel(filterPanel);
            }
        });

        saveEmployeeBtn.setOnAction(e -> {

            if (employeeBeingUpdated == null) {

                addEmployee();

            } else {

                updateEmployee();
            }
        });
        closeDetailsBtn.setOnAction(e -> {
            employeeDetailsPanel.setVisible(false);
            employeeDetailsPanel.setManaged(false);
        });

        saveAllBtn.setOnAction(e -> saveAllChanges());

        exportBtn.setOnAction(e -> exportEmployeesToExcel());

        undoBtn.setOnAction(e -> undoAction());

        redoBtn.setOnAction(e -> redoAction());

        resetBtn.setOnAction(e -> resetTableChanges());
    }

    private void refreshRowNumbers() {

        for (int i = 0; i < EmployeeTable.getItems().size(); i++) {

            EmployeeTable.getItems().get(i).setNo(i + 1);
        }

        EmployeeTable.refresh();
    }

    private void setupActionColumn() {

        colAction.setCellFactory(column -> new TableCell<>() {

            private final Button viewBtn = new Button("👁");
            private final Button editBtn = new Button("✎");
            private final Button deleteBtn = new Button("🗑");

            private final HBox box = new HBox(6, viewBtn, editBtn, deleteBtn);

            {
                box.setAlignment(Pos.CENTER);

                viewBtn.getStyleClass().add("table-show-btn");
                editBtn.getStyleClass().add("table-edit-btn");
                deleteBtn.getStyleClass().add("table-delete-btn");

                viewBtn.setOnAction(e -> {

                    Employee employee = getTableView().getItems().get(getIndex());

                    openEmployeeDetails(employee);
                });

                editBtn.setOnAction(e -> {

                    Employee employee = getTableView().getItems().get(getIndex());

                    openUpdateForm(employee);
                });

                deleteBtn.setOnAction(e -> {

                    Employee employee = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Employee");
                    alert.setHeaderText(null);
                    alert.setContentText(
                            "Are you sure you want to delete employee record for "
                                    + employee.getFullName() + "?");

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isEmpty() || result.get() != ButtonType.OK) {
                        return;
                    }

                    saveStateForUndo();

                    pendingDeletes.add(employee);

                    EmployeeTable.getItems().remove(employee);
                    originalEmployee.remove(employee);

                    recordsFoundLabel.setText(
                            String.valueOf(EmployeeTable.getItems().size()));

                    refreshRowNumbers();

                    redoStack.clear();
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

    private void setupSearch() {

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {

            String keyword = newVal.toLowerCase().trim();

            if (keyword.isEmpty()) {

                EmployeeTable.setItems(
                        FXCollections.observableArrayList(originalEmployees));

                recordsFoundLabel.setText(
                        String.valueOf(originalEmployees.size()));

                refreshRowNumbers();

                return;
            }

            List<Employee> filtered = originalEmployees.stream()

                    .filter(e -> contains(e.getFullName(), keyword)
                            || contains(e.getEmail(), keyword)
                            || contains(e.getEmployee_role(), keyword)
                            || contains(e.getCity(), keyword)
                            || contains(
                                    String.valueOf(e.getEmployeeID()),
                                    keyword))

                    .toList();

            EmployeeTable.setItems(
                    FXCollections.observableArrayList(filtered));

            recordsFoundLabel.setText(
                    String.valueOf(filtered.size()));

            refreshRowNumbers();
        });
    }

    private String formatDate(LocalDate date) {

        if (date == null) {
            return "-";
        }

        return date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String emptyToDash(String value) {

        if (value == null || value.trim().isEmpty()) {
            return "-";
        }

        return value;
    }

    private boolean contains(String value, String keyword) {

        return value != null && value.toLowerCase().contains(keyword);
    }

    private double parseDoubleOrDefault(String text, double defaultValue) {

        try {
            return Double.parseDouble(text.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private void setupDetailsTables() {

        colEmpSaleId.setCellValueFactory(
                new PropertyValueFactory<>("saleID"));

        colEmpSaleDate.setCellValueFactory(
                new PropertyValueFactory<>("saleDate"));

        colEmpSaleAmount.setCellValueFactory(
                new PropertyValueFactory<>("total_Amount"));

        colEmpPurchaseId.setCellValueFactory(
                new PropertyValueFactory<>("purchaseID"));

        colEmpPurchaseDate.setCellValueFactory(
                new PropertyValueFactory<>("purchaseDate"));

        colEmpPurchaseAmount.setCellValueFactory(
                new PropertyValueFactory<>("totalAmount"));

        colEmpWarehouseId.setCellValueFactory(
                new PropertyValueFactory<>("warehouseID"));

        colEmpWarehouseName.setCellValueFactory(
                new PropertyValueFactory<>("warehouseName"));

        colEmpWarehouseCapacity.setCellValueFactory(
                new PropertyValueFactory<>("capacity"));
    }

    private void setupFilters() {

        applyFiltersBtn.setOnAction(e -> applyFilters());

        resetFiltersBtn.setOnAction(e -> resetFilters());
    }

    private void showOnlyPanel(VBox panel) {

        closeAllPanels();

        panel.setVisible(true);
        panel.setManaged(true);
    }

    private void closeAllPanels() {

        filterPanel.setVisible(false);
        filterPanel.setManaged(false);

        addEmployeePanel.setVisible(false);
        addEmployeePanel.setManaged(false);

        employeeDetailsPanel.setVisible(false);
        employeeDetailsPanel.setManaged(false);
    }

    private void addEmployee() {

        if (!validateForm()) {
            return;
        }

        saveStateForUndo();

        Employee employee = new Employee();

        employee.setNo(EmployeeTable.getItems().size() + 1);

        employee.setFirstName(firstNameField.getText().trim());
        employee.setMiddelInitial(middleInitialField.getText().trim());
        employee.setLastName(lastNameField.getText().trim());

        employee.setGender(genderCombo.getValue());
        employee.setEmployee_role(roleCombo.getValue());
        employee.setShiftTime(shiftTimeCombo.getValue());

        employee.setHireDate(java.sql.Date.valueOf(hireDatePicker.getValue()));

        employee.setSalary(
                Double.parseDouble(salaryField.getText()));

        employee.setCity(cityField.getText().trim());
        employee.setEmail(emailField.getText().trim());
        List<String> phones = new ArrayList<>();

        phones.add(phoneField.getText().trim());

        employee.setEmployee_Phone(phones);

        employee.setStatus("Active");

        pendingAdds.add(employee);

        EmployeeTable.getItems().add(employee);

        originalEmployees.add(employee);

        recordsFoundLabel.setText(
                String.valueOf(EmployeeTable.getItems().size()));

        clearForm();

        loadStats();

        refreshRowNumbers();

        redoStack.clear();
    }

    private void updateEmployee() {

        if (!validateForm()) {
            return;
        }

        saveStateForUndo();

        employeeBeingUpdated.setFirstName(firstNameField.getText());
        employeeBeingUpdated.setMiddelInitial(middleInitialField.getText());
        employeeBeingUpdated.setLastName(lastNameField.getText());

        employeeBeingUpdated.setGender(genderCombo.getValue());
        employeeBeingUpdated.setEmployee_role(roleCombo.getValue());
        employeeBeingUpdated.setShiftTime(shiftTimeCombo.getValue());

        employeeBeingUpdated.setHireDate(
                java.sql.Date.valueOf(hireDatePicker.getValue()));

        employeeBeingUpdated.setSalary(
                Double.parseDouble(salaryField.getText()));

        employeeBeingUpdated.setCity(cityField.getText());

        employeeBeingUpdated.setEmail(emailField.getText());
        List<String> phones = new ArrayList<>();

        phones.add(phoneField.getText().trim());

        employeeBeingUpdated.setEmployee_Phone(phones);

        if (!pendingUpdates.contains(employeeBeingUpdated)) {
            pendingUpdates.add(employeeBeingUpdated);
        }

        EmployeeTable.refresh();

        clearForm();

        addEmployeePanel.setVisible(false);
        addEmployeePanel.setManaged(false);

        loadStats();

        refreshRowNumbers();

        redoStack.clear();
    }

    private void openEmployeeDetails(Employee employee) {

        detailsEmployeeNameLabel.setText(
                emptyToDash(employee.getFullName()));

        detailsRoleBadgeLabel.setText(
                emptyToDash(employee.getEmployee_role()));

        detailsEmployeeIdLabel.setText(
                String.valueOf(employee.getEmployeeID()));

        detailsGenderLabel.setText(
                emptyToDash(employee.getGender()));

        detailsHireDateLabel.setText(
                employee.getHireDate() == null ? "-" : employee.getHireDate().toString());

        detailsShiftTimeLabel.setText(
                emptyToDash(employee.getShiftTime()));

        detailsSalaryLabel.setText(
                String.format("$%,.2f", employee.getSalary()));

        detailsCityLabel.setText(
                emptyToDash(employee.getCity()));

        detailsEmailLabel.setText(
                emptyToDash(employee.getEmail()));

        detailsPhoneLabel.setText(
                employee.getEmployee_Phone() != null
                        && !employee.getEmployee_Phone().isEmpty()
                                ? employee.getEmployee_Phone().get(0)
                                : "-");

        detailsSalesCountLabel.setText(
                String.valueOf(employee.getSalesCount()));

        detailsPurchasesCountLabel.setText(
                String.valueOf(employee.getPurchasesCount()));

        detailsDeliveriesCountLabel.setText(
                String.valueOf(employee.getDeliveriesCount()));

        detailsManagedWarehousesLabel.setText(
                String.valueOf(employee.getManagedWarehouses()));

        detailsTotalSalesAmountLabel.setText(
                String.format("$%,.2f", employee.getTotalSalesAmount()));

        detailsTotalPurchaseAmountLabel.setText(
                String.format("$%,.2f", employee.getTotalPurchaseAmount()));

        employeeSalesTable.setItems(
                FXCollections.observableArrayList(
                        employeeDAO.getRecentSalesByEmployee(employee.getEmployeeID())));

        employeePurchasesTable.setItems(
                FXCollections.observableArrayList(
                        employeeDAO.getRecentPurchasesByEmployee(employee.getEmployeeID())));

        employeeWarehousesTable.setItems(
                FXCollections.observableArrayList(
                        employeeDAO.getWarehousesManagedByEmployee(employee.getEmployeeID())));

        showOnlyPanel(employeeDetailsPanel);
    }

    private void saveAllChanges() {

        try {

            for (Employee employee : pendingAdds) {
                employeeDAO.insertEmployee(employee);
            }

            for (Employee employee : pendingUpdates) {
                employeeDAO.updateEmployee(employee);
            }

            for (Employee employee : pendingDeletes) {
                employeeDAO.deleteEmployee(employee.getEmployeeID());
            }

            pendingAdds.clear();
            pendingUpdates.clear();
            pendingDeletes.clear();

            undoStack.clear();
            redoStack.clear();

            closeAllPanels();

            loadEmployees();
            loadStats();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("All employee changes saved successfully.");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyFilters() {

        String role = roleFilterCombo.getValue();
        String gender = genderFilterCombo.getValue();
        String shift = shiftTimeFilterCombo.getValue();
        String city = cityFilterCombo.getValue();
        String status = statusFilterCombo.getValue();
        String performance = performanceFilterCombo.getValue();

        double minSalary = parseDoubleOrDefault(
                minSalaryField.getText(),
                0);

        double maxSalary = parseDoubleOrDefault(
                maxSalaryField.getText(),
                Double.MAX_VALUE);

        LocalDate fromDate = fromHireDatePicker.getValue();
        LocalDate toDate = toHireDatePicker.getValue();

        List<Employee> filtered = originalEmployees.stream()

                .filter(e -> role == null
                        || role.equals("All Roles")
                        || e.getEmployee_role().equals(role))

                .filter(e -> gender == null
                        || gender.equals("All Genders")
                        || e.getGender().equals(gender))

                .filter(e -> shift == null
                        || shift.equals("All Shifts")
                        || e.getShiftTime().equals(shift))

                .filter(e -> city == null
                        || city.equals("All Cities")
                        || e.getCity().equals(city))

                .filter(e -> status == null
                        || status.equals("All Status")
                        || e.getStatus().equals(status))

                .filter(e -> e.getSalary() >= minSalary
                        && e.getSalary() <= maxSalary)

                .filter(e -> fromDate == null
                        || e.getHireDate() == null
                        || !e.getHireDate().toLocalDate().isBefore(fromDate))

                .filter(e -> toDate == null
                        || e.getHireDate() == null
                        || !e.getHireDate().toLocalDate().isAfter(toDate))

                .filter(e -> performance == null
                        || performance.equals("All Employees")
                        || matchesPerformanceFilter(e, performance))

                .toList();

        EmployeeTable.setItems(
                FXCollections.observableArrayList(filtered));

        recordsFoundLabel.setText(
                String.valueOf(filtered.size()));

        refreshRowNumbers();
    }

    private boolean matchesPerformanceFilter(Employee employee, String performance) {

        return switch (performance) {

            case "Has Sales" ->
                employee.getSalesCount() > 0;

            case "Has Purchases" ->
                employee.getPurchasesCount() > 0;

            case "Has Deliveries" ->
                employee.getDeliveriesCount() > 0;

            case "Manages Warehouse" ->
                employee.getManagedWarehouses() > 0;

            case "No Activity" ->
                employee.getSalesCount() == 0
                        && employee.getPurchasesCount() == 0
                        && employee.getDeliveriesCount() == 0
                        && employee.getManagedWarehouses() == 0;

            default ->
                true;
        };
    }

    private void resetFilters() {

        roleFilterCombo.setValue(null);
        genderFilterCombo.setValue(null);
        shiftTimeFilterCombo.setValue(null);
        cityFilterCombo.setValue(null);
        statusFilterCombo.setValue(null);
        performanceFilterCombo.setValue(null);

        minSalaryField.clear();
        maxSalaryField.clear();

        fromHireDatePicker.setValue(null);
        toHireDatePicker.setValue(null);

        EmployeeTable.setItems(
                FXCollections.observableArrayList(originalEmployees));

        recordsFoundLabel.setText(
                String.valueOf(originalEmployees.size()));

        refreshRowNumbers();
    }

    private void exportEmployeesToExcel() {

        try {

            File exportFolder = new File("src/main/resources/exports");

            if (!exportFolder.exists()) {
                exportFolder.mkdirs();
            }

            String fileName = "employees_" + LocalDate.now() + ".xlsx";

            File file = new File(exportFolder, fileName);

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Employees");

            String[] headers = {
                    "No.",
                    "Employee ID",
                    "Full Name",
                    "Gender",
                    "Role",
                    "Shift Time",
                    "City",
                    "Email",
                    "Salary",
                    "Hire Date",
                    "Status"
            };

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {

                Cell cell = headerRow.createCell(i);

                cell.setCellValue(headers[i]);
            }

            int rowIndex = 1;

            for (Employee employee : EmployeeTable.getItems()) {

                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(employee.getNo());

                row.createCell(1).setCellValue(employee.getEmployeeID());

                row.createCell(2).setCellValue(
                        emptyToDash(employee.getFullName()));

                row.createCell(3).setCellValue(
                        emptyToDash(employee.getGender()));

                row.createCell(4).setCellValue(
                        emptyToDash(employee.getEmployee_role()));

                row.createCell(5).setCellValue(
                        emptyToDash(employee.getShiftTime()));

                row.createCell(6).setCellValue(
                        emptyToDash(employee.getCity()));

                row.createCell(7).setCellValue(
                        emptyToDash(employee.getEmail()));

                row.createCell(8).setCellValue(
                        employee.getSalary());

                row.createCell(9).setCellValue(
                        employee.getHireDate() == null
                                ? "-"
                                : employee.getHireDate().toString());

                row.createCell(10).setCellValue(
                        emptyToDash(employee.getStatus()));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream out = new FileOutputStream(file)) {
                workbook.write(out);
            }

            workbook.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Export Success");

            alert.setHeaderText(null);

            alert.setContentText(
                    "Employees exported successfully!\n\nSaved to:\n"
                            + file.getAbsolutePath());

            alert.showAndWait();

        } catch (Exception e) {

            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Export Error");

            alert.setHeaderText(null);

            alert.setContentText(
                    "Failed to export employees file.");

            alert.showAndWait();
        }
    }

}
