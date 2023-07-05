package appgui;

import historyobject.HistoryObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {
    protected ExecData execDataCharacter;
    protected final String dataJson=  "data/final.json";;
    protected JSONObject infoBox;
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
    protected Pane infoPane;
    @FXML
    protected Pane hyperlinkPane;
    @FXML
    protected Label labelName;
    @FXML
    protected AnchorPane infoAnchorPane;

    //    TableView for Character in All Character Tab
    @FXML
    protected TableView<HistoryObject> tbv;
    @FXML
    protected TableColumn<HistoryObject, String> tbcName;
    protected ObservableList<HistoryObject> dataObs = FXCollections.observableArrayList();
    protected List<HistoryObject> objectList;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tbcName.setCellValueFactory(new PropertyValueFactory<HistoryObject, String>("name"));
        dataObs = FXCollections.observableArrayList(objectList);
        tbv.setItems(dataObs);

        search.setOnKeyReleased(event -> Search());

        LinkController.selectedObject = execDataCharacter.searchByName(LinkController.selectedObjectName);

//            Initialize selected object every Controller
        if (LinkController.selectedObject == null){
            LinkController.selectedObject = objectList.get(0);
            displaySelectionInfo(LinkController.selectedObject, execDataCharacter);
            selectCellByValue(LinkController.selectedObject.getName());
            System.out.println(LinkController.selectedObject.getName());
        } else if (LinkController.selectedObject != null) {
            displaySelectionInfo(LinkController.selectedObject, execDataCharacter);
            selectCellByValue(LinkController.selectedObject.getName());
        }

        tbv.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateSelectionInfo(newSelection, execDataCharacter);
        });
    }
    protected void Search() {
        String searchQuery = search.getText().trim().toLowerCase();
        if (searchQuery.isEmpty()) {
            tbv.setItems(dataObs);
        } else {
            List<HistoryObject> searchResults = objectList.stream()
                    .filter(character -> character.getName().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
            tbv.setItems(FXCollections.observableArrayList(searchResults));
        }
    }
    @FXML
    private void addSceneSwitchingHandler(ActionEvent event) {
        Stage stage = (Stage) btnEvent.getScene().getWindow();
        try {
            if (event.getSource() == btnCharacter) {
                Parent newPane = FXMLLoader.load(getClass().getResource("characterPane.fxml"));
                Scene newScene = new Scene(newPane);
                stage.setScene(newScene);
            } else if (event.getSource() == btnEvent) {
                Parent newPane2 = FXMLLoader.load(getClass().getResource("eventPane.fxml"));
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

    protected String sceneFromField(String name){
        String sceneName;
        if (name.equals("Character")){
            sceneName = "characterPane.fxml";
        } else if (name.equals("Dynasty")){
            sceneName = "dynastyPane.fxml";
        } else if (name.equals("Event")){
            sceneName = "eventPane.fxml";
        } else if (name.equals("Festival")){
            sceneName = "festivalPane.fxml";
        } else if (name.equals("Place")){
            sceneName = "placePane.fxml";
        } else {
            sceneName = "characterPane.fxml";
        }
        return sceneName;
    }
    protected void updateSelectionInfo(HistoryObject characterSelection, ExecData execDataCharacter) {
        if (characterSelection != null) {
            displaySelectionInfo(characterSelection, execDataCharacter);
        }
    }
    protected void displaySelectionInfo(HistoryObject eventSelection, ExecData execDataEvent) {
        labelName.setText("" + eventSelection.getName());
        infoAnchorPane.getChildren().clear();

        TextFlow textFlow = new TextFlow();
        String eventDescription = eventSelection.getDescription();

        textFlow.setPrefWidth(infoAnchorPane.getPrefWidth());
        textFlow.setMaxWidth(infoAnchorPane.getPrefWidth());

        Text descriptionStart = new Text("Tổng quan\n\n");
        descriptionStart.setFont(new Font(16));
        textFlow.getChildren().add(descriptionStart);

        Text text = new Text(eventDescription);
        textFlow.getChildren().add(text);
        textFlow.getChildren().add(new Text("\n"));

        Text infoStart = new Text("\nThông tin chi tiết");
        infoStart.setFont(new Font(16));
        textFlow.getChildren().add(infoStart);
        infoAnchorPane.getChildren().add(textFlow);

        infoBox = execDataEvent.getInfoBoxByName(objectList, eventSelection.getName());
        connectionBox = execDataEvent.getConnectionBoxByName(objectList, eventSelection.getName());

        VBox contentContainer = new VBox(7);
        contentContainer.setPadding(new Insets(10));
        contentContainer.getChildren().add(textFlow);

        for (String key : infoBox.keySet()) {
            HBox infoItem = new HBox();
            infoItem.setPrefHeight(0);
            infoItem.setPrefHeight(0);
            infoItem.setAlignment(Pos.CENTER_LEFT);
            Label infoKey = new Label(key + ": ");
            infoItem.getChildren().add(infoKey);
            if (!(infoBox.get(key) instanceof String)) {
                JSONObject value = infoBox.getJSONObject(key);
                if (value.has("name") && value.has("url")) {
                    String fieldName = execDataEvent.dataSearchField(value.getString("name"), dataJson);
                    String sceneName = sceneFromField(fieldName);
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
                } else if(value.has("name")) {
                    Label link = new Label(value.getString("name"));
                    link.setWrapText(true);
                    link.setMaxWidth(infoAnchorPane.getPrefWidth() - infoKey.getPrefWidth());
                    infoItem.getChildren().add(link);
                } else if(value.has("phe 1") || value.has("phe 2")) {
                    VBox componentEvent = new VBox(3);
                    infoItem.setAlignment(Pos.TOP_LEFT);
                    if (value.has("phe 1")) {
                        if (!(value.get("phe 1") instanceof String)) {
                            JSONObject firstComponent = value.getJSONObject("phe 1");
                            HBox componentItem = new HBox(1);
                            componentItem.setAlignment(Pos.CENTER_LEFT);
                            componentItem.getChildren().add(new Label("Phe 1: "));
                            if (firstComponent.has("name")) {
                                if (firstComponent.has("url")) {
                                    String fieldName = execDataEvent.dataSearchField(firstComponent.getString("name"),dataJson);
                                    String sceneName = sceneFromField(fieldName);
                                    Hyperlink link = new Hyperlink(firstComponent.getString("name"));
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
                                    componentItem.getChildren().add(link);
                                } else {
                                    Label link = new Label(firstComponent.getString("name"));
                                    componentItem.getChildren().add(link);
                                }
                            }
                            componentEvent.getChildren().add(componentItem);
                        } else {
                            Label componentItem1 = new Label("Phe 1: " + value.getString("phe 1"));
                            componentEvent.getChildren().add(componentItem1);
                        }
                    }
                    if (value.has("phe 2")) {
                        if (!(value.get("phe 2") instanceof String)) {
                            JSONObject secondComponent = value.getJSONObject("phe 2");
                            HBox componentItem = new HBox(1);
                            componentItem.setAlignment(Pos.CENTER_LEFT);
                            componentItem.getChildren().add(new Label("Phe 2: "));
                            if (secondComponent.has("name")) {
                                if (secondComponent.has("url")) {
                                    String fieldName = execDataEvent.dataSearchField(secondComponent.getString("name"),dataJson);
                                    String sceneName = sceneFromField(fieldName);
                                    Hyperlink link = new Hyperlink(secondComponent.getString("name"));
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
                                } else {
                                    Label link = new Label(secondComponent.getString("name"));
                                    componentItem.getChildren().add(link);
                                }
                            }
                            componentEvent.getChildren().add(componentItem);
                        } else {
                            Label componentItem2 = new Label("Phe 2: " + value.getString("phe 2"));
                            componentEvent.getChildren().add(componentItem2);
                        }
                    }
                    infoItem.getChildren().add(componentEvent);
                }
                contentContainer.getChildren().add(infoItem);
            } else {
                Label infoContent = new Label(infoBox.getString(key));
                infoItem.getChildren().add(infoContent);
            }
        }
        Text connectionStart = new Text("Xem thêm:");
        connectionStart.setFont(new Font(16));
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
                        Hyperlink link = new Hyperlink(connectionName);
                        String fieldName = execDataEvent.dataSearchField(connectionName,dataJson);
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
        infoAnchorPane.getChildren().add(contentContainer);
        infoScrollPane.setContent(infoAnchorPane);
    }

}
