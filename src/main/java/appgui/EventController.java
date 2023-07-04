package appgui;

import appgui.LinkController;
import historyobject.Event;

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
import javafx.scene.text.Font;
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

public class EventController implements Initializable {
    private final String dataJson = "data/final.json";
    private JSONObject eventInfoBox;
    private List<JSONObject> eventConnectionBox;

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



    //    Search Event
    @FXML
    private TextField searchEvent;

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

    //    TableView for Event in All Event Tab
    @FXML
    private TableView<Event> tbvEvents;
    @FXML
    private TableColumn<Event, String> tbcName;
    private ObservableList<Event> dataEvent = FXCollections.observableArrayList();
    private List<Event> eventList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            eventList = EventExecData.loadEvents(dataJson);
            EventExecData execDataEvent = new EventExecData(eventList);
            tbcName.setCellValueFactory(new PropertyValueFactory<Event, String>("name"));
            dataEvent = FXCollections.observableArrayList(eventList);
            tbvEvents.setItems(dataEvent);

            searchEvent.setOnKeyReleased(event -> searchEvent());

            LinkController.selectedEvent = execDataEvent.searchByName(LinkController.selectedEventName);

//            Initialize selected object every Controller
            if (LinkController.selectedEvent == null){
                LinkController.selectedEvent = eventList.get(0);
                displaySelectionInfo(LinkController.selectedEvent, execDataEvent);
                selectCellByValue(LinkController.selectedEvent.getName());
                System.out.println(LinkController.selectedEvent.getName());
            } else if (LinkController.selectedEvent != null) {
                displaySelectionInfo(LinkController.selectedEvent, execDataEvent);
                selectCellByValue(LinkController.selectedEvent.getName());
            }

            tbvEvents.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                updateSelectionInfo(newSelection, execDataEvent);
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void updateSelectionInfo(Event eventSelection, EventExecData execDataEvent) {
        if (eventSelection != null) {
            displaySelectionInfo(eventSelection, execDataEvent);
        }
    }
    private void displaySelectionInfo(Event eventSelection, EventExecData execDataEvent) {
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

        eventInfoBox = execDataEvent.getInfoBoxByName(eventList, eventSelection.getName());
        eventConnectionBox = execDataEvent.getConnectionBoxByName(eventList, eventSelection.getName());

        VBox contentContainer = new VBox(7);
        contentContainer.setPadding(new Insets(10));
        contentContainer.getChildren().add(textFlow);

        for (String key : eventInfoBox.keySet()) {
            HBox infoItem = new HBox();
            infoItem.setPrefHeight(0);
            infoItem.setPrefHeight(0);
            infoItem.setAlignment(Pos.CENTER_LEFT);
            Label infoKey = new Label(key + ": ");
            infoItem.getChildren().add(infoKey);
            if (!(eventInfoBox.get(key) instanceof String)) {
                JSONObject value = eventInfoBox.getJSONObject(key);
                if (value.has("name") && value.has("url")) {
                    String fieldName = execDataEvent.dataSearchField(value.getString("name"));
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
                                    String fieldName = execDataEvent.dataSearchField(firstComponent.getString("name"));
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
                                    String fieldName = execDataEvent.dataSearchField(secondComponent.getString("name"));
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
                Label infoContent = new Label(eventInfoBox.getString(key));
                infoItem.getChildren().add(infoContent);
            }
        }
        Text connectionStart = new Text("Xem thêm:");
        connectionStart.setFont(new Font(16));
        contentContainer.getChildren().add(connectionStart);

//      Add the connections of the Event
        if (eventConnectionBox != null) {
            if (!eventConnectionBox.isEmpty()) {
                for (JSONObject connection : eventConnectionBox) {
                    HBox infoItem = new HBox();
                    infoItem.setPrefHeight(0);
                    infoItem.setAlignment(Pos.CENTER_LEFT);
                    String connectionName = connection.getString("name");
                    String connectionUrl = connection.getString("url");

                    Label infoKey = new Label("Tên : ");
                    infoItem.getChildren().add(infoKey);
                    if (connectionName != null && connectionUrl != null) {
                        Hyperlink link = new Hyperlink(connectionName);
                        String fieldName = execDataEvent.dataSearchField(connectionName);
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

    private String sceneFromField(String name){
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
            sceneName = "eventPane.fxml";
        }
        return sceneName;
    }

    //    Make tableview show selected row by hyperlink
    public void selectCellByValue(String targetValue) {
        for (int row = 0; row < tbvEvents.getItems().size(); row++) {
            String cellValue = tbcName.getCellData(row);
            if (cellValue.equals(targetValue)) {
                tbvEvents.getSelectionModel().select(row, tbcName);
                tbvEvents.scrollTo(row);
                break;
            }
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

    private void searchEvent() {
        String searchQuery = searchEvent.getText().trim().toLowerCase();
        if (searchQuery.isEmpty()) {
            tbvEvents.setItems(dataEvent);
        } else {
            List<Event> searchResults = eventList.stream()
                    .filter(event -> event.getName().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
            tbvEvents.setItems(FXCollections.observableArrayList(searchResults));
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
