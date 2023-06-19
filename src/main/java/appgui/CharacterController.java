package appgui;

import historyobject.CharacterTest;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.AnchorPane;

import javafx.beans.property.SimpleStringProperty;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CharacterController implements Initializable {
    private String dataJson = "data/final.json";
    private List<String> hyperlinkTexts;

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
    private TableView<CharacterTest> tbvCharacters;
    @FXML
    private TableColumn<CharacterTest, String> tbcName;
    private ObservableList<CharacterTest> dataCharacter = FXCollections.observableArrayList();
    private List<CharacterTest> characterList;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            characterList = printData.loadCharacters(dataJson);
            printData execDataCharacter = new printData(characterList);

//            Get name data of all characters
//            dataCharacter = execDataCharacter.getObservableCharacterList(characterList);
//            dataCharacter = FXCollections.observableArrayList(characterList);
            tbcName.setCellValueFactory(new PropertyValueFactory<CharacterTest, String>("name"));

            dataCharacter = FXCollections.observableArrayList(characterList);
            tbvCharacters.setItems(dataCharacter);

//            Vấn đề nho nhỏ - đ hiện tên wtf - fix sau

//            tbcName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
//            for (CharacterTest character : characterList) {
//                dataCharacter.add(character);
//            }

            tbvCharacters.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    labelName.setText("Name: " + newSelection.getName());
                    infoAnchorPane.getChildren().clear();

                    AnchorPane.setTopAnchor(labelName, 10.0);
                    AnchorPane.setLeftAnchor(labelName, 10.0);

                    AnchorPane.setTopAnchor(infoPane, 60.0);
                    AnchorPane.setRightAnchor(infoPane, 10.0);
                    AnchorPane.setLeftAnchor(infoPane, 10.0);

//                    Add the labels, dataPane, and tbVConnection to the AnchorPane
                    infoAnchorPane.getChildren().addAll(labelName, infoPane);

//                    Add character's information to the dataPane
                    infoPane.getChildren().clear();
                    TextFlow textFlow = new TextFlow();
                    String characterInfo = execDataCharacter.searchByName(newSelection.getName());

                    int numLines = characterInfo.split("\n").length;

                    double lineHeight = 20.0;
                    double padding = 10.0;
                    double prefHeight = numLines * lineHeight + padding;

                    textFlow.setPrefWidth(infoPane.getPrefWidth());
                    linebreak(characterInfo, textFlow);

                    double totalPadding = infoPane.getPadding().getTop() + infoPane.getPadding().getBottom();
                    double newHeight = prefHeight + totalPadding;
                    infoPane.setPrefHeight(newHeight);
                    Text infoStart = new Text("\n\n\nThông tin chi tiết của " + newSelection.getName() + ":\n");
                    textFlow.getChildren().add(infoStart);
                    infoPane.getChildren().add(textFlow);

                    hyperlinkTexts = execDataCharacter.getHyperTextLinksByName(characterList, newSelection.getName());

                    VBox contentContainer = new VBox(10);
                    contentContainer.setPadding(new Insets(10));
                    contentContainer.getChildren().addAll(textFlow);

//                    Separator separator = new Separator(Orientation.HORIZONTAL);
//                    contentContainer.getChildren().add(separator);

                    for (String hyperlink : hyperlinkTexts) {
                        Hyperlink link = new Hyperlink(hyperlink);
                        link.setOnAction(event -> {

                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("eventPane.fxml"));
                                Parent root = loader.load();

//                                EventController eventController = loader.getController();
//                                Ta sẽ add các link của các nhân vật vào đây
//                                eventController.setEventData(hyperlink);

                                Stage stage = (Stage) tbvCharacters.getScene().getWindow();
                                stage.setScene(new Scene(root));
                                stage.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        contentContainer.getChildren().add(link);
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

    public void showPersonDetails(CharacterTest character) {
        if (character != null) {
            labelName.setText("Name: " + character.getName());

            infoPane.getChildren().clear();

            TextFlow textFlow = new TextFlow();
            textFlow.setPrefWidth(infoPane.getPrefWidth());

            String data = printData.printdata();
            linebreak(data, textFlow);
            infoPane.getChildren().add(textFlow);

        }
    }

}
