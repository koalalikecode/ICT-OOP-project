package appgui;

import historyobject.Character;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.AnchorPane;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CharacterController implements Initializable {
    private String dataJson = "data/final.json";
    private JSONObject characterInfoBox;

//    Scenes
    private Scene sceneCharacter;
    private Scene sceneDynasty;
    private Scene sceneEvent;
    private Scene sceneFestival;
    private Scene scenePlace;

    @FXML
    private ScrollPane infoScrollPane;
    @FXML
    private Pane infoPane;
    @FXML
    private Pane hyperlinkPane;
    @FXML
    private Label labelName;
    @FXML
    private AnchorPane infoAnchorPane;

    // TableView for Character in All Character Tab
    @FXML
    private TableView<Character> tbvCharacters;
    @FXML
    private TableColumn<Character, String> tbcName;
    private ObservableList<Character> dataCharacter = FXCollections.observableArrayList();
    private List<Character> characterList;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            characterList = printData.loadCharacters(dataJson);
            printData execDataCharacter = new printData(characterList);
            tbcName.setCellValueFactory(new PropertyValueFactory<Character, String>("name"));
            dataCharacter = FXCollections.observableArrayList(characterList);
            tbvCharacters.setItems(
                dataCharacter
            );

            for (Character character : characterList) {
                dataCharacter.add(character);
            }

            tbvCharacters.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    labelName.setText("Name: " + newSelection.getName());
                    infoAnchorPane.getChildren().clear();


//                    Add the labels, dataPane
                    infoAnchorPane.getChildren().addAll(labelName, infoPane);


                    infoPane.getChildren().clear();
                    TextFlow textFlow = new TextFlow();
                    String characterInfo = execDataCharacter.listDataByName(newSelection.getName());

                    int numLines = characterInfo.split("\n").length;

                    double lineHeight = 20.0;
                    double padding = 10.0;
                    double prefHeight = numLines * lineHeight + padding;

                    textFlow.setPrefWidth(infoPane.getPrefWidth());
                    linebreak(characterInfo, textFlow);

                    Text infoStart = new Text("\nThông tin chi tiết của " + newSelection.getName());
                    textFlow.getChildren().add(infoStart);
                    infoPane.getChildren().add(textFlow);

                    characterInfoBox = execDataCharacter.getInfoBoxByName(characterList, newSelection.getName());

                    VBox contentContainer = new VBox(10);
                    contentContainer.setPadding(new Insets(10));
                    contentContainer.getChildren().add(textFlow);

                    for (String key : characterInfoBox.keySet()) {
                        HBox infoItem = new HBox();
                        infoItem.setPrefHeight(0);
                        infoItem.setPrefHeight(0);
                        infoItem.setAlignment(Pos.CENTER_LEFT);
                        JSONObject value = characterInfoBox.getJSONObject(key);
                        Label infoKey = new Label(key + ": ");
                        infoItem.getChildren().add(infoKey);
                        if (value.has("name") && value.has("url")) {
                            Hyperlink link = new Hyperlink(value.getString("name"));
                            infoItem.getChildren().add(link);
                        } else if(value.has("name")) {
                            Label link = new Label(value.getString("name"));
                            infoItem.getChildren().add(link);
                        }
                        contentContainer.getChildren().add(infoItem);
                    }

                    infoPane.getChildren().add(contentContainer);

                    infoScrollPane.setContent(infoAnchorPane);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public TextFlow linebreak(String data, TextFlow textFlow) {
        String[] lines = data.split("\\n");
        for (String line : lines) {
            Text text = new Text(line);
            textFlow.getChildren().add(text);
            textFlow.getChildren().add(new Text("\n"));
        }
        return textFlow;
    }
}
