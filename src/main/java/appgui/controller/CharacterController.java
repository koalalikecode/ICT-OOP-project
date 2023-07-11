/* Việc cần làm khi sửa code :
 * 1. Tối ưu exception handling trong file
 * 2.
 */


package appgui.controller;

import apprunner.executedata.CharacterExecData;

import historyobject.HistoryObject;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CharacterController extends Controller {


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            historyObjectList = CharacterExecData.loadCharacters(dataJson);
            CharacterExecData execDataCharacter = new CharacterExecData(historyObjectList);
            tbcName.setCellValueFactory(new PropertyValueFactory<HistoryObject, String>("name"));
            dataObject = FXCollections.observableArrayList(historyObjectList);
            tbv.setItems(dataObject);

            search.setOnKeyReleased(event -> search());

            LinkController.selectedCharacter = execDataCharacter.searchByName(LinkController.selectedCharacterName);

//            Initialize selected object every Controller
            if (LinkController.selectedCharacterName == null){
                LinkController.selectedCharacter = historyObjectList.get(0);
                displaySelectionInfo(LinkController.selectedCharacter, execDataCharacter);
                selectCellByValue(LinkController.selectedCharacter.getName());
            } else if (LinkController.selectedCharacterName != null) {
                displaySelectionInfo(LinkController.selectedCharacter, execDataCharacter);
                selectCellByValue(LinkController.selectedCharacter.getName());
            }

            tbv.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                updateSelectionInfo(newSelection, execDataCharacter);
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    protected void updateSelectionInfo(HistoryObject characterSelection, CharacterExecData execDataCharacter) {
        if (characterSelection != null) {
            displaySelectionInfo(characterSelection, execDataCharacter);
        }
    }
    protected void displaySelectionInfo(HistoryObject characterSelection, CharacterExecData execDataCharacter) {
        labelName.setText("" + characterSelection.getName());

        infoAnchorPane.getChildren().clear();

        TextFlow textFlow = new TextFlow();
        System.out.println(characterSelection.getImageUrl());
//        Add image view
        if (characterSelection.getImageUrl() != null){
            ImageView imageView = new ImageView();
            Image image = new Image(characterSelection.getImageUrl(), true);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(infoAnchorPane.getPrefWidth() - 20 );
            imageView.setImage(image);

            textFlow.setTextAlignment(TextAlignment.CENTER);
            textFlow.getChildren().add(imageView);
        }

        String characterDescription = characterSelection.getDescription();

        textFlow.setPrefWidth(infoAnchorPane.getPrefWidth());
        textFlow.setMaxWidth(infoAnchorPane.getPrefWidth());
        Text text = new Text("\n"+characterDescription);
        textFlow.getChildren().add(text);
        textFlow.getChildren().add(new Text("\n"));

        Text infoStart = new Text("\nThông tin chi tiết của " + characterSelection.getName() + ":");
        infoStart.setFont(new Font(16));
        textFlow.getChildren().add(infoStart);
        infoAnchorPane.getChildren().add(textFlow);

        objectInfoBox = execDataCharacter.getInfoBoxByName(historyObjectList, characterSelection.getName());
        connectionBox = execDataCharacter.getConnectionBoxByName(historyObjectList, characterSelection.getName());

        VBox contentContainer = new VBox(10);
        contentContainer.setPadding(new Insets(10));
        contentContainer.getChildren().add(textFlow);

        for (String key : objectInfoBox.keySet()) {
            HBox infoItem = new HBox();
            infoItem.setPrefHeight(0);
            infoItem.setPrefHeight(0);
            infoItem.setAlignment(Pos.CENTER_LEFT);
            JSONObject value = objectInfoBox.getJSONObject(key);

            Label infoKey = new Label("\u2023 " + key + ": ");
            infoItem.getChildren().add(infoKey);
            if (value.has("name") && value.has("url")) {
                String fieldName = execDataCharacter.dataSearchField(value.getString("name"));
                String sceneName = sceneFromField(fieldName);
                if (sceneName != null){
                    Hyperlink link = new Hyperlink(value.getString("name"));
                    link.setWrapText(true);
                    link.setMaxWidth(infoAnchorPane.getPrefWidth() - infoKey.getPrefWidth());
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
                } else {
                    Label link = new Label(value.getString("name"));
                    link.setWrapText(true);
                    link.setMaxWidth(infoAnchorPane.getPrefWidth() - infoKey.getPrefWidth() - 80);
                    infoItem.getChildren().add(link);
                }
            } else if(value.has("name")) {
                Label link = new Label(value.getString("name"));
                link.setWrapText(true);
                link.setMaxWidth(infoAnchorPane.getPrefWidth() - infoKey.getPrefWidth() - 80);
                infoItem.getChildren().add(link);
            }
            contentContainer.getChildren().add(infoItem);
        }
        Text connectionStart = new Text("Xem thêm");
        connectionStart.setFont(new Font(16));
        contentContainer.getChildren().add(connectionStart);

//      Add the connections of the Character
        if (connectionBox != null) {
            if (!connectionBox.isEmpty()) {
                for (JSONObject connection : connectionBox) {
                    HBox infoItem = new HBox();
                    infoItem.setPrefHeight(0);
                    infoItem.setAlignment(Pos.CENTER_LEFT);
                    String connectionName = connection.getString("name");
                    String connectionUrl = connection.getString("url");

                    Label infoKey = new Label("Tên : ");
                    infoItem.getChildren().add(infoKey);
                    if (connectionName != null && connectionUrl != null) {
                        String fieldName = execDataCharacter.dataSearchField(connectionName);
                        String sceneName = sceneFromField(fieldName);
                        if (sceneName != null){
                            Hyperlink link = new Hyperlink(connectionName);
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
                        } else {
                            Label link = new Label(connectionName);
                            infoItem.getChildren().add(link);
                        }

                    } else if (connectionName != null) {
                        Label link = new Label(connectionName);
                        infoItem.getChildren().add(link);
                    }
                    contentContainer.getChildren().add(infoItem);
                }
            }
        }
        infoAnchorPane.getChildren().add(contentContainer);
        infoScrollPane.setContent(infoAnchorPane);
    }

}