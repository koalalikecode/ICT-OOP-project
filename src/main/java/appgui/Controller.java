package appgui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    protected final String dataJson = "processed_data/final.json";
    protected JSONObject objectInfoBox;
    protected List<JSONObject> connectionBox;

    //    Menu Buttons
    @FXML
    protected Button btnCharacter;
    @FXML
    protected Button btnDynasty;
    @FXML
    protected Button btnEvent;
    @FXML
    protected Button btnFestival;
    @FXML
    protected Button btnPlace;


    //    Scenes
    protected Scene sceneCharacter;
    protected Scene sceneDynasty;
    protected Scene sceneEvent;
    protected Scene sceneFestival;
    protected Scene scenePlace;



    //    Search Character
    @FXML
    protected TextField search;

    @FXML
    protected ScrollPane infoScrollPane;
    @FXML
    protected Label labelName;
    @FXML
    protected AnchorPane infoAnchorPane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
