package com.furniture.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.util.Duration;

public class MainLayoutController {

    @FXML
    private StackPane contentPane;
    @FXML
    private Label pageTitle;
    @FXML
    private HBox dashboardItem;
    @FXML
    private HBox productsItem;
    @FXML
    private HBox customersItem;
    @FXML
    private HBox salesItem;
    @FXML
    private HBox purchasesItem;
    @FXML
    private HBox warehousesItem;
    @FXML
    private HBox employeesItem;
    @FXML
    private HBox categoriesItem;
    @FXML
    private HBox inventoryItem;
    @FXML
    private HBox deliveriesItem;
    @FXML
    private HBox paymentsItem;
    @FXML
    private HBox returnsItem;
    @FXML
    private HBox discountsItem;
    @FXML
    private HBox stockMovementItem;
    @FXML
    private HBox reportsItem;
    @FXML
    private HBox settingsItem;
    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label breadcrumbCurrent;

    @FXML
    public void initialize() {

        startDateTime();
        loadPage("/view/DashboardView.fxml", "Dashboard");

    }

    @FXML
    private void handleSidebarClick(MouseEvent event) {

        HBox clickedItem = (HBox) event.getSource();

        VBox menu = (VBox) clickedItem.getParent();

        // REMOVE ACTIVE STYLE FROM ALL
        for (Node node : menu.getChildren()) {

            if (node instanceof HBox item) {

                item.getStyleClass().remove("sidebar-item-active");

                if (!item.getStyleClass().contains("sidebar-item")) {
                    item.getStyleClass().add("sidebar-item");
                }

                for (Node child : item.getChildren()) {

                    if (child instanceof Label label) {

                        label.getStyleClass().remove("sidebar-text-active");

                        if (!label.getStyleClass().contains("sidebar-icon")) {

                            label.getStyleClass().remove("sidebar-text");
                            label.getStyleClass().add("sidebar-text");
                        }
                    }
                }
            }
        }

        // ADD ACTIVE STYLE
        clickedItem.getStyleClass().remove("sidebar-item");
        clickedItem.getStyleClass().add("sidebar-item-active");

        // CHANGE TEXT STYLE
        for (Node child : clickedItem.getChildren()) {

            if (child instanceof Label label) {

                if (!label.getStyleClass().contains("sidebar-icon")) {

                    label.getStyleClass().remove("sidebar-text");
                    label.getStyleClass().add("sidebar-text-active");

                    pageTitle.setText(label.getText());
                    breadcrumbCurrent.setText(label.getText());
                }
            }
        }

        switch (clickedItem.getId()) {

            case "dashboardItem":
                goToDashboard(event);
                break;

            case "productsItem":
                goToProducts(event);
                break;

            case "customersItem":
                // goToCustomerPage(event);
                break;

            case "salesItem":
                // goToSalesPage(event);
                break;
        }
    }

    private void startDateTime() {

        Timeline clock = new Timeline(

                new KeyFrame(Duration.ZERO, e -> {

                    LocalDateTime now = LocalDateTime.now();

                    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

                    DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm:ss a");

                    dateLabel.setText(now.format(dateFormat));

                    timeLabel.setText(now.format(timeFormat));

                }),

                new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Timeline.INDEFINITE);

        clock.play();
    }

    private void loadPage(String fxmlPath, String title) {

        try {
            FXMLLoader loader =
                new FXMLLoader(getClass().getResource(fxmlPath));

            Parent page = loader.load();

            contentPane.getChildren().setAll(page);

            pageTitle.setText(title);
            breadcrumbCurrent.setText(title);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToDashboard(MouseEvent event) {
        loadPage("/view/DashboardView.fxml", "Dashboard");
    }

    @FXML
    private void goToProducts(MouseEvent event) {
        loadPage("/view/ProductView.fxml", "Products");
    }

    @FXML
    public void goToHomePage(MouseEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/HomePageView.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource())
                    .getScene()
                    .getWindow();

            Scene scene = new Scene(root);

            scene.getStylesheets().add(
                    getClass().getResource("/style/HomePage.css").toExternalForm());

            stage.setScene(scene);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}