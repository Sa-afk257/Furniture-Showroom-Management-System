package com.furniture.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.furniture.dao.CartDAO;
import com.furniture.model.Account;

public class BaseController {

    protected enum PageMode {
        ALL_PRODUCTS,
        FAVORITES
    }

    private ContextMenu accountMenuPopup;

    protected CartDAO cartDAO = new CartDAO();

    protected void updateCartButton(Button cartBtn) {

        if (cartBtn == null)
            return;

        if (Session.isGuest()) {
            cartBtn.setText("🛒 Cart (0)");
            return;
        }

        int count = cartDAO.getCartItemsCount(Session.getCurrentCustomerId());

        cartBtn.setText("🛒 Cart (" + count + ")");
    }

    protected void openPage(Node source, String path) {
        try {
            java.net.URL url = getClass().getResource(path);

            if (url == null) {
                throw new RuntimeException("FXML not found: " + path);
            }

            Parent root = FXMLLoader.load(url);

            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void openWishlistPage(Node source) {
        openGuestPage(source, PageMode.FAVORITES);
    }

    protected void openAllProductsPage(Node source) {
        openGuestPage(source, PageMode.ALL_PRODUCTS);
    }

    protected void openGuestPage(Node source, PageMode mode) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/GuestView.fxml"));

            Parent root = loader.load();

            GuestController controller = loader.getController();
            controller.setMode(mode);

            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startDateTime(Label dateLabel, Label timeLabel) {
        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    LocalDateTime now = LocalDateTime.now();

                    dateLabel.setText(now.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
                    timeLabel.setText(now.format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                }),
                new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    protected void createAccountMenu(Button accountBtn) {

        if (accountBtn == null) {
            return;
        }

        String fullName = "Guest";

        if (!Session.isGuest()
                && Session.getCurrentAccount() != null) {

            Account acc = Session.getCurrentAccount();

            if (acc.getFirstName() != null
                    && acc.getLastName() != null) {

                fullName = acc.getFirstName()
                        + " "
                        + acc.getLastName();
            }
        }

        Label nameLabel = new Label(fullName);
        nameLabel.getStyleClass().add("account-name");

        Label emailLabel = new Label(
                Session.isGuest()
                        ? ""
                        : Session.getCurrentUserEmail());

        emailLabel.getStyleClass().add("account-email");

        VBox accountInfo = new VBox(5,
                nameLabel,
                emailLabel);

        CustomMenuItem accountInfoItem = new CustomMenuItem(accountInfo);

        accountInfoItem.setHideOnClick(false);

        CustomMenuItem emailItem = new CustomMenuItem(emailLabel);
        emailItem.setHideOnClick(false);

        MenuItem profileItem = new MenuItem("Account");
        MenuItem logoutItem = new MenuItem("Logout");

        profileItem.setOnAction(e -> {
            openPage(accountBtn, "/view/AccountView.fxml");
        });

        logoutItem.setOnAction(e -> {
            Session.logout();
            openGuestPage(accountBtn, PageMode.ALL_PRODUCTS);
        });

        accountMenuPopup = new ContextMenu(
                accountInfoItem,
                profileItem,
                logoutItem);

        accountMenuPopup.getStyleClass().add("account-popup");

        accountBtn.setOnAction(e -> {

            if (accountMenuPopup.isShowing()) {
                accountMenuPopup.hide();
            } else {
                accountMenuPopup.show(
                        accountBtn,
                        javafx.geometry.Side.BOTTOM,
                        0,
                        5);
            }
        });
    }
}