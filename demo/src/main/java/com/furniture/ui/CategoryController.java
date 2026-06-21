
package com.furniture.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.furniture.dao.CategoryDAO;
import com.furniture.dao.CategoryDAO.CategoryStats;
import com.furniture.model.Category;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class CategoryController {

	@FXML
	private Label totalCategoriesLabel;
	@FXML
	private Label usedCategoriesLabel;
	@FXML
	private Label emptyCategoriesLabel;
	@FXML
	private Label totalProductsLabel;
	@FXML
	private Label avgProductsLabel;

	@FXML
	private Label categoriesFoundLabel;

	@FXML
	private TextField txtSearch;

	@FXML
	private Button addCategoryBtn;
	@FXML
	private Button saveCategoryBtn;
	@FXML
	private Button cancelAddBtn;

	@FXML
	private Button filterToggleBtn;
	@FXML
	private Button applyFiltersBtn;
	@FXML
	private Button resetFiltersBtn;

	@FXML
	private Button undoBtn;
	@FXML
	private Button redoBtn;
	@FXML
	private Button resetBtn;
	@FXML
	private Button saveAllBtn;
	@FXML
	private Button exportBtn;

	@FXML
	private VBox addCategoryPanel;
	@FXML
	private VBox filterPanel;

	@FXML
	private TextField categoryNameField;

	@FXML
	private Label categoryNameWarningLabel;

	@FXML
	private ComboBox<String> statusCombo;
	@FXML
	private ComboBox<String> productCountCombo;

	@FXML
	private Label formTitleLabel;

	@FXML
	private TableView<Category> categoryTable;

	@FXML
	private TableColumn<Category, Integer> colNo;
	@FXML
	private TableColumn<Category, Integer> colID;
	@FXML
	private TableColumn<Category, String> colName;
	@FXML
	private TableColumn<Category, Integer> colProducts;
	@FXML
	private TableColumn<Category, Double> colStock;
	@FXML
	private TableColumn<Category, Double> colValue;
	@FXML
	private TableColumn<Category, Void> colAction;
	private CategoryDAO categoryDAO = new CategoryDAO();

	private List<Category> originalCategories = new ArrayList<>();

	private Stack<List<Category>> undoStack = new Stack<>();
	private Stack<List<Category>> redoStack = new Stack<>();

	private List<Category> pendingAdds = new ArrayList<>();
	private List<Category> pendingUpdates = new ArrayList<>();
	private List<Category> pendingDeletes = new ArrayList<>();

	private Category categoryBeingUpdated = null;

	@FXML
	private void initialize() {

		setupTable();
		categoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		loadCategories();

		loadStats();

		loadFilters();

		setupSearch();

		setupButtons();

		setupUndoRedo();
	}

	private void setupTable() {

		colNo.setCellValueFactory(new PropertyValueFactory<>("no"));

		colID.setCellValueFactory(new PropertyValueFactory<>("categoryID"));

		colName.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

		colProducts.setCellValueFactory(new PropertyValueFactory<>("productCount"));

		colStock.setCellValueFactory(new PropertyValueFactory<>("totalStock"));

		colValue.setCellValueFactory(new PropertyValueFactory<>("totalValue"));

		setupActionColumn();
	}

	private void loadCategories() {

		List<Category> categories = categoryDAO.getAllCategoriesForTable();

		originalCategories.clear();
		originalCategories.addAll(categories);

		categoryTable.setItems(FXCollections.observableArrayList(categories));

		categoriesFoundLabel.setText(String.valueOf(categories.size()));
	}

	private void loadStats() {

		CategoryStats stats = categoryDAO.getCategoryStats();

		totalCategoriesLabel.setText(String.valueOf(stats.getTotalCategories()));

		usedCategoriesLabel.setText(String.valueOf(stats.getUsedCategories()));

		emptyCategoriesLabel.setText(String.valueOf(stats.getEmptyCategories()));

		totalProductsLabel.setText(String.valueOf(stats.getTotalProducts()));

		avgProductsLabel.setText(String.valueOf(stats.getAverageProducts()));
	}

	private void setupActionColumn() {

		colAction.setCellFactory(column -> new TableCell<>() {

			private final Button editBtn = new Button("✎");
			private final Button deleteBtn = new Button("🗑");

			private final HBox box = new HBox(8, editBtn, deleteBtn);

			{

				box.setAlignment(Pos.CENTER);

				editBtn.getStyleClass().add("table-edit-btn");

				deleteBtn.getStyleClass().add("table-delete-btn");

				editBtn.setOnAction(e -> {

					Category category = getTableView().getItems().get(getIndex());

					openUpdateForm(category);
				});

				deleteBtn.setOnAction(e -> {

					Category category = getTableView().getItems().get(getIndex());

					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

					alert.setTitle("Delete Category");

					alert.setHeaderText(null);

					alert.setContentText("Delete category " + category.getCategoryName() + " ?");

					Optional<ButtonType> result = alert.showAndWait();

					if (result.isEmpty() || result.get() != ButtonType.OK) {
						return;
					}
					if (category.getProductCount() > 0) {

						Alert alert2 = new Alert(Alert.AlertType.WARNING);
						alert2.setTitle("Delete Category");
						alert2.setHeaderText(null);

						alert2.setContentText("Cannot delete category that contains products.");

						alert2.showAndWait();

						return;
					}
					saveStateForUndo();

					pendingDeletes.add(category);

					categoryTable.getItems().remove(category);
					originalCategories.remove(category);
					categoriesFoundLabel.setText(String.valueOf(categoryTable.getItems().size()));
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

	private void refreshRowNumbers() {

		for (int i = 0; i < categoryTable.getItems().size(); i++) {

			categoryTable.getItems().get(i).setNo(i + 1);
		}

		categoryTable.refresh();
	}

	private void setupButtons() {

		addCategoryBtn.setOnAction(e -> {

			categoryBeingUpdated = null;

			clearForm();

			formTitleLabel.setText("✚ ADD CATEGORY");

			saveCategoryBtn.setText("ADD CATEGORY");

			showOnlyPanel(addCategoryPanel);
		});

		cancelAddBtn.setOnAction(e -> {

			clearForm();

			addCategoryPanel.setVisible(false);
			addCategoryPanel.setManaged(false);
		});

		filterToggleBtn.setOnAction(e -> {

			if (filterPanel.isVisible()) {

				filterPanel.setVisible(false);
				filterPanel.setManaged(false);

			} else {

				showOnlyPanel(filterPanel);
			}
		});

		saveCategoryBtn.setOnAction(e -> {

			if (categoryBeingUpdated == null) {

				addCategory();

			} else {

				updateCategory();
			}
		});

		exportBtn.setOnAction(e -> exportCategoriesToExcel());
		applyFiltersBtn.setOnAction(e -> applyFilters());

		resetFiltersBtn.setOnAction(e -> resetFilters());
	}

	private void showOnlyPanel(VBox panel) {

		addCategoryPanel.setVisible(false);
		addCategoryPanel.setManaged(false);

		filterPanel.setVisible(false);
		filterPanel.setManaged(false);

		panel.setVisible(true);
		panel.setManaged(true);
	}

	private void clearForm() {

		categoryNameField.clear();

		categoryNameWarningLabel.setVisible(false);
		categoryNameWarningLabel.setManaged(false);

		categoryBeingUpdated = null;
	}

	private boolean validateForm() {

		boolean valid = !categoryNameField.getText().trim().isEmpty();

		categoryNameWarningLabel.setVisible(!valid);

		categoryNameWarningLabel.setManaged(!valid);

		return valid;
	}

	private void addCategory() {

		if (!validateForm()) {
			return;
		}

		saveStateForUndo();

		Category category = new Category();

		category.setNo(categoryTable.getItems().size() + 1);

		category.setCategoryName(categoryNameField.getText().trim());

		category.setProductCount(0);
		category.setTotalStock(0);
		category.setTotalValue(0);

		pendingAdds.add(category);

		categoryTable.getItems().add(category);
		originalCategories.add(category);

		categoriesFoundLabel.setText(String.valueOf(categoryTable.getItems().size()));

		clearForm();

		redoStack.clear();
	}

	private void openUpdateForm(Category category) {

		categoryBeingUpdated = category;

		showOnlyPanel(addCategoryPanel);

		categoryNameField.setText(category.getCategoryName());

		formTitleLabel.setText("✏ UPDATE CATEGORY");

		saveCategoryBtn.setText("UPDATE CATEGORY");
	}

	private void updateCategory() {

		if (!validateForm()) {
			return;
		}

		saveStateForUndo();

		categoryBeingUpdated.setCategoryName(categoryNameField.getText().trim());

		if (!pendingUpdates.contains(categoryBeingUpdated)) {

			pendingUpdates.add(categoryBeingUpdated);
		}

		categoryTable.refresh();
		categoriesFoundLabel.setText(String.valueOf(categoryTable.getItems().size()));
		categoryBeingUpdated = null;

		clearForm();

		addCategoryPanel.setVisible(false);
		addCategoryPanel.setManaged(false);

		redoStack.clear();
	}

	private void setupSearch() {

		txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {

			String keyword = newVal.toLowerCase().trim();

			if (keyword.isEmpty()) {

				categoryTable.setItems(FXCollections.observableArrayList(originalCategories));

				refreshRowNumbers();

				return;
			}

			List<Category> filtered = originalCategories.stream()

					.filter(c -> c.getCategoryName().toLowerCase().contains(keyword))

					.toList();

			categoryTable.setItems(FXCollections.observableArrayList(filtered));

			categoriesFoundLabel.setText(String.valueOf(filtered.size()));

			refreshRowNumbers();
		});
	}

	private void setupUndoRedo() {

		undoBtn.setOnAction(e -> undoAction());

		redoBtn.setOnAction(e -> redoAction());

		resetBtn.setOnAction(e -> resetTableChanges());

		saveAllBtn.setOnAction(e -> saveAllChanges());
	}

	private void saveStateForUndo() {

		undoStack.push(new ArrayList<>(categoryTable.getItems()));
	}

	private void undoAction() {

		if (undoStack.isEmpty()) {
			return;
		}

		redoStack.push(new ArrayList<>(categoryTable.getItems()));

		List<Category> previousState = undoStack.pop();

		categoryTable.setItems(FXCollections.observableArrayList(previousState));
		categoriesFoundLabel.setText(String.valueOf(categoryTable.getItems().size()));
		refreshRowNumbers();
	}

	private void redoAction() {

		if (redoStack.isEmpty()) {
			return;
		}

		undoStack.push(new ArrayList<>(categoryTable.getItems()));

		List<Category> nextState = redoStack.pop();

		categoryTable.setItems(FXCollections.observableArrayList(nextState));
		categoriesFoundLabel.setText(String.valueOf(categoryTable.getItems().size()));
		refreshRowNumbers();
	}

	private void resetTableChanges() {

		pendingAdds.clear();
		pendingUpdates.clear();
		pendingDeletes.clear();

		undoStack.clear();
		redoStack.clear();

		loadCategories();
		loadStats();

		clearForm();

		addCategoryPanel.setVisible(false);
		addCategoryPanel.setManaged(false);
	}

	private void saveAllChanges() {

		try {

			for (Category category : pendingAdds) {

				categoryDAO.insertCategory(category);
			}

			for (Category category : pendingUpdates) {

				categoryDAO.updateCategory(category);
			}

			for (Category category : pendingDeletes) {

				categoryDAO.deleteCategory(category.getCategoryID());
			}

			pendingAdds.clear();
			pendingUpdates.clear();
			pendingDeletes.clear();

			undoStack.clear();
			redoStack.clear();

			loadCategories();
			loadStats();

			Alert alert = new Alert(Alert.AlertType.INFORMATION);

			alert.setTitle("Success");

			alert.setHeaderText(null);

			alert.setContentText("All changes saved successfully.");

			alert.showAndWait();
			
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void loadFilters() {

		statusCombo.getItems().setAll("All", "Used Categories", "Empty Categories");

		statusCombo.getSelectionModel().selectFirst();

		productCountCombo.getItems().setAll("All", "0 Products", "1-5 Products", "More Than 5");

		productCountCombo.getSelectionModel().selectFirst();

		categoriesFoundLabel.setText(String.valueOf(originalCategories.size()));
	}

	private void applyFilters() {

		String status = statusCombo.getValue();

		String productCount = productCountCombo.getValue();

		List<Category> filtered = originalCategories.stream()

				.filter(c -> {

					if (status == null || status.equals("All")) {

						return true;
					}

					if (status.equals("Used Categories")) {

						return c.getProductCount() > 0;
					}

					if (status.equals("Empty Categories")) {

						return c.getProductCount() == 0;
					}

					return true;
				})

				.filter(c -> {

					if (productCount == null || productCount.equals("All")) {

						return true;
					}

					int count = c.getProductCount();

					switch (productCount) {

					case "0 Products":
						return count == 0;

					case "1-5 Products":
						return count >= 1 && count <= 5;

					case "More Than 5":
						return count > 5;

					default:
						return true;
					}
				})

				.toList();

		categoryTable.setItems(FXCollections.observableArrayList(filtered));

		categoriesFoundLabel.setText(String.valueOf(filtered.size()));

		refreshRowNumbers();
	}

	private void resetFilters() {

		statusCombo.getSelectionModel().selectFirst();

		productCountCombo.getSelectionModel().selectFirst();

		categoryTable.setItems(FXCollections.observableArrayList(originalCategories));

		categoriesFoundLabel.setText(String.valueOf(originalCategories.size()));

		refreshRowNumbers();
	}

	private void exportCategoriesToExcel() {

		try {

			File exportFolder = new File("src/main/resources/exports");

			if (!exportFolder.exists()) {
				exportFolder.mkdirs();
			}

			String fileName = "categories_" + LocalDate.now() + ".xlsx";

			File file = new File(exportFolder, fileName);

			Workbook workbook = new XSSFWorkbook();

			Sheet sheet = workbook.createSheet("Categories");

			String[] headers = {

					"Category ID", "Category Name", "Products Count", "Total Stock", "Total Value" };

			Row headerRow = sheet.createRow(0);

			for (int i = 0; i < headers.length; i++) {

				Cell cell = headerRow.createCell(i);

				cell.setCellValue(headers[i]);
			}

			int rowIndex = 1;

			for (Category c : categoryTable.getItems()) {

				Row row = sheet.createRow(rowIndex++);

				row.createCell(0).setCellValue(c.getCategoryID());

				row.createCell(1).setCellValue(c.getCategoryName());

				row.createCell(2).setCellValue(c.getProductCount());

				row.createCell(3).setCellValue(c.getTotalStock());

				row.createCell(4).setCellValue(c.getTotalValue());
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

			alert.setContentText("Excel exported successfully.");

			alert.showAndWait();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
