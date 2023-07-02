/* Việc cần làm khi sửa code :
 * 1. Tối ưu exception handling trong file
 * 2.
 */



package appgui;

import historyobject.Character;

import historyobject.CharacterTest;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.MouseEvent;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EventController implements Initializable {
    private final String dataJson = "data/final.json";
    private JSONObject characterInfoBox;
    private List<JSONObject> characterConnectionBox;

    //    Menu Buttons
    @FXML
    private Button btnCharacter;
    @FXML
    private Button btnDynasty;
    @FXML
    private Button btnEvent;
    @FXML
    private Button btnFestival;
    @FXML
    private Button btnPlace;


    //    Scenes
    private Scene sceneCharacter;
    private Scene sceneDynasty;
    private Scene sceneEvent;
    private Scene sceneFestival;
    private Scene scenePlace;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    @FXML
    private void addSceneSwitchingHandler(ActionEvent event) {
        Stage stage = (Stage) btnEvent.getScene().getWindow();

        try {
            if (event.getSource() == btnEvent) {
                Parent newPane = FXMLLoader.load(getClass().getResource("eventPane.fxml"));
                Scene newScene = new Scene(newPane);
                stage.setScene(newScene);
            } else if (event.getSource() == btnCharacter) {
                Parent newPane2 = FXMLLoader.load(getClass().getResource("characterPane.fxml"));
                Scene newScene2 = new Scene(newPane2);
                stage.setScene(newScene2);
            } else if (event.getSource() == btnDynasty) {
                Parent newPane3 = FXMLLoader.load(getClass().getResource("dynastyPane.fxml"));
                Scene newScene3 = new Scene(newPane3);
                stage.setScene(newScene3);
            } else if (event.getSource() == btnFestival) {
                Parent newPane4 = FXMLLoader.load(getClass().getResource("festivalPane.fxml"));
                Scene newScene4 = new Scene(newPane4);
                stage.setScene(newScene4);
            } else if (event.getSource() == btnPlace) {
                Parent newPane5 = FXMLLoader.load(getClass().getResource("placePane.fxml"));
                Scene newScene5 = new Scene(newPane5);
                stage.setScene(newScene5);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
