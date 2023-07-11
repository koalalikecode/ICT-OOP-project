package appgui.controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartController implements Initializable {

    double x,y = 0;
    @FXML
    public MediaView mediaView = new MediaView();
    public MediaPlayer mediaPlayer;

    @FXML
    public Button startButton;
    private File file;
    private Media media;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File file = new File("src/main/resources/appgui/Video/Intro.mp4");
        startButton.setVisible(false);
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(event -> startButton.setVisible(true));
        pause.play();
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
    }
    @FXML
    private void StartView(javafx.event.ActionEvent event) throws IOException {
        mediaPlayer.stop();
        Stage stage = (Stage) startButton.getScene().getWindow();

        Parent root = FXMLLoader.load(getClass().getResource("/appgui/fxml/characterPane.fxml"));
        Scene sc = new Scene(root);

        String css = this.getClass().getResource("/appgui/css/styles.css").toExternalForm();
        sc.getStylesheets().add(css);
        root.setOnMousePressed(lamda -> {
            x = lamda.getSceneX();
            y = lamda.getSceneY();
        });
        root.setOnMouseDragged(lamda -> {
            stage.setX(lamda.getScreenX() - x);
            stage.setY(lamda.getScreenY() - y);
        });

        stage.setScene(sc);
        for(Screen sn : Screen.getScreens()) {
            Rectangle2D bounds = sn.getBounds();
            double x = bounds.getMinX() + (bounds.getWidth() - sc.getWidth()) * 0.2;
            double y = bounds.getMinY() + (bounds.getHeight() - sc.getHeight()) * 0.2;
            stage.setX(x);
            stage.setY(y);
        }
        stage.show();
    }

}