/* Việc cần làm khi sửa code :
 * 1. Tối ưu exception handling trong file
 * 2.
 */


package appgui.controller;

import apprunner.executedata.FestivalExecData;

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
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Font;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FestivalController  extends Controller{

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            historyObjectList = FestivalExecData.loadFestivals(dataJson);
            FestivalExecData execDataFestival = new FestivalExecData(historyObjectList);
            tbcName.setCellValueFactory(new PropertyValueFactory<HistoryObject, String>("name"));
            dataObject = FXCollections.observableArrayList(historyObjectList);
            tbv.setItems(dataObject);

            search.setOnKeyReleased(event -> search());

            LinkController.selectedFestival = execDataFestival.searchByName(LinkController.selectedFestivalName);

//            Initialize selected object every Controller
            if (LinkController.selectedFestivalName == null){
                LinkController.selectedFestival = historyObjectList.get(0);
                displaySelectionInfo(LinkController.selectedFestival, execDataFestival);
                selectCellByValue(LinkController.selectedFestival.getName());
            } else if (LinkController.selectedFestivalName != null) {
                displaySelectionInfo(LinkController.selectedFestival, execDataFestival);
                selectCellByValue(LinkController.selectedFestival.getName());
            }

            tbv.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                updateSelectionInfo(newSelection, execDataFestival);
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void updateSelectionInfo(HistoryObject festivalSelection, FestivalExecData execDataFestival) {
        if (festivalSelection != null) {
            displaySelectionInfo(festivalSelection, execDataFestival);
        }
    }
    private void displaySelectionInfo(HistoryObject festivalSelection, FestivalExecData execDataFestival) {
        labelName.setText("" + festivalSelection.getName());
        infoAnchorPane.getChildren().clear();

        TextFlow textFlow = new TextFlow();

        if (festivalSelection.getImageUrl() != null){
            ImageView imageView = new ImageView();
            Image image = new Image(festivalSelection.getImageUrl(), true);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(infoAnchorPane.getPrefWidth() - 20 );
            imageView.setImage(image);

            textFlow.setTextAlignment(TextAlignment.CENTER);
            textFlow.getChildren().add(imageView);
        }

        String festivalDescription = festivalSelection.getDescription();

        textFlow.setPrefWidth(infoAnchorPane.getPrefWidth());
        textFlow.setMaxWidth(infoAnchorPane.getPrefWidth());
        Text text = new Text("\n"+festivalDescription);
        textFlow.getChildren().add(text);
        textFlow.getChildren().add(new Text("\n"));

        Text infoStart = new Text("\nThông tin chi tiết của " + festivalSelection.getName() + ":");
        infoStart.setFont(new Font(16));
        textFlow.getChildren().add(infoStart);
        infoAnchorPane.getChildren().add(textFlow);

        objectInfoBox = execDataFestival.getInfoBoxByName(historyObjectList, festivalSelection.getName());

        VBox contentContainer = new VBox(10);
        contentContainer.setPadding(new Insets(10));
        contentContainer.getChildren().add(textFlow);

        for (String key : objectInfoBox.keySet()) {
            HBox infoItem = new HBox();
            infoItem.setPrefHeight(0);
            infoItem.setPrefHeight(0);
            infoItem.setAlignment(Pos.CENTER_LEFT);

            Object valueObject = objectInfoBox.get(key);

            Label infoKey = new Label("\u2023 " + key + ": ");
            infoItem.getChildren().add(infoKey);
            if (valueObject instanceof JSONArray){
                JSONArray valueArray = (JSONArray) valueObject;
                for (int i = 0; i < valueArray.length(); i++) {
                    JSONObject valueObjectFromArray = valueArray.getJSONObject(i);
                    String fieldName = execDataFestival.dataSearchField(valueObjectFromArray.getString("name"));
                    String sceneName = sceneFromField(fieldName);
                    if (sceneName != null){
                        Hyperlink link = new Hyperlink(valueObjectFromArray.getString("name"));
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
                        Label link = new Label(valueObjectFromArray.getString("name"));
                        link.setWrapText(true);
                        link.setMaxWidth(infoAnchorPane.getPrefWidth() - infoKey.getPrefWidth());
                        infoItem.getChildren().add(link);
                    }
                }
            } else {
                String value = objectInfoBox.getString(key);
                if (value != null) {
                    String fieldName = execDataFestival.dataSearchField(value);
                    String sceneName = sceneFromField(fieldName);
                    if (sceneName != null) {
                        Hyperlink link = new Hyperlink(value);
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
                    }
                    else {
                        Label link = new Label(value);
                        infoItem.getChildren().add(link);
                    }
                }
            }
            contentContainer.getChildren().add(infoItem);
        }

        infoAnchorPane.getChildren().add(contentContainer);
        infoScrollPane.setContent(infoAnchorPane);
    }

}