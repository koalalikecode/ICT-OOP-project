/* Việc cần làm khi sửa code :
    * 1. Tối ưu exception handling trong file
    * 2.
 */



package appgui;

import appgui.LinkController;
import historyobject.Character;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CharacterController implements Initializable {
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



//    Search Character
    @FXML
    private TextField searchCharacter;

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

//    TableView for Character in All Character Tab
    @FXML
    private TableView<Character> tbvCharacters;
    @FXML
    private TableColumn<Character, String> tbcName;
    private ObservableList<Character> dataCharacter = FXCollections.observableArrayList();
    private List<Character> characterList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            characterList = CharacterExecData.loadCharacters(dataJson);
            CharacterExecData execDataCharacter = new CharacterExecData(characterList);
            tbcName.setCellValueFactory(new PropertyValueFactory<Character, String>("name"));
            dataCharacter = FXCollections.observableArrayList(characterList);
            tbvCharacters.setItems(dataCharacter);

            searchCharacter.setOnKeyReleased(event -> searchCharacter());

            LinkController.selectedCharacter = execDataCharacter.searchByName(LinkController.selectedCharacterName);

//            Initialize selected object every Controller
            if (LinkController.selectedCharacter == null){
                LinkController.selectedCharacter = characterList.get(0);
                displaySelectionInfo(LinkController.selectedCharacter, execDataCharacter);
                selectCellByValue(LinkController.selectedCharacter.getName());
                System.out.println(LinkController.selectedCharacter.getName());
            } else if (LinkController.selectedCharacter != null) {
                displaySelectionInfo(LinkController.selectedCharacter, execDataCharacter);
                selectCellByValue(LinkController.selectedCharacter.getName());
            }

            tbvCharacters.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                updateSelectionInfo(newSelection, execDataCharacter);
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void updateSelectionInfo(Character characterSelection, CharacterExecData execDataCharacter) {
        if (characterSelection != null) {
            displaySelectionInfo(characterSelection, execDataCharacter);
        }
    }
    private void displaySelectionInfo(Character characterSelection, CharacterExecData execDataCharacter) {
        labelName.setText("Name: " + characterSelection.getName());
        infoAnchorPane.getChildren().clear();
        infoPane.getChildren().clear();

//      Add the labels, dataPane
        infoAnchorPane.getChildren().addAll(labelName, infoPane);

        TextFlow textFlow = new TextFlow();
        String characterInfo = execDataCharacter.listDataByName(characterSelection.getName());

        textFlow.setPrefWidth(infoPane.getPrefWidth());
        linebreak(characterInfo, textFlow);

//      Get the Information and Connection of Charater
        characterInfoBox = execDataCharacter.getInfoBoxByName(characterList, characterSelection.getName());
        characterConnectionBox = execDataCharacter.getConnectionBoxByName(characterList, characterSelection.getName());

        Text infoStart = new Text("\nThông tin chi tiết của " + characterSelection.getName() + ": ");
        textFlow.getChildren().add(infoStart);
        infoPane.getChildren().add(textFlow);

        VBox contentContainer = new VBox(10);
        contentContainer.setPadding(new Insets(10));
        contentContainer.getChildren().add(textFlow);

//      Add Information of the character
        for (String key : characterInfoBox.keySet()) {
            HBox infoItem = new HBox();
            infoItem.setPrefHeight(0);
            infoItem.setAlignment(Pos.CENTER_LEFT);

            JSONObject info = characterInfoBox.getJSONObject(key);

            Label infoKey = new Label(key + ": ");
            infoItem.getChildren().add(infoKey);
            if (info.has("name") && info.has("url")) {
                Hyperlink link = new Hyperlink(info.getString("name"));
                String fieldName = execDataCharacter.dataSearchField(info.getString("name"));
                String sceneName = sceneFromField(fieldName);
                link.setOnAction(event -> {
                    try {
                        LinkController.setSelectedObject(link.getText(), fieldName);
                        Stage stage = (Stage) link.getScene().getWindow();
                        Parent root = FXMLLoader.load(getClass().getResource(sceneName));
                        Scene newScene = new Scene(root);
                        stage.setScene(newScene);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                infoItem.getChildren().add(link);
            } else if(info.has("name")) {
                Label link = new Label(info.getString("name"));
                infoItem.getChildren().add(link);
            }
            contentContainer.getChildren().add(infoItem);
        }
        Text connectionStart = new Text("Thông tin liên quan của " + characterSelection.getName() + ": ");
        contentContainer.getChildren().add(connectionStart);

//      Add the connections of the Character
        if (characterConnectionBox != null) {
            if (!characterConnectionBox.isEmpty()) {
                for (JSONObject connection : characterConnectionBox) {
                    HBox infoItem = new HBox();
                    infoItem.setPrefHeight(0);
                    infoItem.setAlignment(Pos.CENTER_LEFT);
                    String connectionName = connection.getString("name");
                    String connectionUrl = connection.getString("url");

                    Label infoKey = new Label("Tên : ");
                    infoItem.getChildren().add(infoKey);
                    if (connectionName != null && connectionUrl != null) {
                        Hyperlink link = new Hyperlink(connectionName);
                        String fieldName = execDataCharacter.dataSearchField(connectionName);
                        String sceneName = sceneFromField(fieldName);
                        link.setOnAction(event -> {
                            try {
                                LinkController.setSelectedObject(link.getText(), fieldName);
                                Stage stage = (Stage) link.getScene().getWindow();
                                Parent root = FXMLLoader.load(getClass().getResource(sceneName));
                                Scene newScene = new Scene(root);
                                stage.setScene(newScene);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        infoItem.getChildren().add(link);
                    } else if (connectionName != null) {
                        Label link = new Label(connectionName);
                        infoItem.getChildren().add(link);
                    }
                    contentContainer.getChildren().add(infoItem);
                }

            }
        }
        infoPane.getChildren().add(contentContainer);
        infoScrollPane.setContent(infoAnchorPane);
    }

    private String sceneFromField(String name){
        String sceneName;
        if (name == "Character"){
            sceneName = "characterPane.fxml";
        } else if (name == "Dynasty"){
            sceneName = "dynastyPane.fxml";
        } else if (name == "Event"){
            sceneName = "eventPane.fxml";
        } else if (name == "Festival"){
            sceneName = "festivalPane.fxml";
        } else if (name == "Place"){
            sceneName = "placePane.fxml";
        } else {
            sceneName = "characterPane.fxml";
        }
        return sceneName;
    }

//    Make tableview show selected row by hyperlink
    public void selectCellByValue(String targetValue) {
        for (int row = 0; row < tbvCharacters.getItems().size(); row++) {
            String cellValue = tbcName.getCellData(row);
            if (cellValue.equals(targetValue)) {
                tbvCharacters.getSelectionModel().select(row, tbcName);
                tbvCharacters.scrollTo(row);
                break;
            }
        }
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

    private void searchCharacter() {
        String searchQuery = searchCharacter.getText().trim().toLowerCase();
        if (searchQuery.isEmpty()) {
            tbvCharacters.setItems(dataCharacter);
        } else {
            List<Character> searchResults = characterList.stream()
                    .filter(character -> character.getName().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
            tbvCharacters.setItems(FXCollections.observableArrayList(searchResults));
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