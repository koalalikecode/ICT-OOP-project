package appgui.controller;

import apprunner.executedata.EventExecData;

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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EventController extends Controller {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            historyObjectList = EventExecData.loadEvents(dataJson);
            EventExecData execDataEvent = new EventExecData(historyObjectList);
            tbcName.setCellValueFactory(new PropertyValueFactory<HistoryObject, String>("name"));
            dataObject = FXCollections.observableArrayList(historyObjectList);
            tbv.setItems(dataObject);

            search.setOnKeyReleased(event -> search());

            LinkController.selectedEvent = execDataEvent.searchByName(LinkController.selectedEventName);

//            Initialize selected object every Controller
            if (LinkController.selectedEventName == null){
                LinkController.selectedEvent = historyObjectList.get(0);
                displaySelectionInfo(LinkController.selectedEvent, execDataEvent);
                selectCellByValue(LinkController.selectedEvent.getName());
            } else if (LinkController.selectedEventName != null) {
                displaySelectionInfo(LinkController.selectedEvent, execDataEvent);
                selectCellByValue(LinkController.selectedEvent.getName());
            }

            tbv.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                updateSelectionInfo(newSelection, execDataEvent);
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void updateSelectionInfo(HistoryObject eventSelection, EventExecData execDataEvent) {
        if (eventSelection != null) {
            displaySelectionInfo(eventSelection, execDataEvent);
        }
    }
    private void displaySelectionInfo(HistoryObject eventSelection, EventExecData execDataEvent) {
        labelName.setText("" + eventSelection.getName());
        labelName.setWrapText(true);
        labelName.setTextAlignment(TextAlignment.JUSTIFY);
        labelName.setMaxWidth(infoAnchorPane.getPrefWidth());

        infoAnchorPane.getChildren().clear();

        TextFlow textFlow = new TextFlow();

//        Add image view
        if (eventSelection.getImageUrl() != null){
            ImageView imageView = new ImageView();
            Image image = new Image(eventSelection.getImageUrl(), true);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(infoAnchorPane.getPrefWidth() - 20 );
            imageView.setImage(image);

            textFlow.setTextAlignment(TextAlignment.CENTER);
            textFlow.getChildren().add(imageView);
        }

        String eventDescription = eventSelection.getDescription();

        textFlow.setPrefWidth(infoAnchorPane.getPrefWidth());
        textFlow.setMaxWidth(infoAnchorPane.getPrefWidth());
        Text text = new Text("\n"+eventDescription);
        textFlow.getChildren().add(text);
        textFlow.getChildren().add(new Text("\n"));

        Text infoStart = new Text("\nThông tin chi tiết của " + eventSelection.getName() + ":");
        infoStart.setFont(new Font(16));
        textFlow.getChildren().add(infoStart);
        infoAnchorPane.getChildren().add(textFlow);

        objectInfoBox = execDataEvent.getInfoBoxByName(historyObjectList, eventSelection.getName());
        connectionBox = execDataEvent.getConnectionBoxByName(historyObjectList, eventSelection.getName());

        VBox contentContainer = new VBox(10);
        contentContainer.setPadding(new Insets(10));
        contentContainer.getChildren().add(textFlow);

        for (String key : objectInfoBox.keySet()) {
            HBox infoItem = new HBox();
            infoItem.setPrefHeight(0);
            infoItem.setPrefHeight(0);
            infoItem.setAlignment(Pos.CENTER_LEFT);
            Object Value = objectInfoBox.get(key);

            Label infoKey = new Label("\u2023 "+ key + ": ");
            infoItem.getChildren().add(infoKey);

            if (Value instanceof JSONObject){
                JSONObject value = (JSONObject) Value;
                if (value.has("name") && value.has("url")) {
                    String fieldName = execDataEvent.dataSearchField(value.getString("name"));
                    String sceneName = sceneFromField(fieldName);
                    if (sceneName != null) {
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
                        infoItem.getChildren().add(link);
                    }

                } else if(value.has("name")) {
                    Label link = new Label(value.getString("name"));
                    link.setWrapText(true);
                    link.setMaxWidth(infoAnchorPane.getPrefWidth() - infoKey.getPrefWidth());
                    infoItem.getChildren().add(link);
                } else {
                    VBox componentPlace = new VBox(3);
                    infoItem.setAlignment(Pos.TOP_LEFT);

                    for (String valueKey : value.keySet()){
                        HBox infoValue = new HBox();
                        Label link = new Label("\t - " + valueKey + ": " );
                        infoValue.getChildren().add(link);

                        Object phe = value.get(valueKey);
                        if (phe instanceof JSONObject){
                            JSONObject pheObject = (JSONObject) phe;
                            if (pheObject.has("name") && pheObject.has("url")){
                                String fieldName = execDataEvent.dataSearchField(pheObject.getString("name"));
                                String sceneName = sceneFromField(fieldName);
                                Hyperlink pheLink = new Hyperlink(pheObject.getString("name"));
                                pheLink.setWrapText(true);
                                pheLink.setMaxWidth(infoAnchorPane.getPrefWidth() - infoKey.getPrefWidth());
                                pheLink.setOnAction(event -> {
                                    try {
                                        LinkController.setSelectedObject(pheLink.getText(), fieldName);
                                        Stage stage = (Stage) pheLink.getScene().getWindow();
                                        Parent root = FXMLLoader.load(getClass().getResource(sceneName));
                                        Scene newScene = new Scene(root);
                                        stage.setScene(newScene);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                infoValue.getChildren().add(pheLink);
                            }
                        } else {
                            Label infovalue = new Label(value.getString(valueKey));
                            infovalue.setWrapText(true);
                            infovalue.setMaxWidth(infoAnchorPane.getPrefWidth() - infoKey.getPrefWidth() - 90);
                            infoValue.getChildren().add(infovalue);
                        }

                        componentPlace.getChildren().add(infoValue);
                    }
                    contentContainer.getChildren().add(infoItem);
                    contentContainer.getChildren().add(componentPlace);
                    continue;
                }
            } else {
                Label infoValue = new Label(Value.toString());
                infoValue.setWrapText(true);
                infoValue.setMaxWidth(infoAnchorPane.getPrefWidth() - infoKey.getPrefWidth());
                infoItem.getChildren().add(infoValue);
            }

            contentContainer.getChildren().add(infoItem);
        }
        Text connectionStart = new Text("Thông tin liên quan của " + eventSelection.getName() + ": ");
        contentContainer.getChildren().add(connectionStart);

//      Add the connections of the Event
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
                        String fieldName = execDataEvent.dataSearchField(connectionName);
                        String sceneName = sceneFromField(fieldName);
                        if (sceneName != null){
                            Hyperlink link = new Hyperlink(connectionName);
                            link.setOnAction(event -> {
                                try {
                                    LinkController.setSelectedObject(link.getText(), fieldName);
                                    System.out.println(link.getText() + "\n" + fieldName + ":" + sceneName);
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