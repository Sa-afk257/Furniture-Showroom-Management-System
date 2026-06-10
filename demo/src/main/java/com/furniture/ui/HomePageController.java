package com.furniture.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public class HomePageController {
    
     @FXML
    private HBox watchVideoBox;

    @FXML
    public void initialize() {
        watchVideoBox.setOnMouseClicked(event -> openVideoWindow());
    }

    private void openVideoWindow() {
        String videoPath = getClass().getResource("/video/abuSalahFurniture.mp4").toExternalForm();

        Media media = new Media(videoPath);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        mediaView.setFitWidth(900);
        mediaView.setFitHeight(520);
        mediaView.setPreserveRatio(true);

        StackPane root = new StackPane(mediaView);
        root.setStyle("-fx-background-color: black;");

        Stage videoStage = new Stage();
        videoStage.setTitle("Abu Salah Furniture Showroom");
        videoStage.setScene(new Scene(root, 900, 520));
        videoStage.show();

        mediaPlayer.play();

        videoStage.setOnCloseRequest(e -> mediaPlayer.stop());
    }

     @FXML
    public void EnterSystem(ActionEvent event) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/view/MainLayout.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage)
                    ((javafx.scene.Node) event.getSource())
                            .getScene()
                            .getWindow();

            Scene scene = new Scene(root);

            scene.getStylesheets().add(
                    getClass().getResource("/style/main.css").toExternalForm()
            );

            stage.setScene(scene);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
