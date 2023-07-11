package appgui.controller;

import historyobject.HistoryObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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

    //    TableView for Character in All Character Tab
    @FXML
    protected TableView<HistoryObject> tbv;
    @FXML
    protected TableColumn<HistoryObject, String> tbcName;
    protected ObservableList<HistoryObject> dataObject = FXCollections.observableArrayList();
    protected List<HistoryObject> historyObjectList;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    protected void search() {
        String searchQuery = search.getText().trim().toLowerCase();
        if (searchQuery.isEmpty()) {
            tbv.setItems(dataObject);
        } else {
            List<HistoryObject> searchResults = historyObjectList.stream()
                    .filter(character -> character.getName().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
            tbv.setItems(FXCollections.observableArrayList(searchResults));
        }
    }
    protected String sceneFromField(String name){
        String sceneName = null;
        if (name.equals("Character")){
            sceneName = "/appgui/fxml/characterPane.fxml";
        } else if (name.equals("Dynasty")){
            sceneName = "/appgui/fxml/dynastyPane.fxml";
        } else if (name.equals("Event")){
            sceneName = "/appgui/fxml/eventPane.fxml";
        } else if (name.equals("Festival")){
            sceneName = "/appgui/fxml/festivalPane.fxml";
        } else if (name.equals("Place")){
            sceneName = "/appgui/fxml/placePane.fxml";
        }
        return sceneName;
    }


    @FXML
    protected void addSceneSwitchingHandler(ActionEvent event) {
        Stage stage = (Stage) btnEvent.getScene().getWindow();
        try {
            if (event.getSource() == btnEvent) {
                LinkController.selectedEventName = null;
                Parent newPane = FXMLLoader.load(getClass().getResource("/appgui/fxml/eventPane.fxml"));
                Scene newScene = new Scene(newPane);
                stage.setScene(newScene);
            } else if (event.getSource() == btnCharacter) {
                LinkController.selectedCharacterName = null;
                Parent newPane2 = FXMLLoader.load(getClass().getResource("/appgui/fxml/characterPane.fxml"));
                Scene newScene2 = new Scene(newPane2);
                stage.setScene(newScene2);
            } else if (event.getSource() == btnDynasty) {
                LinkController.selectedDynastyName = null;
                Parent newPane3 = FXMLLoader.load(getClass().getResource("/appgui/fxml/dynastyPane.fxml"));
                Scene newScene3 = new Scene(newPane3);
                stage.setScene(newScene3);
            } else if (event.getSource() == btnFestival) {
                LinkController.selectedFestivalName = null;
                Parent newPane4 = FXMLLoader.load(getClass().getResource("/appgui/fxml/festivalPane.fxml"));
                Scene newScene4 = new Scene(newPane4);
                stage.setScene(newScene4);
            } else if (event.getSource() == btnPlace) {
                LinkController.selectedPlaceName = null;
                Parent newPane5 = FXMLLoader.load(getClass().getResource("/appgui/fxml/placePane.fxml"));
                Scene newScene5 = new Scene(newPane5);
                stage.setScene(newScene5);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //    Make tableview show selected row by hyperlink
    public void selectCellByValue(String targetValue) {
        for (int row = 0; row < tbv.getItems().size(); row++) {
            String cellValue = tbcName.getCellData(row);
            if (cellValue.equals(targetValue)) {
                tbv.getSelectionModel().select(row, tbcName);
                tbv.scrollTo(row);
                break;
            }
        }
    }
}
