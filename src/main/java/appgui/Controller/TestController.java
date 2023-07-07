package appgui.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;


public class TestController {

    @FXML
    private Button btvTest;


    @FXML
    private void addSceneSwitchingHandler(ActionEvent event) {
        Stage stage = (Stage) btvTest.getScene().getWindow();
        try {
            if (event.getSource() == btvTest) {
                Parent newPane = FXMLLoader.load(getClass().getResource("eventPane.fxml"));
                Scene newScene = new Scene(newPane);
                stage.setScene(newScene);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
