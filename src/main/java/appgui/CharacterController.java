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
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CharacterController implements Initializable {
    private final String dataJson = "data/final.json";
    private JSONObject characterInfoBox;

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
            tbvCharacters.setItems(dataCharacter);

            searchCharacter.setOnKeyReleased(event -> searchCharacter());

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

//            Buttons on the menu to switch scenes

//            btnEvent.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
//                try {
//                    FXMLLoader loader = new FXMLLoader(getClass().getResource("eventPane.fxml"));
//                    Parent root = loader.load();
//                    Stage stage = (Stage) btnEvent.getScene().getWindow();
//                    stage.setScene(new Scene(root));
//                    stage.show();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });

        } catch (IOException e) {
            throw new RuntimeException(e);
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
